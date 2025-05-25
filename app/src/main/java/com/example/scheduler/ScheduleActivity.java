package com.example.scheduler;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    EditText edit_Subject, edit_StartTime, edit_EndTime, edit_detail;
    Button save_button, delete_button, add_button, del_button, det_button;
    LinearLayout schedule_layout;
    Spinner edit_Day;
    LinearLayout layoutMonday, layoutTuesday, layoutWednesday, layoutThursday,
            layoutFriday, layoutSaturday, layoutSunday;

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

        layoutMonday = findViewById(R.id.layoutMonday);
        layoutTuesday = findViewById(R.id.layoutTuesday);
        layoutWednesday = findViewById(R.id.layoutWednesday);
        layoutThursday = findViewById(R.id.layoutThursday);
        layoutFriday = findViewById(R.id.layoutFriday);
        layoutSaturday = findViewById(R.id.layoutSaturday);
        layoutSunday = findViewById(R.id.layoutSunday);




        // 수정 모드일 경우 기존 데이터 불러오기
        if (editIndex != -1) {
            try {
                JSONArray array = ScheduleStorage.loadJsonArray(this, dateMillis);
                if (editIndex < array.length()) {
                    JSONObject item = array.getJSONObject(editIndex);
                    edit_Subject.setText(item.getString("subject"));
                    String day = item.getString("day");
                    String[] days = getResources().getStringArray(R.array.days_array);
                    for (int i = 0; i < days.length; i++) {
                        if (days[i].equals(day)) {
                            edit_Day.setSelection(i);
                            break;
                        }
                    }
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
            String day = edit_Day.getSelectedItem().toString();
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
            String day = edit_Day.getSelectedItem().toString();
            String startTime = edit_StartTime.getText().toString().trim();
            String endTime = edit_EndTime.getText().toString().trim();
            String detail = edit_detail.getText().toString().trim();

            if (subject.isEmpty() || day.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "빈칸을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            //입력한 선택한 요일을 리턴해줄 getLayoutByDay함수
            LinearLayout targetLayout = getLayoutByDay(day);

            if (targetLayout != null) {
                String text = day + "/" + subject + "\n" + startTime + " ~ " + endTime;
                CheckBox cb = new CheckBox(this);
                cb.setText(text);
                cb.setTag(detail);

                insertSortedByTime(targetLayout, cb, startTime);
            }

            edit_Subject.setText("");
            edit_Day.setSelection(0);
            edit_StartTime.setText("");
            edit_EndTime.setText("");
            edit_detail.setText("");
        });

        // ✅ [삭제] 버튼 기능 (체크된 것만 삭제)
        del_button.setOnClickListener(v -> {
            deleteCheckedItems(layoutMonday);
            deleteCheckedItems(layoutTuesday);
            deleteCheckedItems(layoutWednesday);
            deleteCheckedItems(layoutThursday);
            deleteCheckedItems(layoutFriday);
            deleteCheckedItems(layoutSaturday);
            deleteCheckedItems(layoutSunday);
        });

        // ✅ [상세보기] 버튼 기능
        det_button.setOnClickListener(v -> {
            StringBuilder info = new StringBuilder();

            // 요일별 레이아웃 ID 배열로 가져오기
            int[] dayLayoutIds = {
                    R.id.layoutMonday,
                    R.id.layoutTuesday,
                    R.id.layoutWednesday,
                    R.id.layoutThursday,
                    R.id.layoutFriday,
                    R.id.layoutSaturday,
                    R.id.layoutSunday
            };

            // 각 요일별 레이아웃을 순회하며 체크된 항목 찾기
            for (int layoutId : dayLayoutIds) {
                LinearLayout dayLayout = findViewById(layoutId);
                for (int i = 0; i < dayLayout.getChildCount(); i++) {
                    View child = dayLayout.getChildAt(i);
                    if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                        String detail = (String) ((CheckBox) child).getTag();
                        info.append(((CheckBox) child).getText().toString())
                                .append(" : ")
                                .append(detail)
                                .append("\n");
                    }
                }
            }

            // 결과 출력
            if (info.length() > 0) {
                Toast.makeText(this, info.toString().trim(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "선택된 수업이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        edit_StartTime.setOnClickListener(v -> showTimePicker(edit_StartTime));
        edit_EndTime.setOnClickListener(v -> showTimePicker(edit_EndTime));


    }

    private LinearLayout getLayoutByDay(String day) {
        switch (day) {
            case "월": return layoutMonday;
            case "화": return layoutTuesday;
            case "수": return layoutWednesday;
            case "목": return layoutThursday;
            case "금": return layoutFriday;
            case "토": return layoutSaturday;
            case "일": return layoutSunday;
            default: return null;
        }
    }

    private void deleteCheckedItems(LinearLayout layout) {
        for (int i = layout.getChildCount() - 1; i >= 0; i--) {
            View child = layout.getChildAt(i);
            if (child instanceof CheckBox && ((CheckBox) child).isChecked()) {
                layout.removeViewAt(i);
            }
        }
    }

    private void showTimePicker(EditText targetEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
            targetEditText.setText(time);
        }, hour, minute, true);

        dialog.show();
    }

    private void insertSortedByTime(LinearLayout layout, CheckBox newCheckBox, String startTime) {
        int insertIndex = 0;
        int newTime = parseTimeToMinutes(startTime);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof CheckBox) {
                String text = ((CheckBox) view).getText().toString();
                String[] parts = text.split("\n");
                if (parts.length >= 2) {
                    String[] times = parts[1].split("~");
                    if (times.length >= 1) {
                        int existingTime = parseTimeToMinutes(times[0].trim());
                        if (newTime < existingTime) {
                            insertIndex = i;
                            break;
                        }
                    }
                }
            }
            insertIndex = i + 1;
        }

        layout.addView(newCheckBox, insertIndex);
    }

    private int parseTimeToMinutes(String time) {
        String[] parts = time.split(":");
        if (parts.length == 2) {
            try {
                int hour = Integer.parseInt(parts[0].trim());
                int minute = Integer.parseInt(parts[1].trim());
                return hour * 60 + minute;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}