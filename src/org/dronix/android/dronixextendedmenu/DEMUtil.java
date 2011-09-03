package org.dronix.android.dronixextendedmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: 8/27/11
 */
class DEMUtil {
    Context c;
    Activity dem;
    public DEMUtil(Activity dem) {
        this.dem = dem;
        c = dem.getApplicationContext();
    }

    public void ts(String msg){
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
    public void tl(CharSequence msg){
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
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

    public void alertbox(String msg, String title) {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(dem);
        alertbox.setTitle(title);
        alertbox.setMessage(msg);
        alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // the button was clicked
            }
        });
        alertbox.show();
    }
}

