package com.thesler.invdbmgr;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Logout extends Fragment {

    public Logout() {
        // Required empty public constructor
    }

    public static Logout newInstance() {
        return new Logout();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Right when this view is created, end the activity that's running it
        //This will take us back to the previous activity, the login screen
        getActivity().finish();
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
}