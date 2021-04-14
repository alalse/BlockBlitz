package com.albin.blockblitz.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.albin.blockblitz.framework.ResourceLoader;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class BootActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLocale();
        setupRemoteConfig();

        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_boot);
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {

            FirestoreHandler.getUserData(uid).observe(this, responseCode -> {
                if (responseCode != ResponseCode.WAITING) {
                    startMainActivity();
                }
            });
        }
        else {
            startMainActivity();
        }
    }

    private void setupLocale() {
        //Set default app locale if not already defined
        SharedPreferences sharedPref = getSharedPreferences("lang", Context.MODE_PRIVATE);
        String langCode = sharedPref.getString("lang", "");
        if (langCode.equals("")) {
            langCode = "en";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("langIndex", 0);
            editor.putString("lang", langCode);
            editor.apply();
        }

        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale locale = new Locale(langCode);
        conf.setLocale(locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
        ResourceLoader.setResources(res);
    }

    private void setupRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10800) //3 hours
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        HashMap<String, Object> defaults = getDefaults();
        remoteConfig.setDefaultsAsync(defaults);
        remoteConfig.fetchAndActivate().addOnCompleteListener(task ->
                Log.w(TAG, "Config params updated: " + task.getResult()));
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(1, 1);
    }

    private HashMap<String, Object> getDefaults() {
        //because i could'nt figure out how to put nested maps in a resource file
        //i had to make do with this
        HashMap<String, Object> defaults =  new HashMap<>();
        defaults.put("signup_enabled", true);
        defaults.put("email_login_enabled", true);
        defaults.put("google_login_enabled", true);
        defaults.put("facebook_login_enabled", true);

        HashMap<String, Map<String, Integer>> blockColors = new HashMap<>();
        Map<String, Integer> rgb;
        rgb = ImmutableMap.of("R", 115, "G", 29, "B", 181);
        blockColors.put("SMALL_BLOCK", rgb);
        rgb = ImmutableMap.of("R", 72, "G", 212, "B", 70);
        blockColors.put("MEDIUM_BLOCK", rgb);
        rgb = ImmutableMap.of("R", 35, "G", 175, "B", 222);
        blockColors.put("LARGE_BLOCK", rgb);
        rgb = ImmutableMap.of("R", 217, "G", 214, "B", 56);
        blockColors.put("SHORT_LINE", rgb);
        rgb = ImmutableMap.of("R", 237, "G", 171, "B", 28);
        blockColors.put("NORMAL_LINE", rgb);
        rgb = ImmutableMap.of("R", 222, "G", 75, "B", 175);
        blockColors.put("LONG_LINE", rgb);
        rgb = ImmutableMap.of("R", 217, "G", 78, "B", 63);
        blockColors.put("LONGEST_LINE", rgb);
        rgb = ImmutableMap.of("R", 79, "G", 219, "B", 154);
        blockColors.put("SMALL_L", rgb);
        rgb = ImmutableMap.of("R", 80, "G", 149, "B", 199);
        blockColors.put("LARGE_L", rgb);
        rgb = ImmutableMap.of("R", 109, "G", 95, "B", 201);
        blockColors.put("SMALL_T", rgb);

        defaults.put("block_colors", blockColors);
        return defaults;
    }
}