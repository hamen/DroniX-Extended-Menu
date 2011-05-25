package org.cfsm.android.dronixextendedmenu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DroniXExtendedMenuActivity extends Activity {
	// Options menu
	private static final int move2system = Menu.FIRST;
    private static final int about = Menu.FIRST + 1;
//    private static final int scheduleBtnId = Menu.FIRST + 3;
//    private static final int playBtnId = Menu.FIRST + 2;
    private int group1Id = 1;
    private int group2Id = 2;
    
    private static final int DIALOG_ERROR_ID = 1;
    private static final int DIALOG_ABOUT = 2;
    private static final int DIALOG_SSH_STARTED = 3;
    private static final int DIALOG_WEBSERVER_STARTED = 4;

    private static final int MOUNT_RO = 0;
    private static final int MOUNT_RW = 1;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
      
        // get webserver togglebutton and bind onClickListener
        final ToggleButton webserver = (ToggleButton) findViewById(R.id.toggleButtonWebserver);
        webserver.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if(exec("/system/bin/ps").indexOf("mini_httpd") == -1){
        			// start webserver and set togglebutton to true
        			String[] str ={"su","-c","/system/xbin/mini_httpd -C /system/etc/mini-httpd.conf"};
        			try {
						Process p = Runtime.getRuntime().exec(str);
						webserver.setChecked(true);
						String ip = getWIFIip();
			        	if (ip.compareTo("0.0.0.0") == 0)
								ip = "localhost";
			        	showDialog(DIALOG_WEBSERVER_STARTED);
					} catch (IOException e) {
						e.printStackTrace();
					}
        			
                }
        		else if(exec("/system/bin/ps").indexOf("mini_httpd") > 0 ) {
                	// stop webserver and set togglebutton to false
        			String[] str ={"su","-c","/system/xbin/killall mini_httpd"};
        			try {
						Process p = Runtime.getRuntime().exec(str);
						webserver.setChecked(false);
	            		Toast.makeText(getBaseContext(), R.string.webserverStopped, Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						e.printStackTrace();
					}
        			
        		}
        	}
        	});
     // get ssh togglebutton and bind onClickListener
        final ToggleButton sshToggleB = (ToggleButton) findViewById(R.id.toggleButtonSSH);
        sshToggleB.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		if(exec("/system/bin/ps").indexOf("dropbear") == -1){
            		       			
            		// start ssh and set togglebutton to true and show a dialog with connection data
        			try {
						RootTools.sendShell("/data/www/cgi-bin/ssh-on.cgi");
						sshToggleB.setChecked(true);
						showDialog(DIALOG_SSH_STARTED);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (RootToolsException e) {
						e.printStackTrace();
					}
        			
                }
        		else if(exec("/system/bin/ps").indexOf("dropbear") > 0 ) {
                	// stop ssh and set togglebutton to false
        			try {
						RootTools.sendShell("/data/www/cgi-bin/ssh-off.cgi");
						sshToggleB.setChecked(false);
	            		Toast.makeText(getBaseContext(), R.string.sshStopped, Toast.LENGTH_SHORT).show();
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
        String psOutput = exec("/system/bin/ps");
        if(psOutput.indexOf("/system/xbin/mini_httpd") > 0){
        	String ip = getWIFIip();

        	showDialog(DIALOG_WEBSERVER_STARTED);
        	webserver.setChecked(true);
        }
        else {
        	webserver.setChecked(false);
        }
        
        // check is ssh is running and show connection data
        if(psOutput.indexOf("dropbear") > 0){
        	showDialog(DIALOG_SSH_STARTED);
        	sshToggleB.setChecked(true);
        }
        else {
        	sshToggleB.setChecked(false);
        }
    }
    
    // Create Option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(group1Id, move2system, move2system,"Move2System");
        menu.add(group2Id, about, about, R.string.about);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	int id = item.getItemId();
    	switch (id) {
    	case move2system:
    		Toast.makeText(this, "Coming soon...", Toast.LENGTH_SHORT).show();
    		setSSHpassword();
    		return true;
    	case about:
    		showDialog(DIALOG_ABOUT);
    		return true;
    	}
    	return false;
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
    		String password = getSSHpassword();
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
		AlertDialog alert = builder.create();

		return alert;
	}

	private Dialog createErrorDialog() {
		return null;
	}

	// Executes UNIX command.
	private String exec(String command) {
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
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
	
	private String getSSHpassword() {
		String password = null;
		try {
			BufferedReader passfile = new BufferedReader(new FileReader("/etc/ssh/passwd"));
			password = passfile.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
	}
	
	private String getWIFIip() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = Formatter.formatIpAddress(ipAddress);
		return ip;
	}
	
	private void reMountSystem(int mode) throws IOException, InterruptedException, RootToolsException {
		List<String> output;
		
		switch(mode) {
		case MOUNT_RW:
			output = RootTools.sendShell("/system/xbin/mount -o rw,remount -t yaffs2 /dev/block/mtdblock4 /system");
		    break;
		case MOUNT_RO:
			output = RootTools.sendShell("/system/xbin/mount -o ro,remount -t yaffs2 /dev/block/mtdblock4 /system");
			break;
		}
	}
	
	private void setSSHpasswordFileRW() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod +rw /etc/ssh/passwd");
	}
	private void setSSHpasswordFileRO() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod go-w /etc/ssh/passwd");
	}
	
	private boolean setSSHpassword() {
		String password = "hamen"; 
		
		String currentPassword = getSSHpassword();
		try {
			// remount /system rw and set /etc/ssh/passwd to rw to edit password
			reMountSystem(MOUNT_RW);
			setSSHpasswordFileRW();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			// write the new password
			BufferedWriter passFile = new BufferedWriter(new FileWriter("/etc/ssh/passwd"));
			passFile.write(password);
			passFile.close();
			
			// restore permission on /etc/ssh/passewd and remount /system ro
			setSSHpasswordFileRO();
			reMountSystem(MOUNT_RO);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RootToolsException e) {
			e.printStackTrace();
		}
		return true;
	}
	
}
// /system/xbin/mount -o rw,remount -t yaffs2 /dev/block/mtdblock3 /system
// /system/xbin/mount -o ro,remount -t yaffs2 /dev/block/mtdblock3 /system