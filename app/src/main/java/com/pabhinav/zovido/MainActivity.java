package com.pabhinav.zovido;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int STARTOFFSET = 1000;
    public String agentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences(Constants.mypreferences, Context.MODE_PRIVATE);
        agentName = sharedPref.getString(Constants.agentName, null);

        if(agentName != null && agentName.length() > 0){

            setContentView(R.layout.main_activity_with_agent_name);

            final FloatingActionButton forward = (FloatingActionButton) findViewById(R.id.transition_view);
            final TextView letGetYouStarted = (TextView) findViewById(R.id.text_get_you_started);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    letGetYouStarted.setVisibility(View.VISIBLE);
                    letGetYouStarted.setText("Hi, " + agentName);

                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(letGetYouStarted, "alpha", 0f, 1f);
                    fadeIn.setDuration(500);
                    fadeIn.start();

                    forward.setVisibility(View.VISIBLE);
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(forward, "scaleX", 0f, 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(forward, "scaleY", 0f, 1f);
                    scaleX.setDuration(500);
                    scaleY.setDuration(500);
                    scaleX.start();
                    scaleY.start();
                }
            },500);

            return;
        }

        setContentView(R.layout.activity_main);

        final FloatingActionButton forward = (FloatingActionButton) findViewById(R.id.transition_view);
        final ImageView iconImageView = (ImageView) findViewById(R.id.icon_image_view);
        final ImageView iconImageView1 = (ImageView) findViewById(R.id.icon_image_view_1);
        final ImageView iconImageView2 = (ImageView) findViewById(R.id.icon_image_view_2);
        final TextView letGetYouStarted = (TextView) findViewById(R.id.text_get_you_started);

        iconImageView2.setVisibility(View.INVISIBLE);
        iconImageView1.setVisibility(View.INVISIBLE);

        /** Important three phases for animation **/
        popOutIcons(iconImageView2, iconImageView1, STARTOFFSET);
        translateIconsAndShowHelpText(iconImageView2, iconImageView1, letGetYouStarted, STARTOFFSET + 500);
        showCombinedIcon(iconImageView, STARTOFFSET + 1500);
        changeTextAndShowForward(letGetYouStarted, forward, STARTOFFSET + 2500);
    }

    public void forwardClicked(View v){

        if(agentName != null && agentName.length() > 0){

            /** Start the CallDetails activity **/
            Intent intent = new Intent(MainActivity.this, CallDetails.class);
            intent.putExtra(Constants.agentName,agentName);
            startActivity(intent);

            /** Finish this activity **/
            finish();

            return;
        }

        /** Scene Transition **/
        TransitionHelper transitionHelper = new TransitionHelper();
        transitionHelper.doSceneTransition(R.id.root_layout, R.layout.activity_main_name_input, this);

        /**
         *  After scene transition, do some scaling and translation for text
         *  Also take agent name as input, and on successful input,start new
         *  activity
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final MyEditText nameInputEditText = (MyEditText) findViewById(R.id.input_name_in_main);
                final MyEditTextHeading editTextHeading = (MyEditTextHeading) findViewById(R.id.name_heading_in_main);
                final View underline = findViewById(R.id.name_underline_in_main);
                final TextView nameAskingTextView = (TextView) findViewById(R.id.name_asking_text_view);
                final ImageView nameForwardButtn = (ImageView) findViewById(R.id.name_forward_button);

                /** Get location of label above edit text view **/
                int[] locationOfLabelHeading = new int[2];
                editTextHeading.getLocationOnScreen(locationOfLabelHeading);

                /** Get location of name text view **/
                int[] locationOfNameAskingTextView = new int[2];
                nameAskingTextView.getLocationOnScreen(locationOfNameAskingTextView);

                /** Scale in such ratio that name text view reduces to size of label of edit text view **/
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(nameAskingTextView, "scaleX", 1f, (float) editTextHeading.getWidth() / (float) nameAskingTextView.getWidth());
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(nameAskingTextView, "scaleY", 1f, (float) editTextHeading.getHeight() / (float) nameAskingTextView.getHeight());
                scaleX.setDuration(1000);
                scaleY.setDuration(1000);
                scaleX.start();
                scaleY.start();

                /** Translate text view till it reaches position of label of edit text view **/
                TranslateAnimationHelper translateAnimationHelper = new TranslateAnimationHelper(nameAskingTextView, 0, -(locationOfNameAskingTextView[0] - locationOfLabelHeading[0]), 0, -(locationOfNameAskingTextView[1] - locationOfLabelHeading[1]));
                translateAnimationHelper.setTranslateAnimationEndListener(new TranslateAnimationHelper.TranslateAnimationEndListener() {
                    @Override
                    public void onTranslateAnimationEnd() {

                        /** Need to show hidden elements **/
                        nameForwardButtn.setVisibility(View.VISIBLE);
                        underline.setVisibility(View.VISIBLE);
                        nameInputEditText.setVisibility(View.VISIBLE);
                        editTextHeading.setVisibility(View.VISIBLE);

                        nameAskingTextView.clearAnimation();
                        nameAskingTextView.setVisibility(View.INVISIBLE);

                        /** If newly shown forward button is pressed start next activity **/
                        nameForwardButtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(nameInputEditText.getText() != null && nameInputEditText.getText().toString().length() > 0 ){

                                    /** Start the CallDetails activity **/
                                    Intent intent = new Intent(MainActivity.this, CallDetails.class);
                                    intent.putExtra(Constants.agentName,nameInputEditText.getText().toString());
                                    startActivity(intent);

                                    /** Finish this activity **/
                                    finish();

                                } else {

                                    /** Empty agent name, show red indications **/
                                    underline.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                                    editTextHeading.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                }
                            }
                        });
                    }
                });


            }
        }, 1500);

    }

    public void popOutIcons(final ImageView iconImageView2, final ImageView iconImageView1, int duration){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                iconImageView2.setVisibility(View.VISIBLE);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(iconImageView2, "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(iconImageView2, "scaleY", 0f, 1f);
                scaleX.setDuration(500);
                scaleY.setDuration(500);
                scaleX.start();
                scaleY.start();

                iconImageView1.setVisibility(View.VISIBLE);
                ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(iconImageView1, "scaleX", 0f, 1f);
                ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(iconImageView1, "scaleY", 0f, 1f);
                scaleX1.setDuration(500);
                scaleY1.setDuration(500);
                scaleX1.start();
                scaleY1
                        .start();
            }
        }, duration);
    }

    public void changeTextAndShowForward(final TextView letGetYouStarted, final FloatingActionButton forward, int duration){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                letGetYouStarted.setVisibility(View.VISIBLE);
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(letGetYouStarted, "alpha", 1f, 0f);
                fadeOut.setDuration(500);
                fadeOut.start();
                fadeOut.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        letGetYouStarted.clearAnimation();
                        letGetYouStarted.setText("Let's get you started");

                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(letGetYouStarted, "alpha", 0f, 1f);
                        fadeIn.setDuration(500);
                        fadeIn.start();

                        forward.setVisibility(View.VISIBLE);
                        ObjectAnimator scaleX = ObjectAnimator.ofFloat(forward, "scaleX", 0f, 1f);
                        ObjectAnimator scaleY = ObjectAnimator.ofFloat(forward, "scaleY", 0f, 1f);
                        scaleX.setDuration(500);
                        scaleY.setDuration(500);
                        scaleX.start();
                        scaleY.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }
        }, duration);

    }

    public void showCombinedIcon(final ImageView iconImageView, int duration){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(iconImageView, "scaleX",  0f, 1f);
                ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(iconImageView, "scaleY", 0f, 1f);
                scaleX1.setDuration(200);
                scaleY1.setDuration(200);
                scaleX1.start();
                scaleY1.start();
                iconImageView.setVisibility(View.VISIBLE);

            }
        }, duration);
    }

    public void translateIconsAndShowHelpText(final ImageView iconImageView2, final ImageView iconImageView1, final TextView letGetYouStarted, int duration){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();

                int[] locationOfLeftIcon = new int[2];
                iconImageView2.getLocationOnScreen(locationOfLeftIcon);

                int[] locationOfRightIcon = new int[2];
                iconImageView1.getLocationOnScreen(locationOfRightIcon);

                TranslateAnimationHelper translateAnimationHelper = new TranslateAnimationHelper(iconImageView2, 0, width / 2 - locationOfLeftIcon[0] - 96, 0, 0, 1000, 0, new LinearInterpolator());
                translateAnimationHelper.setTranslateAnimationEndListener(new TranslateAnimationHelper.TranslateAnimationEndListener() {
                    @Override
                    public void onTranslateAnimationEnd() {
                        iconImageView2.clearAnimation();
                        iconImageView2.setVisibility(View.GONE);
                    }
                });

                TranslateAnimationHelper translateAnimationHelper1 = new TranslateAnimationHelper(iconImageView1, 0, -(locationOfRightIcon[0] - width / 2), 0, 0, 1000, 0, new LinearInterpolator());
                translateAnimationHelper1.setTranslateAnimationEndListener(new TranslateAnimationHelper.TranslateAnimationEndListener() {
                    @Override
                    public void onTranslateAnimationEnd() {
                        iconImageView1.clearAnimation();
                        iconImageView1.setVisibility(View.GONE);
                    }
                });


                letGetYouStarted.setVisibility(View.VISIBLE);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(letGetYouStarted, "alpha", .3f, 1f);
                fadeIn.setDuration(500);
                fadeIn.start();


            }
        }, duration);

    }

}
