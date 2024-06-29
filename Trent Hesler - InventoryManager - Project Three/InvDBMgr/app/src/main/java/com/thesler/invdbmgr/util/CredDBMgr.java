package com.thesler.invdbmgr.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.thesler.invdbmgr.UserLogin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public class CredDBMgr extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "users.db";
    private static final int VERSION = 1;

    public CredDBMgr(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a database table for user credentials
        db.execSQL("create table if not exists credentials" +
                " ( uid integer primary key autoincrement, " +
                " username text, " +
                " password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        //Drop and recreate the table on upgrade
        db.execSQL("drop table if exists credentials");
        onCreate(db);
    }

    public boolean userExists(String uname){
        SQLiteDatabase db = getReadableDatabase();

        //Query database for provided username
        String query = "select * from credentials where username = ?";
        Cursor cursor = db.rawQuery(query,new String[]{uname});

        //Return true if found, false is not found
        if (cursor.moveToFirst()){
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    private static String hashPasswd(String passwd) throws NoSuchAlgorithmException {
        String salt = "[B@2f57e19";
        String hashedpw = "";

        //Try to hash password with SHA-256 encryption
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(passwd.getBytes());
            for (int i = 0; i < bytes.length; i++){
                String temp = Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
                hashedpw += temp;
            }
        } catch (NoSuchAlgorithmException e) {
            //If for some godforsaken reason an API 34 device doesn't have SHA-256,
            //store the password in plaintext. (Ideally, we would try other algorithms,
            //but for simplicity's sake we're just gonna go with plaintext.)
            Log.d("DEBUG",e.getMessage());
            hashedpw = passwd;
        }
        //Return the hashed password
        return hashedpw;
    }

    public boolean passwordMatches(Context context, String username, String passwd) throws NoSuchAlgorithmException {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from credentials where username = ?";
        Cursor cursor = db.rawQuery(query,new String[]{username});

        //Hash supplied password
        String hashedPasswd = hashPasswd(passwd);

        //Compare hash of supplied password with hash stored in DB
        if (cursor.moveToFirst()){
            String dbpw = cursor.getString(2);
            if (Objects.equals(dbpw, hashedPasswd)){
                return true;
            } else {
                Toast.makeText(context,"ERROR: Incorrect password!",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context,"ERROR: User not found!",Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        return false;
    }

    private boolean passwordMeetsReqs(String password){

        //Make sure password is at least 6 chars
        if (password.length() >= 6){
            int upper = 0;
            int num = 0;
            int symbol = 0;

            //Make sure password has at least one uppercase letter,
            //one number, and one symbol
            for(int i=0; i < password.length(); i++){
                char c = password.charAt(i);
                if (Character.isDigit(c))
                    num++;
                if (Character.isUpperCase(c))
                    upper++;
                if (!Character.isLetterOrDigit(c) && !Character.isSpace(c))
                    symbol++;
                if (Character.isSpace(c))
                    return false;
            }

            //Return results
            return upper >= 1 && num >= 1 && symbol >= 1;
        }
        return false;
    }

    public boolean register(Context context, String username, String password) throws NoSuchAlgorithmException {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Make sure username isn't already taken
        if(userExists(username)){
            alert.setTitle("User already exists");
            alert.setMessage("That username is already in use.");
            alert.setPositiveButton("OK",null);
            alert.show();
        } else {
            //Nake sure username is long enough
            if (username.length() >= 4) {
                //Make sure supplied password meets requirements
                if (passwordMeetsReqs(password)) {

                    //All checks passed? Put it in the database!
                    SQLiteDatabase db = getWritableDatabase();
                    ContentValues newUser = new ContentValues();
                    newUser.put("username", username);
                    newUser.put("password", hashPasswd(password));
                    db.insert("credentials", null, newUser);
                    return true;
                } else {
                    //Password doesn't cut it? Explain why.
                    alert.setMessage("Passwords must meet the following requirements:\n" +
                            "-Six or more characters\n" +
                            "-One uppercase letter\n" +
                            "-One number\n" +
                            "-One symbol");
                    alert.setPositiveButton("OK", null);
                    alert.setTitle("Invalid password");
                    AlertDialog dialog = alert.show();
                }
            } else {
                //Username isn't up to snuff? Tell 'em!
                alert.setTitle("Invalid username");
                alert.setMessage("Username must be at least 4 characters.");
                alert.setPositiveButton("OK",null);
            }
        }
        return false;
    }

    public void drop(){
        //Drop the table and make a new one
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists credentials");
        onCreate(db);
    }
}


