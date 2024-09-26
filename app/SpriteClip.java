package app;
import PrEis.utils.StringUtils;
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
  
  public void toConsole(){toConsole(0);}
  public void toConsole(int paddingLen){
    String pfix = StringUtils.wrapWith('\'', clipName);
    if(paddingLen<1){pfix = pfix+" : ";}
    else{pfix = StringUtils.padR(pfix, paddingLen)+" : ";}
    System.out.print(pfix);
    printSpriteSeqStrArr(spriteIDs,TOCONSOLE_WRAP_CLIP,TOCONSOLE_WRAP_FRMS,TOCONSOLE_WITH_CSV);
  }

  /**
   * @param wrapSeq wrap set of sprites with curly brackets? e.g. "{PLSAA, PLSAB}" -vs- "PLSAA, PLSAB"
   * @param wrapFrm wrap sprites between parenthesis chars? e.g. "(PLSAA)" -vs- "PLSAA"  
   * @param sepSCSV separate sprites with comma as well as space? e.g. "{PLSAA, PLSAB}" -vs- "{PLSAA PLSAB}"
   * @todo test this (and its overload) at some point
   */
  public static void printSpriteSeqStrArr(String[] arr, boolean wrapSeq, boolean wrapFrm, boolean sepSCSV){
    String str = "";
    for(int i=0; i<arr.length; i++){
      str += wrapFrm ? StringUtils.wrapParens(arr[i]) : arr[i];
      if(i<arr.length-1){str += sepSCSV ? ", " : " ";}
    }
    if(wrapSeq){str = StringUtils.wrapWith('{',str);}
    System.out.println(str);
  }

  public static void printSpriteSeqStrArr(String[] arr){
    printSpriteSeqStrArr(arr,true,true,true);
  }


}