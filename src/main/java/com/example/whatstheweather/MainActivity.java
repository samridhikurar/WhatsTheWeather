package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView weatherContent;
    EditText cityName;


    public void getWeather(View view){

        try {
            DownloadTask task = new DownloadTask();
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=eb64ce2cbca5d509d753069ea3167df9");

            //to hide keyboard as soon as we click the button, so that the text view is not hindered
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
        }
    }

    public void onListen(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public class DownloadTask extends AsyncTask<String, Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection=null;
            String result="";

            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while (data!=-1){
                    char current= (char) data;
                    result+=current;
                    data=reader.read();
                }

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject=new JSONObject(s);

                String weatherInfo=jsonObject.getString("weather");

                Log.i("Weather Content",weatherInfo);

                JSONArray arr= new JSONArray(weatherInfo);

                String message="";

                for(int i=0;i<arr.length();i++){
                    JSONObject jsonPart=  arr.getJSONObject(i);

                    String main=jsonPart.getString("main");
                    String description=jsonPart.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message+=main+": "+description+"\r\n";
                    }
                }

                if(!message.equals("")){
                    weatherContent.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName= findViewById(R.id.cityName);
        weatherContent=findViewById(R.id.resultTextView);

        cityName.setText("");

    }
}