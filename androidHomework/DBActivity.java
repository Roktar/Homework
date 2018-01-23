package my.android.projects_2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DBActivity extends AppCompatActivity {

    SQLiteDatabase sql;
    Cursor cursor;

    LinearLayout lin;
    String baseid;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        Intent getIntent;

        DBManager db = new DBManager(this);
        sql = db.getReadableDatabase();

        i=0;

        cursor = sql.query("showapp", null, null, null, null, null, null);

        getIntent = getIntent();

        baseid = getIntent.getStringExtra("id");

        lin = (LinearLayout) findViewById(R.id.testlay);

        while(cursor.moveToNext()) {
            String s1 = cursor.getString(0), s2 = cursor.getString(2), s3 = cursor.getString(3);

            LinearLayout lin2 = new LinearLayout(this);
            lin2.setOrientation(LinearLayout.HORIZONTAL);

            if(s1.equals(baseid))
                lin2.setBackgroundColor(Color.CYAN);

            TextView id = new TextView(this), apname = new TextView(this);
            ImageView img = new ImageView(this);

            settingImage(s3, img);

            id.setText("계정 : " + s1);
            apname.setText(", 등록된 앱 : " + (s2.equals("not_insert") ? "없음" : s2)); // 앱 이름 설정부

            lin2.addView(img);
            lin2.addView(id);
            lin2.addView(apname);
            lin.addView(lin2);

            i++;

            if(i == 5) {
                LinearLayout line = new LinearLayout(this);
                line.setOrientation(LinearLayout.VERTICAL);
                line.setBackgroundColor(Color.BLACK);

                TextView line2 = new TextView(this);
                line2.setText(" ");
                line2.setTextSize(1);

                line.addView(line2);
                lin.addView(line);
                i=0;
            }

        }
    }

    public void back(View v) { // 액티비티 닫기
        finish();
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

    public void settingImage(String Link, ImageView imgViewer) {

        Drawable icon;
        Bitmap bitmap = null;

        try {
            if (!Link.equals("not_insert")) {
                icon = getPackageManager().getApplicationIcon(Link);
                bitmap = drawableToBitmap(icon);

                imgViewer.setImageBitmap(bitmap);
            } else
                imgViewer.setImageResource(R.drawable.questionmark_icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
