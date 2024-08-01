package com.example.grower1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Imgquery extends AppCompatActivity {
    private static final int SELECT_IMAGE_REQUEST = 1;
    private Bitmap image;
    private ImageView imageView;
    private TextView classifyText, descriptionText;
    private ImageButton browseButton, resetButton;
    private androidx.appcompat.widget.AppCompatButton classifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgquery);

        imageView = findViewById(R.id.image);
        classifyText = findViewById(R.id.classifytext);
        descriptionText = findViewById(R.id.link2);
        browseButton = findViewById(R.id.bt_browse);
        resetButton = findViewById(R.id.bt_reset);
        classifyButton = findViewById(R.id.classify);

        browseButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_IMAGE_REQUEST);
        });

        resetButton.setOnClickListener(v -> {
            imageView.setImageResource(R.drawable.up2); // Reset to default image
            image = null;
            classifyText.setText("Quality Index:");
            descriptionText.setText("Description:\n\n\n\n");
        });

        classifyButton.setOnClickListener(v -> {
            if (image != null) {
                classifyImage();
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap originalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                int targetWidth = (int) (originalImage.getWidth() * 0.5);
                int targetHeight = (int) (originalImage.getHeight() * 0.5);
                image = Bitmap.createScaledBitmap(originalImage, targetWidth, targetHeight, false);
                imageView.setImageBitmap(originalImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void classifyImage() {
        GeminiPro model = new GeminiPro();
        String query = "Identify the plant disease in this picture?"; // Adjust query as needed
        model.getResponse(query, image, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                String diseaseOrPestName = parseResponse(response, "name");
                String description = parseResponse(response, "description");

                classifyText.setText("Disease/Pest: " + diseaseOrPestName);
                descriptionText.setText("Description:\n\n" + description);
            }

            @Override
            public void onError(Throwable throwable) {
                Toast.makeText(Imgquery.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String parseResponse(String response, String key) {
        // Implement parsing logic to extract the required information from the response
        // This is a placeholder implementation
        return response.contains(key) ? "Sample " + key : "Not found";
    }
}
