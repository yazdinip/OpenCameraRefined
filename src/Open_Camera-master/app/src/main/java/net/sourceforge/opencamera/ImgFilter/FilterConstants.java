package net.sourceforge.opencamera.ImgFilter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

/**
 * Provides filter values for image filters
 * @author Dominik Buszowiecki
 */
class FilterConstants {

    static ColorMatrixColorFilter GRAYSCALE;
    static ColorMatrixColorFilter RED_FILTER;
    static ColorMatrixColorFilter BLUE_FILTER;

    static ColorMatrixColorFilter[] FILTERS;

    public static void initFilters(){
        GRAYSCALE = getGrayscale();
        //TODO
        RED_FILTER = getGrayscale();
        BLUE_FILTER = getGrayscale();

        FILTERS = new ColorMatrixColorFilter[]{null, GRAYSCALE, RED_FILTER, BLUE_FILTER};

    }

    private static ColorMatrixColorFilter getGrayscale(){
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        return new ColorMatrixColorFilter(cm);
    }
    private static ColorMatrixColorFilter getBlueFilter(){
        //TODO
        return null;
    }
    private static ColorMatrixColorFilter getRedFilter(){
        //TODO
        return null;
    }


}
