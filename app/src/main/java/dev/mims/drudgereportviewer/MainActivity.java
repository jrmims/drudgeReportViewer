package dev.mims.drudgereportviewer;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.mims.drudgereportviewer.ui.main.PageViewModel;
import dev.mims.drudgereportviewer.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PageViewModel pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        // initialize resultLinkMap
        final Map<String, List<DrudgeItem>> resultLinkMap = new HashMap<>();

        try {
            // Set GrabHTMLTask to run
            URL drudgeURL = new URL("http://www.drudgereport.com");
            // TODO make WeakReference
            GrabHTMLTask linksGrabber = new GrabHTMLTask(resultLinkMap, new GrabHTMLTask.AsyncResponse() {
                @Override
                public void processFinish(Map<String, List<DrudgeItem>> resultMap) {
                    // Receives list/map of urls from GrabHTMLTask, pushes to PageViewModel for storage
                    pageViewModel.storeLinks(resultMap);
                }
            });
            linksGrabber.execute(drudgeURL);
        }
        catch(MalformedURLException e)
        {

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing", Snackbar.LENGTH_LONG).show();
                try {
                    URL drudgeURL = new URL("http://www.drudgereport.com");
                    // TODO make WeakReference
                    GrabHTMLTask linksGrabber = new GrabHTMLTask(resultLinkMap, new GrabHTMLTask.AsyncResponse() {
                        @Override
                        public void processFinish(Map<String, List<DrudgeItem>> resultMap) {
                            // Receives list/map of urls from GrabHTMLTask, pushes to PageViewModel for storage
                            pageViewModel.storeLinks(resultMap);
                        }
                    });
                    linksGrabber.execute(drudgeURL);
                }
                catch (MalformedURLException e)
                {

                }
            }
        });
    }
}