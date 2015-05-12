/*
** Module      : AtriJyotishCalc
** File:       : Settings.cpp
** Description : Management of settings 
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


package org.bphs.atrijyotish_calc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import swisseph.SweConst;

public class Settings {
	Globals mGlobals;
	String gdDataPath;

	// Settings
	public int gsSunRiseFlag = 2;     // Tip Apparent
	public int gsPlanetCalcFlag = 0;  // 0: Apparent positions     16: True Positions (SEFLG_TRUEPOS)
	public int gsNodesFlag = SweConst.SE_MEAN_NODE;       // 10:Mean nodes   (True nodes = 11)
	public int gsAyanamsaFlag = SweConst.SE_SIDM_LAHIRI;    // Traditional Lahiri  (Only one for the moment)
	
	int [] pAyanamsaList = {SweConst.SE_SIDM_LAHIRI};
	
    ArrayList<String> pAyanamsaStr = new ArrayList<String>(Arrays.asList("Lahiri"));
    
	public Settings(String tPath) {
		gdDataPath = tPath;
		mGlobals = new Globals(null, gdDataPath + "/log/");
		LoadIni(gdDataPath + "settings.ini");
	}
	
	public void Save() {
		SaveIni(gdDataPath + "settings.ini");

		File inf = new File(gdDataPath + "settings.ini");
	    inf.delete();
		String outPath = gdDataPath + "settings.ini" + "__";
	    File of = new File(outPath);
        mGlobals.AppendLog("Settings: rename ");
	    of.renameTo(inf);
        mGlobals.AppendLog("Settings: renamed... ");
	}
	
	public ArrayList<String> GetAyanamsaList() {
		return pAyanamsaStr;
	}
	public int GetAyanamsaIndex() {
		for (int i =0;i<pAyanamsaStr.size();i++) {
			if (gsAyanamsaFlag == pAyanamsaList[i]) return i;
		}
		return 0;
	}

	public void SetAyanamsaIndex(int id) {
		gsAyanamsaFlag = pAyanamsaList[id];
	}

	public int GetPlanetCalcFlagIndex() {
		return gsPlanetCalcFlag/SweConst.SEFLG_TRUEPOS;  // either 0 or 16
	}

	public void SetPlanetCalcFlagIndex(int id) {
		gsPlanetCalcFlag = id * SweConst.SEFLG_TRUEPOS;
	}

	public int GetNodesFlagIndex() {
		return gsNodesFlag - SweConst.SE_MEAN_NODE;
	}

	public void SetNodesFlagIndex(int id) {
		gsNodesFlag = id + SweConst.SE_MEAN_NODE;
	}

	public int GetSunDiscFlagIndex() {
		return (gsSunRiseFlag - 1);               // Flag is from 1 to 4
	}

	public void SetSunDiscFlagIndex(int id) {
		gsSunRiseFlag = id + 1;
	}
	
    int GetNextValue(BufferedReader br) {
        String tStr;
		try {
	        while (true) {
	        	tStr = br.readLine();
	        	if ((tStr.length() > 0) && (!tStr.contains(";")))
	        		return Integer.valueOf(tStr);
	        }
		} catch (IOException e) {
			mGlobals.AppendLog("Settings:GetNextValue: " + e.toString());
			//e.printStackTrace();
		}
		return -1;
    }
	
	private void LoadIni(String pPath) {
		File f = new File(pPath);
		if (!f.exists())
	       return;
		
		try {
			InputStream is = new FileInputStream(pPath);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
		
			gsSunRiseFlag = GetNextValue(br);
			gsPlanetCalcFlag = GetNextValue(br);
			gsNodesFlag = GetNextValue(br);
			gsAyanamsaFlag = GetNextValue(br);
	
	        is.close();
		} catch (IOException e) {
			mGlobals.AppendLog("Settings:LoadIni: " + e.toString());
			//e.printStackTrace();
		}
	}	

    int PutNextValue(BufferedReader br, BufferedWriter bw, int value) {
        String tStr;
		try {
	        while (true) {
	        	tStr = br.readLine();
	        	if ((tStr.length() > 0) && (!tStr.contains(";"))) {
			        bw.append(String.valueOf(value));
			        bw.append("\r\n");
		            mGlobals.AppendLog("Settings: Value " +String.valueOf(value) + " : " + tStr);
		            return 0;
	        	} else if (tStr.length() > 0){
			        bw.append(tStr);
		            mGlobals.AppendLog("Settings: Str " + tStr);
			        bw.append("\r\n");
			        //bw.newLine();
	        	}
	        }
		} catch (IOException e) {
			mGlobals.AppendLog("Settings:PutNextValue: " + String.valueOf(value) + " : " + e.toString());
			//e.printStackTrace();
		}
		return -1;
    }

	private void SaveIni(String pPath) {
		File inf = new File(pPath);
		if (!inf.exists())
	       return;
		
		String outPath = pPath + "__";
	    File of = new File(outPath);
	    of.delete();
	
        try
        {
        	of.createNewFile();
        } 
        catch (IOException e)
        {
           e.printStackTrace();
        }
        
		try {
			InputStream is = new FileInputStream(pPath);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			FileWriter fw = new FileWriter(of, true);
            BufferedWriter bw = new BufferedWriter(fw); 
			
            mGlobals.AppendLog("Settings: i/o Open ");
			PutNextValue(br, bw, gsSunRiseFlag);
			PutNextValue(br, bw, gsPlanetCalcFlag);
			PutNextValue(br, bw, gsNodesFlag);
			PutNextValue(br, bw, gsAyanamsaFlag);
	
            mGlobals.AppendLog("Settings: written");
            
	        is.close();
	        bw.close();
	        fw.close();

//		    inf.delete();
//	        of.renameTo(inf);
		} catch (IOException e) {
			mGlobals.AppendLog("Settings:LoadIni: " + e.toString());
			//e.printStackTrace();
		}
        mGlobals.AppendLog("Settings: Saved ");
	}	
	
	
}
