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

    EditText edit_Subject, edit_Time, edit_detail;
    Button add_button, del_button, det_button; //추가버튼, 삭제버튼, 상세보기 버튼
    LinearLayout schedule_layout;

    ArrayList<CheckBox> checkBoxList = new ArrayList<>(); // 체크박스를 저장하는 리스트
    ArrayList<String> detailList = new ArrayList<>(); // 각 항목의 상세정보를 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_Subject = findViewById(R.id.editSubject);
        edit_Time = findViewById(R.id.editTime);
        edit_detail = findViewById(R.id.editDetail);
        add_button = findViewById(R.id.buttonAdd);
        del_button = findViewById(R.id.buttonDelete);
        det_button = findViewById(R.id.buttonDetail);
        schedule_layout = findViewById(R.id.scheduleLayout);

        // [추가] 버튼을 클릭시 생성되는 이벤트 처리
        add_button.setOnClickListener(v -> {
            String subject = edit_Subject.getText().toString().trim();
            String time = edit_Time.getText().toString().trim();
            String detail = edit_detail.getText().toString().trim();

            if (!subject.isEmpty() && !time.isEmpty() && !detail.isEmpty()) {
                String text = subject + " / " + time;

                // CheckBox 생성
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(text);
                schedule_layout.addView(checkBox);
                checkBoxList.add(checkBox);
                detailList.add(detail); // 상세정보 저장

                // 입력란 초기화
                edit_Subject.setText("");
                edit_Time.setText("");
                edit_detail.setText("");
            } else {
                Toast.makeText(this, "빈칸을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
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

        // [상세보기] 버튼을 클릭시 생성되는 이벤트 처리
        det_button.setOnClickListener(v -> {
            StringBuilder info = new StringBuilder(); //append로 문자열을 추가하는 string + 클래스

            //체크된 체크박스를 식별
            for (int i = 0; i < checkBoxList.size(); i++) {
                CheckBox checkBox = checkBoxList.get(i);
                if (checkBox.isChecked()) {
                    info.append(checkBox.getText().toString()).append(" : ");
                    info.append(detailList.get(i)).append("\n");
                }
            }

            //추가된 info가 있는지 확인 (체크된 체크박스가 없을시 else)
            if (info.length() > 0) {
                Toast.makeText(MainActivity.this, info.toString().trim(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "선택된 수업이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
