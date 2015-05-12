/*
** Module      : AtriJyotishCalc
** File:       : FlashActivity.cpp
** Description : Activity to display Falsh screen
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



package org.atrijyotish;


import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


public class FlashActivity extends Activity {
	private Thread logoTimer;
	
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null;
        try {
             pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atri_about);

		PackageInfo versionInfo;
        versionInfo = getPackageInfo();
		String title = R.string.pkgName1 + " " + versionInfo.versionName;
	    setTitle(title);
        Button btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setVisibility(View.GONE);

        logoTimer = new Thread() {
            public void run(){
                try{
                	synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(100);
                    }
                	startActivity(new Intent("org.atrijyotish"));
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                finally{
                    finish();
                }
            }
        };
         
        logoTimer.start();
	}
	
    public boolean onTouchEvent(MotionEvent evt)
    {
        if(evt.getAction() == MotionEvent.ACTION_DOWN)
        {
            synchronized(logoTimer){
            	logoTimer.notifyAll();
            }
        }
        return true;
    }  
}
