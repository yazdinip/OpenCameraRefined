package net.sourceforge.opencamera.Filter;

/**
 * Provides filter values for image filters
 * @author Dominik Buszowiecki
 */
class FilterConstants {

    //TODO init
    static final int[][] GRAYSCALE = new int[2][2];
    static final int[][] RED_FILTER = new int[2][2];
    static final int[][] BLUE_FILTER = new int[2][2];

    static int[][][] FILTERS = new int[][][]{GRAYSCALE, RED_FILTER, BLUE_FILTER};

}
