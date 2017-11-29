package me.yeojoy.circleprogressview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

import me.yeojoy.circleprogressview.widget.CircleProgressView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClick(View view) {
        CircleProgressView circleProgressView = findViewById(R.id.circle_progress_view);
        Random random = new Random();
        int percent = random.nextInt(100);
        circleProgressView.animateCircleProgress(percent);
    }
}
