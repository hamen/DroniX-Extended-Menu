package org.cfsm.android.dronixextendedmenu;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: 8/27/11
 */
class DEMUtil {
    Context c;
    public DEMUtil(Activity dem) {
        c = dem.getApplicationContext();
    }

    public void ts(String msg){
        Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
    }
    public void tl(CharSequence msg){
        Toast.makeText(c, msg, Toast.LENGTH_LONG).show();
    }
}

