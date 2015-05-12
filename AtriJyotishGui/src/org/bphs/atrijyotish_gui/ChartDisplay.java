/*
** Module      : AtriJyotishCalc
** File:       : ChartDisplay.cpp
** Description : Display of chart in South Indian format
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

import java.util.ArrayList;
import java.lang.Math;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import android.view.View.OnClickListener;
import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
import org.bphs.atrijyotish_calc.*;


public class ChartDisplay extends View  {
	int oX = 0, oY = 0;
	int mWidth=400, mHeight=400;
	Context ct;
	
	ChartData [] pCharts;
	int nCharts=1, curChart=0;
	ChartData pCurChart;
	ArrayList<String> sCurDasa = new ArrayList<String>();
	String [] pCurDasa;

	Rect [] signRects = new Rect[12];
	Rect centerRectUpper = new Rect(1,1,1,1);
	Rect centerRectLower = new Rect(1,1,1,1);
	int nPlanets = 9;
	int nSigns = 12;
	int drawMode = 1;   // 1=D-1; 2=D-9; 3=D-1 + D-9
	int eventX=10, eventY =10;
	int hardwareType=2;

	public ChartDisplay (Context context) {
		super(context);
		ct = context;
		sCurDasa.clear();
		hardwareType = 2;
		 
		setOnTouchListener(new OnTouchListener() {  
		     @Override
		     public boolean onTouch(View v, MotionEvent event) {
		      //textview.setText("Event captured!");      
		 	    eventX = (int)event.getX();
			    eventY = (int)event.getY();
			    if (centerRectLower.contains(eventX, eventY)) {
			       drawMode++;
			       if (drawMode == 4) drawMode = 1;
			    }
			    else if (centerRectUpper.contains(eventX, eventY)) {
			    	curChart = (curChart +1) % nCharts;
					pCurChart = pCharts[curChart];
					
					int nLines = (pCurDasa.length/nCharts);
					sCurDasa = new ArrayList<String>();   		sCurDasa.clear();

					int ii = curChart * nLines;
			        for (int i=0;i<nLines;i++) 
			        	sCurDasa.add(pCurDasa[ii+i]);
			    }
			    
			    invalidate();
			    return false;
		     }
		    });
		
//		mWidth = width; mHeight = height;
	}

	public void SetHardwareType(int tHardwareType) {
		hardwareType = tHardwareType;
	}
	
	public void SetChartData(ChartData [] cd, String[] cDasa) {
		pCharts = cd;
		nCharts = pCharts.length;
		if (curChart >= nCharts) curChart = nCharts-1;
		pCurChart = pCharts[curChart];
		
		pCurDasa = cDasa;
		int nLines = (pCurDasa.length/nCharts);
		sCurDasa = new ArrayList<String>();   		sCurDasa.clear();

		int ii = curChart * nLines;
        for (int i=0;i<nLines;i++) 
        	sCurDasa.add(pCurDasa[ii+i]);
	}

	/*
	public void SetCurrentDasa(ArrayList<String> tCurDasa) {
		sCurDasa = tCurDasa;
	}
*/
	
	public void SetFrameSize(int width, int height) {
		mWidth = width; mHeight = height;
	}
	
	@Override 
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		oX = 0; oY = 0;
		mWidth = canvas.getWidth();
		mHeight = canvas.getHeight();
		
		//Custom draw code
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		canvas.drawPaint(paint);

		DrawChartLines(canvas, paint);
		DrawSouthIndianChart(canvas, paint);
		
		// Undo rotate
		canvas.restore();
	}		
	
	private void DrawChartLines(Canvas canvas, Paint paint) {
		// red  rect
		paint.setAntiAlias(false);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		
		// CalcSigns
		int iSign = 0;
		int bWid = (mWidth/4);  int bHgt = (mHeight/4);
		int sX = oX, sY = oY;
		Rect []tRects = new Rect[20];
		for (int i=0;i<4;i++) {
			for (int j=0;j<4;j++) {
				Rect r = new Rect();
				r.left = sX; r.top  = sY; r.right  = sX + bWid; r.bottom = sY + bHgt;
				tRects[iSign] = r;
				sX += bWid;
				iSign++; 
			}
			sX = oX;
			sY = sY + bHgt;
		}

		int [] iSigns = {1, 2, 3, 7, 11, 15, 14, 13, 12, 8, 4, 0}; 


		for (int i=0;i<12;i++) {
			signRects[i] = tRects[iSigns[i]];
			canvas.drawRect(signRects[i], paint);
		}
		
		centerRectUpper.left   = signRects[0].left;
		centerRectUpper.right  = signRects[1].right;
		centerRectUpper.top    = signRects[10].top;
		centerRectUpper.bottom = signRects[10].top + (signRects[6].top - signRects[10].top)/2;
				
		centerRectLower.left   = signRects[0].left;
		centerRectLower.right  = signRects[1].right;
		centerRectLower.top    = signRects[10].top + (signRects[6].top - signRects[10].top)/2;
		centerRectLower.bottom = signRects[6].top;
		
/*		
		//Draw rotated text
		paint.setStyle(Style.FILL);
		paint.setTextSize(30);
		canvas.drawText("Su Mo", 10, 40, paint);
		canvas.drawText("Ju Ve", 10, 70, paint);
	
		// 720x 1280    656x 1054
		//String sTxt = String.format(" w: %d  h %d", mWidth, mHeight);
		//canvas.drawText(sTxt, 10, 170, paint);
*/
		
		// Draw image from resources
	    Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.atrijyotish);
	    
	    Rect rcDest = new Rect((mWidth*3/8), (mHeight*7/16), (mWidth*3/8)+mBitmap.getWidth()/2, (mHeight*7/16)+mBitmap.getHeight()/2);
		canvas.drawBitmap(mBitmap, null, rcDest, paint);
    }

	private void DrawSouthIndianChart(Canvas canvas, Paint paint) {
		
		int lagnaSign = pCurChart.GetLagnaSign();
		String [] sGraha = {
				"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke", " ",
				"AL", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "UL", "Ar", "Ak",
				"VL", "HL", "GL", "SL", "PP"}; 

		int scSign = (7-lagnaSign) + 1;
		if (scSign <= 0) scSign += 12;
		
		int aqSign = scSign + 3;
		if (aqSign > 12) aqSign -= 12;
		
		if (aqSign == 12) sGraha[nPlanets+nSigns+1] = String.format("ULr", aqSign);
		else              sGraha[nPlanets+nSigns+1] = String.format("A%dr", aqSign);
		
		if (scSign == 12) sGraha[nPlanets+nSigns+2] = String.format("ULk", scSign);
		else              sGraha[nPlanets+nSigns+2] = String.format("A%dk", scSign);
		
		//paint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
		int textSize= 32;
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		//paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		paint.setColor(Color.BLUE);
		
		// Display Native Name
		oX = signRects[0].left + 50;
		oY = signRects[10].top + 50;   // One char height
		paint.setTextSize(textSize);
		canvas.drawText(String.format("%-15.15s", pCurChart.gpName), oX, oY, paint);
		
		// Display current dasa
		if (sCurDasa.size() > 0) {
			oX = signRects[0].left + 50;
			oY = oY + 50;   // One char height
			paint.setTextSize(textSize);
			//paint.setTypeface(Typeface.MONOSPACE);
			//paint.setColor(Color.rgb(102, 102, 255));
			canvas.drawText(sCurDasa.get(0), oX, oY, paint);  oY += 50;
			canvas.drawText(sCurDasa.get(1), oX, oY, paint);  oY += 50;
			canvas.drawText(sCurDasa.get(2), oX, oY, paint);
		}
		
		// Draw Vaara and Tithi in the middle of chart
		ArrayList<String> pAnalysis = pCurChart.getAnalysis();
		String tStr = pAnalysis.get(0) + "; " + pAnalysis.get(1);
		
		oX = signRects[7].left + 5;
		oY = signRects[7].top - 110;   // 2 char height
		paint.setTextSize(textSize);
		paint.setColor(Color.BLUE);
		canvas.drawText(tStr, oX, oY, paint);

		// Draw Yoga
		tStr = pAnalysis.get(2);
		oY = oY + 50;   // 2 char height
		paint.setTextSize(textSize);
		paint.setColor(Color.BLUE);
		canvas.drawText(tStr, oX, oY, paint);
		
		// Draw Karana
		tStr = pAnalysis.get(3);
		oY = oY + 50;   // 2 char height
		paint.setTextSize(textSize);
		paint.setColor(Color.BLUE);
		canvas.drawText(tStr, oX, oY, paint);
		
		// Draw chart
		textSize = 40;
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		if (hardwareType < 2) {
			textSize= 40;
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		}
		
		// Lagna
		if ((drawMode & 1) > 0) {          // Display D-1
			// Lagna
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setColor(Color.rgb(255, 255, 125));
			canvas.drawRect(signRects[lagnaSign], paint);
		}
		
		int D9LagnaSign;
		if ((drawMode & 2) > 0) {          // Display D-9
			D9LagnaSign = pCurChart.GetD9LagnaSign();
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setColor(Color.rgb(204, 204, 255));
			canvas.drawRect(signRects[D9LagnaSign], paint);
		}		
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.RED);
//		paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		//paint.setTypeface(Typeface.MONOSPACE);
		
		// Planets
		ArrayList<ArrayList<Integer>> signObjsD1 = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> signObjsD9 = new ArrayList<ArrayList<Integer>>();
		
		for (int i=0;i<nSigns;i++) {
			ArrayList<Integer> temp = pCurChart.GetObjectsInSign(i);
			signObjsD1.add(temp);
			temp = pCurChart.GetD9ObjectsInSign(i);
			signObjsD9.add(temp);
		}

		// TODO: Find Text size in pixels so that text can be formated
		paint.setTextSize(textSize);
		//paint.setTypeface(Typeface.MONOSPACE);

		for (int i=0;i<nSigns;i++) {
			ArrayList<Integer> temp = (ArrayList<Integer>)signObjsD1.get(i);
				if (temp.size() % 2 ==1) temp.add(nPlanets);

			temp = (ArrayList<Integer>)signObjsD9.get(i);
				if (temp.size() % 2 ==1) temp.add(nPlanets);
		}
		
		int oX, oY, cHgt = 40;  // TODO Adjust 40 as Char height
		ArrayList<Integer> temp;
		
		for (int i=0;i<nSigns;i++) {
			if ((drawMode & 1) > 0) {
				paint.setColor(Color.BLACK);
				temp = (ArrayList<Integer>)signObjsD1.get(i);
				if (temp.size() > 0) {
					oX = signRects[i].left + 10;
					oY = signRects[i].top  + 10 + 40;   // One char height
					for (int j=0;j<temp.size();j += 2) {
					    tStr = String.format("%2s",   sGraha[Math.abs(temp.get(j))]);
					    if (temp.get(j) < 0) tStr += "'";
					    tStr += String.format(" %2s", sGraha[Math.abs(temp.get(j+1))]);
					    if (temp.get(j+1) < 0) tStr += "'";
						canvas.drawText(tStr, oX, oY, paint);
						oY += cHgt;
					}
				}
			}

			if ((drawMode & 2) > 0) {
				temp = (ArrayList<Integer>)signObjsD9.get(i);
				paint.setColor(Color.RED);
				
				if (temp.size() > 0) {
					oX = signRects[i].left + 10;
					oY = signRects[i].bottom  - 20;   // One char height
					for (int j=0;j<temp.size();j += 2) {
					    tStr = String.format("%2s",   sGraha[Math.abs(temp.get(j))]);
					    if (temp.get(j) < 0) tStr += "'";
					    tStr += String.format(" %2s", sGraha[Math.abs(temp.get(j+1))]);
					    if (temp.get(j+1) < 0) tStr += "'";
						canvas.drawText(tStr, oX, oY, paint);
						oY -= cHgt;
					}
				}
			}
		}

