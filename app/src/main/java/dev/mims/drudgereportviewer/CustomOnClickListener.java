package dev.mims.drudgereportviewer;

import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.browser.customtabs.CustomTabsIntent;

public class CustomOnClickListener implements OnClickListener {
    private CustomTabsIntent.Builder builder;
    private CustomTabsIntent customTabsIntent;
    private String urlStr;

    public CustomOnClickListener()
    {
        builder = new CustomTabsIntent.Builder();
        customTabsIntent = builder.build();
    }

    public void setURL(String urlStr) {this.urlStr = urlStr;}

    @Override
    public void onClick(View view) {
        customTabsIntent.launchUrl(view.getContext(), Uri.parse(urlStr));
    }
}
