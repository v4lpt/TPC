package v4lpt.vpt.c016.TPC;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CircleTimerView extends View {

    private Paint[] sectionPaints;
    private Paint[] progressPaints;
    private Paint zeigerPaint;
    private Paint zeigerDotPaint;
    private long totalTimeInMillis = 60 * 60 * 1000; // 1 hour
    private long timeLeftInMillis = totalTimeInMillis;

    private float centerX;
    private float centerY;
    private float radius;

    private RectF oval;

    private int[] sections = {25, 5, 25, 5};

    public CircleTimerView(Context context) {
        super(context);
        init();
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sectionPaints = new Paint[4];
        progressPaints = new Paint[4];

        for (int i = 0; i < 4; i++) {
            sectionPaints[i] = new Paint();
            sectionPaints[i].setStyle(Paint.Style.FILL);
            sectionPaints[i].setAntiAlias(true);

            progressPaints[i] = new Paint();
            progressPaints[i].setStyle(Paint.Style.FILL);
            progressPaints[i].setAntiAlias(true);
        }

        zeigerPaint = new Paint();
        zeigerPaint.setColor(Color.WHITE);
        zeigerPaint.setStyle(Paint.Style.STROKE);
        zeigerPaint.setStrokeWidth(4f);
        zeigerPaint.setAntiAlias(true);

        zeigerDotPaint = new Paint();
        zeigerDotPaint.setColor(Color.WHITE);
        zeigerDotPaint.setStyle(Paint.Style.FILL);
        zeigerDotPaint.setAntiAlias(true);

        updateColors();

    }

    private void updateColors() {
        sectionPaints[0].setColor(ContextCompat.getColor(getContext(), R.color.vptredt));
        sectionPaints[1].setColor(ContextCompat.getColor(getContext(), R.color.vptturkt));
        sectionPaints[2].setColor(ContextCompat.getColor(getContext(), R.color.vptredt));
        sectionPaints[3].setColor(ContextCompat.getColor(getContext(), R.color.vptturkt));

        progressPaints[0].setColor(ContextCompat.getColor(getContext(), R.color.vptred));
        progressPaints[1].setColor(ContextCompat.getColor(getContext(), R.color.vptturk));
        progressPaints[2].setColor(ContextCompat.getColor(getContext(), R.color.vptred));
        progressPaints[3].setColor(ContextCompat.getColor(getContext(), R.color.vptturk));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startAngle = 270; // Start at 12 o'clock
        float elapsedAngle = 360f * (1 - timeLeftInMillis / (float)totalTimeInMillis);
        boolean isActive = true;

        for (int i = 0; i < 4; i++) {
            float sweepAngle = 360f * sections[i] / 60f;

            if (isActive) {
                // Draw the dark background (remaining time) for the active section
                canvas.drawArc(oval, startAngle, sweepAngle, true, sectionPaints[i]);

                // Draw the light foreground (elapsed time) for the active section
                if (elapsedAngle > 0) {
                    float progressAngle = Math.min(elapsedAngle, sweepAngle);
                    canvas.drawArc(oval, startAngle, progressAngle, true, progressPaints[i]);
                    elapsedAngle -= progressAngle;
                    if (elapsedAngle <= 0) {
                        isActive = false;
                    }
                }
            } else {
                // For inactive sections, flip the colors
                canvas.drawArc(oval, startAngle, sweepAngle, true, progressPaints[i]);
            }

            startAngle += sweepAngle;
        }



        // Draw Zeiger (hand)
        float zeigerAngle = 360f * (1 - timeLeftInMillis / (float)totalTimeInMillis) + 270;
        float zeigerX = (float) (centerX + radius * Math.cos(Math.toRadians(zeigerAngle)));
        float zeigerY = (float) (centerY + radius * Math.sin(Math.toRadians(zeigerAngle)));
        canvas.drawLine(centerX, centerY, zeigerX, zeigerY, zeigerPaint);

        // Draw larger, filled dot at the end of the Zeiger
        float dotRadius = 12f;
        float extendedZeigerX = (float) (centerX + (radius + dotRadius / 2) * Math.cos(Math.toRadians(zeigerAngle)));
        float extendedZeigerY = (float) (centerY + (radius + dotRadius / 2) * Math.sin(Math.toRadians(zeigerAngle)));
        canvas.drawCircle(extendedZeigerX, extendedZeigerY, dotRadius, zeigerDotPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        float dotRadius = 12f;
        float padding = dotRadius * 2; // Increased padding to accommodate the dot
        radius = Math.min(w, h) / 2f - padding;
        oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }


    public void setTimeLeft(long timeInMillis) {
        timeLeftInMillis = timeInMillis;
        invalidate();
    }
}