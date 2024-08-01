package com.example.grower1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MarketPriceActivity extends AppCompatActivity {

    private static final String TAG = "MarketPriceActivity";
    private ImageView lineChartView;
    private ImageView pieChartView;
    private ProgressBar progressBar;
    private TextView tvMarketTitle;

    private static final String API_KEY = "AIzaSyCIj92q9uZdNFTuUvlnL4tkOz3VEOfB98Y";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_price);

        tvMarketTitle = findViewById(R.id.tv_market_title);
        lineChartView = findViewById(R.id.line_chart_view);
        pieChartView = findViewById(R.id.pie_chart_view);
        progressBar = findViewById(R.id.progressBar);

        // Check for storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Fetch market price data
        fetchMarketPriceData();

        // Set up save button listener
        findViewById(R.id.btn_save_pdf).setOnClickListener(v -> {
            // Save current recommendations as PDF
            saveRecommendationAsPDF();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void fetchMarketPriceData() {
        progressBar.setVisibility(View.VISIBLE);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String userPrompt = "Provide the market price data for various crops for the current month.";

        Content content = new Content.Builder().addText(userPrompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(result.getText());
                        JSONArray crops = json.getJSONArray("crops");

                        // Update the charts with the fetched data
                        updateCharts(crops);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing JSON response", e);
                        // Use default values if parsing fails
                        updateCharts(null);
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    tvMarketTitle.setText("Failed to fetch market prices");
                    progressBar.setVisibility(View.GONE);
                    // Use default values if request fails
                    updateCharts(null);
                });
                t.printStackTrace();
            }
        }, getMainExecutor());
    }

    private void updateCharts(JSONArray crops) {
        // Default data in case of failure to fetch data
        Map<String, Integer> cropPrices = new HashMap<>();
        cropPrices.put("Wheat", 150);
        cropPrices.put("Rice", 200);
        cropPrices.put("Corn", 120);
        cropPrices.put("Soybean", 170);

        if (crops != null) {
            try {
                cropPrices.clear();
                for (int i = 0; i < crops.length(); i++) {
                    JSONObject crop = crops.getJSONObject(i);
                    String cropName = crop.getString("name");
                    int price = crop.getInt("price");
                    cropPrices.put(cropName, price);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing market price data", e);
            }
        }

        // Draw the line chart
        drawLineChart(cropPrices);
        // Draw the pie chart
        drawPieChart(cropPrices);
    }

    private void drawLineChart(Map<String, Integer> cropPrices) {
        Bitmap bitmap = Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setTextSize(30);

        int x = 100;
        int y = 350;
        int prevX = x;
        int prevY = y;

        paint.setColor(Color.BLUE);
        for (Map.Entry<String, Integer> entry : cropPrices.entrySet()) {
            int price = entry.getValue();
            y = 350 - price;
            canvas.drawLine(prevX, prevY, x, y, paint);
            canvas.drawCircle(x, y, 10, paint);
            canvas.drawText(entry.getKey() + ": $" + price, x - 40, y - 10, paint);
            prevX = x;
            prevY = y;
            x += 150;
        }

        lineChartView.setImageBitmap(bitmap);
    }

    private void drawPieChart(Map<String, Integer> cropPrices) {
        Bitmap bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(30);

        int total = 0;
        for (int price : cropPrices.values()) {
            total += price;
        }

        float startAngle = 0;
        for (Map.Entry<String, Integer> entry : cropPrices.entrySet()) {
            paint.setColor(Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            float sweepAngle = (entry.getValue() / (float) total) * 360;
            canvas.drawArc(new RectF(100, 100, 700, 700), startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

        startAngle = 0;
        for (Map.Entry<String, Integer> entry : cropPrices.entrySet()) {
            paint.setColor(Color.BLACK);
            float sweepAngle = (entry.getValue() / (float) total) * 360;
            float angle = startAngle + sweepAngle / 2;
            float x = 400 + 300 * (float) Math.cos(Math.toRadians(angle));
            float y = 400 + 300 * (float) Math.sin(Math.toRadians(angle));
            canvas.drawText(entry.getKey() + ": $" + entry.getValue(), x, y, paint);
            startAngle += sweepAngle;
        }

        pieChartView.setImageBitmap(bitmap);
    }

    private void saveRecommendationAsPDF() {
        // Implementation for saving recommendations as a PDF
        // You can use the same implementation as provided in the previous response
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Storage permission granted");
            } else {
                Log.d(TAG, "Storage permission denied");
            }
        }
    }
}
