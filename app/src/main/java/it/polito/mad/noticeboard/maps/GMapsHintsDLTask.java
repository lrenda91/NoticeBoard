package it.polito.mad.noticeboard.maps;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.polito.mad.noticeboard.R;

/**
 * Created by luigi on 15/06/15.
 */

public class GMapsHintsDLTask extends AsyncTask<String, Void, ArrayList<String>> {

    private Context context;
    private HintsDownloadedCallback mCallback;

    public interface HintsDownloadedCallback {
        void onDownloadCompleted(List<String> result);
        void onDownloadError(Exception exception);
    }

    public GMapsHintsDLTask(Context ctx, HintsDownloadedCallback callback){
        context = ctx;
        mCallback = callback;
    }

    @Override
    protected ArrayList<String> doInBackground(String... args)
    {
        ArrayList<String> predictionsArr = new ArrayList<>();
        try
        {
            String outputType = "json";
            String param = URLEncoder.encode(args[0], "utf-8");
            String APIkey = context.getResources().getString(R.string.google_maps_key);
            String language = Locale.getDefault().getLanguage();
            String url = String.format("https://maps.googleapis.com/maps/api/place/autocomplete/%s?" +
                    "input=%s&types=geocode&language=%s&sensor=true&key=%s", outputType, param, language, APIkey);

            URL googlePlaces = new URL(url);
            URLConnection tc = googlePlaces.openConnection();
            tc.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    tc.getInputStream()));

            String line;
            StringBuffer sb = new StringBuffer();
            //take Google's legible JSON and turn it into one big string.
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            //turn that string into a JSON object
            JSONObject predictions = new JSONObject(sb.toString());
            //now get the JSON array that's inside that object
            JSONArray ja = new JSONArray(predictions.getString("predictions"));

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = (JSONObject) ja.get(i);
                //add each entry to our array
                predictionsArr.add(jo.getString("description"));
            }
        } catch (IOException e)
        {

            Log.e("YourApp", "GetPlaces : doInBackground", e);
            return null;

        } catch (JSONException e)
        {

            Log.e("YourApp", "GetPlaces : doInBackground", e);
            return null;

        }
        return predictionsArr;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        if (result == null){
            if (mCallback != null) mCallback.onDownloadError(new Exception());
        }
        else{
            if (mCallback != null) mCallback.onDownloadCompleted(result);
        }
    }

}