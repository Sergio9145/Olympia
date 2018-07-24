package com.olympia.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympia.Globals;
import com.olympia.R;
import com.transitionseverywhere.Fade;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;
import com.transitionseverywhere.extra.Scale;

public class HowToActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto);

        ViewGroup transitionsContainer10 = findViewById(R.id.transitions_container10),
                transitionsContainer20 = findViewById(R.id.transitions_container20),
                transitionsContainer30 = findViewById(R.id.transitions_container30),
                transitionsContainer40 = findViewById(R.id.transitions_container40),
                transitionsContainer50 = findViewById(R.id.transitions_container50),
                transitionsContainer60 = findViewById(R.id.transitions_container60),
                transitionsContainer70 = findViewById(R.id.transitions_container70),
                transitionsContainer80 = findViewById(R.id.transitions_container80),
                transitionsContainer90 = findViewById(R.id.transitions_container90);

        TextView line10 = transitionsContainer10.findViewById(R.id.line10),
                line20 = transitionsContainer20.findViewById(R.id.line20),
                line30 = transitionsContainer30.findViewById(R.id.line30),
                line40 = transitionsContainer40.findViewById(R.id.line40),
                line50 = transitionsContainer50.findViewById(R.id.line50),
                line60 = transitionsContainer60.findViewById(R.id.line60),
                line70 = transitionsContainer70.findViewById(R.id.line70),
                line80 = transitionsContainer80.findViewById(R.id.line80);
        ImageView img20 = transitionsContainer20.findViewById(R.id.img20),
                img40 = transitionsContainer40.findViewById(R.id.img40),
                img50 = transitionsContainer50.findViewById(R.id.img50),
                img60 = transitionsContainer60.findViewById(R.id.img60),
                img70 = transitionsContainer70.findViewById(R.id.img70),
                img80 = transitionsContainer80.findViewById(R.id.img80);
        Button btn = transitionsContainer90.findViewById(R.id.ok);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                int duration = 1000, delay = 500;
                TransitionSet transition1 = new TransitionSet()
                        .addTransition(new Scale(0.5f))
                        .addTransition(new Fade())
                        .setInterpolator(new FastOutLinearInInterpolator())
                        .setDuration(duration);

                Transition transition2 = new Slide(Gravity.END).setDuration(duration).setStartDelay(delay);
                Transition transition3 = new Slide(Gravity.START).setDuration(duration).setStartDelay(delay*2);
                Transition transition4 = new Slide(Gravity.BOTTOM).setDuration(duration).setStartDelay(delay*3);

                TransitionManager.beginDelayedTransition(transitionsContainer10, transition1);
                TransitionManager.beginDelayedTransition(transitionsContainer20, transition2);
                TransitionManager.beginDelayedTransition(transitionsContainer30, transition3);
                TransitionManager.beginDelayedTransition(transitionsContainer40, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer50, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer60, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer70, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer80, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer90, transition1.setStartDelay(delay*4));

                line10.setVisibility(View.VISIBLE);
                line20.setVisibility(View.VISIBLE);
                img20.setVisibility(View.VISIBLE);
                line30.setVisibility(View.VISIBLE);
                line40.setVisibility(View.VISIBLE);
                img40.setVisibility(View.VISIBLE);
                line50.setVisibility(View.VISIBLE);
                img50.setVisibility(View.VISIBLE);
                line60.setVisibility(View.VISIBLE);
                img60.setVisibility(View.VISIBLE);
                line70.setVisibility(View.VISIBLE);
                img70.setVisibility(View.VISIBLE);
                line80.setVisibility(View.VISIBLE);
                img80.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
            }
        };

        handler.postDelayed(r, 500);
    }
}
