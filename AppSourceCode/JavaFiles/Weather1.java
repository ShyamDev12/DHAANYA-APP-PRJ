package com.example.grower1;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.Picasso; // Add Picasso for image loading

public class Weather1 extends AppCompatActivity {

    private static final String TAG = "WeatherAPI";
    private RequestQueue requestQueue;
    private static final String API_KEY = "33b8e53197894604823203233243007";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/current.json";

    private ImageView weatherIcon;
    private TextView temperature;
    private TextView weatherDescription;
    private TextView humidity;
    private TextView windSpeed;
    private TextView location; // New TextView for location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather1);

        // Initialize UI components
        weatherIcon = findViewById(R.id.weather_icon);
        temperature = findViewById(R.id.temperature);
        weatherDescription = findViewById(R.id.weather_description);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.wind_speed);
        location = findViewById(R.id.location); // Initialize the location TextView

        requestQueue = Volley.newRequestQueue(this);
        fetchWeatherData("India");
    }

    private void fetchWeatherData(String locationName) {
        String url = BASE_URL + "?key=" + API_KEY + "&q=" + locationName;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract data from the response
                            JSONObject current = response.getJSONObject("current");
                            double temperatureValue = current.getDouble("temp_c");
                            String conditionText = current.getJSONObject("condition").getString("text");
                            String iconUrl = current.getJSONObject("condition").getString("icon");
                            String locationName = response.getJSONObject("location").getString("name");
                            int humidityValue = current.getInt("humidity");
                            double windSpeedValue = current.getDouble("wind_kph");

                            // Update UI elements
                            location.setText("Location: " + locationName); // Set location text
                            temperature.setText("Temperature: " + temperatureValue + "°C");
                            weatherDescription.setText("Description: " + conditionText);
                            humidity.setText("Humidity: " + humidityValue + "%");
                            windSpeed.setText("Wind Speed: " + windSpeedValue + " kph");

                            // Load weather icon
                            Picasso.get().load("https:" + iconUrl).into(weatherIcon);

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.toString());
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}
