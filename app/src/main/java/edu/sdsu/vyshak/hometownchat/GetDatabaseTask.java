package edu.sdsu.vyshak.hometownchat;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

/**
 * Created by vysha on 4/15/2017.
 */

public class GetDatabaseTask extends AsyncTask<DBHelper ,String, Void> {
    @Override
    protected Void doInBackground(DBHelper... params) {
        params[0].getWritableDatabase();
        params[0].getReadableDatabase();
       // params[0].getUsers(S);

        return null;
    }

    public GetDatabaseTask() {
        super();
    }

}
