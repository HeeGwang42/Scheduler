package com.example.scheduler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edit_Subject, edit_Time;
    Button add_button, del_button;
    LinearLayout schedule_layout;

    ArrayList<CheckBox> checkBoxList = new ArrayList<>(); // 체크박스를 저장하는 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_Subject = findViewById(R.id.editSubject);
        edit_Time = findViewById(R.id.editTime);
        add_button = findViewById(R.id.buttonAdd);
        del_button = findViewById(R.id.buttonDelete);
        schedule_layout = findViewById(R.id.scheduleLayout);

        // [추가] 버튼을 클릭시 생성되는 이벤트 처리
        add_button.setOnClickListener(v -> {
            String subject = edit_Subject.getText().toString().trim();
            String time = edit_Time.getText().toString().trim();

            if (!subject.isEmpty() && !time.isEmpty()) {
                String text = subject + " / " + time;

                // CheckBox 생성
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(text);
                schedule_layout.addView(checkBox);
                checkBoxList.add(checkBox);

                // 입력란 초기화
                edit_Subject.setText("");
                edit_Time.setText("");
            } else {
                Toast.makeText(this, "과목과 시간을 입력하세요", Toast.LENGTH_SHORT).show();
            }
        });

        // [삭제] 버튼을 클릭시 생성되는 이벤트 처리
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제할 요소를 모아두는 리스트 생성
                List<View> removeList = new ArrayList<>();

                // 모든 자식 뷰를 확인하여 체크된 항목 수집
                for (int i = 0; i < schedule_layout.getChildCount(); i++) {
                    View child = schedule_layout.getChildAt(i);
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (checkBox.isChecked()) removeList.add(checkBox);
                    }
                }

                // 수집된 항목들 제거
                for (View view : removeList) schedule_layout.removeView(view);
            }
        });
    }
}
