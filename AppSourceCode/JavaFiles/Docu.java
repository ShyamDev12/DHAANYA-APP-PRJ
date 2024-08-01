package com.example.grower1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Docu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docu);

        Button openButton = findViewById(R.id.openbtn);
        openButton.setOnClickListener(v -> {
            Intent intent = new Intent(Docu.this, Webview.class);
            intent.putExtra("url", "https://www.india.gov.in/farmers-portal");
            startActivity(intent);
        });

        ImageView im1 = findViewById(R.id.im1);
        ImageView im2 = findViewById(R.id.im2);
        ImageView im3 = findViewById(R.id.im3);

        im1.setOnClickListener(v -> showPopupDialog("PM-KISAN Scheme \nPradhan Mantri Kisan Samman Nidhi (PM-KISAN)\n" +
                "PM-KISAN is a central sector scheme launched on 24th February 2019 to supplement financial needs of land holding farmers, subject to exclusions. Under the scheme, financial benefit of Rs. 6000/- per year is transferred in three equal four-monthly installments into the bank accounts of farmers’ families across the country, through Direct Benefit Transfer (DBT) mode.", R.drawable.far));
        im2.setOnClickListener(v -> showPopupDialog("PM-KMY Scheme \nPradhan Mantri Kisan Maandhan Yojna (PMKMY) is a central sector scheme launched on 12th September 2019 to provide security to the most vulnerable farmer families. PM-KMY is contributory scheme, small and marginal farmers (SMFs), subject to exclusion criteria, can opt to become member of the scheme by paying monthly subscription to the Pension Fund.", R.drawable.far));
        im3.setOnClickListener(v -> showPopupDialog("PMFBY Scheme \nPMFBY was launched in 2016 in order to provide a simple and affordable crop insurance product to ensure comprehensive risk cover for crops to farmers against all non-preventable natural risks from pre-sowing to post-harvest and to provide adequate claim amount.", R.drawable.far));
    }

    private void showPopupDialog(String text, int imageResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_popup, null);
        builder.setView(dialogView);

        ImageView dialogImage = dialogView.findViewById(R.id.dialog_image);
        TextView dialogText = dialogView.findViewById(R.id.dialog_text);

        dialogImage.setImageResource(imageResId);
        dialogText.setText(text);

        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
