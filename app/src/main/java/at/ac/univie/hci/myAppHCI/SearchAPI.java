package at.ac.univie.hci.myAppHCI;

import android.media.Image;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchAPI
{


    //JSON Url link  Builder....
    public static String createSearchQuery(String queryTerm, String origin, String dateStart, String dateEnd, String artist) throws JSONException, UnsupportedEncodingException {
        JSONObject root = new JSONObject();

        root.put("q", queryTerm);

        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONArray must = new JSONArray();

        if (!queryTerm.isEmpty()) {
            JSONObject matchTitle = new JSONObject();
            matchTitle.put("title", queryTerm);
            JSONObject placeOfTitleMatch = new JSONObject();
            placeOfTitleMatch.put("match", matchTitle);
            must.put(placeOfTitleMatch);
        }

        if (!origin.isEmpty()) {
            JSONObject matchPlaceOfOrigin = new JSONObject();
            matchPlaceOfOrigin.put("place_of_origin", origin);
            JSONObject placeOfOriginMatch = new JSONObject();
            placeOfOriginMatch.put("match", matchPlaceOfOrigin);
            must.put(placeOfOriginMatch);
        }

        if (!artist.isEmpty()) {
            JSONObject matchArtistDisplay = new JSONObject();
            matchArtistDisplay.put("artist_display", artist);
            JSONObject artistDisplayMatch = new JSONObject();
            artistDisplayMatch.put("match", matchArtistDisplay);
            must.put(artistDisplayMatch);
        }

        if (!dateStart.isEmpty() && !dateEnd.isEmpty()) {
            JSONObject range = new JSONObject();
            JSONObject dateDisplay = new JSONObject();
            dateDisplay.put("gte", Integer.parseInt(dateStart));
            dateDisplay.put("lte", Integer.parseInt(dateEnd));
            range.put("date_display", dateDisplay);
            JSONObject dateRangeMatch = new JSONObject();
            dateRangeMatch.put("range", range);
            must.put(dateRangeMatch);
        }

        bool.put("must", must);
        query.put("bool", bool);
        root.put("query", query);
        Log.d("json", String.valueOf(root));
        String test = root.toString();
        return URLEncoder.encode(test, StandardCharsets.UTF_8.toString());
    }
    public static List<Artwork> getArtworksFromURL(String urlString)
    {
        Log.d("link",urlString);
        String jsonData = getJson(urlString);
        return ExtractArtwork(jsonData);
    }


    //JSON Extracter
    private static String getJson(String urlString)
    {
        HttpURLConnection urlConnection = null;
        String IDjson = "";

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream inputStream = urlConnection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
            {
                IDjson += line;
            }
            reader.close(); // Close reader here within try
            return IDjson;
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        finally
        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }


    //Extract Artworks from JSON
    private static List<Artwork> ExtractArtwork(String json) {
        List<Artwork> res = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObj = dataArray.getJSONObject(i);
                String ApiLink = dataObj.optString("api_link", "No link available");

                //Get Title
                String Title = dataObj.optString("title", "No Title Available");

                //Get Techinque
                String Technique = dataObj.optString("medium_display", "No Technique Available");

                //Access imageID
                String imageID = dataObj.optString("image_id", "no Image Id Available");
                String Imageurl = "https://www.artic.edu/iiif/2/";
                Imageurl += imageID;
                Imageurl += "/full/843,/0/default.jpg";

                //Access Date
                String Date = dataObj.optString("date_display", "no date Available");

                //Access Origin
                String Origin = dataObj.optString("place_of_origin", "no origin Available");

                //Access Author
                String Author = dataObj.optString("artist_display", "no author Available");

                String Medium = dataObj.optString("medium_display", "no medium Available");

                String Dimensions = dataObj.optString("dimensions","no medium Available");

                String Description = dataObj.optString("description","no description Available");


                //Construct Artwork Object and add to list
                res.add(new Artwork(Imageurl, Title, Date, Author, Origin, ApiLink, Medium, Dimensions, Description));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}

