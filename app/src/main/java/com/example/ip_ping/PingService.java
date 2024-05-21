package com.example.ip_ping;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingService extends Service {
    private static final String TAG = "PingService";
    private static final long INTERVAL = 5000; // 5 seconds interval for ping
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ipAddress = intent.getStringExtra("ipAddress");
        ping(ipAddress);
        return START_STICKY;
    }

    private void ping(final String ipAddress) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean success = pingIpAddress(ipAddress);
                if (success) {
                    Log.i(TAG, "Ping successful for IP: " + ipAddress);
                } else {
                    Log.i(TAG, "Ping failed for IP: " + ipAddress);
                }
                // Repeat ping
                ping(ipAddress);
            }
        }, INTERVAL);
    }

    private boolean pingIpAddress(String ipAddress) {
        try {
            // Execute ping command
            Process process = Runtime.getRuntime().exec("ping -c 1 " + ipAddress);

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the command to finish and get the exit code
            int exitCode = process.waitFor();

            // Check if the ping was successful (exit code 0 means success)
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}