package com.example.grower1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecommendationActivity extends AppCompatActivity {

    private static final String TAG = "GeminiAPI";
    private TextView tvRecommendation;
    private Button btnSavePDF;
    private ProgressBar progressBar;
    private String recommendationText;

    private static final String API_KEY = "AIzaSyCIj92q9uZdNFTuUvlnL4tkOz3VEOfB98Y";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        tvRecommendation = findViewById(R.id.tv_recommendation);
        btnSavePDF = findViewById(R.id.btn_save_pdf);
        progressBar = findViewById(R.id.progressBar);

        // Check for storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Get inputs from the intent
        Intent intent = getIntent();
        String cropType = intent.getStringExtra("cropType");
        String soilType = intent.getStringExtra("soilType");
        String landSize = intent.getStringExtra("landSize");
        String location = intent.getStringExtra("location");

        // Send inputs to Gemini API
        sendRequestToGemini(cropType, soilType, landSize, location);

        // Save recommendation as PDF
        btnSavePDF.setOnClickListener(v -> {
            if (recommendationText != null) {
                saveRecommendationAsPDF(recommendationText);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void sendRequestToGemini(String cropType, String soilType, String landSize, String location) {
        progressBar.setVisibility(View.VISIBLE);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String userPrompt = "Suggest a next 1-month farming plan and advisory in layman terms with full detail for the following inputs: " +
                "Crop Type: " + cropType + ", Soil Type: " + soilType + ", Land Size: " + landSize + ", Location: " + location;

        Content content = new Content.Builder().addText(userPrompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    recommendationText = result.getText();
                    tvRecommendation.setText(recommendationText);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    tvRecommendation.setText("Failed to get response");
                    progressBar.setVisibility(View.GONE);
                });
                t.printStackTrace();
            }
        }, getMainExecutor());
    }

    private void saveRecommendationAsPDF(String text) {
        Document document = new Document();
        String filePath = Environment.getExternalStorageDirectory() + "/Recommendation.pdf";

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(text));
            document.close();
            Log.d(TAG, "PDF saved at: " + filePath);
        } catch (DocumentException | IOException e) {
            Log.e(TAG, "Error saving PDF: ", e);
        }
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


