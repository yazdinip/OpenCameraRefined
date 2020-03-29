package net.sourceforge.opencamera.ImgFilter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import net.sourceforge.opencamera.Gesture.ClassifierConstants;
import net.sourceforge.opencamera.Gesture.Filter;
import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.tensorflow.ImageUtils;
import net.sourceforge.opencamera.tensorflow.TFLiteObjectDetectionAPIModel;

import java.io.IOException;


/**
 * Information for the current filter selected
 * @author Dominik Buszowiecki
 */
public class ImgFilter {

    private static int filterIndex = 0; //0 = OFF, 1 = 1st filter, etc
    private final Preview preview;
    private byte[] imageFrame;
    private Bitmap filtered;
    ////
    private Bitmap croppedBitmap = null;
    private Bitmap rgbFrameBitmap = null;
    private Matrix frameToCropTransform = null;
    private Matrix cropToFrameTransform;
    private int previewWidth;
    private int previewHeight;

    public ImgFilter(Preview preview){
        //setup
        this.preview = preview;
        //initialize classifier

        //initialize filter

    }

    public static void changeFilter(){
        int[][][] FILTERS = FilterConstants.FILTERS;
        filterIndex = (filterIndex + 1)%(FILTERS.length + 1);
    }
    public static int getFilter(){
        return filterIndex;
    }

    public void processImage(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (filterIndex == 0) {
                    filtered = null;
                    return;
                };
//                Bitmap newmap = preview.cameraSurface.loadBitmapFromView(preview);
                loadBitmapFromView(imageFrame);
                filtered = toGrayscale(rgbFrameBitmap);
            }
        });
    }

    public Rect getRect(){
        return new Rect(0, 0, previewWidth + 200, previewHeight + 300);
    }

    public Bitmap getFiltered(){
        return filtered;
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public void setFrame(byte[] frame){
        imageFrame = frame;
    }

    public Bitmap loadBitmapFromView(byte[] frame) {
        this.previewWidth = this.preview.getCurrentPreviewSize().width;
        this.previewHeight = this.preview.getCurrentPreviewSize().height;
//        Log.i("image: ", previewWidth + Integer.toString(previewHeight));

        if (this.previewWidth != 0 && this.previewHeight != 0){
            boolean MAINTAIN_ASPECT = true;

            int cropSize = 100;
            //        Log.i("image: ", previewWidth + Integer.toString(previewHeight));
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
//            Log.i("q: ", Integer.toString(preview.CameraFrame.length));
            if (frame != null){
                ImageUtils.convertYUV420SPToARGB8888(frame, previewWidth, previewHeight, rgbBytes);
                rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);
                final Canvas canvas = new Canvas(rgbFrameBitmap);
                canvas.scale(0, 0, canvas.getWidth(), canvas.getHeight());
                canvas.drawBitmap(rgbFrameBitmap, 0, 0, null);
            }
            cropToFrameTransform = new Matrix();
            frameToCropTransform.invert(cropToFrameTransform);

            return croppedBitmap;
        }
        return null;
    }
}
