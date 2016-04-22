package com.razorreborn.robocar;

/**
 * Created by Kiran Anto aka RazorSharp on 21/4/2016.
 * For more Info Contact
 * Kirananto@gmail.com
 * 9495333724
 * All Copyrights Reserved 2016
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

class HttpConnection {
    public String readUrl(String mapsApiDirectionsUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(mapsApiDirectionsUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception while readi", e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            assert urlConnection != null;
            urlConnection.disconnect();
        }
        return data;
    }

}