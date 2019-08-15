package dev.mims.drudgereportviewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GrabHTMLTask extends AsyncTask<URL, Integer, Map<String, List<DrudgeItem>> > {
    public GrabHTMLTask(Map<String, List<DrudgeItem>> resultMap, AsyncResponse asyncResponse)
    {
        this.resultMap = resultMap;
        this.asyncResponseInterface = asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(Map<String, List<DrudgeItem>> resultMap);
    }

    public AsyncResponse asyncResponseInterface = null;
    private Map<String, List<DrudgeItem>> resultMap;

    protected Map<String, List<DrudgeItem>> doInBackground(URL... urls) {
        BufferedReader reader;
        StringBuilder stringBuilder;

        List<DrudgeItem> topLinkList = new ArrayList<>();
        List<DrudgeItem> leftLinkList = new ArrayList<>();
        List<DrudgeItem> middleLinkList = new ArrayList<>();
        List<DrudgeItem> rightLinkList = new ArrayList<>();
        try {
            // Setup Connection
            URL url = urls[0];
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15*1000);
            connection.connect();
            // Extract HTML
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line + "\n");
            }
            String htmlStr = stringBuilder.toString();
            connection.disconnect();

            // parse links
            // find Top Links and Main Headline
            // Located before first <td>
            // find first <td>
            int stopPos = htmlStr.toLowerCase().indexOf("<td");
            String tempStr = htmlStr.substring(0,stopPos);
            Document tempDoc = Jsoup.parse(tempStr);
            Elements tempLinks = tempDoc.select("a, img");
            // TODO: probably could make this smarter
            // removes the Drudge Report Img from list, and the link for that image.
            tempLinks.remove(tempLinks.size()-1);
            tempLinks.remove(tempLinks.size()-1);
            topLinkList = extractInfoFromLinks(tempLinks);
            // Make Headline have larger text
            DrudgeItem lastDrudgeItem = topLinkList.get(topLinkList.size()-1);
            lastDrudgeItem.setSize(R.dimen.text_size_large);

            /*
            To grab left column links, we need to grab the first td table cell. Then we convert to
            string, and find the <! which currently marks the start of the source links which we
            don't want to display. We then convert it back to a Document and extract the links <a>
             */
            // in the first <td> section
            tempDoc = Jsoup.parse(htmlStr);
            Elements tableCells = tempDoc.select("td");
            Element leftCol = tableCells.get(0);
            leftLinkList = extractLinksFromTD(leftCol);
            // middle column
            Element middleCol = tableCells.get(2);
            middleLinkList = extractLinksFromTD(middleCol);
            // right column
            Element rightCol = tableCells.get(4);
            rightLinkList = extractLinksFromTD(rightCol);

        } catch (IOException e)
        {
            Log.e("GrabHTMLTask", e.toString());
        }
        Map<String, List<DrudgeItem>> columnLinksMap = new HashMap<String,List<DrudgeItem>>();
        columnLinksMap.put("top", topLinkList);
        columnLinksMap.put("left", leftLinkList);
        columnLinksMap.put("center", middleLinkList);
        columnLinksMap.put("right", rightLinkList);

        return columnLinksMap;
    }

    protected void onPostExecute(Map<String, List<DrudgeItem>> result) {
        resultMap = result;
        asyncResponseInterface.processFinish(resultMap);
    }

    private List<DrudgeItem> extractInfoFromLinks(Elements links)
    {
        List<DrudgeItem> tempLinkList = new ArrayList<DrudgeItem>();
        for (Element tempLink : links)
        {
            if( tempLink.tagName().toLowerCase() == "a" ) {
                // A Link
                Link linkObj = new Link(tempLink.text(), tempLink.attr("href"));
                String tempColor = extractFontColor(tempLink);
                if(tempColor == "")
                {
                    tempColor = "black"; // default
                }
                linkObj.setColor(tempColor);
                tempLinkList.add(linkObj);
            } else if( tempLink.tagName().toLowerCase() == "img" ) {
                // An Image
                String imgURL = tempLink.attr("src");
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(imgURL).getContent());
                    Image imgObj = new Image(bitmap);
                    tempLinkList.add(imgObj);
                } catch (IOException e)
                {

                }
            } else if( tempLink.tagName().toLowerCase() == "hr" ) {
                // a thematic break
                DrudgeItem lastListObj = tempLinkList.get(tempLinkList.size()-1);
                lastListObj.setHR();
            }
        }
        return tempLinkList;
    }

    String extractFontColor(Element link)
    {
        String color = "";
        Elements fontElList = link.getElementsByTag("font");
        if(fontElList.size() > 0)
        {
            // grab first one and use for color
            Element fontElement = fontElList.get(0);
            color = fontElement.attr("color");
        }
        return color;
    }

    private List<DrudgeItem> extractLinksFromTD(Element tableCell)
    {
        // in the first <td> section
        String tempStr = tableCell.toString();
        // filter out source links
        int stopPos = tempStr.indexOf("<!"); // source links start after this comment section
        tempStr = tempStr.substring(0,stopPos);
        // all links found in tempStr should now be real news links
        Document tempDoc = Jsoup.parseBodyFragment(tempStr);
        Elements columnLinks = tempDoc.select("a, img, hr");
        List<DrudgeItem> linkList = extractInfoFromLinks(columnLinks);
        return linkList;
    }
}
