package com.example.scheduler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editSubject;
    EditText editTime;
    Button buttonAdd;
    TextView textViewResult;
    String resultText = "수업 목록:\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editSubject = findViewById(R.id.editSubject);
        editTime = findViewById(R.id.editTime);
        buttonAdd = findViewById(R.id.buttonAdd);
        textViewResult = findViewById(R.id.textViewResult);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = editSubject.getText().toString().trim();
                String time = editTime.getText().toString().trim();

                if (!subject.isEmpty() && !time.isEmpty()) {
                    resultText += "- " + subject + " / " + time + "\n";
                    textViewResult.setText(resultText);
                    editSubject.setText("");
                    editTime.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "과목명과 시간을 모두 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}