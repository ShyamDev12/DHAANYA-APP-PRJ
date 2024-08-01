package com.example.grower1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ChartView extends View {

    private Map<String, Float> cropPrices;  // Map to store crop names and prices
    private Paint paint;
    private Paint textPaint;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setStyle(Paint.Style.FILL);
    }

    public void setCropPrices(Map<String, Float> prices) {
        this.cropPrices = prices;
        invalidate();  // Trigger a redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cropPrices == null || cropPrices.isEmpty()) {
            drawDefaultChart(canvas);
            return;
        }

        float width = getWidth();
        float height = getHeight();
        float margin = 50;
        float chartWidth = width - 2 * margin;
        float chartHeight = height - 2 * margin;

        float total = 0;
        for (Float price : cropPrices.values()) {
            total += price;
        }

        float currentAngle = 0;
        int index = 0;
        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}; // Example colors

        for (Map.Entry<String, Float> entry : cropPrices.entrySet()) {
            float percentage = (entry.getValue() / total) * 360;
            paint.setColor(colors[index % colors.length]);
            canvas.drawArc(margin, margin, width - margin, height - margin,
                    currentAngle, percentage, true, paint);

            // Draw text
            float x = width / 2;
            float y = margin + (height - 2 * margin) / (cropPrices.size() + 1) * (index + 1);
            textPaint.setColor(colors[index % colors.length]);
            canvas.drawText(entry.getKey() + ": $" + entry.getValue(), x, y, textPaint);

            currentAngle += percentage;
            index++;
        }
    }

    private void drawDefaultChart(Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(4);
        canvas.drawLine(50, 100, getWidth() - 50, 100, paint);
        canvas.drawLine(50, 200, getWidth() - 50, 200, paint);

        textPaint.setColor(Color.BLACK);
        canvas.drawText("Wheat: $20", 60, 90, textPaint);
        canvas.drawText("Corn: $25", 60, 190, textPaint);
    }
}
