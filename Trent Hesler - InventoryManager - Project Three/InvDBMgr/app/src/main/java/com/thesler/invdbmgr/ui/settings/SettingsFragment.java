package com.thesler.invdbmgr.ui.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.thesler.invdbmgr.MainActivity;
import com.thesler.invdbmgr.databinding.FragmentSettingsBinding;
import com.thesler.invdbmgr.util.NotificationService;

import java.util.Map;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Switch smsSwitch = binding.smsSwitch;
        String[] permissions = {Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_PHONE_NUMBERS};

        //Create an activity launcher to get necessary permissions
        ActivityResultLauncher<String[]> getSMS =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean hasPerms = true;

                    //Iterate through passed-in permissions, change hasPerms to false
                    //if we encounter a permission we didn't get
                    for(Map.Entry<String,Boolean> permission : result.entrySet()){
                        if(permission.getValue()){
                            continue;
                        }
                        hasPerms = false;
                    }

                    if (hasPerms){
                        //Got all permissions
                        MainActivity.getInstance().smsAlerts.create();
                    } else {
                        //One or more permissions not granted
                        //Display rationale, if necessary
                        if (shouldShowRequestPermissionRationale(permissions[0])){
                            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.getInstance());
                            alert.setTitle("SMS and phone permissions");
                            alert.setMessage("If you grant it permission, this app can send SMS alerts when items are out-of-stock.\n" +
                                    "You can toggle this setting at any time in Settings.");
                            alert.setPositiveButton("Okie-dokie", null);
                            alert.show();
                        }
                    }
                    smsSwitch.setChecked(hasPerms);
                });

        //Set initial Switch state based on whether we have the permissions already or not.
        boolean smsPermissionGranted = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
        boolean phoneStatePermissionGranted = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        boolean phoneNumberPermissionGranted = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED);
        boolean permissionGranted = (smsPermissionGranted && phoneStatePermissionGranted && phoneNumberPermissionGranted);
        smsSwitch.setChecked(permissionGranted);

        //Make the switch do stuff when switched
        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    //If the switch is turned on, get permissions and start SMS service
                    getSMS.launch(permissions);
                } else {
                    //If switch is turned off, stop SMS service
                    smsSwitch.setChecked(false);
                    MainActivity.getInstance().smsAlerts.destroy();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}