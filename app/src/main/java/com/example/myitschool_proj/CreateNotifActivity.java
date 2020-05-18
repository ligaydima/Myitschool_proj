package com.example.myitschool_proj;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.Calendar;
public class CreateNotifActivity extends AppCompatActivity implements View.OnClickListener {
    private Button et_date_btn, et_time_btn, submit_btn, delete_btn;
    private EditText et_date, et_time, et_title, et_description, et_notif_time;
    private int notif_id;
    private DBInteractor interactor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notif);
        createNotificationChannel();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        this.et_title = findViewById(R.id.et_title);
        this.et_description = findViewById(R.id.et_description);
        this.et_date_btn = findViewById(R.id.et_date_btn);
        this.et_time_btn = findViewById(R.id.et_time_btn);
        this.et_date = findViewById(R.id.et_date);
        this.et_time = findViewById(R.id.et_time);
        this.interactor = DBInteractor.getInstance(this);
        this.et_notif_time = findViewById(R.id.et_notif_time);
        this.submit_btn = findViewById(R.id.submit_btn);
        this.delete_btn = findViewById(R.id.delete_btn);
        notif_id = getIntent().getIntExtra("ID", -1);
        if (notif_id != -1) {
            try {
                Notification notification = interactor.getOne(notif_id);
                Log.e("deb", "got item");
                et_title.setText(notification.getTitle());
                et_description.setText(notification.getDescription());
                Log.e("deb", notification.getNotif_time().toString());
//                et_time.setText(String.format("%d:%d", hour, minute));
                String td = notification.getTime().toString();
                et_date.setText(td.substring(0, 10));
                et_time.setText(td.substring(11, 16));
                String t = ((notification.getTime().getTime() - notification.getNotif_time().getTime()) / 60 / 1000) + "";
                et_notif_time.setText(t);
                this.delete_btn.setOnClickListener(this);
            } catch (Exception e) {
                Toast.makeText(this, "Уведомление не найдено" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            this.delete_btn.setVisibility(View.INVISIBLE);
        }
        this.et_date_btn.setOnClickListener(this);
        this.et_time_btn.setOnClickListener(this);
        this.submit_btn.setOnClickListener(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_reminder_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_reminder_name), name, importance);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private Pair<PendingIntent, PendingIntent> get_pi_for_notif(Notification cur_notif) {
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("text", cur_notif.getTitle());
        intent.putExtra("title", "Осталось " + (cur_notif.getTime().getTime() - cur_notif.getNotif_time().getTime()) / 1000/60 + " минут до события");
        intent.putExtra("description", cur_notif.getTitle());
        intent.setAction("warning" + cur_notif.getId());
        Intent intent2 = new Intent(this, ReminderBroadcast.class);
        intent2.putExtra("title", "Началось событие");
        intent2.putExtra("text", cur_notif.getTitle());
        intent2.putExtra("description", cur_notif.getDescription());
        intent2.setAction("alarm" + cur_notif.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, cur_notif.getId() * 2 + 1, intent, 0);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, cur_notif.getId() * 2 + 1, intent2, 0);
        return new Pair<>(pendingIntent, pendingIntent2);
    }
    private void create_notifications(Notification cur_notif) {
        try {
            Pair<PendingIntent, PendingIntent> p = get_pi_for_notif(cur_notif);
            PendingIntent pendingIntent = p.first;
            PendingIntent pendingIntent2 = p.second;
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cur_notif.getTime().getTime() + cur_notif.getId() % 1000, pendingIntent2);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cur_notif.getNotif_time().getTime() + cur_notif.getId() % 1000, pendingIntent);
        } catch (Exception e) {
            Toast.makeText(this, "LOOOL", Toast.LENGTH_SHORT).show();
        }
    }
    private void delete_notifications(Notification cur_notif) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Pair<PendingIntent, PendingIntent> p = get_pi_for_notif(cur_notif);
            PendingIntent pendingIntent = p.first;
            PendingIntent pendingIntent2 = p.second;
            alarmManager.cancel(pendingIntent);
            alarmManager.cancel(pendingIntent2);
        } catch (Exception e) {
            Log.e("NOTIF", "er del");
        }
    }
    public void onClick(View v) {
        if (v == et_date_btn) {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            et_date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == et_time_btn) {

            Log.e("et", "time");
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            et_time.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        if (v == submit_btn) {
            try {
                int minutes_notif = Integer.parseInt(this.et_notif_time.getText().toString());

                Timestamp cur_ts = Timestamp.valueOf(this.et_date.getText().toString() + " " + this.et_time.getText().toString() + ":00");
                Notification notification = new Notification(
                        this.et_title.getText().toString(),
                        cur_ts,
                        new Timestamp(cur_ts.getTime() - 60 * minutes_notif * 1000),
                        this.et_description.getText().toString()
                );
                if (notif_id != -1) {
                    if (!interactor.deleteOne(notif_id)) throw new RuntimeException();
                    notification.setId(notif_id);
                    delete_notifications(notification);
                }
                long code = interactor.addOne(notification);
                if (code == -1) throw new RuntimeException();
                notification.setId((int) code);
                create_notifications(notification);
                Log.d("NOTIF", notification.toString());
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка" , Toast.LENGTH_SHORT).show();
            }
        }
        if (v == delete_btn) {
            try {
                Notification notification = interactor.getOne(notif_id);
                if (!interactor.deleteOne(notif_id)) throw new RuntimeException();
                delete_notifications(notification);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
