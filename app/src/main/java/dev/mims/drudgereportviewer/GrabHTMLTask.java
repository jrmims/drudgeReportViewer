package dev.mims.drudgereportviewer;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;

public class GrabHTMLTask extends AsyncTask<URL, Integer, String> {
    private String htmlStr;
    public GrabHTMLTask(String htmlStr, AsyncResponse asyncResponse)
    {
        this.htmlStr = htmlStr;
        this.asyncResponseInterface = asyncResponse;
    }

    public interface AsyncResponse {
        void processFinish(String htmlString);
    }

    public AsyncResponse asyncResponseInterface = null;

    protected String doInBackground(URL... urls) {
        int count = urls.length;
        BufferedReader reader;
        StringBuilder stringBuilder;
        String htmlStr = "";
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
            htmlStr = stringBuilder.toString();
            connection.disconnect();

        } catch (IOException e)
        {
            htmlStr = e.toString();
        }

        return htmlStr;
    }

    protected void onPostExecute(String result) {
        htmlStr = result;
        asyncResponseInterface.processFinish(htmlStr);
    }
}
