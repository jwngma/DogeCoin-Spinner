package com.store.dogespinner;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,RewardedVideoAdListener {
    private static final String TAG = "MainActivity";

    ///adds
    private InterstitialAd mAds;
    private RewardedVideoAd mRewardAds;

    private RelativeLayout main_layout;
    private LinearLayout side_layout;
    private ImageView side_menu;
    /* top menu*/
    private ImageView menu, watch_Video_Image;
    private ProgressBar chargeProgress;
    private TextView total_Progress_text;

    /* Result Layout */
    private TextView total_point, total_claimed, newpoint;

    /* spin Layout*/
    private ImageView spinning_Image;
    /*Btns*/
    private Button watchBtn, spinBtn, resetBtn;

    /*Side navigation Buttons*/
    private TextView nav_name, nav_country;
    private Button nav_explore, nav_message, nav_setting, nav_logot;

    /* Anmation*/
    Animation fromTop, fromBottom;

    //random num to spinn
    private static final Random RANDOM = new Random();
    /*reset*/
    private static final Random random = new Random();
    private int lastDir;
    //
    private int degree = 0, degreeOld = 0;
    //diving the 360 degree with 37 sectors because we have 37 sectors in wheel and again diving the 37 sector to 2, to get the mid of each sector
    private static final float HALF_SECTOR = 360f / 37f / 2f;
    private static final String[] sectors = {"32", "15",
            "19", "4", "21", "2", "25", "17", "34",
            "6", "27", "13", "36", "11", "30", "8",
            "23", "10", "5", "24", "16", "33",
            "1", "20", "14", "31", "9", "22",
            "18", "29", "7", "28", "12", "35",
            "3", "26", "0"};

    private String point;
    private boolean spinning;
    private int total_points;

    /*progress*/
    private int progress = 0;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initAll();
        initSpinner();
        initInterstitialAds();
        initRewardAds();

    }

    private void initRewardAds() {
        MobileAds.initialize(this, getString(R.string.reward_add_unit_id));
        mRewardAds = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAds.loadAd(getString(R.string.reward_add_unit_id), new AdRequest.Builder().build());
        mRewardAds.setRewardedVideoAdListener(this);

        watchBtn = findViewById(R.id.watch_videoBtn);
        watch_Video_Image = findViewById(R.id.watch_video_Btn);
        watchBtn.setEnabled(false);
        watch_Video_Image.setEnabled(false);

        watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardVideo();

            }
        });


        watch_Video_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardVideo();

            }
        });
    }

    private void showRewardVideo() {

        if (mRewardAds.isLoaded()) {
            mRewardAds.show();
        } else {
            reloadRewardAds();
        }
    }

    private void reloadRewardAds() {
        if (!mRewardAds.isLoaded()) {

            mRewardAds.loadAd(getString(R.string.reward_add_unit_id), new AdRequest.Builder().build());
        }
    }

    private void initInterstitialAds() {
        mAds = new InterstitialAd(this);
        mAds.setAdUnitId(getString(R.string.app_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAds.loadAd(adRequest);
        mAds.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                StartLoadingAddsAgain();

            }
        });

    }

    private void StartLoadingAddsAgain() {

        if (!mAds.isLoading() && !mAds.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAds.loadAd(adRequest);
        }
    }

    private void showLoadedAds() {
        if (mAds.isLoaded() && mAds != null) {
            mAds.show();
        } else {
            StartLoadingAddsAgain();
        }
    }

    private void initAll() {
        chargeProgress = findViewById(R.id.charge_Progress);
        total_Progress_text = findViewById(R.id.charge_progress_text);

        total_claimed = findViewById(R.id.total_claimed_text);
        total_point = findViewById(R.id.total_spinned_text);
        newpoint = findViewById(R.id.new_point);

        spinBtn = findViewById(R.id.spinBtn);
        resetBtn = findViewById(R.id.reset_spinnerBtn);
        spinning_Image = findViewById(R.id.spinner_image);

        menu = findViewById(R.id.menu);
        main_layout = findViewById(R.id.main_layout);
        side_layout = findViewById(R.id.side_layout);
        side_menu = findViewById(R.id.nav_side_menu);

        /*nav side*/
        nav_name = findViewById(R.id.nav_name);
        nav_country = findViewById(R.id.nav_country);
        nav_explore = findViewById(R.id.nav_explore);
        nav_message = findViewById(R.id.nav_message);
        nav_setting = findViewById(R.id.nav_settings);
        nav_logot = findViewById(R.id.nav_signout);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top);

        initSpinner();
        initMenu();
        initMainLayout();
        initSideMenu();
        initResetWheel();
    }

    private void initNavAnimate() {
        side_menu.startAnimation(fromTop);
        nav_name.startAnimation(fromTop);
        nav_country.startAnimation(fromTop);
        nav_explore.startAnimation(fromBottom);
        nav_message.startAnimation(fromBottom);
        nav_setting.startAnimation(fromBottom);
        nav_logot.startAnimation(fromBottom);
    }
    private void initSideMenu() {
        side_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
            }
        });
    }
    private void initMainLayout() {
        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
            }
        });
    }
    private void initMenu() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                side_layout.animate().translationX(0);
                main_layout.animate().setDuration(300).translationX(600);
                main_layout.setAlpha(0.3f);
                initNavAnimate();
                menu.setEnabled(false);
            }
        });
    }

    private void initResetWheel() {
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!spinning) {
                    resetTheSpinner();
                }
            }
        });
    }
    private void resetTheSpinner() {
        degreeOld = degree % 360;
        //ramdom angle for wheel
        degree = RANDOM.nextInt(360) + 720;
        //rotate spinner on the center of itself
        Animation rotateAnimation = new RotateAnimation(degreeOld, degree, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "onAnimationStart: Spinning started");
                spinning = true;
                spinBtn.setEnabled(false);

                progress = progress - 5;
                chargeProgress.setProgress(progress);
                total_Progress_text.setText(String.valueOf(progress));
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "onAnimationEnd: Spinnig End " + getSector(360 - (degree % 360)));
                spinning = false;
                spinBtn.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d(TAG, "onAnimationRepeat: Spinning repeated");
            }
        });
        spinning_Image.startAnimation(rotateAnimation);
    }

    private void initSpinner() {

        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinning) {
                    SpinTheSpinner();
                }
            }
        });
    }
    private void SpinTheSpinner() {
        if ((Integer.valueOf(progress)) >= 5) {
            degreeOld = degree % 360;
            //ramdom angle for wheel
            degree = RANDOM.nextInt(360) + 720;
            //rotate spinner on the center of itself
            Animation rotateAnimation = new RotateAnimation(degreeOld, degree, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(3600);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setInterpolator(new DecelerateInterpolator());

            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.d(TAG, "onAnimationStart: Spinning started");
                    spinning = true;
                    spinBtn.setEnabled(false);

                    /*progress update*/
                    if ((Integer.valueOf(progress)) >= 5) {
                        progress = progress - 5;
                        chargeProgress.setProgress(progress);
                        total_Progress_text.setText(String.valueOf(progress));

//                    } else {
//                        builder = new AlertDialog.Builder(getApplicationContext());
//                        builder.setTitle(Html.fromHtml("<font color='#FF7F27'>You cant Spin !!!</font>"));
//                        builder.setMessage(Html.fromHtml("<font color='#FF7F27'>Your have less than 5 charge point. Watch some Ads to earn spin point</font>"));
//                        builder.setMessage("");
//                        builder.setPositiveButton("Watch", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                showRewardVideo();
//                            }
//                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                        AlertDialog alertDialog = builder.create();
//                        alertDialog.show();

                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.d(TAG, "onAnimationEnd: Spinnig End " + getSector(360 - (degree % 360)));
                    spinning = false;

                    if ((degree % 2) == 0) {
                        showLoadedAds();
                    } else {
                        showRewardVideo();
                    }
                    // showLoadedAds();
                    spinBtn.setEnabled(true);
                    point = getSector(360 - (degree % 360));
                    total_points = total_points + (Integer.parseInt(getSector(360 - (degree % 360))));
                    String zero = "0";
                    if (point.equals(zero)) {
                        newpoint.setText("Sorry! You did not Earned point this time");
                    } else {
                        newpoint.setText("Congrates !! You Have Won \n" + getSector(360 - (degree % 360)) + " Points");

                    }
                    total_point.setText("Total Points :" + String.valueOf(total_points).toString());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.d(TAG, "onAnimationRepeat: Spinning repeated");
                }
            });
            spinning_Image.startAnimation(rotateAnimation);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
            //builder.setTitle("You can't Spin Now !!!!");
            builder.setTitle(Html.fromHtml("<font color='#FF7F27'>You can't Spin Now !!!!</font>"));

            //builder.setMessage("You have less than 5 charge point. Watch some Ads to earn spin point");
            builder.setMessage(Html.fromHtml("<font color='#FF7F27'>Your have less than 5 charge point. Watch some Ads to earn spin point</font>"));
            builder.setPositiveButton("Watch Now!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showRewardVideo();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            //Set positive button background
            pbutton.setBackgroundColor(Color.GREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                pbutton.setBackground(getDrawable(R.drawable.bg_watch_video));
            }
            //Set positive button text color
            pbutton.setTextColor(Color.WHITE);
        }
    }

    private String getSector(int degrees) {
        int i = 0;
        String text = null;

        do {
            // start and end of each sector on the wheel
            float start = HALF_SECTOR * (i * 2 + 1);
            float end = HALF_SECTOR * (i * 2 + 3);

            if (degrees >= start && degrees < end) {
                // degrees is in [start;end[
                // so text is equals to sectors[i];
                text = sectors[i];
            }

            i++;
            // now we can test our Android Roulette Game :)
        } while (text == null && i < sectors.length);

        return text;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Log.d(TAG, "onRewardedVideoAdLoaded: Reward video is loaded");
        watch_Video_Image.setEnabled(true);
        watchBtn.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened: Reward ads is opened");
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        reloadRewardAds();
    }
    @Override
    public void onRewarded(RewardItem rewardItem) {
        int reward = rewardItem.getAmount();
        progress = progress + reward;
        chargeProgress.setProgress(progress);
        total_Progress_text.setText(String.valueOf(progress));
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        reloadRewardAds();
    }

    @Override
    public void onRewardedVideoCompleted() {
        reloadRewardAds();
    }

    public void side_nav_click(View view) {
        switch (view.getId()) {
            case R.id.nav_side_menu:
                Toast.makeText(getApplicationContext(), "Clicked on Profile Image " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            case R.id.nav_name:
                Toast.makeText(getApplicationContext(), "Clicked on Profile name " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            case R.id.nav_country:
                Toast.makeText(getApplicationContext(), "Clicked on Profile country " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            case R.id.nav_explore:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("AMOUNT TO CLAIM");
                alertDialog.setMessage("Enter The Amount to Claim");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.home_logo);

                alertDialog.setPositiveButton("Claim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String amount;
                                String my_amount="5";
                                amount = input.getText().toString();

                                if ((amount.compareTo(my_amount)<=0)||my_amount.equals(amount) ) {
                                    Toast.makeText(getApplicationContext(),
                                            "Amount has been Requested for withdrawal ", Toast.LENGTH_SHORT).show();
                                    side_layout.animate().translationX(-735);
                                    main_layout.animate().translationX(0);
                                    main_layout.setAlpha(1);
                                    menu.setEnabled(true);
                                }
                                else {

                                    input.setError("Amount should nt be greather");
                                   // Toast.makeText(getApplicationContext(),
                                            //"Amount should not be greater then your points!", Toast.LENGTH_SHORT).show();
//                                    side_layout.animate().translationX(-735);
//                                    main_layout.animate().translationX(0);
//                                    main_layout.setAlpha(1);
//                                    menu.setEnabled(true);
                                }
                            }
                        });

                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                side_layout.animate().translationX(-735);
                                main_layout.animate().translationX(0);
                                main_layout.setAlpha(1);
                                menu.setEnabled(true);
                            }
                        });

                alertDialog.show();

                //


                break;
            case R.id.nav_message:
                Toast.makeText(getApplicationContext(), "Clicked on message " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            case R.id.nav_settings:
                Toast.makeText(getApplicationContext(), "Clicked on settings " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            case R.id.nav_signout:
                Toast.makeText(getApplicationContext(), "Clicked on signout " + "Action not added yet", Toast.LENGTH_SHORT).show();
                side_layout.animate().translationX(-735);
                main_layout.animate().translationX(0);
                main_layout.setAlpha(1);
                menu.setEnabled(true);
                break;
            default:
                return;
        }
    }
}
