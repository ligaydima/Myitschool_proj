package com.example.myitschool_proj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "1";
    private Button create_btn;
    private LinearLayout notif_layout;
    private DBInteractor interactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        this.create_btn = findViewById(R.id.create_btn);
        this.notif_layout = findViewById(R.id.scrollable_layout);
        this.create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CreateNotifActivity.class);
                startActivity(i);
            }
        });
        this.interactor = DBInteractor.getInstance(this);
        update_list();

    }
    private void update_list() {
        interactor.deletePassed();
        notif_layout.removeAllViews();
        List<Notification> notifs = interactor.getAll();
        for (final Notification x: notifs) {
            final Button btn = new Button(this);
            btn.setText(x.getTitle());
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, CreateNotifActivity.class);
                    i.putExtra("ID", x.getId());
                    startActivity(i);
                }
            });
            notif_layout.post(new Runnable() {

                public void run() {
                    notif_layout.addView(btn);
                }
            });
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_reminder_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    protected void onRestart() {
        super.onRestart();
        update_list();

    }
}
