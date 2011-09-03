package org.dronix.android.dronixextendedmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.*;

/**
 * @author Ivan Morgillo
 *	
 */
class SSH {
    private String host;
    private final FSmanager fsm = new FSmanager();
    Activity dem;

    public SSH(Activity dem) {
        this.dem = dem;
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

    public static boolean isRunning() {
        String psOutput = DEMUtil.exec("/system/bin/ps");
        return (psOutput.indexOf("dropbear") > 0);
    }

    public boolean setPassword(String password) {

        String currentPassword = SSH.getPassword();
		try {
			// remount /system rw and set /etc/ssh/passwd to rw to edit password
			fsm.mountRW();
			fsm.setSSHpasswordFileRW();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// write the new password
			BufferedWriter passFile = new BufferedWriter(new FileWriter("/etc/ssh/passwd"));
			passFile.write(password);
			passFile.close();

			// restore permission on /etc/ssh/passwd and remount /system ro
			fsm.setSSHpasswordFileRO();
			fsm.mountRO();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RootToolsException e) {
			e.printStackTrace();
		}


		return true;
	}

        public void showInfos(String body, String title) {

            AlertDialog.Builder alertbox = new AlertDialog.Builder(dem);
            alertbox.setTitle(title);
            alertbox.setMessage(body);
            alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            alertbox.show();
        }
    public void start() throws IOException, RootToolsException, InterruptedException {
        File old = new File("/data/www/cgi-bin/ssh-on.cgi");
        if (old.exists()) {
            /* Old environment detected */
            RootTools.sendShell("/data/www/cgi-bin/ssh-on.cgi");
        }
        else {
            RootTools.sendShell("/system/bin/ssh-on");
        }
    }

    public void stop() throws IOException, RootToolsException, InterruptedException {
        File old = new File("/data/www/cgi-bin/ssh-off.cgi");
        if (old.exists()) {
            /* Old environment detected */
            RootTools.sendShell("/data/www/cgi-bin/ssh-off.cgi");
        }
        else {
            RootTools.sendShell("/system/bin/ssh-off");
        }
    }
}
