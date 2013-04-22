// Copyright 2012 Google Inc. All Rights Reserved.

package com.example.android.notepad;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

/**
 * @author alainv
 * 
 */
public class Preferences extends PreferenceActivity {

  /**
   * Populate the activity with the top-level headers.
   */
  @Override
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.layout.preferences_headers, target);
  }

  @Override
  public Intent getIntent() {
    final Intent modIntent = new Intent(super.getIntent());
    modIntent.putExtra(EXTRA_SHOW_FRAGMENT, PreferencesFragment.class.getName());
    modIntent.putExtra(EXTRA_NO_HEADERS, true);
    return modIntent;
  }

  /**
   * This fragment shows the preferences for the main header.
   */
  public static class PreferencesFragment extends PreferenceFragment {
    public static final String[] ACCOUNT_TYPE = new String[] {GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};

    private static final int CHOOSE_ACCOUNT = 0;

    private static final int STATE_INITIAL = 0;
    private static final int STATE_CHOOSING_ACCOUNT = 1;
    private static final int STATE_DONE = 3;

    private GoogleAccountManager mAccountManager;
    private Preference mAccountPreference;
    private ListPreference mSyncPreference;
    private SharedPreferences mPreferences;
    private int mState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      mState = STATE_INITIAL;

      mAccountManager = new GoogleAccountManager(getActivity());
      mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

      // Load the preferences from an XML resource
      addPreferencesFromResource(R.layout.preferences_screen);

      // Initialize the preferred account setting.
      mAccountPreference = this.findPreference("selected_account_preference");
      mAccountPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
          chooseAccount();
          return true;
        }
      });

      mSyncPreference = (ListPreference) this.findPreference("sync_frequency_preference");
      mSyncPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          SharedPreferences.Editor editor =
              PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
          editor.putString("sync_frequency_preference", (String) newValue);
          editor.commit();
          setSyncFrequency(getPreferenceAccount());
          return true;
        }
      });
    }

    @Override
    public void onResume() {
      super.onResume();
      Account preferenceAccount = getPreferenceAccount();

      if (preferenceAccount != null) {
        mAccountPreference.setSummary(preferenceAccount.name);
        mState = STATE_DONE;
      } else {
        if (mState == STATE_INITIAL) {
          chooseAccount();
        }
      }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
        case CHOOSE_ACCOUNT:
          if (data != null) {

            Log.e(
                "Preferences",
                "SELECTED ACCOUNT WITH EXTRA: "
                    + data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            Bundle b = data.getExtras();
            
            String accountName = b.getString(AccountManager.KEY_ACCOUNT_NAME);

            Log.d("Preferences", "Selected account: " + accountName);
            if (accountName != null && accountName.length() > 0) {
              Account account = mAccountManager.getAccountByName(accountName);
              setAccount(account);
            }
          } else {
            mState = STATE_INITIAL;
          }
          break;
      }
    }

    /**
     * Start an intent to prompt the user to choose the account to use with the
     * app.
     */
    private void chooseAccount() {
      mState = STATE_CHOOSING_ACCOUNT;
      Intent intent =
          AccountPicker.newChooseAccountIntent(getPreferenceAccount(), null, ACCOUNT_TYPE, false,
              null, null, null, null);
      startActivityForResult(intent, CHOOSE_ACCOUNT);
    }

    /**
     * Set the new account to use with the app.
     * 
     * @param account New account to use.
     */
    private void setAccount(Account account) {
      if (account != null) {
        Account oldAccount = getPreferenceAccount();
        // Stop syncing for the previously selected account.
        if (oldAccount != null) {
          ContentResolver.setSyncAutomatically(oldAccount, "com.google.provider.NotePad", false);
        }
        SharedPreferences.Editor editor =
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("selected_account_preference", account.name);
        editor.commit();

        mAccountPreference.setSummary(account.name);
        setSyncFrequency(account);
        mState = STATE_DONE;
      }
    }

    /**
     * Set the sync frequency for the selected account.
     * 
     * @param account Account to set sync frequency for.
     */
    private void setSyncFrequency(Account account) {
      if (account != null) {
        String syncValue = mPreferences.getString("sync_frequency_preference", "900");

        mSyncPreference.setSummary("Sync every "
            + mSyncPreference.getEntries()[mSyncPreference.findIndexOfValue(syncValue)]);

        ContentResolver.setSyncAutomatically(account, "com.google.provider.NotePad", true);
        ContentResolver.addPeriodicSync(account, "com.google.provider.NotePad", new Bundle(),
            Long.parseLong(syncValue));
      }
    }

    /**
     * Get the currently preferred account to use with the app.
     * 
     * @return The preferred account if available, {@code null} otherwise.
     */
    private Account getPreferenceAccount() {
      return mAccountManager.getAccountByName(mPreferences.getString("selected_account_preference",
          ""));
    }

  }

}
