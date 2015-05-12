/*
** Module      : AtriJyotishCalc
** File:       : ChartData.cpp
** Description : Calculation of planet and Lagna Information
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

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.DblObj;

//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class ChartData {
	public Globals mGlobals;

	// Settings
	Settings gpSettings;

	// Input Data
	public String gpName = "SriRama";
    public int gpYear = 1955;
    public int gpMonth = 12;
    public int gpDay = 29;
    int gpHour = 5;
    int gpMinute = 53;
    int gpSec = 40;
    
    String gpPlace = "Gudiwada";
    String gpCountry = "India";
    String gpState = "Andhra Pradesh";
    
    int gpLongD = 80, gpLongM= 59, gpLongS = 0, gpLongEW = 1;
    double gpLongitude = (gpLongD*gpLongEW) + ((double)gpLongM / 60.0) + ((double)gpLongS/3600.);    // Gudiwada
    int gpLatD = 16, gpLatM= 27, gpLatS = 0, gpLatNS = 1;
    double gpLatitude = (gpLatD*gpLatNS) + ((double)gpLatM / 60.0) + ((double)gpLatS/3600.);    // Gudiwada
    int gpTzH = 5, gpTzM = 30, gpTzEW = -1;
    double gpTz = (gpTzH + ((double)gpTzM/60.)) * gpTzEW; // IST
    double UTCHour = gpHour + ((double)(gpMinute) / 60.) + ((double)(gpSec)/3600.0) + gpTz;

    // Data files path
    String gpSwissEphePath;  // = "/sdcard/data/ephe";  // Default if not set
    // String sSwissEphePath = Environment.getExternalStorageDirectory().getPath();
    
    SwissEph gpSW;

    // Defaults
	int iniCalcFlag = SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_NONUT;
	int iCalcFlag = iniCalcFlag;
    
    //Output data: Global data
    int gdnPlanets = 9;
    int gdnSigns = 12, gdnHouses = 12;
    double gdAyanamsa;
    double gdJulDay;
    SweDate gdSunRiseTime, gdSunSetTime;
    double gdLagna;
    double gdPlanetPos[]    = new double[9];
    double gdPlanetSpeed[]  = new double[9];
    boolean gdPlanetRetro[] = new boolean[9];
    byte gdPlanetSign[]     = new byte[12];
    
    int gdHouseArudha[] = new int [14];  // 12 houses + 2 double lordship (Sc, Aq)
    int nArudhas = gdnSigns+2;
    int ArudhaShift = gdnPlanets + 1; // nPlanets + one blank (used to make even no of objects)
    String printString;

    byte P_SU=0, P_MO=1, P_MA=2, P_ME=3, P_JU=4, P_VE=5, P_SA=6, P_RA=7, P_KE=8;
	byte L_VARNADA_LAGNA = 0, L_HORA_LAGNA=1, L_GHATI_LAGNA=2, L_SREE_LAGNA=3, L_PRANAPADA_LAGNA=4, nSplLagnas=5;

	double gdSplLagna[] = new double [10];  // Max of 10 spl lagnas	
    int nSplLagnaShift = ArudhaShift + nArudhas;

    // Analysis
	int tithiId, tithiLord;
	double tithiLeft;
	int yogaId, yogaLord;
	double yogaRemain;
	int karanaId, karanaLord, kNameId;
	double karanaRemain;

    String [] pRasiNames = {"Ar", "Ta", "Ge", "Ca", "Le", "Vi", "Li", "Sc", "Sg", "Cp", "Aq","Pi"};
    String [] pNames   = {"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke"};
    String [] pStarNames = {
    		"Aswini", "Bharani", "Krittika", "Rohini", "Mrigashirsha", "Ardra", "Punarvasu", "Pushya", "Ashlesha", 
    		"Magha", "P.Phalguni","U.Phalguni", "Hasta", "Chitra", "Swati", "Vishakha", "Anuradha", "Jyeshtha", 
    		"Mula", "P.Ashadha", "U.Ashadha", "Shravana", "Dhanishtha", "Shatabhisha", "P.Bhadrapada", "U.Bhadrapada", "Revati"
    		};
    String [] pStarLords = {
    		"Ke", "Ve", "Su", "Mo", "Ma", "Ra", "Ju", "Sa", "Me",
    		"Ke", "Ve", "Su", "Mo", "Ma", "Ra", "Ju", "Sa", "Me",
    		"Ke", "Ve", "Su", "Mo", "Ma", "Ra", "Ju", "Sa", "Me"
    		};
    
    // Constant data
	public byte [] lords1 = {2, 5, 3, 1, 0, 3, 5, 2, 4, 6, 6, 4};
	public byte [] lords2 = {2, 5, 3, 1, 0, 3, 5, 8, 4, 6, 7, 4};

	public byte REL_AM=2, REL_M=1, REL_N=0, REL_S=-1, REL_AS=-2;
	byte [][] rNat = {   {REL_M, REL_M, REL_M, REL_N, REL_M, REL_S, REL_S, REL_N, REL_M},
						{REL_M, REL_M, REL_N, REL_M, REL_N, REL_N, REL_N, REL_N, REL_S},
						{REL_M, REL_M, REL_M, REL_S, REL_M, REL_N, REL_N, REL_S, REL_M},
						{REL_M, REL_S, REL_N, REL_M, REL_N, REL_M, REL_N, REL_N, REL_S},
						{REL_M, REL_M, REL_M, REL_S, REL_M, REL_S, REL_N, REL_S, REL_M},
						{REL_S, REL_S, REL_N, REL_M, REL_N, REL_M, REL_M, REL_M, REL_M},
						{REL_S, REL_S, REL_S, REL_M, REL_N, REL_M, REL_M, REL_M, REL_N},
						{REL_M, REL_M, REL_N, REL_N, REL_N, REL_S, REL_S, REL_M, REL_M},
						{REL_M, REL_M, REL_N, REL_N, REL_N, REL_S, REL_S, REL_M, REL_M} };
	int [][] rTemp;
	int [][] rComp;
	
	String sTestStr ="";
	
//    public void Chartdata() {};

	public ChartData(Settings tSettings) {
		gpSettings = tSettings;
		gpSwissEphePath = gpSettings.gdDataPath + "ephe/";
		mGlobals = new Globals(null, gpSettings.gdDataPath + "/log/");
	}
	
	public void SetName(String tName) {
		gpName = tName;
	}

    public void SetDate(int iYear, int iMonth, int iDay)
    {
    	gpYear = iYear; gpMonth = iMonth; gpDay = iDay;
    }

    public void SetTime(int iHour, int iMinute, int iSec)
    {
    	gpHour = iHour;
    	gpMinute = iMinute;
    	gpSec = iSec;
    }

    public void SetPlace(String place, int iLongD, int iLongM, int iLongS, int iLongEW, int iLatD, int iLatM, int iLatS, int iLatNS, int iTzH, int iTzM, int iTzEW)
    {
    	gpLongD = iLongD; gpLongM = iLongM; gpLongS = iLongS; gpLongEW = iLongEW;
    	gpLongitude = (iLongD + ((double)gpLongM/60.0) + ((double)gpLongS/3600.0)) * gpLongEW ; 
    	gpLatD = iLatD; gpLatM = iLatM; gpLatS = iLatS; gpLatNS = iLatNS;
    	gpLatitude =  (iLatD + ((double)gpLatM/60.0) + ((double)gpLatS/3600.0)) * gpLatNS;
    	gpTzH = iTzH;  gpTzM = iTzM;  gpTzEW = iTzEW;
    	gpTz = (iTzH + ((double)iTzM/60.)) *iTzEW;
    	gpPlace = place;
    }
    
    public void compute() {
    	gpSW = new SwissEph(gpSwissEphePath);   // Instances of utility classes
        
        UTCHour = gpHour + ((double)(gpMinute) / 60.) + ((double)(gpSec)/3600.0) + gpTz;
        SweDate sd = new SweDate(gpYear, gpMonth, gpDay, UTCHour);
        gdJulDay = sd.getJulDay();
        
        // Set sidereal mode:
        gpSW.swe_set_sid_mode(gpSettings.gsAyanamsaFlag, 0, 0);  // TODO Ayanamsa Type: Others to add
        gdAyanamsa = gpSW.swe_get_ayanamsa_ut(sd.getJulDay());
        
        ComputeSunRiseSet();
        
        ComputeLagna(sd, gpLongitude, gpLatitude);
        ComputeAllPlanets(sd);
        ComputeSplLagnas();
        ComputeArudha();
        ComputeTithi();
        ComputeYoga();
        ComputeKarana();
        ComputeRelation();
    }
    
    public ArrayList<String> getPlanetInfo()
    {
    	ArrayList<String> pList = new ArrayList<String>(); pList.clear();
    	String pInfo = getDateInfo();   pList.add(pInfo);
    	pInfo = getSunRiseSetInfo();	pList.add(pInfo);
    	
    	pInfo = getLagnaInfo();			pList.add(pInfo);
    	pInfo = getAllPlanetsInfo();	pList.add(pInfo);
    	pInfo = getSplLagnaInfo();      pList.add(pInfo);
    	pInfo = getPlanetInfo2();       pList.add(pInfo);
        pInfo = getLocationInfo();  	pList.add(pInfo);
        pInfo = getAyanamsaInfo();      pList.add(pInfo);
        
        pList.add(gpSwissEphePath);
    	return pList;
    }
  
    
    // return Graha; Star; Pada; Star lord; Longitude Speed
    public String getPlanetInfo2() {
        
        String s = "";
    	double starPeriod = 13.0 + (20.0/60.0);  // 13 Deg 20 Mnts = 13 + 20/60
		double paada = 3.0 + 20.0/60.0;

		double xhold = gdLagna + (0.5/3600./10000.);	// round to 1/1000 of a second
		int starId = (int)(xhold / starPeriod);  
		double dTemp = (xhold - (starPeriod*starId));
	    int padaId = (int) (dTemp/paada) + 1;  // PaadaID is between 1 and 4
        s += String.format("Lg           %-9.9s %01d %-2s\n",
                pStarNames[starId], padaId, pStarLords[starId]);
		
    	for (int p=0;p< gdnPlanets;p++) {
    		xhold = gdPlanetPos[p] + (0.5/3600./10000.);	// round to 1/1000 of a second
			starId = (int)(xhold / starPeriod);  
			dTemp = (xhold - (starPeriod*starId));
		    padaId = (int) (dTemp/paada) + 1;  // PaadaID is between 1 and 4
		    
            s += String.format("%-2s %c % 7.3f %-9.9s %01d %-2s",
                    pNames[p], (gdPlanetRetro[p] ? 'R' : ' '), gdPlanetSpeed[p], pStarNames[starId], padaId, pStarLords[starId]);
            if (p != pNames.length-1) s += "\n";
    	}
    	return s;
    }
        
    public byte PlanetInSign(byte pId) {
    	return gdPlanetSign[pId];
    }
    
    
    public int GetLagnaSign()
    {
        int d1Sign = (int)(gdLagna /30.);
        return d1Sign;   
    }

    public int GetD9LagnaSign()
    {
		int d9Sign = GetD9Sign(gdLagna);
        return d9Sign;   
    }
    
    // Retro planets will be -ve values
    public ArrayList<Integer>GetObjectsInSign(int iSign)
    {
		ArrayList<Integer> temp = new ArrayList<Integer>(); temp.clear();
        
		for (int p=0;p<gdnPlanets;p++) {
			int d1Sign = (int)(gdPlanetPos[p] /30.0);
			if (d1Sign == iSign) 
				{
				    if (gdPlanetRetro[p]) temp.add(-p);
				    else           temp.add(p);
				}
		}
		
		for (int p=0;p<nArudhas;p++) {
			if (gdHouseArudha[p] == iSign) 
				    temp.add(p+ArudhaShift);  // ArudhaShift is used to display purpose only
		}

		for (int p=0;p<nSplLagnas;p++) {
			int d1Sign = (int)(gdSplLagna[p] /30.0);
			if (d1Sign == iSign) temp.add(p+nSplLagnaShift);
		}

		return temp;
    }

    // Retro planets will be -ve values
    public ArrayList<Integer>GetD9ObjectsInSign(int iSign)
    {
		ArrayList<Integer> temp = new ArrayList<Integer>(); temp.clear();
        
		for (int p=0;p<gdnPlanets;p++) {
			int d9Sign = GetD9Sign(gdPlanetPos[p]);
			if (d9Sign == iSign) {
			    if (gdPlanetRetro[p]) temp.add(-p);
			    else           temp.add(p);
			}
		}
		
		return temp;
    }
    
    
	public double getMoonPos() {
		return gdPlanetPos[P_MO];
	}
	
	public double getJulDay() {
		return gdJulDay;
	}

	public ArrayList<String> getAnalysis() {
    	ArrayList<String> pList = new ArrayList<String>(); pList.clear();
        String [] pNames   = {"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke"};
    	String [] sDayShortNames = {"Sun", "Mon","Tue","Wed","Thu","Fri","Sat"}; 
/*    	String [] sTithiNames = {"Sukla Pratipat", "Sukla Dwitiya", "Sukla Tritiya", "Sukla Chaturthi",
            "Sukla Panchami", "Sukla Shasthi", "Sukla Saptami", "Sukla Ashtami",
            "Sukla Navami", "Sukla Dashami", "Sukla Ekadashi", "Sukla Dwadashi",
            "Sukla Trayodashi", "Sukla Chaturdashi", "Pourniamasya", 
			   "Krishna Pratipat", "Krishna Dwitiya", "Krishna TadTritiyaiya", "Krishna Chaturthi",
            "Krishna Panchami", "Krishna Shasthi", "Krishna Saptami", "Krishna Ashtami",
            "Krishna Navami", "Krishna Dashami", "Krishna Ekadashi", "Krishna Dwadashi",
			   "Krishna Trayodashi", "Krishna Chaturdashi", "Amavasya"};
*/			   
    	String [] sTithiNames1 = {"S.01", "S.02", "S.03", "S.04", "S.05","S.06", "S.07", "S.08",
                "S.09", "S.10", "S.11", "S.12", "S.13", "S.14", "Pour", 
                "K.01", "K.02", "K.03", "K.04", "K.05","K.06", "K.07", "K.08",
                "K.09", "K.10", "K.11", "K.12", "K.13", "K.14", "Amav"};
    	
    	String [] sYogaNames = {"Vishkumbha", "Priti", "Ayushman", "Saubhagya", "Shobhan", "Atiganda",
    							"Sukarma", "Dhriti", "Shula", "Ganda", "Vridhi", "Dhruv", "Vyaghata",
    							"Harshan", "Vraj", "Sidhi", "Vyatipata", "Variyana", "Parigha",
    							"Shiva", "Shidha", "Sadhya", "Shubha", "Shukla", "Bhramha", "Indra", "Vraidhiti" };

    	// 1st 7 are Movable karanas and the rest of 4 are Fixed Karanas
        String [] sKaranaNames = {"Bava", "Balava", "Kaulava", "Taitula", "Garija", "Vanija", "vishti", 
        		"Shakuna", "Catushpad", "Naga", "Kimstugna"};  

		int iDay = gdSunRiseTime.getDayOfWeekNr();
		pList.add(sDayShortNames[iDay]);
		
		String tStr = String.format("%s (%d%1s)(%2s)", sTithiNames1[tithiId], (int)tithiLeft, "%", pNames[tithiLord] );
		pList.add(tStr);   // S.13 (100%)(Su)
		
		// Yoga in string
		//tStr = String.format("Y:%s (%d%1s)(%2s)", sYogaNames[yogaId], (int)yogaRemain, "%", pNames[yogaLord]);
		tStr = String.format("Y:%s (%2s)", sYogaNames[yogaId], pNames[yogaLord]);
		pList.add(tStr);   
		
		// Karana in String
		//tStr = String.format("K:%s (%d%1s)(%2s)", sKaranaNames[kNameId], (int)karanaRemain, "%", pNames[karanaLord]);
		tStr = String.format("K:%s (%2s)", sKaranaNames[kNameId], pNames[karanaLord]);
		pList.add(tStr);   
		
		return pList;
	}
    
	private void ComputeTithi() {
		double moPos, suPos;
	    double tithiDuration = 12.0;   // 360/ 30 Tithis
		byte [] pLord = {
			P_SU, P_MO, P_MA, P_ME, P_JU, P_VE, P_SA, P_RA, P_SU, P_MO, P_MA, P_ME, P_JU, P_VE, P_SA, 
			P_SU, P_MO, P_MA, P_ME, P_JU, P_VE, P_SA, P_RA, P_SU, P_MO, P_MA, P_ME, P_JU, P_VE, P_RA
		    }; 

		moPos = gdPlanetPos[P_MO];
		suPos = gdPlanetPos[P_SU];
	    
	    if (moPos < suPos) moPos += 360.0;
		double tithiPos = (moPos - suPos) / tithiDuration;

		tithiId   = (int)(tithiPos);
	    tithiLeft = ((tithiId+1)*tithiDuration - tithiPos*tithiDuration)*100.0/tithiDuration;
		tithiLord = pLord[tithiId];
	}
	
    private void ComputeYoga() {
		double moPsuPos;
		moPsuPos = (gdPlanetPos[P_MO] + gdPlanetPos[P_SU])*60/800;  // 13:20 deg = 800 '
		yogaId = (int)moPsuPos;
		yogaRemain = (1 - (moPsuPos - yogaId))*100;
		yogaId = yogaId % 27;  
		
		byte [] pLord = {P_SA, P_ME, P_KE, P_VE, P_SU, P_MO, P_MA, P_RA, P_JU, 
				  	    P_SA, P_ME, P_KE, P_VE, P_SU, P_MO, P_MA, P_RA, P_JU, 
				  	    P_SA, P_ME, P_KE, P_VE, P_SU, P_MO, P_MA, P_RA, P_JU};
		
        yogaLord = pLord[yogaId];
    }

    //  4 Fixed and 7 Movable 
    private void ComputeKarana() {
		double moPos, suPos;
	    double kDuration = 6.0;   // 360/ 30 Tithis
	    byte [] pLord2 = {P_SU, P_MO, P_MA, P_ME, P_JU, P_VE, P_SA};
		
	    //sTestStr = "k: ";
	    
		moPos = gdPlanetPos[P_MO];
		suPos = gdPlanetPos[P_SU];
	    
	    if (moPos < suPos) moPos += 360.0;
		double kPos = (moPos - suPos) / kDuration;

		karanaId   = (int)(kPos);
		karanaId = (karanaId % 60);  // Should never occur
	    karanaRemain = ((karanaId+1)*kDuration - kPos*kDuration)*100.0/kDuration;

  	  //sTestStr += String.format("B: %f %f %d %d", moPos, suPos, karanaId, kNameId);

    	//int lordId;
		int cRasiId = (int)(gdLagna /30.);
        		
	    switch(karanaId) {
	      case 0:  karanaLord = lords1[(cRasiId+10)%12]; kNameId = 7+3; break;  // 10th L
	      case 59: karanaLord = lords1[(cRasiId+7)%12];  kNameId = 7+2;break;   // 7th L
	      case 58: karanaLord = lords1[(cRasiId+4)%12];  kNameId = 7+1;break;   // 4th L
	      case 57: karanaLord = lords1[cRasiId];         kNameId = 7+0;break;          // LL

	      default:  // Id 1 to 56
	    	  //sTestStr += String.format("I: %f %f %d %d", moPos, suPos, karanaId, kNameId);
	    	  kNameId = (karanaId - 1) % 7;
	    	  karanaLord = pLord2[kNameId];
	    	  break;
        }
    }

    
