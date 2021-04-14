package com.albin.blockblitz.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends BaseFragment {
    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        setupUpdateUsernameView(view);
        setupLangDropdown(view);

        return view;
    }

    private void setupUpdateUsernameView(View view) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            TextView currentUsername = view.findViewById(R.id.currentUsernameView);
            currentUsername.setText(getString(R.string.current_username, FirestoreHandler.getUsername()));

            EditText usernameInput = view.findViewById(R.id.usernameInput);
            Button btn = view.findViewById(R.id.updateUsernameBtn);
            btn.setOnClickListener(_view -> {
                String username = usernameInput.getText().toString().trim();
                if (username.length() < 3) {
                    Toast.makeText(getActivity(), R.string.short_username_error, Toast.LENGTH_SHORT).show();
                }
                else if (username.contains(" ")) {
                    Toast.makeText(getActivity(), R.string.whitespace_username_error, Toast.LENGTH_SHORT).show();
                }
                else if (username.length() > 12) {
                    Toast.makeText(getActivity(), R.string.long_username_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    FirestoreHandler.updateUsername(username).observe(this, responseCode -> {
                        if (responseCode == ResponseCode.SUCCESS) {
                            Toast.makeText(getActivity(), R.string.username_update_success, Toast.LENGTH_SHORT).show();
                            currentUsername.setText(getString(R.string.current_username, FirestoreHandler.getUsername()));
                            usernameInput.setText("");
                        }
                        else if (responseCode == ResponseCode.FAILURE) {
                            Toast.makeText(getActivity(), R.string.username_update_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            LinearLayout updateUsernameView = view.findViewById(R.id.updateUsernameView);
            updateUsernameView.setVisibility(View.GONE);
        }
    }

    private void setupLangDropdown(View view) {
        Spinner langDropdown = view.findViewById(R.id.langDropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.langs, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        langDropdown.setAdapter(adapter);

        SharedPreferences sharedPref = Objects.requireNonNull(getContext()).getSharedPreferences("lang", Context.MODE_PRIVATE);
        int currentIndex = sharedPref.getInt("langIndex", -1);
        if (currentIndex == -1) { langDropdown.setSelection(0); }
        else { langDropdown.setSelection(currentIndex); }

        langDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (!first) {
                    String current = sharedPref.getString("lang", "English");
                    String lang = (String) parent.getItemAtPosition(pos);
                    String langCode = lang.split(" ")[1]
                            .replace("[","").replace("]", "");

                    if (!current.equals(langCode)) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("lang", langCode);
                        editor.putInt("langIndex", pos);
                        editor.apply();

                        Resources res = getResources();
                        Configuration conf = res.getConfiguration();
                        Locale locale = new Locale(langCode);
                        conf.setLocale(locale);
                        res.updateConfiguration(conf, res.getDisplayMetrics());

                        Intent refresh = new Intent(getActivity(), MainActivity.class);
                        Objects.requireNonNull(getActivity()).finish();
                        startActivity(refresh);
                    }
                }
                else {
                    first = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }
}