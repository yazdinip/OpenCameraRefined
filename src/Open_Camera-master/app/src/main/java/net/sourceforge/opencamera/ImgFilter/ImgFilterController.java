package net.sourceforge.opencamera.ImgFilter;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.tensorflow.ImageUtils;

/**
 * Information for the current filter selected
 * @author Dominik Buszowiecki
 */
public class ImgFilterController {


    private static int filterIndex = 0; //0 = OFF, 1 = 1st filter, etc
    private static byte[] imageFrame;
    private static Bitmap filtered;

    private final Preview preview;
    ////
    private Bitmap croppedBitmap = null;
    private Bitmap rgbFrameBitmap = null;
    private Matrix frameToCropTransform = null;
    private Matrix cropToFrameTransform;
    private int previewWidth;
    private int previewHeight;

    /**
     * Creates a ImgFilter Controller
     * @param preview - the android camera preview
     */
    public ImgFilterController(Preview preview){
        this.preview = preview;
        //initializes constants
    }

    /**
     * Changes the current filter that will be applied to the preview
     */
    public void changeFilter(){
        ColorMatrixColorFilter[] FILTERS = FilterConstants.FILTERS;
        filterIndex = (filterIndex + 1)%(FILTERS.length);
    }

    /**
     * Returns which filter the application is set to display
     * @return - the filterIndex
     */
    public int getFilter(){
        return filterIndex;
    }

    /**
     * Sets the image frame from the camera preview to be processed
     * @param frame - frame from a camera controller
     */
    public void setFrame(byte[] frame){
        imageFrame = frame;
        Log.d("IMG", "setFrame: frame is "+  frame.toString());
    }

    /**
     * Processes the current imageFrame and applies the selected filter
     */
    public void processImage(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (filterIndex == 0){
                    destroyFiltered();
                    return;
                }
                loadBitmapFromView(imageFrame);
                filtered = setFiltered(rgbFrameBitmap);
                if (filtered == null) Log.d("IMG", "Process Image: Filter is null");
            }
        });
    }

    synchronized public void destroyFiltered(){
        if (filtered != null) filtered.recycle();
        filtered = null;
    }

    /**
     * Returns a Rect object
     * @return - A Rect object that is the size of the entire preview screen
     */
    public Rect getRect(){
        return new Rect(0, 0, filtered.getWidth()+160, filtered.getHeight()+150);
    }

    /**
     * Returns the last filtered image
     * @return - a Bitmap of the last frame that was filtered
     */
    public Bitmap getFiltered(){
        return filtered;
    }


    //***** Helper Method ******

    /**
     * Converts a frame to a Bitmap and loads it into rgbFrameBitmap
     * @param frame - a frame from the Camera Controller
     */
    private void loadBitmapFromView(byte[] frame) {
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
            int[] rgbBytes = null;
            rgbBytes = new int[previewWidth * previewHeight];
//            Log.i("q: ", Integer.toString(preview.CameraFrame.length));
            if (frame != null){
                ImageUtils.convertYUV420SPToARGB8888(frame, previewWidth, previewHeight, rgbBytes);
                rgbFrameBitmap.setPixels(rgbBytes, 0, previewWidth, 0, 0, previewWidth, previewHeight);
                final Canvas canvas = new Canvas(rgbFrameBitmap);
                canvas.scale(0, 0, previewWidth, previewHeight);
                canvas.drawBitmap(rgbFrameBitmap, 0, 0, null);
            }
        }
    }

    /**
     * Applies a filter to a image
     * @param bmpOriginal - the orginal bitMap image
     * @return - the original bitMap image with the corresponding filter applied.
     */
    private Bitmap setFiltered(Bitmap bmpOriginal) {
        ColorMatrixColorFilter[] FILTERS = FilterConstants.FILTERS;
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpFiltered = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpFiltered);
        Paint paint = new Paint();
        //The filter used depends on the value of filterIndex
        paint.setColorFilter(FILTERS[filterIndex]);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpFiltered;
    }
}
