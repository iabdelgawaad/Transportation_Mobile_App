package com.FBLoginSample.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sarah on 5/24/2015.
 */
public class StorageDatabaseAdapter {
    StorageHelper helper ;
    public StorageDatabaseAdapter(Context context ){

        helper = new StorageHelper(context);
    }

    public long insertData(String id , String name , String type , String longtude, String latt , String page_type, String date){
        SQLiteDatabase db =    helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StorageHelper.id, id);
        contentValues.put(StorageHelper.name , name);
        contentValues.put(StorageHelper.type , type);
        contentValues.put(StorageHelper.longtude, longtude);
        contentValues.put(StorageHelper.latt, latt);
        contentValues.put(StorageHelper.page_type, page_type);
        contentValues.put(StorageHelper.date, date);




        long insert_id = db.insert(StorageHelper.TABLE_NAME,null , contentValues);
        return insert_id;
    }
    public String[] getData(String pageType){
        SQLiteDatabase db =    helper.getWritableDatabase();
        String[] coloums={StorageHelper.name};

        Cursor cursor=db.query(StorageHelper.TABLE_NAME,coloums, StorageHelper.page_type + " = '"+pageType+"' ",null,null,null,null);

        String names[]=new String[cursor.getCount()];
        int i=0;
        while(cursor.moveToNext()){
            int index1=cursor.getColumnIndex(StorageHelper.name);
            String name=cursor.getString(index1);
            names[i]=name;
            i++;
        }
        return names;
    }
    public String getDataVersion(String pageType){
        SQLiteDatabase db =    helper.getWritableDatabase();
        String[] coloums={StorageHelper.date};
        Cursor cursor=db.query(StorageHelper.TABLE_NAME, coloums, StorageHelper.page_type + " = '" + pageType + "' ", null, null, null, null);
        if(cursor.moveToLast())
        {
            int index1=cursor.getColumnIndex(StorageHelper.date);
            String lastdate=cursor.getString(index1);
            return lastdate;
        }
        else
            return null;



    }
    public int delete(String pageType,int id){
        SQLiteDatabase db =    helper.getWritableDatabase();
        int count = db.delete(StorageHelper.TABLE_NAME, StorageHelper.page_type +" = '"+pageType +"' AND "+StorageHelper.id+" = "+id , null);
        return count ;
    }
    static class StorageHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "transportationDB";
        private static final String TABLE_NAME = "STATION_CACHE";
        private static final int DATABASE_VERSION =1;
        private static final String id = "st_id";
        private static final String type = "st_type";
        private static final String name = "st_name";
        private static final String longtude = "st_long";
        private static final String latt = "st_latt";
        private static final String date = "st_date";
        private static final String page_type = "page_type";
        private static final String DROP_TABLE = "DROP TABLE " + TABLE_NAME + " IF EXISTS;";
        private Context context;
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" (" + id + " INTEGER  ," + name +
                " VARCHAR(255), " + type + " VARCHAR(255)  , " + longtude + " VARCHAR(255) , " + latt + " VARCHAR(255) , "+page_type+" VARCHAR(255) ,"+date+" VARCHAR(255) ,PRIMARY KEY ("+id+", "+page_type+" ) );";


        public StorageHelper(Context context) {


            // super ( context , database name , custom cursor , version number )
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
            //Message.message(context, "constructor is called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {

                db.execSQL(CREATE_TABLE);
              //  Message.message(context, "onCreate is called");
            } catch (SQLException e) {

                //Message.message(context, "" + e);
            }

        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {

                db.execSQL(DROP_TABLE);
                onCreate(db);
                //Message.message(context, "onUpgrade is called");
            } catch (SQLException e) {
                //Message.message(context, "" + e);
            }
        }
    }
}
