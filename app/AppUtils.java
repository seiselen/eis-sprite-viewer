package app;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import PrEis.utils.Cons;
import PrEis.utils.Cons.Act;
import processing.core.PApplet;
import processing.data.JSONObject;

/** 
 * Utils and State specific to the <b>EiSpriteViewer</b> App; ergo analogous to
 * a combination of both <code>App.ts</code> and <code>AppSettings.ts</code>.
 */
public class AppUtils {
  public PApplet app;
  public SpriteClip[] curAnimClips;
  public SpriteGroup  curSpriteGroup;
  public boolean      curExtractOffs;

  public String     DP_SPRITES;
  public String     DP_BRIGHTS;
  public JSONObject JO_STATE_ANIMS;
  public JSONObject JO_SPRITE_OFFS;
  public JSONObject JO_CUR_TARGET;

  /** Play loaded target immediately on successfully loading/initializing it? */
  public static boolean PLAY_ON_LOAD = false;
  
  public AppUtils(PApplet p){app = p;}

  private void onSelErr(String msg){
    Cons.err("onSelectionMade - "+msg); Cons.act(Act.RETURN_NO_ACTION);  
  }


  /** 
   * This is either the callback to `selectInput` else called thereby. Expected
   * input is filepath to 'Mk.2 Target JSON' as defined via resp. Obsidian page.
   * @implNote <b>ORDER <i>(likely)</i> COUNTS!</b> 
   */
  public void onSelectionMade(File sel){
    //> TODO: put these into `reset` function as with `SpriteGroup`
    curSpriteGroup = null;
    curAnimClips = null;
    curExtractOffs = false;


    //-[ GET SELECTED TARGET JSON OBJ ]----------------------------------------#
    handle_JO_CUR_TARGET(sel);
    if(JO_CUR_TARGET==null){onSelErr("JSON was loaded but is somehow null"); return;}
    //-[ GET SPRITES (AND BRIGHTS) DIRPATH ]-----------------------------------#
    handle_SPRITE_PATH();
    if(DP_SPRITES==null){onSelErr("Failed to find or load sprite path"); return;}
    if(DP_BRIGHTS==null){Cons.log("NOTE: Failed to find or load brights path (assuming N/A).");}
    //-[ GET ANIM DEFS JSON OBJ ]----------------------------------------------#
    handle_STATE_ANIMS();
    if(JO_STATE_ANIMS==null){onSelErr("Failed to find or load anim defs"); return;}
    //-[ GET SPRITE OFFS JSON OBJ ]--------------------------------------------#
    handle_SPRITE_OFFS();
    if(JO_SPRITE_OFFS==null){
      if (curExtractOffs==false){Cons.log("NOTE: Failed to find or load sprite offsets file (assuming N/A)");}
      else{Cons.log("NOTE: Target file specifies DO extract sprite offs from pngs!");}
    }

    
    curAnimClips = SpriteUtils.getAllSpriteClipsOf(this);

    AppMain.appGUI.addOptionsToDropdown();

    curSpriteGroup = new SpriteGroup(this)
    .initializeΘ(
      curAnimClips,
      JO_SPRITE_OFFS
    );

    if(curExtractOffs){
      System.out.println("Attention: Extracting sprite offsets!");
      curSpriteGroup.extractOffsets();
    }
    
  
    AppMain.player.injectSpriteGroup(curSpriteGroup);
    if(PLAY_ON_LOAD){AppMain.player.onPlay();}
  }


  /** Assigns value to {@link #JO_CUR_TARGET}. */
  private void handle_JO_CUR_TARGET(File fSel){
    //> Data Validation #1: null file and non-JSON MIME.
    if (fSel==null || !fSel.getName().endsWith("json")){onSelErr("selection null xor not JSON"); return;}

    String fnSel = fSel.getAbsolutePath();
    //> Data Validation #2: string filepath of target is null[ish]
    if(fnSel==null || fnSel.isEmpty()){onSelErr("selection file path string nullish"); return;}

    try {JO_CUR_TARGET = app.loadJSONObject(fnSel);} 
    //> Data Validation #3: loadJSOBObject failed s.t. fatal exception
    catch (Exception e) {onSelErr("fatal error on `loadJSONObject` call"); return;}
  }