// Private functions
    //  If Birth time < Sun rise use prev day sunrise, sunset.
    private void ComputeSunRiseSet() {
		double timezone_offset_in_days = -gpTz/24.0;
		int flags;

		switch(gpSettings.gsSunRiseFlag) {
			case 1: // Tip of Sun's disc is truly on the eastern horizon : 5:31:43/6:27:53
				flags = (SweConst.SE_BIT_NO_REFRACTION);
				break;

			default:
			case 2:    // tip of Sun's disc appears to be on the eastern horizon: 5:28:57/6:30:39
				 flags = 0;
				 break;
				
			case 3: // Center of Sun's disc is truly on the eastern horizon : 5:31:43/6:27:53
				flags = (SweConst.SE_BIT_NO_REFRACTION|SweConst.SE_BIT_DISC_CENTER);
				break;
				
			case 4:    // center of Sun's disc appears to be on the eastern horizon:
				flags = (SweConst.SE_BIT_DISC_CENTER);
				break;
		}
		
		SweDate sd = new SweDate(gpYear, gpMonth, gpDay, 0);
		sd.makeValidDate(); // Garantuees the date fields to be in their respective limits
		SweDate midnight = new SweDate(sd.getYear(), sd.getMonth(), sd.getDay(), 0);
		double utLocalMidnight = midnight.getJulDay() - timezone_offset_in_days;
		
		DblObj sunrise = new DblObj();
		gpSW.swe_rise_trans(utLocalMidnight, SweConst.SE_SUN, null, 0, (flags | SweConst.SE_CALC_RISE), 
				new double[] {gpLongitude, gpLatitude, 0}, 0, 0, sunrise, null);
		gdSunRiseTime = new SweDate(sunrise.val + timezone_offset_in_days);
		
		//sunRiseTime = sdRise.getHour();   // + 0.5/60; // Round hour to minutes
		
		// If birth time < SunRiseTime ==> SunRiseTime if from prev date.
        double dHour = gpHour + ((double)(gpMinute) / 60.) + ((double)(gpSec)/3600.0);
		SweDate sd1 = new SweDate(gpYear, gpMonth, gpDay, dHour, SweDate.SE_GREG_CAL);
		double birthJulDay = sd1.getJulDay();
		if (gdSunRiseTime.getJulDay() > birthJulDay) {
			midnight = new SweDate(sd.getYear(), sd.getMonth(), sd.getDay(), 0);
			utLocalMidnight = midnight.getJulDay() - 1 - timezone_offset_in_days;
			gpSW.swe_rise_trans(utLocalMidnight, SweConst.SE_SUN, null, 0, (flags | SweConst.SE_CALC_RISE), 
					new double[] {gpLongitude, gpLatitude, 0}, 0, 0, sunrise, null);
			gdSunRiseTime = new SweDate(sunrise.val + timezone_offset_in_days);
		}
		
		
		// Sunset:
		gpSW.swe_rise_trans(utLocalMidnight, SweConst.SE_SUN, null, 0, (flags | SweConst.SE_CALC_SET),
				new double[] {gpLongitude, gpLatitude, 0}, 0, 0, sunrise, null);
		gdSunSetTime = new SweDate(sunrise.val + timezone_offset_in_days);
		
		//sunSetTime = sdSet.getHour(); // + 0.5/60; // Round hour to minutes
    }
        
    private void ComputeLagna(SweDate sd, double longitude, double latitude) {
        int flags = SweConst.SEFLG_SIDEREAL;
        double[] cusps = new double[13];
        double[] acsc = new double[10];

        int result = gpSW.swe_houses(sd.getJulDay(), flags, latitude, longitude, 'P', cusps, acsc);
/*		mGlobals.AppendLog(" Lagna Calc: " + String.valueOf(result));
		mGlobals.AppendLog(" Lagna Calc Julday: " + String.valueOf(sd.getJulDay()));
		mGlobals.AppendLog(" Lagna Calc: acsc0" + String.valueOf(acsc[0]));
		mGlobals.AppendLog(" Lagna Calc: acsc1" + String.valueOf(acsc[1]));
		mGlobals.AppendLog(" Lagna Calc: Cusp0" + String.valueOf(cusps[0]));
		mGlobals.AppendLog(" Lagna Calc: Cusp0" + String.valueOf(cusps[1]));
		mGlobals.AppendLog(" Lagna Calc: Ayan" + String.valueOf(mAyanamsa));
*/
        // TODO Test result
        gdLagna = acsc[0];
    }

    // Only for planets Sun to Saturn  (No for nodes)
    private double ComputePlanetPos(double julDay, int planetId) {
        double[] xp = new double[6];
        StringBuffer serr = new StringBuffer();
        
		iCalcFlag = (iniCalcFlag|gpSettings.gsPlanetCalcFlag);

        int ret = gpSW.swe_calc_ut(julDay, planetId, iCalcFlag, xp, serr);

        if (ret != iCalcFlag) {   // TODO May not be same!!
            if (serr.length() > 0)
        		mGlobals.AppendLog(" Planet Calc: " + String.valueOf(ret) + " : " + serr);
            else
        		mGlobals.AppendLog(" Planet Calc: " + String.format("Warning, different flags used (0x%x)", ret));
                //System.err.println(String.format("Warning, different flags used (0x%x)", ret));  // TODO Error
        }

        return xp[0];  // Not returning retrograde info
    }
    
    private void ComputeAllPlanets(SweDate sd) {
        double[] xp = new double[6];
        StringBuffer serr = new StringBuffer();

		iCalcFlag = (iniCalcFlag|gpSettings.gsPlanetCalcFlag);
        
        int[] planets = { SweConst.SE_SUN, SweConst.SE_MOON, SweConst.SE_MARS,
                          SweConst.SE_MERCURY, SweConst.SE_JUPITER, SweConst.SE_VENUS,
                          SweConst.SE_SATURN, gpSettings.gsNodesFlag };	// Some systems prefer SE_MEAN_NODE

        int p;
        for(p = 0; p < planets.length; p++) {
            int planet = planets[p];
            int ret = gpSW.swe_calc_ut(sd.getJulDay(), planet, iCalcFlag, xp, serr);

            if (ret != iCalcFlag) {
                if (serr.length() > 0)
            		mGlobals.AppendLog(" Planet Calc: " + String.valueOf(ret) + " : " + serr);
                else
            		mGlobals.AppendLog(" Planet Calc: " + String.format("Warning, different flags used (0x%x)", ret));
            }

            gdPlanetPos[p]   = xp[0];
            gdPlanetRetro[p] = (xp[3] < 0);
            gdPlanetSpeed[p] = xp[3];   // Longitude speed
            gdPlanetSign[p] = (byte)(gdPlanetPos[p] /30.0);       // Planet is in sign
			if (gdPlanetSign[p] > 11) gdPlanetSign[p] = 11;
        }
        // KETU
        xp[0] = (xp[0] + 180.0) % 360;

        gdPlanetPos[p] = xp[0];    // Ketu
        gdPlanetRetro[p] = true;
        gdPlanetSign[p] = (byte)(gdPlanetPos[p] /30.0);       // Planet is in sign
		if (gdPlanetSign[p] > 11) gdPlanetSign[p] = 11;
    }
    
    // Spl Lagnas: HL, SL, GL, VL, PP
    private void ComputeSplLagnas() {

    	gpSW.swe_set_sid_mode(gpSettings.gsAyanamsaFlag, 0, 0);

		// Get Jul day at birth time
        double dHour = gpHour + ((double)(gpMinute) / 60.) + ((double)(gpSec)/3600.0);
		SweDate sd1 = new SweDate(gpYear, gpMonth, gpDay, dHour, SweDate.SE_GREG_CAL);
		double birthJulDay = sd1.getJulDay();
		birthJulDay += (gpTz/24.0);

		// Jul day at Sun rise
		double sunRiseJulDay = gdSunRiseTime.getJulDay();
		sunRiseJulDay += (gpTz/24.0);
		
	    double timeDiff = (birthJulDay - sunRiseJulDay) * 24.0 * 60.0;     // Convert Days to Minutes
	    
        // Sun position at Sun rise on the birth day
		//sunRiseJulDay -= (mTz/24.0);  // TEST
        UTCHour = gdSunRiseTime.getHour() + gpTz;
        SweDate sd2 = new SweDate(gdSunRiseTime.getYear(), gdSunRiseTime.getMonth(), gdSunRiseTime.getDay(), UTCHour);
        sunRiseJulDay = sd2.getJulDay();
	    double sunPos = ComputePlanetPos (sunRiseJulDay, SweConst.SE_SUN);
	    //sTestStr = String.format("Sun Pos: %f  %f %f %f %f", sunPos, birthJulDay, sunRiseJulDay, UTCHour, timeDiff);
	    
	    
	    // PP: depends on: (a) Diff between Sun rise and birth time (b) Sun Pos
	    double ppDeg = timeDiff * 5;    // 30 Deg = 15 Phala (Vighatika) = 6 minutes ==> 1 Minute = 5 deg.
	    ppDeg = BoundDeg(ppDeg);
	    double sunDeg = gdPlanetPos[P_SU] ;
	    
	    double d = sunDeg + (0.5/3600./10000.);	// round to 1/1000 of a second
	    int d1Sign = (int)(d /30.0);
	    int signType = (d1Sign % 3);  // Movable (0); Fixed(1); Dual (2)

	    ppDeg = ppDeg + sunDeg;               // Sun in Movable Sign 
	    if (signType == 1)  ppDeg += 240.0;   // Sun in Fixed sign
	    if (signType == 2)  ppDeg += 120.0;   // Sun in Dual sign
	    
	    gdSplLagna[L_PRANAPADA_LAGNA] = BoundDeg(ppDeg);
	    
    	// SL
    	double dMoon = gdPlanetPos[P_MO] * 9.0 / 120.0; // Part of Star in which Moon has advanced
    	dMoon = dMoon - ((int)dMoon);
    	gdSplLagna[L_SREE_LAGNA] = BoundDeg((dMoon*360.0) + gdLagna);

    	// GL: Ghati Lagna: GL same as sun Pos at sun rise and moves at One raasi in 24 minutes (1° = 4/5 mnts)
    	// (Sun longitude at Sun rise) + (Birth time - sun rise time)*(5/4)
            
    	double degDiff  = (timeDiff *5.0)/4.0;                 // GL Moves at 1° in 4/5 Minute
    	gdSplLagna[L_GHATI_LAGNA] = BoundDeg(sunPos + degDiff);
    	
    	// HL: Hora Lagna: HL same as sun Pos at sun rise and moves at One raasi in 60 minutes (1° = 2 mnts)
    	// (Sun longitude at Sun rise) + (Birth time - sun rise time)/2
     
    	degDiff  = timeDiff/2.0;                 // HL Moves at 1° for 2 minutes
    	gdSplLagna[L_HORA_LAGNA] = BoundDeg(sunPos + degDiff);

    	// VL
	    d = gdLagna + (0.5/3600./10000.);	// round to 1/1000 of a second
	    int lD1Sign = (int)(d /30.0);
	    d = gdSplLagna[L_HORA_LAGNA] + (0.5/3600./10000.);	// round to 1/1000 of a second
	    int hlD1Sign = (int)(d /30.0);
    	
	    int c1, c2, cc, vlSign;
	    if ((lD1Sign % 2) == 0) c1 = lD1Sign + 1;   // Odd signs (Count from Aries)
	    else                    c1 = 12 - lD1Sign;  // Even signs (count from Pisces)

	    if ((hlD1Sign % 2) == 0) c2 = hlD1Sign + 1;   // Odd signs (Count from Aries)
	    else                     c2 = 12 - hlD1Sign;  // Even signs (count from Pisces)

	    if ((lD1Sign % 2) == (hlD1Sign % 2)) cc = c1 + c2;   // Both L and HL of same type (Odd/Even)
	    else                                 cc = Math.abs (c1-c2);
	    
	    if (cc > 12) cc = cc - 12;
	    
	    if ((lD1Sign % 2) == 0) vlSign = cc;       // Odd signs (Count from Aries)
	    else                    vlSign = 12 - cc+1;  // Even signs (count from Pisces)
	    
	    vlSign = vlSign - 1;
	    
	    gdSplLagna[L_VARNADA_LAGNA] = BoundDeg((gdLagna - lD1Sign * 30) + (vlSign * 30.0));
    }

    // Compute Arudhas
    // "AL", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "UL", "A?R", "A?K"
    private void ComputeArudha() {
    	int lordId;
		int cRasiId = (int)(gdLagna /30.);
    	for(int i1 = 0; i1 < gdnHouses; i1++)
    	{
    		lordId = lords1[cRasiId];
    		gdHouseArudha[i1] = CalcArudha(cRasiId, lordId);
	        
			// TODO: For Sc and Aq Take 2 lords = 2 Arudhas)
	        if (lords1[cRasiId] != lords2[cRasiId]) {
	            int i2 = gdnSigns + lords2[cRasiId]-7;
	    		lordId = lords2[cRasiId];
	    		gdHouseArudha[i2] = CalcArudha(cRasiId, lordId);
	        }
	        cRasiId = ((cRasiId+1) %12);
    	}
    }

	private int CalcArudha(int cRasiId, int lordId) {
		int lRasiId = (int)(gdPlanetPos[lordId]/30.0);
	    int dist = lRasiId - cRasiId + 1;
	    if (dist < 0) dist += 12;
		int ap = (lRasiId + dist-1) % 12;
		if (ap < 0) ap += 12;
		int cRasiId7 = (cRasiId+6) % 12;
	    if ((ap == cRasiId) || (ap == cRasiId7))
			ap = (ap+10-1) % 12;
	    return ap;
	}    

	// Private functions to format info    
    private String getDateInfo() {
        return String.format(Locale.US, "Date: %4d-%02d-%02d, %d:%02d:%02dh",
                gpYear, gpMonth, gpDay, gpHour, gpMinute, gpSec);
    }
    
    private String getSunRiseSetInfo() {
    	String tStr;
    	tStr = String.format("Sun Rise/set on:%4d-%02d-%02d\n (%s => %s)", 
    			gdSunRiseTime.getYear(), gdSunRiseTime.getMonth(), gdSunRiseTime.getDay(), toHMS(gdSunRiseTime), toHMS(gdSunSetTime));
//    	tStr = String.format("%s\n %s", toDMS(sunRiseTime), toDMS(sunSetTime));
    	return tStr;
    }
    
    private String getLocationInfo() {
        return String.format(Locale.US, "Place: %-20s\nLomgitude:\t %d:%02d:%02d %1s\nLatitude: \t %d:%02d:%02d %1s\nTime Zone:\t  %d:%02d    %1s",
                gpPlace, gpLongD, gpLongM, gpLongS, (gpLongEW  > 0 ? "E" : "W"),
                gpLatD, gpLatM, gpLatS, (gpLatNS  > 0 ? "N" : "S"), gpTzH, gpTzM, (gpTzEW < 0 ? 'E' : 'W'));
    }

    private String getAyanamsaInfo() {
        return "Ayanamsa " + toDMS1(gdAyanamsa);
    }

    private String getLagnaInfo() {
    	return "Lagna " + toDMS(gdLagna);
    }
    
    private String getAllPlanetsInfo() {
        String s = "";

        String [] pNames   = {"Su", "Mo", "Ma", "Me", "Ju", "Ve", "Sa", "Ra", "Ke"};
        String [] pKaaraka = {"AK","AmK","BK", "MK", "PiK", "PK", "GK", "DK", " "}; 

        int p, i1;
        double [] cPos = new double[gdnPlanets];
        for(p = 0; p < pNames.length; p++) {
            double d =  gdPlanetPos[p] + (0.5/3600./10000.);	// round to 1/1000 of a second
            int d1Sign = (int)(d /30.0);
            cPos[p] = d - (d1Sign *30.0);
        	if (p == P_RA) cPos[p] = 30.0 - cPos[p];
        }        
        
        for(p = 0; p < pNames.length; p++) {
        	// Find Kaarakatva of planet
        	i1 = 0;
            for (int p1 = 0; p1 < (gdnPlanets-1); p1++) {
            	if ((p != p1) && (cPos[p1] > cPos[p]) ) i1++;
            }
        	if (p == P_KE) i1 = 8;
        	
            s += String.format("%-2s %c  %s %s",
                    pNames[p], (gdPlanetRetro[p] ? 'R' : ' '), toDMS(gdPlanetPos[p]), pKaaraka[i1]);
            if (p != pNames.length-1)
            	s += "\n";
        }

        return s;
    }

    private String getSplLagnaInfo() {
        String s = "";

        String [] pNames = {"VL", "HL", "GL", "SL", "PP"};

        int p;
        for(p = 0; p < pNames.length; p++) {
            s += String.format("%-2s    %s",
                    pNames[p], toDMS(gdSplLagna[p]));
            if (p != pNames.length-1)
            	s += "\n";
        }

        
        return s;
    }

    // Lagna => iHouse = 0   (Houses 0 to 11). Rahu, Ketu not used
    public int GetSignLord1(int iHouse)
    {
        int iSign = (int)(gdLagna /30.);
        iSign += iHouse;  
        
        return lords1[iSign];   
    }

    // Lagna => iHouse = 0   (Houses 0 to 11). 2nd lord used
    public int GetSignLord2(int iHouse)
    {
        int iSign = (int)(gdLagna /30.);
        iSign += iHouse;  
        
        return lords2[iSign];   
    }

    // Find the lord of wign where the planet is placed
    public int GetSignLordOfPlanet1(int iPlanet)
    {
        int iSign = (int)(gdPlanetPos[iPlanet] /30.);
        
        return lords1[iSign];   
    }

    // Find the lord of wign where the planet is placed
    public int GetSignLordOfPlanet2(int iPlanet)
    {
        int iSign = (int)(gdPlanetPos[iPlanet] /30.);
        
        return lords2[iSign];   
    }

    public int [] GetPlanetStar(int pId)
    {
    	double starPeriod = 13.0 + (20.0/60.0);  // 13 Deg 20 Mnts = 13 + 20/60
		double paada = 3.0 + 20.0/60.0;

    	int [] starId = new int[2];
		double xhold = gdPlanetPos[pId] + (0.5/3600./10000.);	// round to 1/1000 of a second
		starId[0] = (int)(xhold / starPeriod);  
		double dTemp = (xhold - (starPeriod*starId[0]));
	    starId[1] = (int) (dTemp/paada);  // PaadaID is between 0 and 3
	    if (starId[1] > 3) starId[1] = 3;
		return starId;
	}

    public int [] GetLagnaStar()
    {
    	double starPeriod = 13.0 + (20.0/60.0);  // 13 Deg 20 Mnts = 13 + 20/60
		double paada = 3.0 + 20.0/60.0;

    	int [] starId = new int[2];
		double xhold = gdLagna + (0.5/3600./10000.);	// round to 1/1000 of a second
		starId[0] = (int)(xhold / starPeriod);  
		double dTemp = (xhold - (starPeriod*starId[0]));
	    starId[1] = (int) (dTemp/paada);  // PaadaID is between 0 and 3
	    if (starId[1] > 3) starId[1] = 3;
		return starId;
	}
    
    public byte GetPlanetRasi(int pId) {
		return (byte)(gdPlanetPos[pId]/30.0);
    }

    public byte GetLagnaRasi() {
		return (byte)(gdLagna/30.0);
    }
    
    public byte [] GetAllPlanetsRasi() {
    	byte [] rId = new byte[gdnPlanets];
    	
		for (int pId=0;pId<gdnPlanets;pId++) {
			rId[pId] = (byte) GetPlanetRasi(pId);
		}

		return rId;
    }
    
    public String GetMoonInfo() {
    	String tStr;
    	int [] star = GetPlanetStar(P_MO);
		int rasiId = (int)(gdPlanetPos[P_MO]/30.0);

    	tStr = String.format("%s (Pada:%d);   Raasi:%s", pStarNames[star[0]],(star[1]+1), pRasiNames[rasiId]);
    	
    	return tStr;
    }
    
    public String GetLagnaULInfo() {
    	String tStr;
		int lRasiId = (int)(gdLagna/30.0);
		int [] lStar = GetLagnaStar();
		int ulRasiId = gdHouseArudha[11];

    	tStr = String.format("Lagna: %s (%s-%d);   UL:%s", 
    			pRasiNames[lRasiId], pStarNames[lStar[0]], lStar[1], pRasiNames[ulRasiId]);
    	
    	return tStr;
    }

    public byte [] GetArudha() {
    	byte [] temp = new byte[nArudhas];
    	
		for (int p=0;p<nArudhas;p++)
		    temp[p] = (byte)gdHouseArudha[p];
		
		return temp;
    }
    
    
    public int GetNatRelation(int lord1, int lord2) {
    	return rNat[lord1][lord2];
    }
    
    public int GetCompRelation(int lord1, int lord2) {
    	return rComp[lord1][lord2];
    }

    
    // Ati Mitra, Mitra, Neutral, Shatru, Ati Shatru\
    // Natural relation: M, N, S;  TODO: Assumttion: Planet to itself is Mitra. Ex: Su is Mitra to Su
    // Temp    relation: M,    S;  TODO: Assumttion: Planet to itself is Shatru. Ex: Su is Shatru to Su
    // Comp ==> TODO: Due to above deficition, Planet to itself is Neutral (Ex: Su is sama to Su)
    public void ComputeRelation() {
    	int [] tempRelBase = {REL_S, REL_M, REL_M, REL_M, REL_S, REL_S, REL_S, REL_S, REL_S, REL_M, REL_M, REL_M};
    	rTemp = new int[gdnPlanets][gdnPlanets];
    	rComp = new int[gdnPlanets][gdnPlanets];

    	// Calculate Temp relation matrix
    	int rId, rId1;
    	for (int i=0;i<gdnPlanets;i++)
    	{
    		rId1 = 	(int)(gdPlanetPos[i] /30.0);
    		for (int j=0;j<gdnPlanets;j++)
    		{
    			if (i == j) {
    				rId = 0;
    			}
    			else {
	    			rId = ((int)(gdPlanetPos[i] /30.0) - rId1);
	    			if (rId < 0) rId += 12;
    			}
                rTemp[i][j] = tempRelBase[rId];
    		}
    	}
    	
    	// Compund relationship: Nat + Temp
    	for (int i=0;i<gdnPlanets;i++)
    	{
    		for (int j=0;j<gdnPlanets;j++)
    		{
                rComp[i][j] = rNat[i][j] + rTemp[i][j];
    		}
    	}
    }

    ArrayList<Byte> GetPlanetsRasiAspectOnSign(byte sign) {
		byte [] start = {4,2,3, 4,2,3, 4,2,3, 4,2,3};
		ArrayList<Byte> pList = new ArrayList<Byte>(); pList.clear();

		for (byte p=0;p<gdnPlanets;p++) {
			if (gdPlanetSign[p] == sign) pList.add(p); 
		}
		
		for (byte i=0;i<3;i++) {
			byte i1 = (byte) ((sign + start[sign] + i*3)%12); 
			for (byte p=0;p<gdnPlanets;p++) {
				if (gdPlanetSign[p] == i1) pList.add(p); 
			}
		}
		return pList;
	}
    
    
    // Find strongest of two signs for Narayana Dasa
    
    // Source 1; rule 3: Strongest in order: Exalt; MT; own house  (NOTE: Frendly; neutral;enemy sign not used)
	byte IsSignStrength_Source1_rule3(byte sign1, byte sign2) {
		byte [] pExalt = {0, 1, 9, 5, 3, 11, 6, 2, 8};
		byte [] pMT    = {4, 1, 0, 5, 8, 6,10, 5, 11};
		byte [] nResults1 = new byte[3];  // 0:Exalt; 1: MT; 2:Own; 3: Freind's house; 4: Neutral; 5: Enemy house
		byte [] nResults2 = new byte[3];  // 0:Exalt; 1: MT; 2:Own; 3: Freind's house; 4: Neutral; 5: Enemy house
		
    	byte nPlanets1 = 0;
    	byte nPlanets2 = 0;
		for (int p=0;p<gdnPlanets;p++) {
			if (gdPlanetSign[p] == sign1) nPlanets1++; 
			if (gdPlanetSign[p] == sign2) nPlanets2++; 
		}
    	if ((nPlanets1 + nPlanets2) == 0) return -1; // No planets this rule fails

    	for (int i=0;i<3;i++) {
    		nResults1[i] =0;
    		nResults2[i] =0;
    	}
		
		for (int p=0;p<gdnPlanets;p++) {
			if (gdPlanetSign[p] != sign1) continue;
			if (pExalt[p] == sign1) nResults1[0]++;
			if (pMT[p]    == sign1) nResults1[1]++;
			if ((lords1[sign1] == p) || (lords2[sign1] == p)) nResults1[2]++;
		}
		
		for (int p=0;p<gdnPlanets;p++) {
			if (gdPlanetSign[p] != sign2) continue;
			if (pExalt[p] == sign2) nResults2[0]++;
			if (pMT[p]    == sign2) nResults2[1]++;
			if ((lords1[sign2] == p) || (lords2[sign2] == p)) nResults2[2]++;
		}
		
		if (nResults1[0] > nResults2[0]) return sign1;   // Exalt
		if (nResults1[0] < nResults2[0]) return sign2;
		
		if (nResults1[1] > nResults2[1]) return sign1;   // Exalt
		if (nResults1[1] < nResults2[1]) return sign2;
		
		if (nResults1[2] > nResults2[2]) return sign1;   // Exalt
		if (nResults1[2] < nResults2[2]) return sign2;
		
		return -1;  // failed
	}    
    
    
    
    // Sign 1, sign 2 are between 0 and 11.
	// S1R2; S2R1; S1R3; S1R4; S1R7; S1R6
    byte FindStrongestSign(byte sign1, byte sign2) {
    	
    	// Source I: Rule 2: Democratic rule: More planets =============================================
    	byte nPlanets1 = 0;
    	byte nPlanets2 = 0;
		for (int p=0;p<gdnPlanets;p++) {
			if (gdPlanetSign[p] == sign1) nPlanets1++; 
			if (gdPlanetSign[p] == sign2) nPlanets2++; 
		}
		mGlobals.AppendLog("S1 R2 ");
    	if (nPlanets1 > nPlanets2) return sign1;
    	else if (nPlanets1 < nPlanets2) return sign2;
    
    	// Source II: Rule 1: Rasi aspect of Ju, Me, Lord of sign =========================================
    	ArrayList<Byte> pList1 = GetPlanetsRasiAspectOnSign(sign1);
    	
    	byte nAspects1 = 0;
    	byte lord = (byte)(lords1[sign1]);       // Here we use only first lord (Sa, Ma) and not Ra, Ke
		for (byte i=0;i<pList1.size();i++) {
			if (pList1.get(i) == P_JU) nAspects1++;
			if (pList1.get(i) == P_ME) nAspects1++;
			if (pList1.get(i) == lord) nAspects1++;
		}

    	pList1 = GetPlanetsRasiAspectOnSign(sign2);
    	byte nAspects2 = 0;
    	lord = (byte)(lords1[sign2]);    // Here we use only first lord (Sa, Ma) and not Ra, Ke
		for (byte i=0;i<pList1.size();i++) {
			if (pList1.get(i) == P_JU) nAspects2++;
			if (pList1.get(i) == P_ME) nAspects2++;
			if (pList1.get(i) == lord) nAspects2++;
		}
		
		mGlobals.AppendLog("S2 R1 : " + nAspects1 + " : "+ nAspects2);
    	if (nAspects1 > nAspects2) return sign1;
    	else if (nAspects1 < nAspects2) return sign2;

		mGlobals.AppendLog("S1 R3 after");
    	
    	// Source I: Rule 3: Exalt, MT, Own, Friend's hosue ==============================
    	byte iRes = IsSignStrength_Source1_rule3(sign1, sign2);
    	if (iRes >= 0) return iRes;

		mGlobals.AppendLog("S1 R3 ");
    	
    	// Source I: Rule 4
    	byte dfm1 = (byte)(sign1%3); 
    	byte dfm2 = (byte)(sign2%3); 
    	
    	if (dfm1 > dfm2) return sign1;
    	if (dfm1 < dfm2) return sign2;

		mGlobals.AppendLog("S1 R4 ");
    	
    	// Source I: Rule 7: Even/odd
    	byte s1oe = (byte) (sign1%2);
    	byte s2oe = (byte) (sign2%2);
    	byte s1LordOe = gdPlanetSign[lords1[sign1]];
    	byte s2LordOe = gdPlanetSign[lords1[sign2]];
    	if ((s1oe != s1LordOe) && (s2oe == s2LordOe)) return sign1;
    	if ((s1oe == s1LordOe) && (s2oe != s2LordOe)) return sign2;

		mGlobals.AppendLog("S1 R7 ");
    	
    	// Source 1, Rule 6: longitude
		double pos1 = gdPlanetPos[lords1[sign1]];   // Only 1st lord (Sa; Ma) used
		double pos2 = gdPlanetPos[lords1[sign2]];
		
    	if (pos1 > pos2) return sign1;
    	if (pos1 < pos2) return sign2;
    	    	
    	return -1;
    }
    
    byte GetStronger(byte pId1, byte pId2) {
		double pos1 = gdPlanetPos[pId1]; 
		double pos2 = gdPlanetPos[pId2];
		if ((pId1 == P_RA) || (pId1 == P_KE)) pos1 = 30-pos1;
		if ((pId2 == P_RA) || (pId2 == P_KE)) pos2 = 30-pos1;
		
    	if (pos1 > pos2) return pId1;
    	if (pos1 < pos2) return pId2;
    	return pId1; // Never happens: When both has exact same longitude
    }
    
    
    // Private routines
    static String toDMS(double d) {
    	int d9Sign = GetD9Sign(d); 
        d += 0.5/3600./10000.;	// round to 1/1000 of a second
        double d1 = d;
        int deg = (int) d;
        d = (d - deg) * 60;
        int min = (int) d;
        d = (d - min) * 60;
        double sec = d;

        int d1Sign = (int)(d1 /30.0);
        int deg1  = deg - d1Sign *30;
        
        //int d8Sign = (int)((d1 - d1Sign *30) * 9.0/ 30.0);
		//int [] rasiTatva =  { 0,9,6,3, 0,9,6,3, 0,9,6,3};
        //int d9Sign = (rasiTatva[d1Sign] + d8Sign) % 12;  
        
        String [] sNames = {"Ar", "Ta", "Ge", "Ca", "Le", "Vi", "Li", "Sc","Sg", "Cp", "Aq", "Pi"};
        // sNames[d9Sign]
        return String.format("%2s %3d°%02d'%02d\"  %s", sNames[d1Sign], deg1, min, (int)sec, sNames[d9Sign]);
    }

    
