package com.example.scheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CalendarView calendarView;
    Button createScheduleButton;
    LinearLayout scheduleListLayout;
    long selectedDateMillis;
    private static final int REQ_SCHEDULE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        createScheduleButton = findViewById(R.id.createScheduleButton);
        scheduleListLayout = findViewById(R.id.scheduleListLayout); // ScrollView 아래에 LinearLayout 있다고 가정

        selectedDateMillis = System.currentTimeMillis();
        drawSchedule();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDateMillis = calendar.getTimeInMillis();
            drawSchedule();
        });

        createScheduleButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
            intent.putExtra("selectedDateMillis", selectedDateMillis);
            startActivityForResult(intent, REQ_SCHEDULE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SCHEDULE && resultCode == RESULT_OK) {
            drawSchedule();
        }
    }

    private void drawSchedule() {
        scheduleListLayout.removeAllViews();
        List<String> items = ScheduleStorage.loadAll(this, selectedDateMillis);

        if (items.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("일정 없음");
            scheduleListLayout.addView(tv);
        } else {
            for (int i = 0; i < items.size(); i++) {
                String text = items.get(i);
                final int index = i;

                TextView tv = new TextView(this);
                tv.setText("• " + text);
                tv.setPadding(8, 8, 8, 8);
                tv.setTextSize(16);
                tv.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                    intent.putExtra("selectedDateMillis", selectedDateMillis);
                    intent.putExtra("editIndex", index);  // 몇 번째 일정인지 전달
                    startActivityForResult(intent, REQ_SCHEDULE);
                });
                scheduleListLayout.addView(tv);
            }
        }
    }
}
