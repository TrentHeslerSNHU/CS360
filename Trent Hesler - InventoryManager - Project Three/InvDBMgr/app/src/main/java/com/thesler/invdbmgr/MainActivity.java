package com.thesler.invdbmgr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.thesler.invdbmgr.databinding.ActivityMainBinding;
import com.thesler.invdbmgr.ui.edit.EditItemFragment;
import com.thesler.invdbmgr.ui.settings.SettingsFragment;
import com.thesler.invdbmgr.util.InvDBMgr;
import com.thesler.invdbmgr.util.ItemRecyclerViewAdapter;
import com.thesler.invdbmgr.util.NotificationService;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public NotificationService smsAlerts;
    public static WeakReference<MainActivity> weakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] permissions = {Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS};
        boolean hasSMS = (checkSelfPermission(permissions[0]) == PackageManager.PERMISSION_GRANTED);
        boolean hasPhoneState = (checkSelfPermission(permissions[1]) == PackageManager.PERMISSION_GRANTED);
        boolean hasPhoneNumber = (checkSelfPermission(permissions[2]) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermissions = (hasSMS && hasPhoneState && hasPhoneNumber);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //This is here to let the Settings fragment interact with MainActivity's NotificationService
        weakReference = new WeakReference<>(MainActivity.this);

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.settingsFragment,R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        smsAlerts = new NotificationService(this);
        getPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public void submitEdits(View view){
        InvDBMgr invDB = new InvDBMgr(this);
        TextView itemName = findViewById(R.id.editItemName);
        EditText newItemQty = findViewById(R.id.editItemQty);

        //Update item name if it changed
        if(!Objects.equals(EditItemFragment.origItemName, itemName.getText().toString())){
            invDB.updateItemName(EditItemFragment.origItemName,itemName.getText().toString());
        }

        //Update item quantity if it changed
        invDB.updateItemCount(itemName.getText().toString(),Integer.parseInt(newItemQty.getText().toString()));

        //Display a message and go back to home screen
        Toast.makeText(this,"Item edits submitted!",Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.nav_home);
    }

    public void addNewItem(View view){
        InvDBMgr invDB = new InvDBMgr(this);
        EditText itemName = findViewById(R.id.editNewItemName);
        EditText newItemQty = findViewById(R.id.editNewItemQty);

        //Add item
        invDB.addItem(itemName.getText().toString(),Integer.parseInt(newItemQty.getText().toString()));

        //Display message and return to home
        Toast.makeText(this,"Item added!",Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.nav_home);
    }

    public void purgeDB(View view){
        InvDBMgr invDB = new InvDBMgr(this);
        invDB.drop();
        Toast.makeText(this,"Inventory database purged!",Toast.LENGTH_SHORT).show();
    }

    public void getPermissions(){
        String[] permissions = {Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_NUMBERS};


        ActivityResultLauncher<String[]> getSMS = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean smsGranted = result.getOrDefault(permissions[0], false);
            boolean phoneStateGranted = result.getOrDefault(permissions[1],false);
            boolean phoneNumberGranted = result.getOrDefault(permissions[2],false);
            boolean hasPerms = (smsGranted && phoneStateGranted && phoneNumberGranted);
            if (hasPerms){
                //Got all permissions
                smsAlerts.create();
            } else {
                //One or more permissions not granted
                //Show rationale, if necessary
                if (shouldShowRequestPermissionRationale(permissions[0])){
                    androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(this);
                    alert.setTitle("SMS and phone permissions");
                    alert.setMessage("If you grant it permission, this app can send SMS alerts when items are out-of-stock.\n" +
                                     "You can toggle this setting at any time in Settings.");
                    alert.setPositiveButton("Okie-dokie", null);
                    alert.show();
                }
            }
        });
        getSMS.launch(permissions);
    }

    public static MainActivity getInstance(){
        return weakReference.get();
    }
}