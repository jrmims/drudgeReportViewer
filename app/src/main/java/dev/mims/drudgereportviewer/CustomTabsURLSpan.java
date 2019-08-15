package dev.mims.drudgereportviewer;

import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

public class CustomTabsURLSpan extends URLSpan {
    private CustomOnClickListener onClickListener;
    public CustomTabsURLSpan(String url)
    {
        super(url);
    }

    public void setOnClickListener(CustomOnClickListener onClickListener)
    {
        this.onClickListener = onClickListener;
    }

    public void updateDrawState(TextPaint drawState) {
        super.updateDrawState(drawState);
        drawState.setUnderlineText(false);
    }

    @Override
    public void onClick(View view) {
        /*
        TODO
        try to open with CustomTabs. If not available, use default WebView
        also check if a web browser is installed
        more suggestions: https://medium.com/google-developers/best-practices-for-custom-tabs-5700e55143ee
        */
        try {
            onClickListener.setURL(getURL());
            onClickListener.onClick(view);
        } catch (Exception e) {
            super.onClick(view);
        }
    }

}
