package org.cfsm.android.dronixextendedmenu;

import java.io.IOException;

/**
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: 8/25/11
 */
class WebServer {

    public static boolean isRunning() {
        return DroniXExtendedMenuActivity.exec("/system/bin/ps").contains("mini_httpd");
    }

    public WebServer() {
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
}