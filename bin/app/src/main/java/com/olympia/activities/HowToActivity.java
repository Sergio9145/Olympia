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

        ViewGroup transitionsContainer1 = findViewById(R.id.transitions_container1),
                transitionsContainer2 = findViewById(R.id.transitions_container2),
                transitionsContainer3 = findViewById(R.id.transitions_container3),
                transitionsContainer4 = findViewById(R.id.transitions_container4),
                transitionsContainer5 = findViewById(R.id.transitions_container5),
                transitionsContainer6 = findViewById(R.id.transitions_container6);

        TextView line1 = transitionsContainer1.findViewById(R.id.line1),
                line2 = transitionsContainer2.findViewById(R.id.line2),
                line3 = transitionsContainer3.findViewById(R.id.line3),
                line4 = transitionsContainer4.findViewById(R.id.line4),
                line5 = transitionsContainer5.findViewById(R.id.line5);
        ImageView img3 = transitionsContainer3.findViewById(R.id.img3),
                img4 = transitionsContainer4.findViewById(R.id.img4),
                img5 = transitionsContainer5.findViewById(R.id.img5);
        Button btn = transitionsContainer6.findViewById(R.id.ok);

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

                TransitionManager.beginDelayedTransition(transitionsContainer1, transition1);
                TransitionManager.beginDelayedTransition(transitionsContainer2, transition2);
                TransitionManager.beginDelayedTransition(transitionsContainer3, transition3);
                TransitionManager.beginDelayedTransition(transitionsContainer4, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer5, transition4);
                TransitionManager.beginDelayedTransition(transitionsContainer6, transition1.setStartDelay(delay*4));

                line1.setVisibility(View.VISIBLE);
                line2.setVisibility(View.VISIBLE);
                line3.setVisibility(View.VISIBLE);
                img3.setVisibility(View.VISIBLE);
                line4.setVisibility(View.VISIBLE);
                img4.setVisibility(View.VISIBLE);
                line5.setVisibility(View.VISIBLE);
                img5.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
            }
        };

        handler.postDelayed(r, 500);
    }
}
