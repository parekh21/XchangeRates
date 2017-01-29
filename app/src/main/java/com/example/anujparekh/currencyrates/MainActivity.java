package com.example.anujparekh.currencyrates;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    JSONObject jsonObject = null;
    boolean loadSpinner;
    String to;
    HashMap<String, String> hm = new HashMap<>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSpinner = true;
        new JSONTask().execute("http://api.fixer.io/latest?base=USD");


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    public void onClickButton(View v) throws JSONException {
        String url;
        String from;
        String getFromVal;
        String toSelection;

        loadSpinner = false;

        Spinner lSpinner = (Spinner) findViewById(R.id.from_spinner);
        Spinner lSpinner2 = (Spinner) findViewById(R.id.to_spinner);
        from = lSpinner.getSelectedItem().toString();
        toSelection = lSpinner2.getSelectedItem().toString();
        getFromVal = hm.get(from);
        to = hm.get(toSelection);
        url = "http://api.fixer.io/latest?base=" + getFromVal;
        new JSONTask().execute(url);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private class JSONTask extends AsyncTask<String, String, String> {

        ArrayList<String> al;
        ArrayList<String> al2;
        ArrayAdapter<String> adapter;
        ArrayAdapter<String> adapter2;
        Spinner spinner = (Spinner) findViewById(R.id.from_spinner);
        Spinner spinner2 = (Spinner) findViewById(R.id.to_spinner);

        @Override
        protected String doInBackground(String... params) {

            String JSONString="";
            String countries = "";
            URL countriesURL = null;
            JSONObject jsonObject2 = null;
            try {
                URL url = new URL(params[0]);
                JSONString = getJSON(url);

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            try {
                jsonObject = new JSONObject(JSONString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (loadSpinner) {
                al = new ArrayList<String>();
                al.add("USD");
                int obj = 0;
                try {
                    obj = jsonObject.getJSONObject("rates").names().length();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                for (int i = 0; i < obj; i++) {
                    try {
                        al.add(jsonObject.getJSONObject("rates").names().getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(al);
                try {
                    countriesURL = new URL("https://openexchangerates.org/api/currencies.json");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                countries = getJSON(countriesURL);
                try {
                    jsonObject2 = new JSONObject(countries);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                al2 = new ArrayList<String>();
                for(int i = 0; i<al.size(); i++){
                    try {
                        hm.put((String) jsonObject2.get(al.get(i)), al.get(i));
                        al2.add((String) jsonObject2.get(al.get(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(al2);
                adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, al2);
                adapter.setDropDownViewResource(R.layout.spinner_item);
            }

            return JSONString;


        }

        @Override
        protected void onPostExecute(String s) {
            double test = 0;

            super.onPostExecute(s);

            if (loadSpinner) {
                spinner.setAdapter(adapter);
                spinner2.setAdapter(adapter);
            }

            if (!loadSpinner && !spinner.getSelectedItem().toString().equals(spinner2.getSelectedItem().toString())) {
                try {
                    test = jsonObject.getJSONObject("rates").getDouble(to);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            TextView textView = (TextView) findViewById(R.id.textView);
            if (spinner.getSelectedItem().toString().equals(spinner2.getSelectedItem().toString())) {
                textView.setText("1");
            } else {
                textView.setText(Double.toString(test));
            }
        }

        protected String getJSON(URL url) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();

            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return buffer.toString()
                    ;
        }
    }
}

