package net.sourceforge.opencamera.ImgFilter;

/**
 * Information for the current filter selected
 * @author Dominik Buszowiecki
 */
public class ImgFilter {

    private static int filterIndex = 0; //0 = OFF, 1 = 1st filter, etc

    public static void changeFilter(){
        int[][][] FILTERS = FilterConstants.FILTERS;
        filterIndex = (filterIndex + 1)%(FILTERS.length + 1);
    }
    public static int getFilter(){
        return filterIndex;
    }
}
