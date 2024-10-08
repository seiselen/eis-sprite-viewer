package app;
import java.util.ArrayList;
import PrEis.utils.DataStructUtils;
import PrEis.utils.FormatUtils;
import PrEis.utils.StringUtils;
import PrEis.utils.ZScriptUtils;
import processing.data.JSONObject;
import processing.data.StringList;

/** 
 * Realizes a sprite sequence (i.e. encompassing a state of a DECORATE statedef)
 * as an array of sprite IDs and index of a current element thereof; of which is
 * (inc|dec)remented like a filmstrip. Note that sprites are NOT rendered here
 * nor even mentioned! Such is the {@link AnimGroup} object's responsibility.
 */
public class SpriteClip {
  private static final boolean TOCONSOLE_WRAP_CLIP = false;
  private static final boolean TOCONSOLE_WRAP_FRMS = true;
  private static final boolean TOCONSOLE_WITH_CSV  = false;
  
  private String clipName;
  private String[] spriteIDs;
  private int curIdx;

  public SpriteClip(String iName, String[] iSpriteIDs){
    clipName=iName;
    spriteIDs=iSpriteIDs;
    startFrame();
  }
  
  public void startFrame(){curIdx = 0;}

  public void finalFrame(){curIdx = spriteIDs.length-1;}

  public void nextFrame(){_offFrameIdx(1);}

  public void prevFrams(){_offFrameIdx(-1);}

  private void _offFrameIdx(int off){
    curIdx+=off;
    if(curIdx>=spriteIDs.length){startFrame();}
    else if(curIdx<0){finalFrame();}
    //Cons.log(getClipProgress());
  }

  public String getClipName(){return clipName;}

  public String getCurSpriteName(){return spriteIDs[curIdx];}

  public String getNumSprites(){return ""+(spriteIDs==null ? 0 : spriteIDs.length);}

  public int getCurSpriteIdx(){return curIdx;}

  public String getClipProgress(){return StringUtils.wrapParens(getCurSpriteIdx()+"/"+getNumSprites());}

  public String[] getAllSpritePKIDs(){
    StringList pkids = new StringList();
    for(int i=0; i<spriteIDs.length; i++){pkids.appendUnique((spriteIDs[i]));}
    return pkids.toArray();
  }
  



  /*=[ STATIC UTIL GETTERS ]----------------------------------------------------
  +===========================================================================*/  

  public static String[] animClipsToClipNames(SpriteClip[] clips){
    String[] ret = new String[clips.length];
    for(int i=0; i<clips.length; i++){
      ret[i] = clips[i].getClipName();
    } 
    return ret;
  }

  /** @todo TEST THIS */
  public static SpriteClip[] getAllSpriteClipsOf(AppUtils au){
    String[] stateNames = DataStructUtils.keyArrayOfJSONObj(au.JO_STATE_ANIMS);    
    int nStates = stateNames.length;
    SpriteClip[] allClips = new SpriteClip[nStates];
    for (int i=0; i<nStates; i++){
      allClips[i] = rawStateDefToSpriteClip(au, stateNames[i],au.JO_STATE_ANIMS);
    }
    return allClips;
  }

  /** @todo TEST THIS */
  public static SpriteClip rawStateDefToSpriteClip(AppUtils appUtils, String stateName, JSONObject allStates){
    String[] stateSeqArr = allStates.getJSONArray(stateName).toStringArray();
    ArrayList<String> allFrames = new ArrayList<String>();
    for (int i=0; i<stateSeqArr.length; i++){
      java.util.Collections.addAll(allFrames, ZScriptUtils.frameLineToSpriteArray(stateSeqArr[i]));}
    return new SpriteClip(stateName, FormatUtils.strArrListToStrArr(allFrames));
  }

  /*=[ TOSTRING/TOCONSOLE ]-----------------------------------------------------
  +===========================================================================*/

  public void toConsole(){toConsole(0);}
  public void toConsole(int paddingLen){
    String pfix = StringUtils.wrapWith('\'', clipName);
    if(paddingLen<1){pfix = pfix+" : ";}
    else{pfix = StringUtils.padR(pfix, paddingLen)+" : ";}
    System.out.print(pfix);
    ZScriptUtils.printSpriteSeqStrArr(spriteIDs,TOCONSOLE_WRAP_CLIP,TOCONSOLE_WRAP_FRMS,TOCONSOLE_WITH_CSV);
  }

}