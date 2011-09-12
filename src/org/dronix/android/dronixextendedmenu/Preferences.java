package org.dronix.android.dronixextendedmenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;


public class Preferences extends PreferenceActivity {
    boolean SSHCheckboxPreference;
    boolean WebServerCheckboxPreference;
    SSH ssh = new SSH(this);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager()
                .findPreference("ssh_checkbox_preference");

            checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.toString().equals("true") && !SSH.isRunning()) {
                        try {
                            ssh.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RootToolsException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (SSH.isRunning()) {
                            Toast.makeText(getApplicationContext(),
                                getText(R.string.sshStarted),
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        try {
                            ssh.stop();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RootToolsException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!SSH.isRunning()) {
                            Toast.makeText(getApplicationContext(),
                                getText(R.string.sshStopped),
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }

            });
        }

    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
            .getDefaultSharedPreferences(getBaseContext());
        SSHCheckboxPreference = prefs.getBoolean("ssh_checkbox_preference", true);
        WebServerCheckboxPreference = prefs.getBoolean("webserver_checkbox_preference", true);
    }

}