  /** Assigns values to {@link #DP_SPRITES} and <i>(maybe)</i> {@link #DP_BRIGHTS}. */
  private void handle_SPRITE_PATH(){
    String sPthDir;
    try {
      sPthDir = JO_CUR_TARGET.getString(ResPath.SPRITE_PATH.get());
      if(sPthDir==null || sPthDir.isEmpty()){onSelErr("Sprite pathstring exists but is nullish"); return;}
      else{DP_SPRITES = sPthDir;}
    } catch (Exception e){;}

    JSONObject sPthObj = null;
    try {
      sPthObj = JO_CUR_TARGET.getJSONObject(ResPath.SPRITE_PATH.get());
      if(sPthObj==null){onSelErr("Sprite pathstrings object is nullish"); return;}
    } catch (Exception e){;}
    
    try {
      sPthDir = sPthObj.getString(ResPath.SPRITE_PATH.get());
      if(sPthDir==null || sPthDir.isEmpty()){onSelErr("Sprite pathstring exists but is nullish"); return;}
      else{DP_SPRITES = sPthDir;}
    } catch (Exception e){;}    

    try {
      DP_BRIGHTS = sPthObj.getString(ResPath.BRIGHT_PATH.get());      
    } catch (Exception e){;}
  }


  /** Assigns value to {@link #JO_STATE_ANIMS}. */
  private void handle_STATE_ANIMS(){
    String sPthDir;
    JSONObject josa = null;
    //-[CASE: Animdefs JSONObject Realized As Extern File]---------------------#
    try {
      sPthDir = JO_CUR_TARGET.getString(ResPath.STATE_ANIMS.get());
      if(sPthDir==null || sPthDir.isEmpty()){onSelErr("State anims pathstring exists but is nullish"); return;}
      josa = app.loadJSONObject(sPthDir);
    } catch (Exception e){;}
    //-[CASE: Animdefs JSONObject Realized In Target JSON File]----------------#
    try {
      josa = JO_CUR_TARGET.getJSONObject(ResPath.STATE_ANIMS.get());
    } catch (Exception e){;}

    if(josa==null){onSelErr("State anims object loaded but is nullish"); return;}
    JO_STATE_ANIMS = josa;
  }
  
  /** Assigns value to {@link #JO_SPRITE_OFFS}. */
  private void handle_SPRITE_OFFS(){
    String oPthDir;
    JSONObject joso = null;
    //-[CASE: Target File Specs Do Extract Offs From Sprites]------------------#
    try {
      curExtractOffs = JO_CUR_TARGET.getBoolean(ResPath.SPRITE_OFFS.get());
      return;
    } catch (Exception e){;}
    //-[CASE: Sprite Offs JSONObject Realized As Extern File]------------------#
    try {
      oPthDir = JO_CUR_TARGET.getString(ResPath.SPRITE_OFFS.get());
      if(oPthDir==null || oPthDir.isEmpty()){onSelErr("Sprite offs pathstring exists but is nullish"); return;}
      joso = app.loadJSONObject(oPthDir);
    } catch (Exception e){;}
    //-[CASE: Sprite Offs JSONObject Realized In Target JSON File]-------------#
    try {
      joso = JO_CUR_TARGET.getJSONObject(ResPath.SPRITE_OFFS.get());
    } catch (Exception e){;}

    if(joso==null){onSelErr("Sprite offs object loaded but is nullish"); return;}
    JO_SPRITE_OFFS = joso;
  }


  public Path getSpriteDirpath(){return Paths.get(DP_SPRITES);}
  public boolean hasCurSpriteGroup(){return curSpriteGroup!=null;}
  public SpriteGroup getSpriteGroup(){return curSpriteGroup;}
}