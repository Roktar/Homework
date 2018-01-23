package my.android.projects_2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.support.*;

public class LoginActivity extends AppCompatActivity {

    SQLiteDatabase sql;

    EditText id, pw;

    String iid, ipw;

    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        id = (EditText) findViewById(R.id.inputid);
        pw = (EditText) findViewById(R.id.inputpw);

        DBManager DB = new DBManager(this);
        sql = DB.getReadableDatabase();

        check = false;
    }
    public void log_Form(View v) {
        Cursor cur = sql.query("showapp", null, null, null, null, null, null);
        iid = id.getText().toString();
        ipw = pw.getText().toString();

        if (iid.isEmpty() || ipw.isEmpty())
            Toast.makeText(getApplicationContext(), "정보가 입력되지 않았습니다. \n입력 후 시도하세요", Toast.LENGTH_SHORT).show();
        else {
            while (cur.moveToNext()) {
                String s = cur.getString(0), s2 = cur.getString(1);

                if (!s.isEmpty()) {
                    if (iid.equals(s)) {
                        if (ipw.equals(s2))
                            check = true;
                    } else
                        check = false;
                }

                if (check)
                    break;
            }
        }
        if (check) {
            Intent itn = new Intent(this, MainActivity.class);
            itn.putExtra("userid", iid);
            finish();
            startActivity(itn);
        } else
            Toast.makeText(getApplicationContext(), "등록된 계정이 없거나 비밀번호가 틀렸습니다. \n다시 확인하십시오.", Toast.LENGTH_SHORT).show();
    }

    public void register(View v) {
        Intent itn = new Intent(this, RegisterActivity.class);

        startActivity(itn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
