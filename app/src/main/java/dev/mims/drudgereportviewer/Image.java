package dev.mims.drudgereportviewer;

import android.graphics.Bitmap;

public class Image extends DrudgeItem {
    private Bitmap bitmap = null;
    private String uri = "";

    public Image(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Image(String uri) {this.uri = uri;}
    public Image() {}

    public Bitmap getImg() {return bitmap;}
    public String getUri() {return uri;}

    public void setImg(Bitmap bitmap) {this.bitmap = bitmap;}
    public void setUri(String uri) {this.uri = uri;}
}
