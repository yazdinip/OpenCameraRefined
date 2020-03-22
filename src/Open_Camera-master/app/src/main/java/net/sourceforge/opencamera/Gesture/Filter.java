package net.sourceforge.opencamera.Gesture;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.sourceforge.opencamera.R;

import java.util.Arrays;
import java.util.List;

public class Filter {

    private Bitmap c;
    private Bitmap d;
    private List<Bitmap> filters;
    private int filterIndex;

    public Filter(Resources resources){
        c = BitmapFactory.decodeResource(resources, R.drawable.cat);
        d=BitmapFactory.decodeResource(resources, R.drawable.dog);
        filters = Arrays.asList(c,d,d);
        filterIndex = 0;
    }

    public void changeFilter(){
        if (filterIndex==filters.size()-1) {
            filterIndex = 0;
        }else{
            filterIndex +=1;
        }
    }

    public Bitmap getFilter(){
        return filters.get(filterIndex);
    }
}
