package org.cfsm.android.dronixextendedmenu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.*;

/**
 * @author Ivan Morgillo
 *	
 */
class SSH {
    private String host;
    private final String password;
    private final FSmanager fsm = new FSmanager();

    private static final int MOUNT_RO = 0;
    private static final int MOUNT_RW = 1;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public static String getPassword() {
        String password = null;
		try {
			BufferedReader passfile = new BufferedReader(new FileReader("/etc/ssh/passwd"));
			password = passfile.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return password;
    }

    public SSH() {
        host = this.host;
        password = SSH.getPassword();
    }

    public static boolean isRunning() {
        String psOutput = DroniXExtendedMenuActivity.exec("/system/bin/ps");
        return (psOutput.indexOf("dropbear") > 0);
    }

    public boolean setPassword(String password) {

        String currentPassword = SSH.getPassword();
		try {
			// remount /system rw and set /etc/ssh/passwd to rw to edit password
			fsm.reMountSystem(MOUNT_RW);
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
			fsm.reMountSystem(MOUNT_RO);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RootToolsException e) {
			e.printStackTrace();
		}
		return true;
	}

    private void setSSHpasswordFileRW() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod +rw /etc/ssh/passwd");
	}
	private void setSSHpasswordFileRO() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod go-w /etc/ssh/passwd");
	}
}
