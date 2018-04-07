package com.example.maphw;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import android.os.StrictMode;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng pos;
    private String country = "Failed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivityReal.EXTRA_MESSAGE);
        // pos = geocode(address); // new LatLng(-34,151);// geocode(address);
        new GetDataTask().execute(address);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public LatLng geocode(String address) {
        URL url;
        HttpURLConnection urlConnection = null;

        String response = "";

        try {
            url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + replaceSpaces(address));

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            InputStreamReader isw = new InputStreamReader(in);

            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                response += current;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // return new LatLng(0,0);
            return new LatLng(-34,151);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        JSONObject json = null;

        try {
            json = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new LatLng(0,0);
        }

        try {
            double lat = (double)((JSONArray) json.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat");
            double lng = (double)((JSONArray) json.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng");

            try {
                JSONArray address_components = ((JSONArray) json.get("results")).getJSONObject(0).getJSONArray("address_components");
                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject add_comp = address_components.getJSONObject(i);

                    // "types" : [ "country", "political" ]

                    JSONArray types = add_comp.getJSONArray("types");
                    for (int j = 0; j < types.length(); j++) {
                        String type = types.getString(j);
                        if (type.equals("country")) {
                            country = add_comp.getString("long_name");
                        }
                    }
                }
            } catch (JSONException e) {
                country = "Country unknown";
            }

            return new LatLng(lat, lng);
        } catch (JSONException e) {
            return new LatLng(0,0);
        }
    }

    class GetDataTask extends AsyncTask<String, Void, LatLng> {
        @Override
        protected LatLng doInBackground(String... params) {
            return geocode(params[0]);
        }

        @Override
        protected void onPostExecute(LatLng result) {
            pos = result;

            mMap.addMarker(new MarkerOptions().position(pos).title("Country: " + country));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // LatLng pos = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(pos).title("Country: " + country));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
    }

    public String replaceSpaces(String s) {
        String[] words = s.split(" ");
        StringBuilder url = new StringBuilder(words[0]);

        for (int i = 1; i < words.length; i++) {
            url.append("%20");
            url.append(words[i]);
        }

        return url.toString();
    }
}
