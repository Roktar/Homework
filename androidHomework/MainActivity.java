package my.android.projects_2;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private Switch run;

    public String[] setLinkPath, appName;
    public boolean[] isLinked;
    public boolean switchStatus;

    TextView login_status;

    Button bt1, bt2, bt3, bt4, bt5, bt1d, bt2d, bt3d, bt4d, bt5d;

    Intent getPackageIntent;
    PackageManager m_pac;

    Intent getItn;
    Intent[] in;
    IntentFilter inf;
    PendingIntent[] p;

    BroadcastReceiver br;

    SQLiteDatabase sql;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        run = (Switch) findViewById(R.id.run_app);
        bt1 = (Button) findViewById(R.id.bt1);
        bt1d = (Button) findViewById(R.id.bt1d);
        bt2 = (Button) findViewById(R.id.bt2);
        bt2d = (Button) findViewById(R.id.bt2d);
        bt3 = (Button) findViewById(R.id.bt3);
        bt3d = (Button) findViewById(R.id.bt3d);
        bt4 = (Button) findViewById(R.id.bt4);
        bt4d = (Button) findViewById(R.id.bt4d);
        bt5 = (Button) findViewById(R.id.bt5);
        bt5d = (Button) findViewById(R.id.bt5d);
        switchStatus = false;

        context = getApplicationContext();
        getItn = getIntent();

        login_status = (TextView) findViewById(R.id.log_status);
        login_status.setText(getItn.getStringExtra("userid"));
        login_status.setTextColor(Color.BLUE);
        setLinkPath = new String[5];
        appName = new String[5];
        isLinked = new boolean[5];

        in = new Intent[6];
        p = new PendingIntent[6];

        inf = new IntentFilter();
        for (int i = 0; i < 5; i++) {
            in[i] = new Intent("bt" + i);
            inf.addAction("bt" + i);
        }

        m_pac = getPackageManager();

        in[5] = new Intent(MainActivity.this, MainActivity.class);
        in[5].addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        inf.addAction(getPackageName());

        for (int i = 0; i < 5; i++)
            p[i] = PendingIntent.getBroadcast(this, i, in[i], PendingIntent.FLAG_UPDATE_CURRENT);
        p[5] = PendingIntent.getActivity(this, 5, in[5], 0);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getExternalFilesDir(null) + File.separator + "LinkedApp.txt"), "MS949"));
            for (int i = 0; i < 5; i++) {
                String s = br.readLine();
                if (!s.isEmpty()) {
                    setLinkPath[i] = s;
                    isLinked[i] = true;
                } else {
                    setLinkPath[i] = "";
                    isLinked[i] = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            for(int i=0; i<5; i++) {
                setLinkPath[i] = "";
                isLinked[i] = false;
            }
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getExternalFilesDir(null) + File.separator + "appName.txt"), "MS949"));
            for (int i = 0; i < 5; i++) {
                String s = br.readLine();
                if (!s.isEmpty())
                    appName[i] = s;
                else
                    appName[i] = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final NotificationManager m_manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification m_noti = new Notification(android.R.drawable.ic_input_add, "Run Quick Launcher", System.currentTimeMillis());
        final RemoteViews m_view = new RemoteViews(getPackageName(), R.layout.noti_design);

        m_view.setOnClickPendingIntent(R.id.first, p[0]);
        m_view.setOnClickPendingIntent(R.id.second, p[1]);
        m_view.setOnClickPendingIntent(R.id.third, p[2]);
        m_view.setOnClickPendingIntent(R.id.fourth, p[3]);
        m_view.setOnClickPendingIntent(R.id.fifth, p[4]);
        m_view.setOnClickPendingIntent(R.id.res, p[5]);

        run.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() { // 노티피케이션 실행부
            public void onCheckedChanged(CompoundButton cb, boolean isRun) {

                switchStatus = isRun;

                if (isRun) {
                    for (int i = 0; i < 5; i++)
                        settingImage(setLinkPath[i], i, m_view);

                    m_noti.contentView = m_view;
                    m_noti.flags = m_noti.FLAG_NO_CLEAR;
                    m_manager.notify(1, m_noti);
                } else {
                    Toast.makeText(getApplicationContext(), "Delete Quick Launcher", Toast.LENGTH_SHORT).show();

                    m_manager.cancel(1);
                }
            }
        });

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int number = -1;

                switch (intent.getAction()) {
                    case "bt0":
                        number = 0;                        break;
                    case "bt1":
                        number = 1;                        break;
                    case "bt2":
                        number = 2;                        break;
                    case "bt3":
                        number = 3;                        break;
                    case "bt4":
                        number = 4;                        break;
                }

                if (isLinked[number] == true) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(setLinkPath[number]);
                    startActivity(launchIntent);
                } else
                    Toast.makeText(getApplicationContext(), (number + 1) + "번에 등록된 앱이 없습니다. \n앱을 먼저 등록하세요", Toast.LENGTH_SHORT).show();

                Intent autoClose = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                getApplicationContext().sendBroadcast(autoClose);
            }
        };
        registerReceiver(br, inf);
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void login(View v) {
        if(login_status.getText().toString().equals("Login")) {
            Intent itn = new Intent(this, LoginActivity.class);
            startActivity(itn);
        } else {
            Intent itn = new Intent(this, LoginActivity.class);
            finish();
            startActivity(itn);
        }
    }

    public void showDatabase(View v) {
        Intent goDB = new Intent(this, DBActivity.class);

        goDB.putExtra("id", login_status.getText().toString());
        startActivity(goDB);
    }

    public void add_app(View v) {
        Intent itn = new Intent(getApplicationContext(), itemListActivity.class);

        if(switchStatus) {
            int id = v.getId();
            switch (id) {
                case R.id.bt1:
                    startActivityForResult(itn, 1);
                    break;
                case R.id.bt2:
                    startActivityForResult(itn, 2);
                    break;
                case R.id.bt3:
                    startActivityForResult(itn, 3);
                    break;
                case R.id.bt4:
                    startActivityForResult(itn, 4);
                    break;
                case R.id.bt5:
                    startActivityForResult(itn, 5);
                    break;
            }
        } else
            Toast.makeText(getApplicationContext(),"상태바가 작동된 상태에서만 가능한 작업입니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getPackageIntent = getIntent();

        DBManager db = new DBManager(this);
        sql = db.getWritableDatabase();
        String s,n;

        if(resultCode == RESULT_OK) {

            s = data.getStringExtra("link");
            n = data.getStringExtra("appName");

            switch (requestCode) {
                case 1:
                    setLinkPath[0] = s;
                    isLinked[0] = true;
                    appName[0] = n;
                    db.appInfo_insert(this, db, login_status.getText().toString(), setLinkPath[0], appName[0], 0);
                    // or db.appInfo_insert(this, db, login_status.getText().toString(), s, n, 0);
                    break;
                case 2:
                    setLinkPath[1] = s;
                    isLinked[1] = true;
                    appName[1] = n;
                    db.appInfo_insert(this, db, login_status.getText().toString(), setLinkPath[1], appName[1], 1);
                    // or db.appInfo_insert(this, db, login_status.getText().toString(), s, n, 1);
                    break;
                case 3:
                    setLinkPath[2] = s;
                    isLinked[2] = true;
                    appName[2] = n;
                    db.appInfo_insert(this, db, login_status.getText().toString(), setLinkPath[2], appName[2], 2);
                    // or db.appInfo_insert(this, db, login_status.getText().toString(), s, n, 2);
                    break;
                case 4:
                    setLinkPath[3] = s;
                    isLinked[3] = true;
                    appName[3] = n;
                    db.appInfo_insert(this, db, login_status.getText().toString(), setLinkPath[3], appName[3], 3);
                    // or db.appInfo_insert(this, db, login_status.getText().toString(), s, n, 3);
                    break;
                case 5:
                    setLinkPath[4] = s;
                    isLinked[4] = true;
                    appName[4] = n;
                    db.appInfo_insert(this, db, login_status.getText().toString(), setLinkPath[4], appName[4], 4);
                    // or db.appInfo_insert(this, db, login_status.getText().toString(), s, n, 4);
                    break;
            }
            refreshNotification();
            Toast.makeText(getApplicationContext(), "앱이 등록되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteApp(View v) {

        SQLiteDatabase sql;
        DBManager db = new DBManager(this);

        sql = db.getWritableDatabase();

        if(switchStatus) {
            switch (v.getId()) {
                case R.id.bt1d:
                    setLinkPath[0] = "";
                    isLinked[0] = false;
                    db.Delete(this, sql, login_status.getText().toString(), setLinkPath[0], 0);
                    break;
                case R.id.bt2d:
                    setLinkPath[1] = "";
                    isLinked[1] = false;
                    db.Delete(this, sql, login_status.getText().toString(), setLinkPath[1], 1);
                    break;
                case R.id.bt3d:
                    setLinkPath[2] = "";
                    isLinked[2] = false;
                    db.Delete(this, sql, login_status.getText().toString(), setLinkPath[2], 2);
                    break;
                case R.id.bt4d:
                    setLinkPath[3] = "";
                    isLinked[3] = false;
                    db.Delete(this, sql, login_status.getText().toString(), setLinkPath[3], 3);
                    break;
                case R.id.bt5d:
                    setLinkPath[4] = "";
                    isLinked[4] = false;
                    db.Delete(this, sql, login_status.getText().toString(), setLinkPath[4], 4);
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "알 수 없는 요청입니다.", Toast.LENGTH_SHORT).show();
            }
            refreshNotification();
            writeLink();
        } else
            Toast.makeText(getApplicationContext(),"상태바가 작동된 상태에서만 가능한 작업입니다.", Toast.LENGTH_SHORT).show();
    }

    public void resetLink(View v) {
        final DBManager db = new DBManager(this);
        sql = db.getWritableDatabase();

        if(switchStatus) {
            new AlertDialog.Builder(this)
                    .setTitle("전체 앱 해제")
                    .setIcon(R.drawable.questionmark_icon)
                    .setMessage("본 기능을 수행하시겠습니까? \n[삭제]를 누르면 등록된 모든 앱을 해제합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < 5; i++) {
                                setLinkPath[i] = "";
                                isLinked[i] = false;
                                db.Delete(context, sql, login_status.getText().toString(), "ALL_DELETE", -1);

                            }
                            writeLink();
                            refreshNotification();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } else
            Toast.makeText(getApplicationContext(),"상태바가 작동된 상태에서만 가능한 작업입니다.", Toast.LENGTH_SHORT).show();
    }

    public void settingImage(String Link, int number, RemoteViews m_view) { // 패키지네임과 버튼 번호를 이용하여 이미지 재설정하는 함수

        Drawable icon;
        Bitmap bitmap = null;

        try {
            if(!Link.isEmpty()) {
                icon = getPackageManager().getApplicationIcon(Link);
                bitmap = drawableToBitmap(icon);
            }
        } catch(PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(Link.isEmpty()) {
            switch(number) {
                case 0:
                    m_view.setImageViewResource(R.id.first, R.drawable.button); break;
                case 1:
                    m_view.setImageViewResource(R.id.second, R.drawable.button); break;
                case 2:
                    m_view.setImageViewResource(R.id.third, R.drawable.button); break;
                case 3:
                    m_view.setImageViewResource(R.id.fourth, R.drawable.button); break;
                case 4:
                    m_view.setImageViewResource(R.id.fifth, R.drawable.button); break;
                default:
                    Toast.makeText(getApplicationContext(), "잘못된 접근입니다.", Toast.LENGTH_SHORT).show(); break;
            }
        } else {
            switch(number) {
                case 0:
                    m_view.setImageViewBitmap(R.id.first, bitmap);break;
                case 1:
                    m_view.setImageViewBitmap(R.id.second, bitmap);break;
                case 2:
                    m_view.setImageViewBitmap(R.id.third, bitmap);break;
                case 3:
                    m_view.setImageViewBitmap(R.id.fourth, bitmap);break;
                case 4:
                    m_view.setImageViewBitmap(R.id.fifth, bitmap);break;
                default:
                    Toast.makeText(getApplicationContext(), "잘못된 접근입니다.", Toast.LENGTH_SHORT).show(); break;
            }
        }
    }

    public void refreshNotification() {
        NotificationManager m_manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification m_noti = new Notification(android.R.drawable.ic_input_add, "Refresh Quick Launcher", System.currentTimeMillis());
        RemoteViews m_view = new RemoteViews(this.getPackageName(), R.layout.noti_design);

        m_manager.cancel(1);
        m_view.setOnClickPendingIntent(R.id.first, p[0]);
        m_view.setOnClickPendingIntent(R.id.second, p[1]);
        m_view.setOnClickPendingIntent(R.id.third, p[2]);
        m_view.setOnClickPendingIntent(R.id.fourth, p[3]);
        m_view.setOnClickPendingIntent(R.id.fifth, p[4]);
        m_view.setOnClickPendingIntent(R.id.res, p[5]);

        for(int i=0; i<5; i++)
            settingImage(setLinkPath[i], i, m_view);

        m_noti.contentView = m_view;
        m_noti.flags = m_noti.FLAG_NO_CLEAR;
        m_manager.notify(1, m_noti);
    }

    public void writeLink() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getExternalFilesDir(null) + File.separator + "LinkedApp.txt"), "MS949"));
            for (int i = 0; i < 5; i++)
                bw.write(setLinkPath[i] + "\n");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getExternalFilesDir(null) + File.separator + "appName.txt"), "MS949"));
            for (int i = 0; i < 5; i++)
                bw.write(appName[i] + "\n");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        super.onDestroy();

        NotificationManager m_manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        m_manager.cancel(1);

        writeLink();

        unregisterReceiver(br);
    }
}