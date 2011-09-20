package org.dronix.android.dronixextendedmenu;

import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.widget.Toast;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;


public class Preferences extends PreferenceActivity {
    boolean SSHCheckboxPreference;
    boolean WebServerCheckboxPreference;
    String SSHpassword;

    SSH ssh = new SSH(this);
    WebServer wb = new WebServer(this);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            final CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager()
                .findPreference("ssh_checkbox_preference");
            checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    checkSSHstatus(newValue);
                    return true;
                }
            });
            final CheckBoxPreference webserverCeckboxPref = (CheckBoxPreference) getPreferenceManager()
                .findPreference("webserver_checkbox_preference");
            webserverCeckboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    checkWebServerStatus(newValue);
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
        SSHpassword = prefs.getString("ssh_password", "Nothing has been entered");
    }

    private void checkSSHstatus(Object newValue) {
        if (newValue.toString().equals("true") && !SSH.isRunning()) {
            try {
                ssh.start();
                String password = SSH.getPassword();
                String ip = getWIFIip();
                String connectionData = "username: root\n" +
                    "password: " + password + "\n" +
                    "IP: " + ip;
                String title =   getString(R.string.sshStarted);
                ssh.showInfos(connectionData, title);
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
    }

    private void checkWebServerStatus(Object newValue) {
        if (newValue.toString().equals("true") && !WebServer.isRunning()) {
            try {
                wb.start();
                String ip = getWIFIip();
                if (ip.compareTo("0.0.0.0") == 0)
                    ip = "localhost";
                wb.showInfos(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (WebServer.isRunning()) {
                Toast.makeText(getApplicationContext(),
                    getText(R.string.webserverStarted),
                    Toast.LENGTH_SHORT).show();
            }
        }
        else {
            try {
                wb.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!WebServer.isRunning()) {
                Toast.makeText(getApplicationContext(),
                    getText(R.string.webserverStopped),
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getWIFIip() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return Formatter.formatIpAddress(ipAddress);
    }
}