/*		
		private void OnClickListener this.clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
					drawMode = ((drawMode + 1)%3);
			}
		};
*/			
			
			
/*		
        // To find text size in pixels
		paint.setTextSize(40);
		paint.setTypeface(Typeface.MONOSPACE);

		Rect bounds = new Rect();
		int text_height = 0, text_width = 0;
		String text = "SuM";
		paint.getTextBounds(text, 0, text.length(), bounds);
		text_height =  bounds.height();
		text_width =  bounds.width();
		
		canvas.drawText(text, 10, 40, paint);
		String tStr = String.format("W: %d    H: %d :  %d", text_width, text_height, mWidth);
		canvas.drawText(tStr, 10, 40 + text_height+ 15, paint);
*/		
		
/*		// To Draw text in a given path
		paint.setTextSize(30);

		Path path = new Path();
		String s = "Atri Jyotish version 1.0 Test text";
		//path.addCircle(mWidth/2, mHeight/2, 150, Direction.CW);
		//path.addRect(mWidth/3, mHeight/3, mWidth*2/3, mHeight*2/3, Direction.CCW);
		float factW = mWidth/4;
		float factH = mHeight/4;
		RectF rectf = new RectF(factW, factH, (float)(factW*2.5), (float)(factH*2.5));
		path.addArc(rectf, (float)135.0, (float)220.0);

	    canvas.drawTextOnPath(s, path, 1, 1, paint);
	    setLayerType(View.LAYER_TYPE_SOFTWARE, null); 
*/
		
	}


}


/*
Collections.sort(testList);
Collections.reverse(testList);
*/
