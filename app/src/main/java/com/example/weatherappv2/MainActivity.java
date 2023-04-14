package com.example.weatherappv2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText editCity;
    private ImageFilterView search;
    private ImageView weatherIcon;
    private TextView temperature,cityName, condition,feelsLike,humidity,wind,pressure,max_temp,temp2day,temp3day,temp4day,temp5day,day3date,day4date,day5date,avgCityName;
    private Button next4day;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editCity = findViewById(R.id.idEditCity);
        search = findViewById(R.id.idIVSearch);
        temperature = findViewById(R.id.idTVTemperature);
        cityName = findViewById(R.id.idTVCityName);
        weatherIcon = findViewById(R.id.idIVIcon);
        condition = findViewById(R.id.idTVCondition);
        feelsLike = findViewById(R.id.idTVFeels);
        humidity = findViewById(R.id.humidity_text);
        wind = findViewById(R.id.wind_text);
        pressure = findViewById(R.id.pressure_text);
        max_temp = findViewById(R.id.temp_max_text);

        next4day = findViewById(R.id.button_week);
        temp2day = findViewById(R.id.day2_temp);
        temp3day = findViewById(R.id.day3_temp);
        temp4day = findViewById(R.id.day4_temp);
        temp5day = findViewById(R.id.day5_temp);
        day3date = findViewById(R.id.day3);
        day4date = findViewById(R.id.day4);
        day5date = findViewById(R.id.day5);
        avgCityName = findViewById(R.id.avg);



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCity.getText().toString().trim().equals("")){
                    Toast.makeText(MainActivity.this, "Enter the City", Toast.LENGTH_SHORT).show();
                }else {
                    String city = editCity.getText().toString().trim();
                    String key = "8520eae302d1fd79163dac50bbcd4ff1";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+key+"&units=metric";

                    new GetURLData().execute(url);
                }

            }
        });


        next4day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = editCity.getText().toString().trim();
                avgCityName.setText("Average temp:"+city);
                String key = "8520eae302d1fd79163dac50bbcd4ff1";
                String url = "https://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid="+key+"&units=metric";
                new GetURLData5days().execute(url);
            }

        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetURLData extends AsyncTask<String,String,String>{

        protected void onPreExecute(){
            super.onPreExecute();
            temperature.setText("Hold on");
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                if (connection!=null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject object = new JSONObject(result);
                temperature.setText( object.getJSONObject("main").getString("temp")+"°C");
                feelsLike.setText("Feels like:"+object.getJSONObject("main").getString("feels_like")+"°C");
                cityName.setText(editCity.getText().toString());

                String cur_condition = object.getJSONArray("weather").getJSONObject(0).getString("main").toLowerCase();
                condition.setText(cur_condition.toString());
                Picasso.get().load(resolveImage(cur_condition)).into(weatherIcon);

                humidity.setText(object.getJSONObject("main").getString("humidity")+"%");
                pressure.setText(object.getJSONObject("main").getString("pressure")+"mmhg");
                max_temp.setText(object.getJSONObject("main").getString("temp_max")+"°c");
                wind.setText(object.getJSONObject("wind").getString("speed")+"km/h");


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
        private Integer resolveImage(String condition) {
            int result;
            switch (condition) {
                case "clouds":
                    result = R.drawable.clouds;
                    break;
                case "rain":
                    result = R.drawable.art_rain;
                    break;
                case "snow":
                    result = R.drawable.art_snow;
                    break;
                case "haze":
                    result = R.drawable.art_fog;
                    break;
                case "clear":
                    result = R.drawable.art_clear;
                    break;
                default:
                    result = R.drawable.sun;
                    break;
            }
            return result;
        }

    }

    private class GetURLData5days extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                if (connection!=null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                double day2=0,day3 = 0,day4 = 0,day5 = 0;
                int arr = jsonObject.getJSONArray("list").length();
                for (int i = 8; i < arr; i++){
                    if (i>=8 && i<=16)
                        day2 += jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp");
                    else if (i>16 && i<=24) {
                        day3 += jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp");
                    }
                    else if (i>24 && i<=32) {
                        day4 += jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp");
                    }
                    else if (i>32 && i<=40) {
                        day5 += jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp");
                    }
                }

                    String date1 = jsonObject.getJSONArray("list").getJSONObject(16).getString("dt_txt");
                    String date2 = jsonObject.getJSONArray("list").getJSONObject(24).getString("dt_txt");
                    String date3 = jsonObject.getJSONArray("list").getJSONObject(32).getString("dt_txt");

                    day3date.setText(date1.substring(date1.indexOf("-")+1,date1.indexOf(" ")).substring(3) + "." + date1.substring(date1.indexOf("-")+1,date1.indexOf(" ")).substring(0, 2));
                    day4date.setText(date2.substring(date2.indexOf("-")+1,date2.indexOf(" ")).substring(3) + "." + date2.substring(date2.indexOf("-")+1,date1.indexOf(" ")).substring(0, 2));
                    day5date.setText(date3.substring(date3.indexOf("-")+1,date3.indexOf(" ")).substring(3) + "." + date3.substring(date1.indexOf("-")+1,date1.indexOf(" ")).substring(0, 2));









                temp2day.setText(String.format("%.2f", day2 / 8));
                temp3day.setText(String.format("%.2f", day3 / 8));
                temp4day.setText(String.format("%.2f", day4 / 8));
                temp5day.setText(String.format("%.2f", day5 / 8));



            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }


}