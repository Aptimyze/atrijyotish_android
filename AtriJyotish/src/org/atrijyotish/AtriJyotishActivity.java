 /*
** Module      : AtriJyotishCalc
** File:       : AtriJyotishActivity.cpp
** Description : Main Activity
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

import org.bphs.atrijyotish_calc.*;
import org.bphs.atrijyotish_gui.*;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
//import android.app.ActionBar;
import android.content.Context;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
//import android.text.Html;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
//import android.graphics.drawable.ColorDrawable;

public class AtriJyotishActivity extends ActionBarActivity {
	Context ct;
	// Hardware type: Mobile (<5") OR 7" or 10"
	int hardwareType;    // 1=Small (< 4"); 2 Normal (4-5"); 3 Large (7"); 4 XLarge (10")
	String dataPath;

	ViewPager mViewPager;
	CustomPagerAdapter pPageAdapter;

	Settings pSettings;
	
	FragDTP	    pFragDTP;
	FragChart 	pFragChart;
	FragGraha   pFragGraha;
	FragDasa	pFragDasa;
	FragNarayanaDasa pFragNarayanaDasa;

	Globals mGlobals;
	InputMethodManager imm;
	PackageInfo versionInfo;
	
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
		
		setContentView(R.layout.atri_jyotish_pager);

		ct = getApplicationContext();
		dataPath= "/sdcard/atrijyotish/";

		mGlobals = new Globals(ct, dataPath + "/log/");
	    mGlobals.AppendLog();
	    mGlobals.AppendLog("AtriJyotish: Start Activity ");
	    
		new AtriJyotishEULA(this).show();
	    
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        versionInfo = getPackageInfo();
        final String copyKey = "CopyAssetFiles_" + versionInfo.versionName;

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasBeenShown = prefs.getBoolean(copyKey, false);
        //if(hasBeenShown == false)
        {
		    new CopyAssetFiles(".*\\.se1", ct, dataPath + "ephe", false).copy();   // Ephe files
		    new CopyAssetFiles(".*\\.db",  ct, dataPath + "db", false).copy();     // Place DB
		    new CopyAssetFiles(".*\\.xml", ct, dataPath + "ajh",true).copy();      // Atri Jyotish formed files (*.xml)
		    new CopyAssetFiles(".*\\.jhd", ct, dataPath + "jhd", true).copy();     // J Hora files
		    new CopyAssetFiles(".*\\.ini", ct, dataPath, true).copy();             // J Hora files
		    new CopyAssetFiles(".*\\.log", ct, dataPath + "log",true).copy();      // log files
		    
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(copyKey, true);
            editor.commit();
    	    mGlobals.AppendLog("AtriJyotish2: Files copied: " + copyKey);
        }
        
		int  layout = ct.getResources().getConfiguration().screenLayout;
		hardwareType = (layout & 15);

		pSettings = new Settings(dataPath);

/*		
		try {		
		}catch (Exception e) {
		    mGlobals.AppendLog("Activity: FM: " + e.toString());
		    e.printStackTrace();
		}
*/

    	pFragDTP = new FragDTP();
    	pFragChart = new FragChart(pSettings);
    	pFragGraha = new FragGraha();
    	pFragDasa = new FragDasa();
    	pFragNarayanaDasa = new FragNarayanaDasa();
    	
    	pPageAdapter = new CustomPagerAdapter(getSupportFragmentManager(), ct);
        
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(pPageAdapter);
        mViewPager.setOffscreenPageLimit(6);

	    mGlobals.AppendLog("Pages created");
	}

    public void SetChart(NativeData pNative) {
//	    mGlobals.AppendLog("Activity: " + pNative.mName);
		try{				
	    	pFragChart.SetChart(pNative);
	        ArrayList<String> pList = pFragChart.GetPlanetInfo();
	        pFragGraha.SetPlanetInfo(pList);
	        
	        VimsottariDasa pvDasa = pFragChart.GetVDasa();
	        pFragDasa.SetVDasa(pvDasa);

			// Set Narayana Dasa
	        NarayanaDasa pNDasa = new NarayanaDasa();
	        pNDasa.SetChart(pFragChart.GetChart());
	        pFragNarayanaDasa.SetNarayanaDasa(pNDasa);
	        
		    mViewPager.setCurrentItem(1);  
		}catch (Exception e) {
		    mGlobals.AppendLog("Activity: SetChart: " + e.toString());
		    e.printStackTrace();
		}
    }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		View view = this.getCurrentFocus();
	    if (view != null)
	        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		int id = item.getItemId();
		switch (id) {
		  default:
		  case R.id.itmNative:
 			  mViewPager.setCurrentItem(0);  
			  break;
			
		  case R.id.itmChart:
 			  mViewPager.setCurrentItem(1);  
			  break;

		  case R.id.itmGraha:
 			  mViewPager.setCurrentItem(2);  
			  break;
			  
		  case R.id.itmDasa:
 			  mViewPager.setCurrentItem(3);  
			  break;

		  case R.id.itmNDasa:
 			  mViewPager.setCurrentItem(4);  
			  break;
			  
		  case R.id.itmHelp: {
	     	    FragmentManager fm = getSupportFragmentManager();
	     	    InfoDialog editDialog = InfoDialog.newInstance(ct, "Atri Jyotish" + versionInfo.versionName, R.layout.atri_help);
	    	    editDialog.show(fm, "");
			}
		    break;

		  case R.id.itmSettings: {
	     	    FragmentManager fm = getSupportFragmentManager();
	     	    SetDialog editDialog = SetDialog.newInstance(ct, "Atri Jyotish: Settings", R.layout.atri_settings, pSettings);
	    	    editDialog.show(fm, "");
			}
		    break;
		    
		  case R.id.itmAbout: {
	     	    FragmentManager fm = getSupportFragmentManager();
	     	    InfoDialog editDialog = InfoDialog.newInstance(ct, "About: Atri Jyotish "+ versionInfo.versionName, R.layout.atri_about);
	     	    //String pTitle = "<b><font color='red'> About: Atri Jyotish "+ versionInfo.versionName + "</font></b>";
	     	    //editDialog.getDialog().setTitle(Html.fromHtml(pTitle));
	    	    editDialog.show(fm, "");
			}
		    break;
		}
		
		return super.onOptionsItemSelected(item);
	}


    class CustomPagerAdapter extends FragmentPagerAdapter {
    	 
        Context mContext;
 
        public CustomPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }
 
        @Override
        public Fragment getItem(int position) {
        	Fragment frag = null;
            Bundle args = new Bundle();
            args.putInt("page_position", position);
            args.putInt("hType", hardwareType);
            args.putString("path", dataPath);
            
        	switch(position) {
        	case 0:
        	default:
                frag = (Fragment) pFragDTP;
        		break;

        	case 1:
       			frag = (Fragment) pFragChart;
        		break;

        	case 2:
                frag = (Fragment) pFragGraha;
        		break;
      		
        	case 3:
                frag = (Fragment) pFragDasa;
        		break;

        	case 4:
                frag = (Fragment) pFragNarayanaDasa;
        		break;
        	}
        	
            frag.setArguments(args);
            return frag;
        }
 
        @Override
        public int getCount() {
            return 5;
        }

/*
        @Override
        public CharSequence getPageTitle(int position) {
        	//String [] tNames = {"Query", "Result", "Chart", "CA"};
        	String [] tNames = {"Q", "R", "Ch", "CA"};
            SpannableStringBuilder sb = new SpannableStringBuilder(tNames[position]); // space added before text for convenience
         
            Drawable drawable = mContext.getResources().getDrawable( R.drawable.m_query);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

       
            mGlobals.AppendLog(String.format(" ==> %d %d  <==", drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
         
            return sb;
        }
*/
        
    }

    
}
