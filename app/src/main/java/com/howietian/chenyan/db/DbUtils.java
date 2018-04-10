package com.howietian.chenyan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.howietian.chenyan.entities.Message;
import com.howietian.chenyan.entities.User;

import java.util.ArrayList;

/**
 * Created by 83624 on 2018/3/19 0019.
 */

public class DbUtils {
    private static final String QUERY_ALL_MESSAGE = "select * from messages;";
    private static final String INSERT_MESSAGE = "insert or ignore into messages(msg) values(?);";


    public static ContentValues msg2CV(Message msg) {
        ContentValues cv = new ContentValues();
        cv.put("msg", msg.getMessage());
        return cv;
    }

    public static ArrayList<Message> queryAllMsg(Context context) {
        ArrayList<Message> messages = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery(QUERY_ALL_MESSAGE, null);
        while (cursor.moveToNext()) {
            String msg = cursor.getString(cursor.getColumnIndex("msg"));
            Integer id = cursor.getInt(cursor.getColumnIndex("id"));
            Message message = new Message();
            message.setId(id);
            message.setMessage(msg);

            messages.add(message);

        }
        return messages;
    }


    public static void deleteMessage(Context context, int id) {
        String mid = String.valueOf(id);
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        sqLiteDatabase.delete("messages", "id=?", new String[]{mid});
        sqLiteDatabase.close();
    }

    public static boolean isHasMsg(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(QUERY_ALL_MESSAGE, null);
        if (cursor.moveToFirst()) {
            return true;
        } else {
            return false;
        }
    }

    public static void insertMsg(Context context, String msg) {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL(INSERT_MESSAGE,new String[]{msg});


//        database.insertWithOnConflict(DatabaseHelper.TABLE_MESSAGE,null,cv,)
//        Message message = new Message();
//        message.setMessage(msg);
//        ContentValues contentValues = msg2CV(message);
       // database.insert(DatabaseHelper.TABLE_MESSAGE, null, contentValues);

    }
}
