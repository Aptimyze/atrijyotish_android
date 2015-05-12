/*
** Module      : AtriJyotishCalc
** File:       : NativeData.cpp
** Description : Maintain Native data (DTP)
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.*;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
 









import android.content.Context;

import org.xmlpull.v1.XmlSerializer;


//import android.util.Log;
import android.util.Xml;

public class NativeData {
    public String mName = "Swami";
    public int mGender = 0;  // 0 Male; 1 female
    public int mYear = 1863;
    public int mMonth = 1;
    public int mDay = 12;
    public int mHour = 5;
    public int mMinute = 53;
    public int mSec = 0;
    
    public String mPlace = "Calcutta";
    public String mCountry = "India";
    public String mState = "West Bengal";
    
    public int mLongD = 88, mLongM= 22, mLongS = 0, mLongEW = 1;
    double mLongitude = (mLongD*mLongEW) + ((double)mLongM / 60.0) + ((double)mLongS/3600.);    // Gudivada
    public int mLatD = 22, mLatM= 32, mLatS = 0, mLatNS = 1;
    double mLatitude = (mLatD*mLatNS) + ((double)mLatM / 60.0) + ((double)mLatS/3600.);    // Gudivada
    public int mTzH = 5, mTzM = 30, mTzEW = -1;
    double mTz = (mTzH + ((double)mTzM/60.)) * mTzEW; // IST

    public String sLatitude = String.format("%03d:%02d:%02d:%1s", mLatD, mLatM, mLatS, ((mLatNS == 1)? "N":"S" )); 
    public String sLongitude = String.format("%03d:%02d:%02d:%1s", mLongD, mLongM, mLongS, ((mLongEW == 1)? "E":"W" )); 
    public String sTz = String.format("%02d:%02d:%1s", mTzH, mTzM, ((mTzEW == 1)? "W":"E" )); 
    		
    String sTestStr;
    
	Globals mGlobals;

    public NativeData() {
    	mGlobals = new Globals(null, "/sdcard/atrijyotish/log/");
    }
    
    public void SetDateTime(String sDate, String sTime) {
    	String[] strArray = sDate.split("-");
    	mYear  = Integer.parseInt(strArray[0]);
    	mMonth = Integer.parseInt(strArray[1]);
    	mDay   = Integer.parseInt(strArray[2]);

    	strArray = sTime.split(":");
    	mHour  = Integer.parseInt(strArray[0]);
    	mMinute = Integer.parseInt(strArray[1]);
    	mSec   = Integer.parseInt(strArray[2]);
    }

    public void SetDate(String sDate) {
    	String[] strArray = sDate.split("-");
    	mYear  = Integer.parseInt(strArray[0]);
    	mMonth = Integer.parseInt(strArray[1]);
    	mDay   = Integer.parseInt(strArray[2]);
    }

    public void SetTime(String sTime) {
    	String[] strArray = sTime.split(":");
    	mHour  = Integer.parseInt(strArray[0]);
    	mMinute = Integer.parseInt(strArray[1]);
    	mSec   = Integer.parseInt(strArray[2]);
    }
    
    // TODO Validate and return error code
	public int SetLatLongTz(String tLatitude, String tLongitude, String tTz) {
		String[] strArray = tLatitude.split(":");
		if (strArray.length < 4) return 0; //-1;  // Must have 3 parts 
		
    	mLatD  = Integer.parseInt(strArray[0]);
    	mLatM  = Integer.parseInt(strArray[1]);
    	mLatS   = Integer.parseInt(strArray[2]);

		if (strArray[3].equals("N"))  mLatNS = 1;
		else                          mLatNS = -1;

		sLatitude = tLatitude;
		
		strArray = tLongitude.split(":");
		if (strArray.length < 4) return 0; //-2;  // Must have 3 parts 

		mLongD  = Integer.parseInt(strArray[0]);
    	mLongM  = Integer.parseInt(strArray[1]);
    	mLongS  = Integer.parseInt(strArray[2]);

		if (strArray[3].equals("E")) mLongEW = 1;
		else                         mLongEW = -1;
		
		sLongitude = tLongitude;
		
		strArray = tTz.split(":");
		if (strArray.length < 3) return 0; // -3;  // Must have 3 parts 
    	mTzH  = Integer.parseInt(strArray[0]);
    	mTzM  = Integer.parseInt(strArray[1]);

		if (strArray[2].equals("W")) mTzEW = 1;
		else                         mTzEW = -1;
		
		//mGlobals.AppendLog(String.format(" NativeData: %s: %d %d", tTz, mTzH, mTzM));
		sTz = tTz;
		return 0;
	}

	public void SetPlace(String tPlace, String tState, String tCountry) {
		mPlace = tPlace;
		mState = tState;
		mCountry = tCountry;
	}
	
    public void SaveXML(String pPath) {
        File newxmlfile = new File(pPath);
        try{
            newxmlfile.createNewFile();
        }catch(IOException e)
        {
    		mGlobals.AppendLog("IOException: Exception in create new File");
        }
        
        FileOutputStream fileos = null;
        try{
            fileos = new FileOutputStream(newxmlfile);

        }catch(FileNotFoundException e)
        {
    		mGlobals.AppendLog("FileNotFoundException" + e.toString());
        }
        XmlSerializer serializer = Xml.newSerializer();
        try{
	        serializer.setOutput(fileos, "UTF-8");
	        serializer.startDocument(null, Boolean.valueOf(true));
	        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
	        serializer.startTag(null, "root");
	        serializer.startTag(null, "dtp");
	        	
	        	serializer.attribute(null, "timezone", String.format("%02d:%02d:%1s", mTzH, mTzM, ((mTzEW == 1)? "W":"E" )));
	        	serializer.attribute(null, "longitude", String.format("%03d:%02d:%02d:%1s", mLongD, mLongM, mLongS, ((mLongEW == 1)? "E":"W" )));
	        	serializer.attribute(null, "latitude",   String.format("%03d:%02d:%02d:%1s", mLatD, mLatM, mLatS, ((mLatNS == 1)? "N":"S" )));
	        	serializer.attribute(null, "country", mCountry);
	        	serializer.attribute(null, "state", mState);
	        	serializer.attribute(null, "place", mPlace);
	        	serializer.attribute(null, "time", String.format("%02d:%02d:%02d", mHour, mMinute, mSec));
	        	serializer.attribute(null, "date", String.format("%04d-%02d-%02d", mYear, mMonth, mDay));
	        	serializer.attribute(null, "gender", String.valueOf(mGender));  // TODO
	        	serializer.attribute(null, "name", mName);
	        	
	        serializer.endTag(null, "dtp");
	        serializer.endTag(null,"root");
	        serializer.endDocument();
	        serializer.flush();
	        fileos.close();
        }catch(Exception e)
       {
    		mGlobals.AppendLog("Exception" + "Exception occured in wroting");
       }
    }
    
    public void Load(Context ct, String pPath) {
    	int dot = pPath.lastIndexOf('.');
        String pExt = pPath.substring(dot + 1);
        if (pExt.toLowerCase().contains("xml")) LoadXML(ct, pPath);
        if (pExt.toLowerCase().contains("jhd")) LoadJHD(ct, pPath);
    }

    public void LoadJHD(Context ct, String pPath) {
		sTestStr = "";
//	   mGlobals.AppendLog("JHD1: "+ pPath);

		File f = new File(pPath);
		if (!f.exists())
           return;
		
		try {
			InputStream is = new FileInputStream(pPath);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
		    int dot = pPath.lastIndexOf('.');
		    int sep = pPath.lastIndexOf('/');
		    mName = pPath.substring(sep + 1, dot);
			
		    String read;
			read = br.readLine(); 		mMonth = Integer.valueOf(read);
			read = br.readLine();	    mDay   = Integer.valueOf(read);
			read = br.readLine();	    mYear  = Integer.valueOf(read);
			
			read = br.readLine();   // Time
			
	    	dot = read.lastIndexOf('.');
	        String pRight = read.substring(dot+1);
	        String pLeft = read.substring(0, dot);
			//sTestStr +=  "  " + pExt + " " + pFile;
			
			mHour = Integer.valueOf(pLeft);
			mMinute = Integer.valueOf(pRight.substring(0,2));
			mSec = 0;
			if (pRight.length() > 2)
			{
				mSec = 0;
				pRight = pRight.substring(2, pRight.length()-2);
				//if ((Integer.valueOf(pRight)) > 0) {
					pRight = "." + pRight;
					mSec = (int) ((Double.valueOf(pRight)) * 60.0);
				//}
			}

			//mGlobals.AppendLog("JHD11: ");

			read = br.readLine();   			// Time Zone
	    	dot = read.lastIndexOf('.');
	        pRight = read.substring(dot+1);
	        pLeft = read.substring(0, dot);
			mTzH = Integer.valueOf(pLeft);
			mTzM = Integer.valueOf(pRight.substring(0,2));
            if (mTzH < 0) {mTzH = - mTzH; mTzEW = -1;}
            else  mTzEW = 1;
        	sTz = String.format("%02d:%02d:%1s", mTzH, mTzM, ((mTzEW == 1)? "W":"E" ));
			//mGlobals.AppendLog("JHD12: ");
			
			read = br.readLine();   			// Longitude
	    	dot = read.lastIndexOf('.');
	        pRight = read.substring(dot+1);
	        pLeft = read.substring(0, dot);
			mLongD = Integer.valueOf(pLeft);
			mLongM = Integer.valueOf(pRight.substring(0,2));
			mLongS = 0;
			if (pRight.length() > 2)
			{
				pRight = pRight.substring(2,pRight.length()-2);
				pRight = "." + pRight;
				mLongS = (int) ((Double.valueOf(pRight)) * 60.0);
			}
            if (mLongD < 0) {mLongD = - mLongD; mLongEW = 1;}
            else mLongEW = -1;
        	sLongitude = String.format("%03d:%02d:%02d:%1s", mLongD, mLongM, mLongS, ((mLongEW == 1)? "E":"W" ));
			//mGlobals.AppendLog("JHD13: ");
		
			read = br.readLine();   			// Latitude
	    	dot = read.lastIndexOf('.');
	        pRight = read.substring(dot+1);
	        pLeft = read.substring(0, dot);
			mLatD = Integer.valueOf(pLeft);
			mLatM = Integer.valueOf(pRight.substring(0,2));
			mLatS = 0;
			if (pRight.length() > 2)
			{
				pRight = pRight.substring(2, pRight.length()-2);
				pRight = "." + pRight;
				mLatS = (int) ((Double.valueOf(pRight)) * 60.0);
			}
            if (mLatD < 0) {mLatD = - mLatD; mLatNS = -1;}
            else mLatNS = 1;
        	sLatitude = String.format("%03d:%02d:%02d:%1s", mLatD, mLatM, mLatS, ((mLatNS == 1)? "N":"S" ));
			//mGlobals.AppendLog("JHD14: ");

			read = br.readLine();
			if (read == null)  { is.close(); return;} 			// Skip next 5 lines
			read = br.readLine();   if (read == null) { is.close(); return;}
			read = br.readLine();    if (read == null)  { is.close(); return;}
			read = br.readLine();    if (read == null)  { is.close(); return;}
			read = br.readLine();    if (read == null)  { is.close(); return;}
            
			//mGlobals.AppendLog("JHD15: ");
			read = br.readLine();     if (read == null)  { is.close(); return;}
			mPlace   = read;			//  Place
			read = br.readLine();     if (read == null)  { is.close(); return;}
			mCountry = read;			//  Place: Country
			mState = "";
			//mGlobals.AppendLog("JHD16: " + mPlace+ mCountry);
			
			read = br.readLine();   	  if (read == null)  { is.close(); return;}		// Skip next 5 lines
			read = br.readLine();		  if (read == null)  { is.close(); return;}
			read = br.readLine();		  if (read == null)  { is.close(); return;}

			read = br.readLine(); 		  if (read == null)  { is.close(); return;}
//			sTestStr += read;
			//mGlobals.AppendLog("JHD16-Gender: " + read);
			
			mGender = Integer.valueOf(read);
			if (mGender > 0) mGender--;
			//mGlobals.AppendLog("JHD17: ");

            is.close();
		} catch (IOException e) {
		   mGlobals.AppendLog("JHDIOExp: "+ pPath +" => "+ e.toString());
			//e.printStackTrace();
		} catch (Exception e) {
			   mGlobals.AppendLog("JHD Exp: "+ pPath +" => "+ e.toString());
				//e.printStackTrace();
		}
		
		//mGlobals.AppendLog("JHD2: "+ pPath);
    }
    
    
    public void LoadXML(Context ct, String pPath) {
		ArrayList<String> userData = new ArrayList<String>();
		userData.clear();

		File f = new File(pPath);
		if (!f.exists())
           return;
		
		try {
			InputStream is = new FileInputStream(pPath);
			
			InputStreamReader isr = new InputStreamReader(is);
			StringBuilder sb=new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String read = br.readLine();

			while(read != null) {
			    sb.append(read);
			    read = br.readLine();
			}
		
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    XmlPullParser parser = factory.newPullParser();
		    parser.setInput(new StringReader(sb.toString()));
            parseXML(parser);
		    
            is.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	public void parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		sTestStr = "";
        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
			String tStr = parser.getName();
			int eventType = parser.getEventType();
			if (eventType == XmlPullParser.START_TAG) {
			   if (tStr.equals("dtp")) {	
				   //int count = parser.getAttributeCount();
				   String tStr1, tStr2, tStr3;
				   tStr3 = parser.getAttributeValue(null, "timezone");
				   tStr2 = parser.getAttributeValue(null, "longitude");
				   tStr1 = parser.getAttributeValue(null, "latitude");
				   /*int err = */SetLatLongTz(tStr1, tStr2, tStr3);
				   //mGlobals.AppendLog(String.valueOf(err) + " => "+ tStr1+"=="+tStr2+"=="+tStr3);
				   sTestStr = sTestStr + " " + tStr1+" ; " + tStr2+" ; " + tStr3+" ; ";
				   tStr3 = parser.getAttributeValue(null, "country");
				   tStr2 = parser.getAttributeValue(null, "state");
				   tStr1 = parser.getAttributeValue(null, "place");
				   sTestStr = sTestStr + tStr1+" ; " + tStr2+" ; " + tStr3+" ; ";
				   SetPlace(tStr1, tStr2, tStr3);
				   tStr2 = parser.getAttributeValue(null, "time");
				   tStr1 = parser.getAttributeValue(null, "date");
				   sTestStr = sTestStr + tStr1+" ; " + tStr2+" ; ";
				   SetDateTime(tStr1, tStr2);
				   mName = parser.getAttributeValue(null, "name");
				   tStr1 = parser.getAttributeValue(null, "gender");
				   mGender = Integer.valueOf(tStr1);
				   sTestStr = sTestStr + tStr1+" ; " + mName;
			   }
			}
					
            parser.next();
                
        }
        
    }
}


/*
 <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<dtp
    name = "Bhanu"
    gender = "0"
    date = "1955-12-29"
    time = "05:53:40"
    place="Gudivada"
    state = "Andhra pradesh"
    country="India"
    latidue="016:27:00 E"
	longitude="089:59:00 N"
	timezone="05:30 E"
/>

<event
	nevents = 0
/>

*/

