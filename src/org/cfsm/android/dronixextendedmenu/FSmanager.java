package org.cfsm.android.dronixextendedmenu;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.List;

/*
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: ${DATE}
 */

class FSmanager {
    private static final int MOUNT_RO = 0;
    private static final int MOUNT_RW = 1;

    	public void reMountSystem(int mode) throws IOException, InterruptedException, RootToolsException {
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

    public void setSSHpasswordFileRW() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod +rw /etc/ssh/passwd");
    }

    public void setSSHpasswordFileRO() throws IOException, InterruptedException, RootToolsException {
		RootTools.sendShell("/system/xbin/chmod go-w /etc/ssh/passwd");
	}
}
