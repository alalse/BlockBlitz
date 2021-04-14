package com.albin.blockblitz.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.albin.blockblitz.interfaces.backButtonListener;

public class BaseFragment extends Fragment {
    private backButtonListener bblistener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bblistener = (backButtonListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Handle for the backbutton
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                bblistener.backButtonCallbackMethod();
            }
        });
    }
}