package dev.mims.drudgereportviewer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
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

public class GrabHTMLTask extends AsyncTask<URL, Integer, Map<String, List<Map<String,String>>> > {
    public GrabHTMLTask(Map<String, List<Map<String,String>>> resultMap, AsyncResponse asyncResponse)
    {
        this.resultMap = resultMap;
        this.asyncResponseInterface = asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(Map<String, List<Map<String,String>>> resultMap);
    }

    public AsyncResponse asyncResponseInterface = null;
    private Map<String, List<Map<String,String>>> resultMap;

    protected Map<String, List<Map<String,String>>> doInBackground(URL... urls) {
        BufferedReader reader;
        StringBuilder stringBuilder;

        List<Map<String,String>> topLinkList = new ArrayList<>();
        List<Map<String,String>> leftLinkList = new ArrayList<>();
        List<Map<String,String>> middleLinkList = new ArrayList<>();
        List<Map<String,String>> rightLinkList = new ArrayList<>();
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
            Elements tempLinks = tempDoc.select("a");
            topLinkList = extractInfoFromLinks(tempLinks);
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
        Map<String, List<Map<String,String>>> columnLinksMap = new HashMap<String,List<Map<String,String>>>();
        columnLinksMap.put("top", topLinkList);
        columnLinksMap.put("left", leftLinkList);
        columnLinksMap.put("middle", middleLinkList);
        columnLinksMap.put("right", rightLinkList);

        return columnLinksMap;
    }

    protected void onPostExecute(Map<String, List<Map<String,String>>> result) {
        resultMap = result;
        asyncResponseInterface.processFinish(resultMap);
    }

    private List<Map<String,String>> extractInfoFromLinks(Elements links)
    {
        List<Map<String,String>> tempLinkList = new ArrayList<Map<String,String>>();
        for (Element tempLink : links)
        {
            Map<String,String> tempLinkMap = new HashMap<String, String>();
            tempLinkMap.put("title",tempLink.text());
            tempLinkMap.put("url",tempLink.attr("href"));
            tempLinkMap.put("img",""); // TODO
            tempLinkList.add(tempLinkMap);
        }
        return tempLinkList;
    }

    private List<Map<String,String>> extractLinksFromTD(Element tableCell)
    {
        // in the first <td> section
        String tempStr = tableCell.toString();
        // filter out source links
        int stopPos = tempStr.indexOf("<!"); // source links start after this comment section
        tempStr = tempStr.substring(0,stopPos);
        // all links found in tempStr should now be real news links
        Document tempDoc = Jsoup.parseBodyFragment(tempStr);
        Elements columnLinks = tempDoc.select("a");
        List<Map<String,String>> linkList = extractInfoFromLinks(columnLinks);
        return linkList;
    }
}
