package org.dronix.android.dronixextendedmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Author: Ivan Morgillo
 * E-mail: imorgillo [at] gmail [dot] com
 * Date: 8/26/11
 */
public class SSHpasswordChange extends Activity {
    private Button change_button;
    private Button reset_button;
    private DEMUtil alert;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sshpasswordchange);
        alert = new DEMUtil(this);

        change_button = (Button)this.findViewById(R.id.ssh_password_change_button);
        change_button.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         EditText ssh_oldpassword_change_textview = (EditText) findViewById(R.id.ssh_oldpassword_edittext);
                         EditText ssh_newpassword_change_textview = (EditText) findViewById(R.id.ssh_newpassword_edittext);

                         String old_password = ssh_oldpassword_change_textview.getText().toString();
                         String new_password = ssh_newpassword_change_textview.getText().toString();

                         if (old_password.length() == 0 || new_password.length() == 0) {
                             alert.tl(getString(R.string.ssh_password_empty));
                         }
                         else {
                             SSH ssh = new SSH(SSHpasswordChange.this);
                             String current_password = SSH.getPassword();

                             if (current_password.equals(old_password)) {
                                 if(ssh.setPassword(new_password)) {
                                     alert.tl(getString(R.string.ssh_password_change_success) + " " + new_password);
                                 }
                                 else {
                                     alert.tl(getString(R.string.error));
                                 }
                             }
                             else {
                                 alert.tl(getString(R.string.ssh_password_notmatching));
                             }
                         }
                     }
                 });
        reset_button = (Button)this.findViewById(R.id.ssh_password_reset_button);
        reset_button.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         resetPassword();
                     }
                 });
    }

    private void resetPassword() {
        SSH ssh = new SSH(SSHpasswordChange.this);
        if(ssh.setPassword("android")) {
            alert.tl(getString(R.string.ssh_password_change_success) + " android" );
        }
    }
}