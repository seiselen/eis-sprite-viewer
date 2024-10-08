package app;
import java.util.HashMap;

import processing.core.PVector;
import processing.data.JSONObject;
import processing.data.StringList;
import PrEis.utils.Cons;
import PrEis.utils.StringUtils;
import PrEis.utils.ZScriptUtils;
import PrEis.utils.Cons.Act;
import PrEis.utils.Cons.Err;

public class SpriteGroup {
  String fileName;
  String filePath;
  SpriteClip[] animClips;
  SpriteClip curClip;
  int curClipIdx;
  String[] allSpritePKIDs;
  HashMap<String,Sprite> sprDict;
  AppUtils appUtil;

  public SpriteGroup(AppUtils iAppUtils){
    sprDict = new HashMap<String,Sprite>();
    appUtil = iAppUtils;
  }

  public SpriteGroup initializeΘ(SpriteClip[] in_animClips, JSONObject in_spriteOffs){
    initialize(in_animClips, in_spriteOffs); return this;
  }

  public void initialize(SpriteClip[] in_animClips, JSONObject in_spriteOffs){
    //> REMEMBER: ORDER COUNTS!
    reset();
    animClips = in_animClips;
    getAllSpritePKIDs();
    installSprites();
    if (in_spriteOffs!=null){installOffsets(in_spriteOffs);}
    firstClip();
  }


  private void installSprites(){    
    if(allSpritePKIDs==null || allSpritePKIDs.length==0){Cons.err(Err.NULL_VALUE);}
    else{for (String pkid : allSpritePKIDs){sprDict.put(pkid,Sprite.withName(appUtil, pkid));}
    }
  }
  
  public void installOffsets(JSONObject inOffs){
    HashMap<String,PVector> offs = ZScriptUtils.offsetJSONToOffsetMap(inOffs);

    PVector buffVec;
    Sprite buffSpr;
    for(String s : allSpritePKIDs){
      buffVec = offs.get(s);     
      if (buffVec==null){
        Cons.warn("Offset cannot be found for sprite '"+s+"'. Leaving at init value i.e. {0,0}");
      }
      else {
        buffSpr = sprDict.get(s);
        if(buffSpr==null){Cons.err(Err.NULL_VALUE);}
        else{buffSpr.setOff(buffVec);}
      }
    }
  }
 
  public SpriteGroup extractOffsets(){
    PVector buffVec; Sprite buffSpr;
    for(String s : allSpritePKIDs){
      buffVec = ZScriptUtils.extractSpriteOffset(appUtil.app, appUtil.getSpriteDirpath(), s); 
      if(buffVec==null){
        Cons.warn("Warning: Offset cannot be found for sprite '"+s+"'. Leaving at init value i.e. {0,0}");
      }
      else{
        buffSpr = sprDict.get(s);
        if(buffSpr==null){Cons.err(Err.NULL_VALUE);}
        else{buffSpr.setOff(buffVec);}
      }
    }
    return this; // for function chaining
  }
  
  public void reset(){curClipIdx=0; curClip=null;}

  public void curClipNextFrame(){if(curClip==null){return;} curClip.nextFrame();}

  public void curClipPrevFrame(){if(curClip==null){return;} curClip.prevFrams();}

  public void curClipReset(){if(curClip==null){return;} curClip.startFrame();}

  public void firstClip(){if(animClips.length>0){setCurClipByIdx(0);}}

  public SpriteGroup firstClipΘ(){firstClip(); return this;}  
  
  public void nextClip(){_offClipIter(1);}

  public void prevClip(){_offClipIter(-1);}

  private void _offClipIter(int v){
    curClipIdx+=v;
    if(curClipIdx>=animClips.length){curClipIdx=0;}
    if(curClipIdx<0){curClipIdx=animClips.length-1;}
    setCurClipToCurIdx();
  }

  public void setCurClipByName(String name){
    int idx = getIndexByName(name);
    if(idx==-1){
      System.err.println("Error: NO clip of name '"+name+"'");
      return;
    }
    setCurClipByIdx(idx);
  }

  public void setCurClipByIdx(int idx){
    if(idx<0||idx>=animClips.length){
      System.err.println("Error: Invalid Index ["+idx+"]");
      return;
    }
    curClipIdx=idx;
    curClip = animClips[curClipIdx];
  }

  public void setCurClipToCurIdx(){curClip = animClips[curClipIdx];}

  public SpriteClip getCurAnimClip(){return curClip;}

  public String getCurClipName(){if(curClip==null){return "N/A";} return curClip.getClipName();}
  
  public String getCurSpriteName(){if(curClip==null){return "N/A";} return curClip.getCurSpriteName();}

  public String getCurSpriteOff(){
    if(curClip==null){return "N/A";}
    //> RISKY as no `null` protection because I'm too stupid to def a local var
    return sprDict.get(getCurSpriteName()).offToString();
  }



  public Sprite getCurSpriteObj(){
    if(
      allSpritePKIDs==null     ||
      allSpritePKIDs.length==0 ||
      sprDict==null         ||
      sprDict.size()==0     ){
      Cons.err_act(Err.NULL_VALUE, Act.RETURN_NULL);
      return null;
    }
    Sprite s = sprDict.get(getCurSpriteName());

    return s;
  }
  
  public String[] getClipNames(){
    int len = animClips.length;
    String[] names = new String[len];
    for (int i=0; i<len; i++){names[i] = animClips[i].getClipName();}
    return names;
  }

  public int getIndexByName(String clipName){
    int retIdx = -1;
    for (int i=0; i<animClips.length; i++){
      if(animClips[i].getClipName().equals(clipName)){retIdx=i; break;}
    }
    return retIdx;
  }

  //> Returns `String[]` of all unique sprite names/IDs (i.e. no dupes, plz!)
  public void getAllSpritePKIDs(){
    StringList all_pkids = new StringList(); String[] buff_pkids;
    for (SpriteClip clip : animClips){
      buff_pkids = clip.getAllSpritePKIDs();
      for (String pkid : buff_pkids){all_pkids.appendUnique(pkid);}
    }
    allSpritePKIDs = all_pkids.toArray();
  }
  
  public String[] getAllStateNames(){
    int len = animClips.length;
    String[] names = new String[len];
    for(int i=0; i<len; i++){names[i] = animClips[i].getClipName();}
    return names;
  }


  public String curClipNameToString(){
    return (curClip==null) ? "N/A" : curClip.getClipName();
  }
  
  public void toConsole(){
    int padLen = StringUtils.maxCharLengthOf(getAllStateNames())+2; //> `2` => `2` single quote chars;
    Cons.log(
      StringUtils.charTimesN('#',80),
      "States Of ["+fileName+"] As Follows:",
      StringUtils.charTimesN('-', 80)
    );
    for(SpriteClip c : animClips){c.toConsole(padLen);}
    Cons.log(StringUtils.charTimesN('-', 80));
    Cons.log("Current Clip: "+curClipNameToString());
    Cons.log(StringUtils.charTimesN('#',80));
  }

}