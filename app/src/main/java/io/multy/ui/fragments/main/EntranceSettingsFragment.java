/*
 * Copyright 2018 Idealnaya rabota LLC
 * Licensed under Multy.io license.
 * See LICENSE for details
 */

package io.multy.ui.fragments.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.samwolfand.oneprefs.Prefs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.multy.R;
import io.multy.ui.activities.BaseActivity;
import io.multy.ui.activities.PinSetupActivity;
import io.multy.ui.activities.SeedActivity;
import io.multy.ui.fragments.BaseFragment;
import io.multy.util.Constants;
import io.multy.util.analytics.Analytics;
import io.multy.util.analytics.AnalyticsConstants;

public class EntranceSettingsFragment extends BaseFragment {

    public static EntranceSettingsFragment newInstance() {
        return new EntranceSettingsFragment();
    }

    @BindView(R.id.button_warn)
    ConstraintLayout buttonWarn;
    @BindView(R.id.container_setup_pin)
    ConstraintLayout containerSetupPin;
    @BindView(R.id.switch_password)
    SwitchCompat switchPassword;
    @BindView(R.id.image_pin)
    ImageView imagePin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_entrance, container, false);
        ButterKnife.bind(this, view);
        Analytics.getInstance(getActivity()).logEntranceSettingsLaunch();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUI();
    }

    @OnClick(R.id.button_back)
    public void onClickBack() {
        Analytics.getInstance(getActivity()).logEntranceSettings(AnalyticsConstants.BUTTON_CLOSE);
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.button_warn)
    public void onClickWarn() {
        startActivity(new Intent(getActivity(), SeedActivity.class));
    }

    @OnClick(R.id.container_setup_pin)
    void onClickPin(View view) {
        view.setEnabled(false);
        view.postDelayed(() -> view.setEnabled(true), 1500);
        startActivity(new Intent(getActivity(), PinSetupActivity.class));
    }

    private void setUI() {
        buttonWarn.setVisibility(Prefs.getBoolean(Constants.PREF_BACKUP_SEED) ? View.GONE : View.VISIBLE);
        if (Prefs.contains(Constants.PREF_PIN)) {
            containerSetupPin.setVisibility(View.VISIBLE);
            switchPassword.setChecked(true);
            imagePin.setVisibility(View.VISIBLE);
        } else {
            containerSetupPin.setVisibility(View.GONE);
            switchPassword.setChecked(false);
            imagePin.setVisibility(View.GONE);
        }
        switchPassword.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                Analytics.getInstance(getActivity()).logEntranceSettings(AnalyticsConstants.ENTRANCE_SETTINGS_PIN_ENABLED);
                containerSetupPin.setVisibility(View.VISIBLE);
                if (Prefs.contains(Constants.PREF_PIN)) {
                    imagePin.setVisibility(View.VISIBLE);
                } else {
                    imagePin.setVisibility(View.GONE);
                }
            } else {
                Analytics.getInstance(getActivity()).logEntranceSettings(AnalyticsConstants.ENTRANCE_SETTINGS_PIN_DISABLED);
                containerSetupPin.setVisibility(View.GONE);
                Prefs.remove(Constants.PREF_PIN);
                Prefs.putBoolean(Constants.PREF_LOCK, false);
            }
        });
    }
}
