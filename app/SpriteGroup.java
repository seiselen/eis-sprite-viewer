package app;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;
import processing.data.StringList;
import PrEis.utils.Cons;
import PrEis.utils.FileSysUtils;
import PrEis.utils.StringUtils;
import PrEis.utils.Cons.Act;
import PrEis.utils.Cons.Err;

public class SpriteGroup {
  String fileName;
  String filePath;
  String offsPath;
  SpriteClip[] animClips;
  SpriteClip curClip;
  int curClipIdx;
  String[] allSpritePKIDs;
  HashMap<String,Sprite> spriteDict;

  PApplet app;
  AppUtils appUtils;

  public SpriteGroup(AppUtils iAppUtils, String in_filePath, String in_offsPath){
    spriteDict     = new HashMap<String,Sprite>();
    appUtils       = iAppUtils;
    app            = appUtils.app;
    filePath       = in_filePath;
    fileName       = FileSysUtils.fnameFromFpath(in_filePath,false);
    offsPath       = in_offsPath;
    animClips      = SpriteUtils.getAllSpriteClipsOf(appUtils, app.loadJSONObject(filePath));
    allSpritePKIDs = getAllSpritePKIDs();
    installSprites();
    installOffsets();
    reset();
  }

  //> and now we get to the climax of this project! (well, one of the big'gunz, anyway...)
  public SpriteGroup installSprites(){
    if(allSpritePKIDs==null || allSpritePKIDs.length==0){
      Cons.err(Err.NULL_VALUE);
    }
    else{
      for (String pkid : allSpritePKIDs){
        spriteDict.put(pkid,SpriteUtils.spriteWithName(appUtils, pkid));
      }
    }
    return this; // for function chaining
  }
  
  public SpriteGroup installOffsets(){

    HashMap<String,PVector> loadedOffs = SpriteUtils.offsetJSONToOffsetMap(
      FileSysUtils.loadJSONObjNullFail(app,offsPath));

    //> harmless pass if `offs` happens to be nullish  
    if(loadedOffs==null){return null;} 
    PVector buffVec;
    Sprite buffSpr;
    for(String s : allSpritePKIDs){
      buffVec = loadedOffs.get(s);     
      if (buffVec==null){
        Cons.warn("Offset cannot be found for sprite '"+s+"'. Leaving at init value i.e. {0,0}");
      }
      else {
        buffSpr = spriteDict.get(s);
        if(buffSpr==null){Cons.err(Err.NULL_VALUE);}
        else{buffSpr.setOff(buffVec);}
      }
    }
    return this; // for function chaining
  }
 
  public SpriteGroup extractOffsets(){
    PVector buffVec; Sprite buffSpr;
    for(String s : allSpritePKIDs){
      buffVec = SpriteUtils.extractSpriteOffset(appUtils, s); 
      if(buffVec==null){
        Cons.warn("Warning: Offset cannot be found for sprite '"+s+"'. Leaving at init value i.e. {0,0}");
      }
      else{
        buffSpr = spriteDict.get(s);
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


  public void setCurClipToCurIdx(){
    curClip = animClips[curClipIdx];
  }



    
  public SpriteClip getCurAnimClip(){return curClip;}

  public String getCurClipName(){
    if(curClip==null){return "N/A";}    
    return curClip.getClipName();
  }
  
  public String getCurSpriteName(){
    if(curClip==null){return "N/A";}
    return curClip.getCurSpriteName();  
  }

  public Sprite getCurSpriteObj(){
    if(
      allSpritePKIDs==null     ||
      allSpritePKIDs.length==0 ||
      spriteDict==null         ||
      spriteDict.size()==0     ){
      Cons.err_act(Err.NULL_VALUE, Act.RETURN_NULL);
      return null;
    }
    Sprite s = spriteDict.get(getCurSpriteName());

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
  public String[] getAllSpritePKIDs(){
    StringList all_pkids = new StringList(); String[] buff_pkids;
    for (SpriteClip clip : animClips){
      buff_pkids = clip.getAllSpritePKIDs();
      for (String pkid : buff_pkids){all_pkids.appendUnique(pkid);}
    }
    return all_pkids.toArray();
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