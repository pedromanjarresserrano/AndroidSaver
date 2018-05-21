package com.service.saver.saverservice.twitter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.service.saver.saverservice.MainTabActivity;
import com.service.saver.saverservice.R;
import com.service.saver.saverservice.services.SaverService;

public class TwitterFragment extends Fragment {

    public TwitterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        SwitchCompat switchCompat = view.findViewById(R.id.service_switch);
        switchCompat.setChecked(isMyServiceRunning(SaverService.class, MainTabActivity.activity));
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent service = new Intent(MainTabActivity.activity, SaverService.class);
                    getContext().startService(service);
                } else {
                    Intent service = new Intent(MainTabActivity.activity, SaverService.class);
                    getContext().stopService(service);
                }

            }
        });
        return view;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already", "running");
                return true;
            }
        }
        Log.i("Service not", "running");
        return false;
    }
}
