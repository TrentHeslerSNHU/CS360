package com.thesler.invdbmgr.util;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.List;

public class NotificationService{

    public Context context;
    public Handler handler;
    public static Runnable runnable;

    public NotificationService(Context newcontext){
        context = newcontext;
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                InvDBMgr invDB = new InvDBMgr(context);
                String outsText = "";

                //Get all out-of-stock items
                List<InventoryItem> outs = invDB.getOuts();

                for (InventoryItem item : outs){
                    outsText += item.getName() + "(" +
                            item.getId() + ") - Qty: " +
                            item.getCount() + "\n";
                }

                //Make sure we have permissions needed and there are outs to report
                if(context.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                        !outsText.equals("")){

                    //Get phone number
                    SubscriptionManager subMgr = SubscriptionManager.from(context);
                    String phoneNumber = subMgr.getPhoneNumber(SubscriptionManager.DEFAULT_SUBSCRIPTION_ID);

                    //Send the outs list
                    SmsManager smsMgr = SmsManager.getDefault();
                    smsMgr.sendTextMessage(phoneNumber,phoneNumber,"The following items are out of stock:\n" + outsText,null,null);
                }

                //Re-run every 2 minutes
                //(Should be longer for a commercial app, but this is for school)
                handler.postDelayed(runnable, 120000);
            }
        };

    }

    public void create() {
        //Wait 10 seconds, then start alerts
        Log.d("INFO","Background service started.");
        handler.postDelayed(runnable,10000);
    }

    public void destroy() {
        //Stop alerts
        Log.d("INFO","Background service stopped.");
        handler.removeCallbacks(runnable);
    }
}