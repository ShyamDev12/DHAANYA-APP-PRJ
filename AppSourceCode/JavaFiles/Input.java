package com.example.grower1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Input extends AppCompatActivity {

    private EditText etCropType, etSoilType, etLandSize, etLocation;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        etCropType = findViewById(R.id.et_crop_type);
        etSoilType = findViewById(R.id.et_soil_type);
        etLandSize = findViewById(R.id.et_land_size);
        etLocation = findViewById(R.id.et_location);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> {
            // Collect inputs
            String cropType = etCropType.getText().toString();
            String soilType = etSoilType.getText().toString();
            String landSize = etLandSize.getText().toString();
            String location = etLocation.getText().toString();

            // Send inputs to next activity
            Intent intent = new Intent(Input.this, RecommendationActivity.class);
            intent.putExtra("cropType", cropType);
            intent.putExtra("soilType", soilType);
            intent.putExtra("landSize", landSize);
            intent.putExtra("location", location);
            startActivity(intent);
        });
    }
}