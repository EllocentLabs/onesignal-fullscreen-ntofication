package com.onesignalfullscreennotification;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.os.Vibrator;
import android.content.Context;
import android.widget.ImageView;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.view.WindowManager;
import com.squareup.picasso.Picasso;
import android.app.KeyguardManager;
import com.facebook.react.bridge.WritableMap;
import android.os.Build;
import com.facebook.react.bridge.Arguments;
import com.rnonesignalts.MainActivity;
import android.os.CountDownTimer;


public class RideActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvInfo;
    private TextView timerView;
    private ImageView ivAvatar;
    private CountDownTimer timer;
    private Integer timeout = 0;
    private Vibrator v;
    private long[] pattern = {0, 1000, 800};
    private MediaPlayer player;
    private String uuid = "";
    static boolean active = false;
    
    @Override
    public void onStart() {
        super.onStart();

        active = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_incoming);
        tvName = findViewById(R.id.tvName);
        tvInfo = findViewById(R.id.tvInfo);
        ivAvatar = findViewById(R.id.ivAvatar);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        Bundle bundle = getIntent().getExtras();
        startTimer();

        if (bundle != null) {
            if (bundle.containsKey("uuid")) {
                uuid = bundle.getString("uuid");
            }
            if (bundle.containsKey("name")) {
                String name = bundle.getString("name");
                tvName.setText(name);
            }
            if (bundle.containsKey("info")) {
                String info = bundle.getString("info");
                tvInfo.setText("Pickup Location: " + info);
            }
            if (bundle.containsKey("avatar")) {
                String avatar = bundle.getString("avatar");
                if (avatar != null) {
                    Picasso.get().load(avatar).transform(new CircleTransform()).into(ivAvatar);
                }
            }
            if (bundle.containsKey("timeout")) {
                this.timeout = bundle.getInt("timeout");
            }
            else this.timeout = 0;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        v.vibrate(pattern, 0);
        player.start();

        AnimateImage acceptCallBtn = findViewById(R.id.ivAcceptCall);
        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    v.cancel();
                    player.stop();
                    player.prepareAsync();
                    acceptDialing();
                } catch (Exception e) {
                    WritableMap params = Arguments.createMap();
                    params.putString("message", e.getMessage());
                    dismissDialing();
                }
            }
        });

        AnimateImage rejectCallBtn = findViewById(R.id.ivDeclineCall);
        rejectCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.cancel();
                player.stop();
                player.prepareAsync();
                dismissDialing();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Dont back
    }

    private void acceptDialing() {
        timer.cancel();
        WritableMap params = Arguments.createMap();
        params.putBoolean("accept", true);
        params.putString("uuid", uuid);
               
        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        if (mKeyguardManager.isDeviceLocked()) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mKeyguardManager.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() {
              @Override
              public void onDismissSucceeded() {
                super.onDismissSucceeded();
              }
            });
          }
        }

        Intent launchIntentt = new Intent(this, MainActivity.class);
        launchIntentt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntentt.putExtra("rideId", uuid);
        launchIntentt.putExtra("status", "accepted");
        if (launchIntentt != null) {
            this.startActivity(launchIntentt);
        }
        finish();
    }

    private void dismissDialing() {
        timer.cancel();
        finish();
    }

    public void dismissIncoming() {
        v.cancel();
        player.stop();
        player.prepareAsync();
        dismissDialing();
    }

    public void startTimer() {
        TextView timerView = findViewById(R.id.tvTimer);
        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerView.setText(" "+(millisUntilFinished / 1000)+ " ");
            }

            public void onFinish() {
                timerView.setText("done!");
                dismissIncoming();
            }

        };

        timer.start();
    }

}