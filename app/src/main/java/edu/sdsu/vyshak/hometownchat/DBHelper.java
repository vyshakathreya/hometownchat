package edu.sdsu.vyshak.hometownchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by vysha on 3/31/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "name.db";
    private static final int DATABASE_VERSION = 1;
    private static final String FRIENDS_COLUMN_NAME = "nickname";
    private static final String FRIENDS_COLUMN_COUNTRY="country";
    private static final String FRIENDS_COLUMN_STATE="state";
    private static final String FRIENDS_COLUMN_CITY="city";
    private static final String FRIENDS_COLUMN_EMAIL="email";
    private static final String FRIENDS_COLUMN_LATITUDE="latitude";
    private static final String FRIENDS_COLUMN_LONGITUDE="longitude";
    private static final String FRIENDS_COLUMN_YEAR="year";

    public DBHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( "CREATE TABLE IF NOT EXISTS friends " +
        "(nickname text primary key, email text, city text, state text, country text,latitude real, longitude real, year integer)"
        );
    }

    public boolean insertUser (String nickname, String email, String city, String state, String country,double latitude,
                                  double longitude,int year) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("nickname", nickname);
        contentValues.put("email", email);
        contentValues.put("city", city);
        contentValues.put("state",state);
        contentValues.put("country", country);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("year", year);
        db.insert("friends", null, contentValues);
        return true;
    }

    public ArrayList<User> getUsers(String query) {
        ArrayList<User> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( query, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            User user = new User();
            user.setNickname(res.getString(res.getColumnIndex(FRIENDS_COLUMN_NAME)));
            user.setYear(res.getString(res.getColumnIndex(FRIENDS_COLUMN_YEAR)));
            user.setState(res.getString(res.getColumnIndex(FRIENDS_COLUMN_STATE)));
            user.setCountry(res.getString(res.getColumnIndex(FRIENDS_COLUMN_COUNTRY)));
            user.setLatitude(res.getDouble(res.getColumnIndex(FRIENDS_COLUMN_LATITUDE)));
            user.setLongitude(res.getDouble(res.getColumnIndex(FRIENDS_COLUMN_LONGITUDE)));
            array_list.add(user);
            res.moveToNext();
        }
        //res.close();
        return array_list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
