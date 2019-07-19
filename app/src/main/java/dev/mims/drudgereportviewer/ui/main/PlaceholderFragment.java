package dev.mims.drudgereportviewer.ui.main;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;
import java.util.Map;

import dev.mims.drudgereportviewer.CustomOnClickListener;
import dev.mims.drudgereportviewer.R;
import dev.mims.drudgereportviewer.CustomTabsURLSpan;

/**
 * Creates the same base fragment for each tab. Populates UI based on tab index; "right" tab grabs
 * data for the correct tab.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private CustomTabsIntent.Builder builder;
    private CustomTabsIntent customTabsIntent;
    private CustomOnClickListener onClickListener;

    public PlaceholderFragment() {
        builder = new CustomTabsIntent.Builder();
        customTabsIntent = builder.build();
        onClickListener = new CustomOnClickListener();
    }

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        // stores index with this instance of PlaceholderFragment
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // store PageViewModel
        pageViewModel = ViewModelProviders.of(getActivity()).get(PageViewModel.class);
    }

    private Spannable createURLSpan(String text, String url) {
        // TODO Check Spannable for changing link colors after click?
        // TODO Fix blank space issue: https://stackoverflow.com/questions/9274331/clickablespan-strange-behavioronclick-called-when-clicking-empty-space
        Spannable span = new SpannableString(text);
        CustomTabsURLSpan customURLSpan = new CustomTabsURLSpan(url);
        customURLSpan.setOnClickListener(onClickListener);
        span.setSpan(customURLSpan,0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final LinearLayout linearLayout = root.findViewById(R.id.linearLayout);

        pageViewModel.getLinks().observe(this,new Observer<Map<String, List<Map<String, Object>>>>() {
            @Override
            public void onChanged(Map<String,List<Map<String,Object>>> linkMap)
            {
                // If mlinkMap from pageViewModel changes, update the UI
                Context context = getContext();
                linearLayout.removeAllViews(); // clear out linearLayout

                int currIndex = 0;
                // grab tab index from Bundle
                if (getArguments() != null) {
                    currIndex = getArguments().getInt(ARG_SECTION_NUMBER);
                }
                String tab = null;
                switch (currIndex) {
                    case 0:
                        tab = "top";
                        break;
                    case 1:
                        tab = "left";
                        break;
                    case 2:
                        tab = "center";
                        break;
                    case 3:
                        tab = "right";
                        break;
                }
                List<Map<String,Object>> tempList = linkMap.get(tab);
                for( Map<String,Object> tempMap : tempList )
                {
                    String tempTitle = (String)tempMap.get("title");
                    String tempURL = (String)tempMap.get("url");
                    if ( tempTitle.length() == 0 || tempURL.length() == 0 )
                    {
                        // We have an image
                        Object tempBitmap = tempMap.get("img");
                        if(tempBitmap != null)
                        {
                            ImageView tempIV = new ImageView(context);
                            tempIV.setImageBitmap( (Bitmap)tempBitmap );
                            linearLayout.addView(tempIV);
                        }

                    } else
                    {
                        // We have a Link
                        TextView tempTV = new TextView(context);
                        Spannable span = createURLSpan(tempTitle, tempURL);
                        tempTV.append(span);
                        tempTV.setMovementMethod(LinkMovementMethod.getInstance());
                        linearLayout.addView(tempTV);
                    }

                }
            }
        });

        return root;
    }

}