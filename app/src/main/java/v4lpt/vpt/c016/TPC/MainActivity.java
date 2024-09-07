package v4lpt.vpt.c016.TPC;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private CircleTimerView circleTimerView;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60 * 60 * 1000; // 1 hour
    private int currentSection = 0;
    private MediaPlayer shortBreakSound;
    private MediaPlayer longBreakSound;
    private boolean timerRunning = false;
    private Button backButton;
    private Handler handler = new Handler();
    private Button infoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

        showSystemUI();

        shortBreakSound = MediaPlayer.create(this, R.raw.kanlgschalde);
        longBreakSound = MediaPlayer.create(this, R.raw.kanlgschalde);
        Button startTimerButton = findViewById(R.id.startTimerButton);

        setupStartTimerButton(startTimerButton, savedInstanceState);
    }
    private void setupStartTimerButton(Button startTimerButton, final Bundle savedInstanceState) {
        infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(v ->  openInfoFragment());
        startTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSystemUI();
                setContentView(R.layout.activity_main);
                circleTimerView = findViewById(R.id.circleTimerView);
                backButton = findViewById(R.id.backButton);
                View rootView = findViewById(android.R.id.content);
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBackButton();
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetTimerAndGoBack();
                        showSystemUI();
                    }
                });
                if (savedInstanceState != null) {
                    timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
                    timerRunning = savedInstanceState.getBoolean("timerRunning");
                    if (timerRunning) {
                        startTimer();
                    } else {
                        updateTimerUI();
                    }
                }
                setupTimer();
            }
        });
    }
    private void setupTimer() {
        circleTimerView = findViewById(R.id.circleTimerView);
        longBreakSound.start();

        startTimer();
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Adjust the layout params of the rating layout
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        getWindow().setAttributes(params);
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // Reset the layout params
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= ~(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setAttributes(params);
    }
    private void showBackButton() {
        backButton.setVisibility(View.VISIBLE);
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                backButton.setVisibility(View.INVISIBLE);
            }
        }, 1727); // 1.727 seconds
    }
    private void openInfoFragment() {
        InfoFragment infoFragment = new InfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, infoFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void closeInfoFragment() {
        getSupportFragmentManager().popBackStack();
    }
    private void resetTimerAndGoBack() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timeLeftInMillis = 60 * 60 * 1000; // Reset to 1 hour
        updateTimerUI();
        setContentView(R.layout.welcome_layout);
        Button startTimerButton = findViewById(R.id.startTimerButton);
        setupStartTimerButton(startTimerButton, null);
        // Re-initialize any necessary views or listeners for the welcome layout
    }
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerUI();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 60 * 60 * 1000; // Reset to 1 hour
                currentSection = 0;
                startTimer(); // Restart the timer
            }
        }.start();
        timerRunning = true;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("timerRunning", timerRunning);
    }
    private void checkSectionTransition() {
        int[] sections = {25, 5, 25, 5}; // in minutes
        long totalTime = 60 * 60 * 1000; // 1 hour in milliseconds
        long elapsedTime = totalTime - timeLeftInMillis;

        int newSection = 0;
        long accumulatedTime = 0;
        for (int i = 0; i < sections.length; i++) {
            accumulatedTime += sections[i] * 60 * 1000;
            if (elapsedTime < accumulatedTime) {
                newSection = i;
                break;
            }
        }

        if (newSection != currentSection) {
            currentSection = newSection;
            if (currentSection % 2 == 0) {
                longBreakSound.start();
            } else {
                shortBreakSound.start();
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // The timer will continue running as is
    }
    private void updateTimerUI() {
        circleTimerView.setTimeLeft(timeLeftInMillis);
        checkSectionTransition();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shortBreakSound != null) {
            shortBreakSound.release();
        }
        if (longBreakSound != null) {
            longBreakSound.release();
        }
    }
}