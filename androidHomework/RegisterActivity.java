package my.android.projects_2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText id, pw;
    String[] info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        id = (EditText) findViewById(R.id.userid);
        pw = (EditText) findViewById(R.id.userpw);
        info = new String[2];
    }

    public void submit_userInfo(View v) {

        info[0] = id.getText().toString();
        info[1] = pw.getText().toString();

        if(!info[0].isEmpty() || !info[1].isEmpty()) {
            DBManager db = new DBManager(this);

            db.userinsert(this, db, info);

            finish();
        }
        else
            Toast.makeText(getApplicationContext(), "유저 정보를 입력하십시오.", Toast.LENGTH_SHORT).show();
    }

    public void canceled_userInfo (View v) {
        Toast.makeText(getApplicationContext(), "사용자 등록이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
