/*
** Module      : AtriJyotishGui
** File:       : AtriJyotishEULA.cpp
** Description : License agreement display
**
** Contact     : AstroOpenSource@gmail.com
**
** Web site    : http://AtriJyotishAndroid.sourceforge.net/
** Download    : http://sourceforge.net/projects/atrijyotishandroid/
**
** This software is provided "as is", under GNU public license with 
** NO WARRANTY OF ANY KIND, either express or implied.
** No author or distributor accepts any responsibility for the consequences of using it,
** or for whether it serves any particular purpose or works at all. In no event shall   
** the authors or copyright holders be liable for any claim, damages or 
** other liability, arising from the use or performance of this software.
**
*/


package org.bphs.atrijyotish_gui;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
//import android.text.Html;
import android.content.SharedPreferences;


public class AtriJyotishEULA {
	 
    private String EULA_PREFIX = "eula_";
    private Activity mActivity;
 
    public AtriJyotishEULA(Activity context) {
        mActivity = context;
    }
 
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
             pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
 
     public void show() {
        PackageInfo versionInfo = getPackageInfo();
 
        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false);
        if(hasBeenShown == false){
 
            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName;
 
            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.updates) + "\n\n" 
            		+ mActivity.getString(R.string.eula) + "\n\n"
            		+ mActivity.getString(R.string.eula1) + "\n\n"
             		+ mActivity.getString(R.string.eula2) + "\n\n"
		    		+ mActivity.getString(R.string.eula3);

//            .setMessage(Html.fromHtml(mActivity.getText(R.string.eulahtml)))
            
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Accept", new Dialog.OnClickListener() {
 
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Mark this version as read.
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(eulaKey, true);
                            editor.commit();
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Close the activity as they have declined the EULA
                            mActivity.finish();
                        }
 
                    });
            builder.create().show();
        }
    }
 
}
