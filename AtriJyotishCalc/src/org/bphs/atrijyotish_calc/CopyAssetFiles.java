/*
** Module      : AtriJyotishCalc
** File:       : CopyAssetFiles.cpp
** Description : Copy asset files to respective directries
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

import android.content.Context;
import android.content.res.AssetManager;
//import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mack on 06.10.2014.
 */
public class CopyAssetFiles {
    String pattern;
    Context ct;
    String pPath = "/sdcard/AtriJyotish/";
    boolean bOverWrite;

    public CopyAssetFiles(String pattern, Context ct, String tPath, boolean aOverwrite) {
        this.pattern = pattern;
        this.ct = ct;
        pPath = tPath;
        bOverWrite = aOverwrite;
    }
    public void copy() {
        AssetManager assetManager = ct.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            //Log.e("tag", "Failed to get asset file list.", e);
        }

//        String outdir = ct.getFilesDir() + File.separator + "/ephe";
        String outdir = pPath;  // So that the files are visible
        
        outdir += File.separator;
        File saveDir = new File(outdir);
        if(!saveDir.exists()){
            saveDir.mkdirs(); 
        }
        //new File(outdir).mkdirs(); 
        //outdir += File.separator;

        for(String filename : files) {
            if (!filename.matches(pattern)) continue;
            if (new File(outdir + filename).exists()) {
            	if (!bOverWrite)
                   continue;
            }
            
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);

                File outFile = new File(outdir, filename);

                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(IOException e) {
                //Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
