package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private String currency;
    private EditText input;
    private TextView result_usd, result_euro, result_naira;
    private double inputValue;
    private int index;
    private String result[] = new String[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.currency_selector);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        input = findViewById(R.id.amount);

        findViewById(R.id.convert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                convert();
            }
        });
    }

    private void convert() {
        String inputAmt = input.getText().toString().trim();
        if (inputAmt.isEmpty()){
            input.setError("Enter an Amount");
            input.requestFocus();
            return;
        }
        inputValue = Double.parseDouble(inputAmt);
        new calculate().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        currency = adapterView.getItemAtPosition(i).toString();
        index = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class calculate extends AsyncTask<String, String, String[]> {

        public static final String ACCESS_KEY = "32f90c68addd49295a07b7e0ca0bce0a";
        public static final String BASE_URL = "http://apilayer.net/api/";
        public static final String ENDPOINT = "convert";

        @Override
        protected String[] doInBackground(String... strings) {

            if (index == 0){
                String uRl;
                try {
                    uRl = getJson(BASE_URL + ENDPOINT + "?from=USD&to=NGN&amount="+ inputValue +"?access_key=" + ACCESS_KEY);
                    JSONObject USDtoObject;
                    USDtoObject = new JSONObject(uRl);
                    JSONArray rateArray = USDtoObject.getJSONArray("quotes");
                    result[0] = rateArray.getJSONObject(0).getString("USDGBP");
                    result[1] = rateArray.getJSONObject(1).getString("USDNGN");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (index == 0){
                double usdtogpbval, usdtogpbinp, usdtongnval, usdtongninp, usdtousdinp;

                usdtousdinp = inputValue * 1;
                result_usd.setText(""+usdtousdinp);

                usdtogpbval = Double.parseDouble(result[0]);
                usdtogpbinp = inputValue * usdtogpbval;
                result_euro.setText(""+usdtogpbinp);

                usdtongnval = Double.parseDouble(result[1]);
                usdtongninp = inputValue * usdtongnval;
                result_naira.setText(""+usdtongninp);
            }
//            else if (index == 1){
//                double ngntogpbval, ngntogpbinp, ngntousdval, ngntongninp, ngntousdinp;
//
//                ngntongninp = inputValue * 1;
//                result_naira.setText(""+ngntongninp);
//            }
            super.onPostExecute(strings);
        }

        public String getJson (String url) throws ClientProtocolException, IOException{
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String con;
            while ((con = reader.readLine()) != null){
                builder.append(con);
            }
            return builder.toString();
        }
    }
}
