package org.dronix.android.dronixextendedmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.io.IOException;

/**
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: 8/25/11
 */
class WebServer {
    Activity dem;

    public static boolean isRunning() {
        return DEMUtil.exec("/system/bin/ps").contains("mini_httpd");
    }

    public WebServer(Activity dem) {
        this.dem = dem;
    }

    public int start() throws IOException {
        String[] str ={"su","-c","/system/xbin/mini_httpd -C /system/etc/mini-httpd.conf"};
        Process p = Runtime.getRuntime().exec(str);
        return p.hashCode();
    }

    public void stop() throws IOException {
        String[] str ={"su","-c","/system/xbin/killall mini_httpd"};
        Process p = Runtime.getRuntime().exec(str);
    }
    public void showInfos(String ip) {
        if (ip.compareTo("0.0.0.0") == 0)
            ip = "localhost";

        AlertDialog.Builder alertbox = new AlertDialog.Builder(dem);
        alertbox.setTitle(dem.getString(R.string.webserverStarted));
        alertbox.setMessage("IP: " + ip);
        alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alertbox.show();
    }
}