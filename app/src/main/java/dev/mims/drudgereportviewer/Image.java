package dev.mims.drudgereportviewer;

import android.graphics.Bitmap;

public class Image extends DrudgeItem {
    private Bitmap bitmap;

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getImg() {return bitmap;}
}