static int GetD9Sign(double d) {
    d += 0.5/3600./10000.;	// round to 1/1000 of a second
    int d1Sign = (int)(d /30.0);
    int d8Sign = (int)((d - d1Sign *30) * 9.0/ 30.0);
	int [] rasiTatva =  { 0,9,6,3, 0,9,6,3, 0,9,6,3};
    int d9Sign = (rasiTatva[d1Sign] + d8Sign) % 12;  
    return d9Sign;
}

    
    
    static String toDMS1(double d) {
        d += 0.5/3600./10000.;	// round to 1/1000 of a second
        int deg = (int) d;
        d = (d - deg) * 60;
        int min = (int) d;
        d = (d - min) * 60;
        double sec = d;

        return String.format("%3d° %02d' %07.4f\"", deg, min, sec);
    }

    static String toHMS(SweDate pDate) {
        double d = pDate.getHour() + 0.5/3600./10000.;	// round to 1/1000 of a second
        int hour = (int) d;
        d = (d - hour) * 60;
        int min = (int) d;
        d = (d - min) * 60;
        int sec = (int)d;

        return String.format("%2d:%02d:%02d\"", hour, min, sec);
    }
   
	private double BoundDeg(double x) {
		while (x <  0.0  ) x += 360;
		while (x >= 360.0) x -= 360;
		return x;
	};

}



/*
array<String^> ^sLunarMonthNames = gcnew array<String^> {"Chaitra","Vaisakha","Jyesta",
"Aashada","Sravana","BhadraPada",
"Aswayuja","Kaartika","Maargasira","Pushya","Maagha","Phalguna"," "};
*/
