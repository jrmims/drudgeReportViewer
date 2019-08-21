package dev.mims.drudgereportviewer;

import android.graphics.Color;

public class Link extends DrudgeItem {
    private String title;
    private String url;
    private int colorInt;
    private int size;

    public Link(String title, String url) {
        this.title = title;
        this.url = url;
        size = R.dimen.text_size_medium;
        colorInt = Color.BLACK;
    }

    public String getTitle() {return title;}
    public String getUrl() {return url;}
    public int getColor() {return colorInt;}
    public int getSize() {return size;}

    @Override
    public void setSize(int size) {this.size = size;}

    public void setColor(String colorStr) {
        try {
            colorInt = Color.parseColor(colorStr);
        } catch (IllegalArgumentException e)
        {
            colorInt = Color.BLACK; // default
        }
    }
}
