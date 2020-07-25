package com.example.ioboardcontroller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "IOBoards.db";
    public String dbPath = "";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        dbPath = context.getDatabasePath(DATABASE_NAME).toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // TODO Auto-generated method stub
        db.execSQL("create table controllers (id integer primary key, name char(20), hostname char(15), username char(20), password char(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS controllers");
        onCreate(db);
    }

    public ArrayList<NewController> getControllers()
    {
        ArrayList<NewController> controllers = new ArrayList<NewController>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from controllers", null );
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            controllers.add(new NewController(res.getInt(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4), false));
            res.moveToNext();
        }
        return controllers;
    }

    public Integer saveNewController(NewController c)
    {
        Integer id = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select max(id) as id from controllers", null );
        res.moveToFirst();
        while(!res.isAfterLast())
        {
            id = res.getInt(0);
            res.moveToNext();
        }
        id++;
        db.execSQL("insert into controllers values(" + id.toString() + ", '" + c.getName() + "' ,'" + c.getHostname() + "', '" +  c.getUsername() + "', '" + c.getPassword() + "')");

        return(id);
    }

    public void deleteController(NewController c)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from controllers where id = " + c.getID().toString());
    }

    public void updateController(NewController c)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update controllers set name = '" + c.getName() + "', hostname = '" + c.getHostname() + "', username = '" + c.getUsername() + "', password = '" + c.getPassword() + "' where id = " + c.getID().toString());
    }

    public void setPassword(NewController c)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update controllers set password = '" + c.getPassword() + "' where id = " + c.getID().toString());
    }
}

