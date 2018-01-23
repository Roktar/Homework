package my.android.projects_2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context) {
        super(context, "appList", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table showapp(id text, pw text, appname text, pacname text, count INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void userinsert(Context context, DBManager db, String[] info) {
        SQLiteDatabase sql;
        sql = db.getWritableDatabase();
        boolean isOverlap = false;

        Cursor idcheck = sql.rawQuery("SELECT * FROM showapp WHERE id = '" + info[0] + "';", null);

        while(idcheck.moveToNext()) {
            String s = idcheck.getString(0);

            if(s.equals(info[0])) {
                Toast.makeText(context, " 중복된 계정이 있습니다. \n다른 계정을 사용하세요.", Toast.LENGTH_SHORT).show();
                isOverlap = true;
                break;
            }
        }

        if(!isOverlap) {
            for(int i=0; i<5; i++)
                sql.execSQL("INSERT INTO showapp VALUES ('" + info[0] + "','" + info[1] + "', 'not_insert', 'not_insert', " + i + ");");
            Toast.makeText(context, "사용자가 등록되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void appInfo_insert(Context context, DBManager db, String id, String pacname, String appname, int location) {
        SQLiteDatabase sql = db.getWritableDatabase();
        sql.execSQL("UPDATE showapp SET appname = '" + appname +"', pacname = '" + pacname + "' WHERE id = '" + id + "' AND count = " + location +";");
        Toast.makeText(context, "DB에 앱이 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void Delete(Context context, SQLiteDatabase sql, String id, String pacname, int location) {
        if(!pacname.equals("ALL_DELETE")) {
            sql.execSQL("UPDATE showapp SET appname = 'not_insert', pacname = 'not_insert' WHERE id ='" + id +"' AND count = " + location +";");
            Toast.makeText(context, id + " 사용자의 " + (location +1) + "번째로 등록된 앱을 제거하였습니다.", Toast.LENGTH_SHORT).show();
        } else {
            sql.execSQL("UPDATE showapp SET appname = 'not_insert', pacname = 'not_insert' WHERE id = '" + id + "';");
            Toast.makeText(context, id + " 사용자로 등록된 모든 앱을 제거하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
