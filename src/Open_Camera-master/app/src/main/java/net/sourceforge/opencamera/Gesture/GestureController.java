package net.sourceforge.opencamera.Gesture;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import net.sourceforge.opencamera.Preview.Preview;
import net.sourceforge.opencamera.tensorflow.Classifier;
import net.sourceforge.opencamera.tensorflow.ImageUtils;
import net.sourceforge.opencamera.tensorflow.TFLiteObjectDetectionAPIModel;
import net.sourceforge.opencamera.tensorflow.env.Logger;
import net.sourceforge.opencamera.ImgFilter.ImgFilterController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for enabling gesture features
 * @author Faisal Jaffer
 */
public class GestureController {
    private final boolean devMode = true; // make false to capture picture on smile
    private final Preview preview;
    private Classifier classifier;
    private static final Logger LOGGER = new Logger();
//    private Filter filter;
    private byte[] imageFrame;
    private List<Classifier.Recognition> recognitions;
    private float minConfidenceSmile = 0.99f; // min confidence
    private float minConfidenceThumb = 0.85f;
    private List<Classifier.Recognition> smiles = new ArrayList<Classifier.Recognition>();
    private List<Classifier.Recognition> thumbup = new ArrayList<Classifier.Recognition>();
    //////////////////////
    private Bitmap croppedBitmap = null;
    private Bitmap rgbFrameBitmap = null;
    private Matrix frameToCropTransform = null;
    private Matrix cropToFrameTransform;
    private int previewWidth;
    private int previewHeight;

    private long startTime;

    public ImgFilterController img_filter;
    //////////////////////

    public Canvas canvas;
    /**
     * Creates a GestureController
     * @param preview the android camera preview
     */
    public GestureController(Preview preview){
        //setup
        this.preview = preview;
        //initialize classifier
        img_filter = new ImgFilterController(this.preview);

        try {
            this.classifier = TFLiteObjectDetectionAPIModel.create(
                    preview.getResources().getAssets(),
                    ClassifierConstants.TF_OD_API_MODEL_FILE,
                    ClassifierConstants.TF_OD_API_LABELS_FILE,
                    ClassifierConstants.TF_OD_API_INPUT_SIZE,
                    ClassifierConstants.TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
        }

        //initialize filter
        startTime = System.nanoTime();

    }
    /**
     * Responsible for classifying objects in an image frame
     */
    public void processImage(){


        img_filter.setFrame(imageFrame);
        img_filter.processImage();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
//                Bitmap newmap = preview.cameraSurface.loadBitmapFromView(preview);
                Bitmap newmap = loadBitmapFromView(imageFrame);
                if (newmap != null){
                    recognitions = preview.classifier.recognizeImage(newmap);
                }
            }
        });
        processRecognitions();
    }

    /**
     * Responsible for filtering recognitions
     */
    public void processRecognitions(){
        if (recognitions != null) {
            for (int i = 0; i < recognitions.size(); i++) {
                if (recognitions.get(i).getConfidence() >= minConfidenceSmile && recognitions.get(i).getTitle().contains("smile")){
                    if (thumbup.size() == 0){
                        captureImage();
                    }
                    smiles.add(recognitions.get(i));
                } else if (recognitions.get(i).getConfidence() >= minConfidenceThumb && recognitions.get(i).getTitle().contains("thumbup")) {
                    if (thumbup.size() == 0 && System.nanoTime() - startTime > 2000000000) {
                        startTime = System.nanoTime();
                        img_filter.changeFilter();
                    }
                    thumbup.add(recognitions.get(i));
                }
            }
        }

    }

    /**
     * Removes detections once corresponding drawings have finished
     */
    public void doneDrawing(){
        smiles.clear();
        thumbup.clear();
    }

    /**
     * Converts float rectangle coordinates to int
     * @param rect rectangle coordinates in float
     * @return rectangle coordinates in int
     */
    public Rect convertRectF(RectF rect){
//        cropToFrameTransform.mapRect(rect);
        return new Rect((int)rect.left, (int)rect.top, (int)rect.right, (int)rect.bottom);
    }

    /**
     * Returns smiles detected
     * @return smiles detected
     */
    public List<Classifier.Recognition> getSmiles() {
        return smiles;
    }

    /**
     * Returns thumbs detected
     * @return thumbs detected
     */
    public List<Classifier.Recognition> getThumbup() {
        return thumbup;
    }

    /**
     * Sets the new frame received
     * @param frame current image frame
     */
    public void setFrame(byte[] frame){
        imageFrame = frame;
    }

    /**
     * Triggers capture of image
     */
    public void captureImage(){
        if (!preview.isOnTimer() && !devMode) {
            preview.takePicturePressed();
        }
    }

    public void showFilter(){

    }
    /**
     * Converts frame to bitmap
     * @param frame current frame
     * @return transformed bitmap
     */
    public Bitmap loadBitmapFromView(byte[] frame) {
        this.previewWidth = this.preview.getCurrentPreviewSize().width;
        this.previewHeight = this.preview.getCurrentPreviewSize().height;
//        Log.i("image: ", previewWidth + Integer.toString(previewHeight));

        if (this.previewWidth != 0 && this.previewHeight != 0){
            boolean MAINTAIN_ASPECT = false;

            int cropSize = 300;
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
                final Canvas canvas = new Canvas(croppedBitmap);
                canvas.scale(-1, 1, canvas.getWidth()/2, canvas.getHeight()/2);
                canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
            }
            cropToFrameTransform = new Matrix();
            frameToCropTransform.invert(cropToFrameTransform);

            return croppedBitmap;
        }
        return null;
    }
}
