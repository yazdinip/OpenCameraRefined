package net.sourceforge.opencamera.Preview.CameraSurface;

import net.sourceforge.opencamera.MainActivity;
import net.sourceforge.opencamera.MyDebug;
import net.sourceforge.opencamera.CameraController.CameraController;
import net.sourceforge.opencamera.CameraController.CameraControllerException;
import net.sourceforge.opencamera.Preview.Preview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import android.graphics.Paint;


import android.graphics.Rect;

import android.content.res.Resources;
import android.view.ViewGroup;

import net.sourceforge.opencamera.R;
import net.sourceforge.opencamera.tensorflow.*;
import net.sourceforge.opencamera.tensorflow.env.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/** Provides support for the surface used for the preview, using a SurfaceView.
 */
public class MySurfaceView extends SurfaceView implements CameraSurface {
	private static final String TAG = "MySurfaceView";

	private final Preview preview;
	private final int [] measure_spec = new int[2];
	private final Handler handler = new Handler();
	private final Runnable tick;
	private Bitmap croppedBitmap = null;
	private Bitmap rgbFrameBitmap = null;
	private Rect detection = new Rect((int) 0,(int)0,(int)0,(int)0);
	private Matrix frameToCropTransform = null;
	private Matrix cropToFrameTransform;

	Bitmap c=BitmapFactory.decodeResource(getResources(), R.drawable.cat);
	Bitmap d=BitmapFactory.decodeResource(getResources(), R.drawable.dog);
	List<Bitmap> filters = Arrays.asList(c,d,d);
	int filterIndex = 0;
	List<Classifier.Recognition> rec = null;

	private static final Logger LOGGER = new Logger();

	@SuppressWarnings("deprecation")
	public
	MySurfaceView(Context context, final Preview preview) {
		super(context);
		this.preview = preview;
		if( MyDebug.LOG ) {
			Log.d(TAG, "new MySurfaceView");
		}

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		getHolder().addCallback(preview);
        // deprecated setting, but required on Android versions prior to 3.0
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // deprecated

		tick = new Runnable() {
			public void run() {
				/*if( MyDebug.LOG )
					Log.d(TAG, "invalidate()");*/
				preview.test_ticker_called = true;
				invalidate();
				// avoid overloading ui thread when taking photo
				handler.postDelayed(this, preview.isTakingPhoto() ? 500 : 100);
			}
		};

	}
	
	@Override
	public View getView() {
		return this;
	}
	
	@Override
	public void setPreviewDisplay(CameraController camera_controller) {
		if( MyDebug.LOG )
			Log.d(TAG, "setPreviewDisplay");
		try {
			camera_controller.setPreviewDisplay(this.getHolder());
		}
		catch(CameraControllerException e) {
			if( MyDebug.LOG )
				Log.e(TAG, "Failed to set preview display: " + e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public void setVideoRecorder(MediaRecorder video_recorder) {
    	video_recorder.setPreviewDisplay(this.getHolder().getSurface());
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return preview.touchEvent(event);
    }

    @Override
    public void detect(){
		Log.i("image: ", preview.getCurrentPreviewSize().height + Integer.toString(preview.getCurrentPreviewSize().width));


		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				rec = preview.classifier.recognizeImage(loadBitmapFromView(preview.getView(), preview));

			}
		});

	}

	public Bitmap loadBitmapFromView(View v, Preview preview) {
//		classifier.
		int previewWidth = preview.getCurrentPreviewSize().width;
		int previewHeight = preview.getCurrentPreviewSize().height;
		boolean MAINTAIN_ASPECT = false;

		int cropSize = 300;
		Log.i("image: ", previewWidth + Integer.toString(previewHeight));
		if (rgbFrameBitmap == null) {
			rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888);
		}
		if (croppedBitmap == null) {
			croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);
		}
		if (frameToCropTransform == null) {
			frameToCropTransform =
					ImageUtils.getTransformationMatrix(
							previewWidth, previewHeight,
							cropSize, cropSize,
							0, MAINTAIN_ASPECT);
		}
		int[] rgbBytes = null;
		rgbBytes = new int[previewWidth * previewHeight];
		Log.i("q: ", Integer.toString(preview.CameraFrame.length));
		if (preview.CameraFrame != null){
			ImageUtils.convertYUV420SPToARGB8888(preview.CameraFrame, previewWidth, previewHeight, rgbBytes);
			rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);
			final Canvas canvas = new Canvas(croppedBitmap);
			canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

		}
		cropToFrameTransform = new Matrix();
		frameToCropTransform.invert(cropToFrameTransform);

		return croppedBitmap;
	}

	@Override
	public void onDraw(Canvas canvas) {
		Canvas TextCanvas = canvas;
		Paint  paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		if (rec != null) {
			int smileCount = 0;
			int thumbCount = 0;
			for (int i = 0; i < rec.size(); i++) {
//				Log.i("image: ", rec.get(i).getTitle());
//				Log.i("image: ", Float.toString(rec.get(i).getConfidence()));
				RectF rect = rec.get(i).getLocation();
				cropToFrameTransform.mapRect(rect);
				if (rec.get(i).getConfidence() > 0.93) {
					if (rec.get(i).getTitle().contains("smile")){
						Bitmap filter = Bitmap.createScaledBitmap(filters.get(filterIndex), (int) rect.right - (int) rect.left, (int) rect.bottom - (int) rect.top, true);
						canvas.drawBitmap(filter, (int) rect.left, (int) rect.top, paint);
						if (!preview.isOnTimer()) {
//							preview.takePicturePressed();
						}
						smileCount +=1;
					} else if (rec.get(i).getTitle().contains("thumbup")){
						if (filterIndex==filters.size()-1) {
							filterIndex = 0;
						}else{
							filterIndex +=1;
						}
						thumbCount +=1;
					}
					detection = new Rect((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
				} else {
					detection = new Rect((int) 0, (int) 0, (int) 0, (int) 0);
				}
				canvas.drawRect(detection,paint);
				paint.setTextSize(20);
				canvas.drawText(rec.get(i).getTitle() + " " + rec.get(i).getConfidence().toString(), detection.centerX(), detection.centerY(), paint);
			}



			TextCanvas.rotate(-90, canvas.getWidth()/2, canvas.getHeight()/2);
			paint.setTextSize(40);
			TextCanvas.drawText("# of Smiles detected: " + smileCount, 200, 0, paint);
			TextCanvas.drawText("# of Thumb detected: " + thumbCount, 200, 50, paint);
		}
		preview.draw(TextCanvas);
		preview.draw(canvas);
	}

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
    	preview.getMeasureSpec(measure_spec, widthSpec, heightSpec);
    	super.onMeasure(measure_spec[0], measure_spec[1]);
    }

	@Override
	public void setTransform(Matrix matrix) {
		if( MyDebug.LOG )
			Log.d(TAG, "setting transforms not supported for MySurfaceView");
		throw new RuntimeException();
	}

	@Override
	public void onPause() {
		if( MyDebug.LOG )
			Log.d(TAG, "onPause()");
		handler.removeCallbacks(tick);
	}

	@Override
	public void onResume() {
		if( MyDebug.LOG )
			Log.d(TAG, "onResume()");
		tick.run();
	}
}
