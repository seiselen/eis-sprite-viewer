package app;
import java.nio.file.Path;
import java.nio.file.Paths;
import PrEis.utils.Cons;
import PrEis.utils.FileSysUtils;
import PrEis.utils.QueryUtils;
import processing.core.PApplet;
import processing.data.JSONObject;

/** 
 * Utils and State specific to the <b>EiSpriteViewer</b> App; ergo analogous to
 * a combination of both <code>App.ts</code> and <code>AppSettings.ts</code>.
 */
public class AppUtils {

  /*=[ COMPONENTS (PUBLIC TO EASE INTEGRATION B.S.) ]=========================*/
  public SpriteGroup curSpriteGroup;
  public SpriteGroupPlayer spritePlayer;

  /*=[ DIRS-&-DIRPATHS ]========================================================*/
  public String COMMON_DIR;
  public String SPRITE_DIR;
  public String STATE_ANIMS_JSON;
  public String SPRITE_OFFS_JSON;

  public PApplet app;

  public String tempTarPath = "C:/Users/Phoenix/Documents/projectsWorkspace/EiSpriteViewer/examples/target.json";

  public AppUtils(PApplet p){app = p;}

  /** @implNote <b>ORDER <i>(likely)</i> COUNTS!</b> */
  public void setup(){
    getTargetPaths();
    spritePlayer = new SpriteGroupPlayer(app);
    loadTarget();    
    curSpriteGroup.firstClip();
    spritePlayer.onPlay();
  }

  public void update(){spritePlayer.update();}
  public void onKeyPressed(){spritePlayer.onKeyPressed();}
  public void render(){spritePlayer.render();}

  public Path getSpriteDirpath(){return Paths.get(SPRITE_DIR);}
  public Path getAnimJsonFilepath(){return Paths.get(STATE_ANIMS_JSON);}
  public Path getSpriteOffsetsJsonFilepath(){return Paths.get(SPRITE_OFFS_JSON);}

  public boolean hasCurSpriteGroup(){return curSpriteGroup!=null;}

  public boolean hasSpritePlayer(){return spritePlayer!=null;}

  
  public SpriteGroup getSpriteGroup(){return curSpriteGroup;}
  public SpriteGroupPlayer getSpritePlayer(){return spritePlayer;}

  /** 
   * This is the "OnLoadTarget" behavior (more or less s.t. if less: move here the
   * rest) that needs to be turned into an action s.t. app loads bare OnInit s.t.
   * I can use Processing's file upload util to select a target (or otherwise).
   */
  public void loadTarget(){ 
    curSpriteGroup = new SpriteGroup(this, STATE_ANIMS_JSON, SPRITE_OFFS_JSON);
    spritePlayer.injectSpriteGroup(curSpriteGroup);
  }

  /** 
   * Fetches filepath/dirpath metadata located in <code>/data/target.json</code>
   * for one/more/all examples; assigning values to {@link #STATE_ANIMS_JSON},
   * {@link #SPRITE_OFFS_JSON}, and {@link #SPRITE_OFFS_JSON} (optional & A/A).
   */
  public void getTargetPaths (){
    JSONObject jsonObj = null; String tarName = null;
    try {
      String tarFullPath = FileSysUtils.pathConcat(app.sketchPath(),ResPath.EXAMPATH.get(),"target.json");
      //System.out.println("Target Full Path = '"+tarFullPath+"'");
      jsonObj = app.loadJSONObject(tarFullPath);
      if(jsonObj==null){Cons.err("Failed to load target json file"); return;}
      tarName = jsonObj.getString("LAUNCH_WITH");
      if(tarName==null||tarName.isEmpty()){Cons.err("Field 'LAUNCH_WITH' missing or null!"); app.exit(); return;}
    }
    catch (Exception e){
      Cons.err("Exception while attempting to load target json file"); return;
    }
    loadTargetConfig(jsonObj, tarName);
  }

  private void loadTargetConfig(JSONObject tarObj, String tarKey){
    JSONObject tarItm = null;
    /*-[ EXTRACT INPUT EXAMPLE METADATA ]---------------------------------------*/
    try{tarItm = tarObj.getJSONObject(tarKey);}
    catch (Exception e){Cons.err("NO metadata exists for example of key ["+tarKey+"]");}
    /*-[ EXTRACT STATE ANIMS JSON FILEPATH ]------------------------------------*/
    try{STATE_ANIMS_JSON = pathOf(tarItm.getString("STATE_ANIM_FILE"));}
    catch (Exception e){Cons.err("No State Anim File Specified for example of key ["+tarKey+"]");}
    /*-[ EXTRACT SPRITE ROOT DIRPATH ]------------------------------------------*/
    try{SPRITE_DIR = pathOf(tarItm.getString("SPRITE_ROOT_DIR"));}
    catch (Exception e){Cons.err("No Sprite Root Dir Specified for example of key ["+tarKey+"]");}
    /*-[ EXTRACT SPRITE OFFS DIR ]----------------------------------------------*/
    try{SPRITE_OFFS_JSON = pathOf(tarItm.getString("SPRITE_OFF_FILE"));}
    catch (Exception e){Cons.warn("No Sprite Offset File Specified for example of key ["+tarKey+"]");}
    /*-[ NULLISH REQS HANDLE ]--------------------------------------------------*/
    if(QueryUtils.nullAny(tarObj,STATE_ANIMS_JSON,SPRITE_DIR)){
      Cons.err("Metadata missing or deficient for example of key ["+tarKey+"]. Exiting");
      app.exit(); return;
    }
  }

  private String pathOf(String s){
    if(s.substring(0,2).equals("C:")){return s;}
    else if(s.charAt(0)=='/'){return app.sketchPath()+s;}
    else{return null;}
  }

}