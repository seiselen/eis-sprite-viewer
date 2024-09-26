package app;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import PrEis.utils.Cons;
import PrEis.utils.DataStructUtils;
import PrEis.utils.FormatUtils;
import PrEis.utils.StringUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class SpriteUtils {

  /*=[ PRIVATE STATIC FINAL VARS ]============================================*/
  /** Include BRIGHTMAPS files in search results? */
  private static final boolean INCL_BMAPS = false;

  /** Distinguish states of frames of `{-1,0}` tic length via special preceding `"#LOOP"` handle? */
  private static final boolean HANDLE_NON_POS_TIC_LENS = false;


  /** Default <code>maxDepth</code> for <code>Files.find(...)</code> operations. */
  private static final int FIND_MAX_DEPTH = 2;

  /** EXPECTED dir of which contains brightmaps sprites.  */
  private static final String BMAP_DIR = "BRIGHTMAPS";



  public static PVector extractSpriteOffset(AppUtils aUtils, String sName){
    String sprPath = findSprite(aUtils.getSpriteDirpath(), sName)[0];
    byte[] byteArr = aUtils.app.loadBytes(sprPath);
    int offStartIdx = -1;
    for(int i=0; i<byteArr.length; i++){if(findOffsetIdxStart(byteArr,i)){offStartIdx=i+4; break;}}
    if(offStartIdx==-1){return conswarn_cantFindHdrOffset();}
    return new PVector(
      PApplet.unhex(FormatUtils.byteArrSubStrToHex(byteArr,offStartIdx,4)),
      PApplet.unhex(FormatUtils.byteArrSubStrToHex(byteArr,offStartIdx+4,4))
    );
  }

  public static boolean findOffsetIdxStart(byte[] bArr, int idx){
    if (!PApplet.hex(bArr[  idx]).equals("67")){return false;}
    if (!PApplet.hex(bArr[idx+1]).equals("72")){return false;}
    if (!PApplet.hex(bArr[idx+2]).equals("41")){return false;}
    if (!PApplet.hex(bArr[idx+3]).equals("62")){return false;}  
    return true;
  }



  public static PImage pimageWithFilepath(PApplet iApp, String iPath) {
    try {
       return iApp.loadImage(iPath);
    } catch (Exception e) {
       System.err.println(e);
       return null;
    }
 }

  public static Sprite spriteWithName(AppUtils iAppUtils, String sName){
    String sPath = findSprite(iAppUtils.getSpriteDirpath(), sName)[0];
    if(sPath==null){return conserr_findSprite(sName, iAppUtils.getSpriteDirpath().toString());}
    PImage spImg = pimageWithFilepath(iAppUtils.app, sPath);
    if(spImg==null){return conserr_loadSprite(sName,sPath);}
    Sprite sprite = new Sprite(iAppUtils.app, sName, sPath, spImg);
    return sprite;
  }





  /**
   * Returns filepath of input sprite name if such exists within input dirpath.
   * @param fPath query filepath
   * @param pfix4 sprite name <code>char[4]</code> prefix; e.g. <code>"PLSA"</code>
   * @param sfix1 sprite name <code>char</code> suffix; e.g. <code>'B'→"PLSAB"</code>
   */
  public static String[] findSprite(Path fPath, String pfix4, char sfix1){
    return findSprite(fPath, pfix4+sfix1);
  }

  /**
   * Returns filepath of input sprite name if such exists within input dirpath.
   * @param fPath query filepath
   * @param name5 sprite name <code>char[5]</code>; e.g. <code>"PLSAB"</code>
   */
  public static String[] findSprite(Path fPath, String name5){
    ArrayList<String> buff = new ArrayList<String>();
    try {
      Files.find(fPath, FIND_MAX_DEPTH,(p,a)->(evalFName(p,name5) && evalBMap(p)))
      .forEach(f -> buff.add(f.toString()));
    }
    catch(IOException ie) {ie.printStackTrace();}
    return FormatUtils.arrFromList(String.class, buff);

  }

  /** Filepath is a match IFF it includes the <code>String[5]</code> sprite name. */
  private static boolean evalFName(Path q, String n5){
    return q.getFileName().toString().startsWith(n5);
  }

  /** If Filepath in brightmap dir: it is a match IFF brightmaps are not filtered. */
  private static boolean evalBMap(Path q){
    return INCL_BMAPS || !q.toString().contains(BMAP_DIR);
  }


  /** @todo TEST THIS */
  public static SpriteClip[] getAllSpriteClipsOf(AppUtils appUtils, JSONObject allStates){
    String[] stateNames = DataStructUtils.keyArrayOfJSONObj(allStates);
    int nStates = stateNames.length;
    SpriteClip[] allClips = new SpriteClip[nStates];
    for (int i=0; i<nStates; i++){allClips[i] = rawStateDefToSpriteClip(appUtils, stateNames[i],allStates);}
    return allClips;
  }

  /** @todo TEST THIS */
  public static SpriteClip rawStateDefToSpriteClip(AppUtils appUtils, String stateName, JSONObject allStates){
    String[] stateSeqArr = allStates.getJSONArray(stateName).toStringArray();
    ArrayList<String> allFrames = new ArrayList<String>();
    for (int i=0; i<stateSeqArr.length; i++){
      java.util.Collections.addAll(allFrames, frameLineToSpriteArray(appUtils, stateSeqArr[i]));}
    return new SpriteClip(stateName, FormatUtils.strArrListToStrArr(allFrames));
  }

  /** @todo TEST THIS */
  public static HashMap<String,PVector> offsetJSONToOffsetMap(JSONObject offsets){
    if(offsets==null){return null;}
    String[] keys = DataStructUtils.keyArrayOfJSONObj(offsets);
    HashMap<String,PVector> ret = new HashMap<String,PVector>();
    JSONArray arr;
    for(String k: keys){arr=offsets.getJSONArray(k); ret.put(k,new PVector(arr.getInt(0),arr.getInt(1)));}
    return ret;
  }

  /** @todo TEST THIS */
  public static String[] frameLineToSpriteArray(AppUtils aUtil, String line){  
    String[] components = line.trim().split("\\s+");
    if(components.length != 3){return conserr_exp3CompDECStr(line);}
    String prefixStr = components[0];
    char[] suffixArr = components[1].toCharArray();
    int    ticLength = Integer.valueOf(components[2]);
    ArrayList<String> BUFF_STR_ARRLIST = new ArrayList<String>();
    for (char f : suffixArr){
      if(ticLength<=0){
        //> @TODO can clean these two lines into one with ternary (low priority)
        if(HANDLE_NON_POS_TIC_LENS){BUFF_STR_ARRLIST.add("#LOOP");}
        BUFF_STR_ARRLIST.add(""+prefixStr+f);
      } 
      else {
        for (int i=0; i<ticLength; i++){BUFF_STR_ARRLIST.add(""+prefixStr+f);}
      }
    }
    return FormatUtils.strArrListToStrArr(BUFF_STR_ARRLIST);
}



  /** @todo TEST THIS */
  public static void stateSeqRawSideBySideToConsole(AppUtils appUtils, String[] stateNames, JSONObject allStates){
    Cons.log(StringUtils.charTimesN('#',80));
    String[] stateSeqArr;
    for (int i=0; i<stateNames.length; i++){
      Cons.log(StringUtils.charTimesN('=',80));
      Cons.log("State: '"+stateNames[i]+"'");
      Cons.log(StringUtils.charTimesN('-',80));
      stateSeqArr= allStates.getJSONArray(stateNames[i]).toStringArray();
      for (int j=0; j<stateSeqArr.length; j++){
        Cons.log(StringUtils.padR(stateSeqArr[j],12)+" : ");
        SpriteClip.printSpriteSeqStrArr(frameLineToSpriteArray(appUtils, stateSeqArr[j]));
      }
      if(i<stateNames.length-1){Cons.log();}
    }
    Cons.log(StringUtils.charTimesN('#',80)+'\n');
  }

  /** @todo TEST THIS */
  public static void stateSeqRawFlatSequenceToConsole(AppUtils appUtils, String[] stateNames, JSONObject allStates){
    Cons.log(StringUtils.charTimesN('#',80));
    int paddingLen = StringUtils.maxCharLengthOf(DataStructUtils.keyArrayOfJSONObj(allStates))+2;
    ArrayList<String> allFrames = new ArrayList<String>();
    String[] stateSeqArr;
    for (String stateName : stateNames){
      Cons.log(StringUtils.padR("'"+stateName+"'", paddingLen)+" : ");
      stateSeqArr = allStates.getJSONArray(stateName).toStringArray();
      allFrames.clear();
      for (int i=0; i<stateSeqArr.length; i++){
        java.util.Collections.addAll(allFrames, frameLineToSpriteArray(appUtils,stateSeqArr[i]));
      }
      SpriteClip.printSpriteSeqStrArr(FormatUtils.strArrListToStrArr(allFrames),false,true,false);
    }
    Cons.log(StringUtils.charTimesN('#',80));
  }




  private static String[] conserr_exp3CompDECStr(String l){
    System.err.println("Error: Expecting [3] (three) components! Erroneous line String = '"+l+"'");
    return null;
  }

  private static Sprite conserr_findSprite(String sn, String fp){
    System.err.println("Error: Cannot find sprite '"+sn+"' at location '"+fp+"'");
    return null;
  }

  private static Sprite conserr_loadSprite(String sn, String fp){
    System.err.println("Error: Cannot load sprite '"+sn+"' at location '"+fp+"'");
    return null;
  }

  private static PVector conswarn_cantFindHdrOffset(){
    Cons.warn("Warning: Image does NOT seem to specify sprite offset in PNG header. Check it in VSCode hex view mode? Returning {0,0}.");
    return new PVector();
  }

}