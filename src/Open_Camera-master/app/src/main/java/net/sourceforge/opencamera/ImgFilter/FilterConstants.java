package net.sourceforge.opencamera.ImgFilter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

/**
 * Provides filter values for image filters
 * @author Dominik Buszowiecki
 */
class FilterConstants {

    static ColorMatrixColorFilter GRAYSCALE = getGrayscale();
    static ColorMatrixColorFilter RED_FILTER = getRedFilter();
    static ColorMatrixColorFilter YELLOW_FILTER = getYellowFilter();

    static ColorMatrixColorFilter[] FILTERS =
            new ColorMatrixColorFilter[]{null, GRAYSCALE, RED_FILTER, YELLOW_FILTER};

    private static ColorMatrixColorFilter getGrayscale(){
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        return new ColorMatrixColorFilter(cm);
    }
    private static ColorMatrixColorFilter getRedFilter(){
        ColorMatrix cm = new ColorMatrix();
        cm.setRGB2YUV();
        return new ColorMatrixColorFilter(cm);
    }
    private static ColorMatrixColorFilter getYellowFilter(){
        ColorMatrix cm = new ColorMatrix();
        cm.setRotate(0,30f);
        return new ColorMatrixColorFilter(cm);
    }


}
