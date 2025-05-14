package com.example.scheduler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText edit_Subject;
    EditText edit_Time;
    Button Add_button;
    TextView textView_Result;
    String result_Text = "수업 목록:\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_Subject = findViewById(R.id.editSubject);
        edit_Time = findViewById(R.id.editTime);
        Add_button = findViewById(R.id.buttonAdd);
        textView_Result = findViewById(R.id.textViewResult);

        Add_button.setOnClickListener(new View.OnClickListener() {  //추가 버튼을 눌렀을 때 이벤트를 처리
            @Override
            public void onClick(View view) {
                String subject = edit_Subject.getText().toString().trim();
                String time = edit_Time.getText().toString().trim();

                if (!subject.isEmpty() && !time.isEmpty()) {
                    result_Text += "- " + subject + " / " + time + "\n";
                    textView_Result.setText(result_Text);
                    edit_Subject.setText("");
                    edit_Time.setText("");
                } else Toast.makeText(MainActivity.this, "과목명과 시간을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}