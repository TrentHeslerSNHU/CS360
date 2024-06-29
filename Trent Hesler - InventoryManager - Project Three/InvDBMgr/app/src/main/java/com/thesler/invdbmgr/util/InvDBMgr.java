package com.thesler.invdbmgr.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class InvDBMgr extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    private SQLiteDatabase invDB = getWritableDatabase();

    public InvDBMgr(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a table for inventory
        db.execSQL("CREATE TABLE IF NOT EXISTS inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, itemName TEXT, itemCount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop and recreate table on upgrade
        db.execSQL("drop table if exists inventory");
        onCreate(db);
    }

    public void drop(){
        invDB.execSQL("drop table if exists inventory");
        onCreate(invDB);
    }

    private boolean itemExists(String itemName){
        SQLiteDatabase db = getReadableDatabase();

        //Check for item with supplied name
        String sql = "select * from inventory where itemName = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { itemName });

        //Return true if found, false if not
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public long addItem(String itemName, int itemCount){
        //Create a new item and store provided values in it
        ContentValues newItem = new ContentValues();
        newItem.put("itemName",itemName);
        newItem.put("itemCount",itemCount);

        //Check if the item already exists, if so add supplied quantity
        //to existing quantity
        if (itemExists(itemName)){
            updateItemCount(itemName,getItemCount(itemName) + itemCount);
            return 0;
        }

        //If it doesn't already exist, add the item entry
        return invDB.insert("inventory", null, newItem);
    }

    public int deleteItem(String itemName){
        return invDB.delete("inventory","itemName = ?",new String[]{itemName});
    }

    public int updateItemName(String itemName, String updateName){
        ContentValues updateItem = new ContentValues();
        updateItem.put("itemName",updateName);
        return invDB.update("inventory",updateItem,"itemName = ?",new String[] {itemName});
    }

    public int updateItemCount(String itemName, int updateCount){
        ContentValues updateItem = new ContentValues();
        updateItem.put("itemCount",updateCount);
        return invDB.update("inventory",updateItem,"itemName = ?",new String[] {itemName});
    }

    public long getNumEntries(){
        return DatabaseUtils.queryNumEntries(invDB,"inventory");
    }

    public int getItemCount(String itemName) {
        SQLiteDatabase db = getReadableDatabase();
        int value = 0;

        //Check for item in db
        String sql = "select * from inventory where itemName = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { itemName });

        //If found, get quantity
        if (cursor.moveToFirst()) {
            value = cursor.getInt(2);
        }
        cursor.close();

        //Return quantity or 0 (if nonexistent)
        return value;
    }

    public List<InventoryItem> getItems(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<InventoryItem> results = new ArrayList<>();
        ArrayList<InventoryItem> outResults = new ArrayList<>();

        //Get all items from the table
        String sql = "select * from inventory order by itemName";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int count = cursor.getInt(2);

                //If count is 0 or less, put on outs list
                if(count <= 0)
                    outResults.add(new InventoryItem(id,name,count));
                //Otherwise put it on the regular list
                else
                    results.add(new InventoryItem(id,name,count));

            } while (cursor.moveToNext());
        }

        //Combine outs and regular items lists, placing outs at the top
        outResults.addAll(results);
        cursor.close();

        //Return a list of results
        return outResults;
    }

    public List<InventoryItem> getOuts(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<InventoryItem> results = new ArrayList<>();

        //Find all objects with a count less than 1
        String sql = "select * from inventory where itemCount < ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"1"});

        //Put all found objects in a list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int count = cursor.getInt(2);
                results.add(new InventoryItem(id,name,count));

            } while (cursor.moveToNext());
        }
        cursor.close();

        //Return the list
        return results;
    }
}