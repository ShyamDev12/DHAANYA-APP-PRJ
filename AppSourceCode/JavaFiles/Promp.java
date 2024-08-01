package com.example.grower1;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class Promp extends AppCompatActivity {

    private TextView modelResponseTextView;
    private EditText queryEditText;
    private ProgressBar sendPromptProgressBar;
    private Button sendPromptButton;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promp);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        queryEditText = findViewById(R.id.queryEditText);
        sendPromptButton = findViewById(R.id.sendPromptButton);
        sendPromptProgressBar = findViewById(R.id.sendPromptProgressBar);
        modelResponseTextView = findViewById(R.id.modelResponseTextView);

        sendPromptButton.setOnClickListener(v -> modelCall());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void modelCall() {
        sendPromptProgressBar.setVisibility(ProgressBar.VISIBLE);
        sendPromptButton.setEnabled(false);

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyCIj92q9uZdNFTuUvlnL4tkOz3VEOfB98Y");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        String userPrompt = queryEditText.getText().toString();
        Content content = new Content.Builder().addText(userPrompt).build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                runOnUiThread(() -> {
                    modelResponseTextView.setText(result.getText());
                    sendPromptProgressBar.setVisibility(ProgressBar.GONE);
                    sendPromptButton.setEnabled(true);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    modelResponseTextView.setText("Failed to get response");
                    sendPromptProgressBar.setVisibility(ProgressBar.GONE);
                    sendPromptButton.setEnabled(true);
                });
                t.printStackTrace();
            }
        }, getMainExecutor());
    }
}
