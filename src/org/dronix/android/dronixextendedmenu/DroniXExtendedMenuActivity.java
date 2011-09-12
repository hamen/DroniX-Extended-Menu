package org.dronix.android.dronixextendedmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;

public class DroniXExtendedMenuActivity extends Activity {

    private static final int DIALOG_ERROR_ID = 1;
    private static final int DIALOG_ABOUT = 2;
    private static final int DIALOG_SSH_STARTED = 3;
    private static final int DIALOG_WEBSERVER_STARTED = 4;

    SSH ssh ;
    WebServer wbsr;
    DEMUtil alert;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        alert = new DEMUtil(this);
        wbsr = new WebServer(this);
        ssh = new SSH(this);

        // get webserver togglebutton and bind onClickListener
        final ToggleButton webserverTB = (ToggleButton) findViewById(R.id.toggleButtonWebserver);
        webserverTB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!WebServer.isRunning()) {
                    // start webserver and set togglebutton to true
                    int success = 0;
                    try {
                        success = wbsr.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (success > 0) {
                        webserverTB.setChecked(true);
                        String ip = getWIFIip();
                        if (ip.compareTo("0.0.0.0") == 0)
                            ip = "localhost";
                        wbsr.showInfos(ip);
                    } else {
                        alert.ts("!*ERROR*!");
                    }

                } else {
                    // stop webserver and set togglebutton to false
                    try {
                        wbsr.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    webserverTB.setChecked(false);
                    alert.ts(getString(R.string.webserverStopped));
                }
            }
        });
        /* get ssh togglebutton and bind onClickListener */
        final ToggleButton sshTB = (ToggleButton) findViewById(R.id.toggleButtonSSH);
        sshTB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SSH.isRunning()) {
                    /* start ssh and set togglebutton to true and show a dialog with connection data */
                    try {
                        ssh.start();
                        checkSSH(sshTB);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (RootToolsException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    // stop ssh and set togglebutton to false
                    try {
                        ssh.stop();
                        sshTB.setChecked(false);
                        alert.ts(getString(R.string.sshStopped));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RootToolsException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        
        /* AT START TIME */
        // check if webserver is running and set ToggleButton
        if(WebServer.isRunning()){
            wbsr.showInfos(getWIFIip());
            webserverTB.setChecked(true);
        } else {
        	webserverTB.setChecked(false);
        }

       checkSSH(sshTB);

        Button prefBtn = (Button) findViewById(R.id.prefButton);
                prefBtn.setOnClickListener(new OnClickListener() {

                        public void onClick(View v) {
                                Intent settingsActivity = new Intent(getBaseContext(),
                                                Preferences.class);
                                startActivity(settingsActivity);
                        }
                });
    }

    private void checkSSH(ToggleButton b) {
        if (SSH.isRunning()) {
            String password = SSH.getPassword();
            String ip = getWIFIip();
            String connectionData = "username: root\n" +
                "password: " + password + "\n" +
                "IP: " + ip;
            String title =   getString(R.string.sshStarted);
            ssh.showInfos(connectionData, title);
            b.setChecked(true);
        } else {
            b.setChecked(false);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	String ip;
    	String connectionData;
    	switch (id) {
    	case DIALOG_ERROR_ID:
    		dialog = createErrorDialog();
    		break;

        default:
    		dialog = null;
    		break;
    	}
    	return dialog;
    }
    
	private Dialog createConfirmDialog(String[] dialogData) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(dialogData[0]);
		builder.setMessage(dialogData[1]);
		builder.setCancelable(false);
		builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeDialog(DIALOG_SSH_STARTED);
				removeDialog(DIALOG_WEBSERVER_STARTED);

			}
		});

        return builder.create();
	}

	private Dialog createErrorDialog() {
		return null;
	}

	private String getWIFIip() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		return Formatter.formatIpAddress(ipAddress);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.ssh_password_change_menuitem:
                Intent ssh_password_change_intent = new Intent(DroniXExtendedMenuActivity.this, SSHpasswordChange.class);
                DroniXExtendedMenuActivity.this.startActivity(ssh_password_change_intent);
                return true;
            case R.id.about:
                alert.alertbox(getString(R.string.createdBy), "Author");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}