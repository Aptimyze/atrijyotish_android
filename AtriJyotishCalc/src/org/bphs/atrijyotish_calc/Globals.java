/*
** Module      : AtriJyotishCalc
** File:       : Globals.cpp
** Description : Global information. Log
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

import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;


public class Globals {
	Context mContext;
	String dataPath;
	
    public Globals(Context context, String path){
        mContext = context;
    	dataPath = path;
    }

    
    public void AppendLog()
    {       
	    File logFile = new File(dataPath + "log.txt");
	    logFile.delete();
    }    
    
    public void AppendLog(String text)
    {       
       File logFile = new File(dataPath + "log.txt");
       if (!logFile.exists())
       {
          try
          {
             logFile.createNewFile();
          } 
          catch (IOException e)
          {
             // TODO Auto-generated catch block
             e.printStackTrace();
          }
       }
       try
       {
          //BufferedWriter for performance, true to set append to file flag
          BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
          buf.append(text);
          //buf.append("\r\n");
          buf.newLine();
          buf.close();
       }
       catch (IOException e)
       {
          // TODO Auto-generated catch block
          e.printStackTrace();
       }
    }    
    
/*    
	public void WriteTrace(String path, String tStr) {
		
		try {
			FileOutputStream fop = null;
			File file;			 
			file = new File(dataPath + path);
			if (!file.exists()) {
				file.createNewFile();
			}
			fop = new FileOutputStream(file);
			//fop = mContext.openFileOutput(dataPath + path, mContext.MODE_APPEND);
 
			// get the content in bytes
			byte[] contentInBytes = tStr.getBytes();
 
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
 
			System.out.println("Done");
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
}
