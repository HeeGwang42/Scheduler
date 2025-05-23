package com.example.scheduler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleActivity extends AppCompatActivity {

    EditText edit_Subject, edit_StartTime, edit_EndTime, edit_detail, edit_Day;
    Button save_button, delete_button, add_button, del_button, det_button;
    LinearLayout schedule_layout;

    long dateMillis;
    int editIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        dateMillis = getIntent().getLongExtra("selectedDateMillis", -1);
        editIndex = getIntent().getIntExtra("editIndex", -1);

        edit_Subject = findViewById(R.id.editSubject);
        edit_StartTime = findViewById(R.id.editStartTime);
        edit_EndTime = findViewById(R.id.editEndTime);
        edit_Day = findViewById(R.id.editDay);
        edit_detail = findViewById(R.id.editDetail);
        save_button = findViewById(R.id.buttonSave);
        delete_button = findViewById(R.id.buttonDeleteSchedule);

        add_button = findViewById(R.id.buttonAdd);
        del_button = findViewById(R.id.buttonDelete);
        det_button = findViewById(R.id.buttonDetail);
        schedule_layout = findViewById(R.id.scheduleLayout);

        // 수정 모드일 경우 기존 데이터 불러오기
        if (editIndex != -1) {
            try {
                JSONArray array = ScheduleStorage.loadJsonArray(this, dateMillis);
                if (editIndex < array.length()) {
                    JSONObject item = array.getJSONObject(editIndex);
                    edit_Subject.setText(item.getString("subject"));
                    edit_Day.setText(item.getString("day"));
                    edit_StartTime.setText(item.getString("start"));
                    edit_EndTime.setText(item.getString("end"));
                    edit_detail.setText(item.getString("detail"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 저장 버튼
        save_button.setOnClickListener(v -> {
            String subject = edit_Subject.getText().toString().trim();
            String day = edit_Day.getText().toString().trim();
            String startTime = edit_StartTime.getText().toString().trim();
            String endTime = edit_EndTime.getText().toString().trim();
            String detail = edit_detail.getText().toString().trim();

            boolean saved = false;

            // ✅ 직접 입력해서 저장하는 경우
            if (!subject.isEmpty() && !day.isEmpty() && !startTime.isEmpty() && !endTime.isEmpty()) {
                if (editIndex != -1) {
                    ScheduleStorage.update(this, dateMillis, editIndex, subject, day, startTime, endTime, detail);
                } else {
                    ScheduleStorage.save(this, dateMillis, subject, day, startTime, endTime, detail);
                }
                saved = true;
            } else {
                // ✅ 체크박스를 통한 저장 (여러 개 지원)
                for (int i = 0; i < schedule_layout.getChildCount(); i++) {
                    View child = schedule_layout.getChildAt(i);
                    if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                        CheckBox cb = (CheckBox) child;
                        String[] lines = cb.getText().toString().split("\n");
                        if (lines.length >= 2) {
                            String[] top = lines[0].split("/");
                            String[] times = lines[1].split("~");

                            if (top.length >= 2 && times.length >= 2) {
                                String cbDay = top[0].trim();
                                String cbSubject = top[1].trim();
                                String cbStart = times[0].trim();
                                String cbEnd = times[1].trim();
                                String cbDetail = cb.getTag() != null ? cb.getTag().toString() : "";

                                ScheduleStorage.save(this, dateMillis, cbSubject, cbDay, cbStart, cbEnd, cbDetail);
                                saved = true;
                            }
                        }
                    }
                }
            }

            if (saved) {
                Toast.makeText(this, "일정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "빈칸을 모두 입력하거나 체크박스를 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        });


        // 일정 삭제 버튼
        delete_button.setOnClickListener(v -> {
            if (editIndex != -1) {
                ScheduleStorage.delete(this, dateMillis, editIndex);
                Toast.makeText(this, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "삭제할 항목이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ [추가] 버튼 기능
        add_button.setOnClickListener(v -> {
            String subject = edit_Subject.getText().toString().trim();
            String day = edit_Day.getText().toString().trim();
            String startTime = edit_StartTime.getText().toString().trim();
            String endTime = edit_EndTime.getText().toString().trim();
            String detail = edit_detail.getText().toString().trim();

            if (subject.isEmpty() || day.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "빈칸을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String text = day + "/" + subject + "\n" + startTime + " ~ " + endTime;
            CheckBox cb = new CheckBox(this);
            cb.setText(text);
            cb.setTag(detail);
            schedule_layout.addView(cb);

            edit_Subject.setText("");
            edit_Day.setText("");
            edit_StartTime.setText("");
            edit_EndTime.setText("");
            edit_detail.setText("");
        });

        // ✅ [삭제] 버튼 기능 (체크된 것만 삭제)
        del_button.setOnClickListener(v -> {
            for (int i = schedule_layout.getChildCount() - 1; i >= 0; i--) {
                View child = schedule_layout.getChildAt(i);
                if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                    schedule_layout.removeViewAt(i);
                }
            }
        });

        // ✅ [상세보기] 버튼 기능
        det_button.setOnClickListener(v -> {
            StringBuilder info = new StringBuilder();
            for (int i = 0; i < schedule_layout.getChildCount(); i++) {
                View child = schedule_layout.getChildAt(i);
                if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                    String detail = (String) ((CheckBox) child).getTag();
                    info.append(((CheckBox) child).getText().toString()).append(" : ").append(detail).append("\n");
                }
            }
            if (info.length() > 0) {
                Toast.makeText(this, info.toString().trim(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "선택된 수업이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
