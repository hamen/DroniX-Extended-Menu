package org.cfsm.android.dronixextendedmenu;

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
import android.widget.ToggleButton;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DroniXExtendedMenuActivity extends Activity {

    private static final int DIALOG_ERROR_ID = 1;
    private static final int DIALOG_ABOUT = 2;
    private static final int DIALOG_SSH_STARTED = 3;
    private static final int DIALOG_WEBSERVER_STARTED = 4;

    SSH ssh = new SSH();
    WebServer wbsr = new WebServer();
    DEMUtil alert;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        alert = new DEMUtil(this);

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
                        showDialog(DIALOG_WEBSERVER_STARTED);
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
                        RootTools.sendShell("/data/www/cgi-bin/ssh-on.cgi");
                        sshTB.setChecked(true);
                        showDialog(DIALOG_SSH_STARTED);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RootToolsException e) {
                        e.printStackTrace();
                    }

                } else {
                    // stop ssh and set togglebutton to false
                    try {
                        RootTools.sendShell("/data/www/cgi-bin/ssh-off.cgi");
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
            String ip = getWIFIip();
            showDialog(DIALOG_WEBSERVER_STARTED);
        	webserverTB.setChecked(true);
        } else {
        	webserverTB.setChecked(false);
        }
        
        if (SSH.isRunning()) {
            showDialog(DIALOG_SSH_STARTED);
            sshTB.setChecked(true);
        } else {
            sshTB.setChecked(false);
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
    	case DIALOG_ABOUT:
    		String[] dialogData = {"About", getBaseContext().getString(R.string.createdBy)} ;
    		dialog = createConfirmDialog(dialogData);
    		break;
    	case DIALOG_SSH_STARTED:
    		String password = SSH.getPassword();
			ip = getWIFIip();
			connectionData = "username: root\n" +
					"password: " + password + "\n" +
					"IP: " + ip;
			
    		String[] dialogSSHstarted = {"SSH started", connectionData} ;
    		dialog = createConfirmDialog(dialogSSHstarted);
    		break;
    	case DIALOG_WEBSERVER_STARTED:
			ip = getWIFIip();
        	if (ip.compareTo("0.0.0.0") == 0)
				ip = "localhost";
			connectionData = "IP: " + ip;
			
			String[] dialogWebServerStarted = {"Web Server started", connectionData} ;
    		dialog = createConfirmDialog(dialogWebServerStarted);
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

	// Executes UNIX command.
	public static String exec(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
            StringBuilder output = new StringBuilder();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();
			process.waitFor();
			return output.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
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
            case R.id.edit_ssh_password:
                Intent myIntent = new Intent(DroniXExtendedMenuActivity.this, SSHpasswordChange.class);
                DroniXExtendedMenuActivity.this.startActivity(myIntent);
                return true;
            case R.id.reset_ssh_password:
                alert.ts("RESET PASSWORD");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
// /system/xbin/mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system
// /system/xbin/mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system