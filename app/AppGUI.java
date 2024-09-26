package app;
import PrEis.utils.BBox;
import PrEis.utils.DataStructUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.data.IntDict;
import processing.data.JSONObject;
import processing.data.StringList;
import PrEis.gui.AppFont;
import PrEis.gui.ConfirmState;
import PrEis.gui.IActionCallback;
import PrEis.gui.IConfirmAction;
import PrEis.gui.ISelectAction;
import PrEis.gui.IToggleCallback;
import PrEis.gui.IUpdateCallback;
import PrEis.gui.LabelType;
import PrEis.gui.UIClick;
import PrEis.gui.UIConfirm;
import PrEis.gui.UIContainer;
import PrEis.gui.UIDropdown;
import PrEis.gui.UILabel;
import PrEis.gui.UIManager;
import PrEis.gui.UIObject;
import PrEis.gui.UIToggle;
import PrEis.gui.PosOri;

public class AppGUI {
  UIManager uim;
  AppUtils appUtil;
  PApplet  app;
  IntDict  glyphDict;

  PFont labelFont;
  PFont glyphFont;

  public AppGUI(AppUtils iAppUtils, PFont iLabelFont, PFont iGlyphFont){
    appUtil   = iAppUtils;
    app       = appUtil.app;
    labelFont = iLabelFont;
    glyphFont = iGlyphFont;
    uim = new UIManager(app)
    .injectFonts(iLabelFont, iGlyphFont);
    initCustomGlyphs();
    loadUIOBjects();
  }

  private void initCustomGlyphs(){
    JSONObject jo = app.loadJSONObject(AppMain.fullpathOf(ResPath.SYMCMAP));
    glyphDict = new IntDict();
    String[] keys = DataStructUtils.keyArrayOfJSONObj(jo);
    for (String k : keys){glyphDict.add(k,jo.getInt(k));}
  }

  private String glyphChar(String n){return ""+(char)glyphDict.get(n);}

  private PVector vec(float ... coords){return DataStructUtils.createVector(coords);}
  private BBox bbox(PVector pos, PVector dim){return new BBox(pos, dim);}
  
  public void update(){uim.update();}
  public void render(){uim.render();}
  public void onMousePressed(){uim.onMousePressed();}
  public void onMouseWheel(int v){uim.onMouseWheel(v);}

  /**
   * @todo {@link UIContainer}, {@link UIManager}, and {@link UIObject} need to
   * be reimplemented WRT each other as to appropriately handle how containers
   * bind, update, render, etc. their children; as it's fuckall right now. The
   * current situ (i.e. containers are invisible to manager) should thankfully
   * not be breaking at this time... but they <b>WILL</b> break eventually.
   */
  public void loadUIOBjects(){
    float xOff = 0; 
    float yOff = 0;
    float yPad = 4;
    float xPad = 8;
    float eDim = 48;
    float eWid = 64;
    int tspt = app.color(255,0);
    PVector panPos = vec(416,72);

  //=[PLAYBACK BUTTONS GROUP]===================================================
    UIContainer.create(uim, bbox(panPos, panPos)).addChildren(
      UIClick.create(app, bbox(vec(xOff,yOff), vec(eWid,eDim)),
        glyphChar("gClipBack"), AppFont.GLYPH,        
        new PrevClipAction(appUtil)
      ).setTitle("Switch To Previous Anim Clip").castTo(UIClick.class),

      UIClick.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gFrameBack"), AppFont.GLYPH,       
        new PrevFrameAction(appUtil)
      ).setTitle("Go Back By One Frame").castTo(UIClick.class),

      UIClick.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gStop"), AppFont.GLYPH,
        new PlayerStopAction(appUtil)
      ).setTitle("Stops Anim Clip Playback & Goes To First Frame").castTo(UIClick.class),

      UIClick.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gPlay"), AppFont.GLYPH,
        new PlayerPlayAction(appUtil)
      ).setTitle("[Re]Starts Anim Clip Playback").castTo(UIClick.class),

      UIToggle.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gPause"), AppFont.GLYPH,
        new PlayerPauseAction(appUtil)
      ).setTitle("Pauses Anim Clip Playback").castTo(UIToggle.class),
  
      UIClick.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gFrameFore"), AppFont.GLYPH,
        new NextFrameAction(appUtil)
      ).setTitle("Go Forward By One Frame").castTo(UIClick.class),
  
      UIClick.create(app, bbox(vec(xOff+=eWid+xPad,yOff), vec(eWid,eDim)),
        glyphChar("gClipFore"), AppFont.GLYPH,
        new NextClipAction(appUtil)
      )
      .setTitle("Switch To Next Anim Clip").castTo(UIClick.class)
    );

  //=[CUR CLIP/FRAME LABELS GROUP]==================================================  
    xOff = 0; yOff = 0; yPad=8; PVector v = vec(8,0);

    UIContainer.create(
      uim, bbox(vec(0,AppMain.appBar.getTopBarYOff()+8), vec(eWid,eDim))
    ).addChildren(
      UILabel.create(app, bbox(vec(96,yOff), vec(168,32)),
        SpriteGroupPlayer.BLURB_CURCLIP, AppFont.TEXT, LabelType.TP, null
      ).setStyleProp("strk_transp", Integer.class, tspt)
      .setStyleProp("fill_transp", Integer.class, tspt)
      .setStyleProp("txt_anchor",  PosOri.class,  PosOri.RGT)
      .setStyleProp("txt_offset",  PVector.class, v.copy()),

      UILabel.create(app, bbox(vec(96,yOff+32), vec(168,32)),
        SpriteGroupPlayer.BLURB_CURSPRITE, AppFont.TEXT, LabelType.TP, null
      ).setStyleProp("strk_transp", Integer.class, tspt)
      .setStyleProp("fill_transp", Integer.class, tspt)
      .setStyleProp("txt_anchor",  PosOri.class,  PosOri.RGT)
      .setStyleProp("txt_offset",  PVector.class, v.copy()),

      UILabel.create(app, bbox(vec(96,yOff), vec(168,32)),
        null, AppFont.TEXT, LabelType.TP, new DispCurClip(appUtil)
      ).setStyleProp("strk_transp", Integer.class, tspt)
      .setStyleProp("fill_transp", Integer.class, tspt)
      .setStyleProp("txt_anchor",  PosOri.class,  PosOri.LFT)
      .setStyleProp("txt_offset",  PVector.class, v.copy()),
      
      
      UILabel.create(app, bbox(vec(96,yOff+32), vec(168,32)),
        null, AppFont.TEXT, LabelType.TP, new DispCurSprt(appUtil)
      ).setStyleProp("strk_transp", Integer.class, tspt)
      .setStyleProp("fill_transp", Integer.class, tspt)
      .setStyleProp("txt_anchor",  PosOri.class,  PosOri.LFT)
      .setStyleProp("txt_offset",  PVector.class, v.copy())
    );

  //=[ZOOM BUTTONS GROUP]===========================================================
    yPad = 8; eDim = 48;

    UIContainer.create(
      uim, bbox(vec(AppMain.CANVAS_WIDE-56,AppMain.CANVAS_TALL-256), vec(eDim,232))
    ).addChildren(
      UILabel.create(app, bbox(vec(0,yOff), vec(eDim,eDim)),
        null, AppFont.TEXT, LabelType.OP, new DispZoomLevAction(AppMain.hudManager)
      )
      .setTitle("Current Zoom Level"),

      UIClick.create(app, bbox(vec(0,yOff+=eDim+yPad), vec(eDim,eDim)),
        glyphChar("gZoomIn"), AppFont.GLYPH, new ZoomInAction(AppMain.hudManager)
      )
      .setTitle("Zoom In (Increase Scalar)"),

      UIClick.create(app, bbox(vec(0,yOff+=eDim+yPad), vec(eDim,eDim)),
        glyphChar("gZoomOne"), AppFont.GLYPH, new ZoomOneAction(AppMain.hudManager)
      )
      .setTitle("Zoom To Default (1X Scalar)")
      .castTo(UIClick.class),

      UIClick.create(app, bbox(vec(0,yOff+=eDim+yPad), vec(eDim,eDim)),
        glyphChar("gZoomOut"), AppFont.GLYPH, new ZoomOutAction(AppMain.hudManager)
      )
      .setTitle("Zoom Out (Decrease Scalar)")
      
      .castTo(UIClick.class)
    );


  //=[STATES BUTTONS GROUP]=========================================================
    eWid = 160;
    eDim = 320;
    xOff = app.width - eWid - 32;
    yOff = AppMain.appBar.getTopBarYOff()+32;


    StringList states = new StringList(appUtil.curSpriteGroup.getAllStateNames());
    states.sort();

    UIDropdown.create(uim, new BBox(xOff, yOff, eWid, eDim))
    .addOptions(states.toArray(null)) 
    .bindAction(new DropdownSelectAnim(appUtil));


  //=[FPS BUTTON GROUP]=============================================================
    eDim = 48; xPad = 8; xOff = 0;


    UIContainer.create(
      uim, vec(8,AppMain.CANVAS_TALL-88), vec(160,eDim)
    ).addChildren(
      UIClick.create(app, bbox(vec(xOff,0), vec(eDim,eDim)),
        glyphChar("gFPSDec"), AppFont.GLYPH, new FpsDecAction(AppMain.fpsManager)
      ).setTitle("Reduce Playback Speed [-5] FPS").castTo(UIClick.class),

      UIClick.create(app, bbox(vec(xOff+=eDim+xPad,0), vec(eDim,eDim)),
        glyphChar("gFPSInc"), AppFont.GLYPH, new FpsIncAction(AppMain.fpsManager)
      ).setTitle("Increase Playback Speed [+5] FPS)").castTo(UIClick.class),

      UIClick.create(app, bbox(vec(xOff+=eDim+xPad,0), vec(eDim,eDim)), 
        glyphChar("gFPSDef"), AppFont.GLYPH, new FpsDefAction(AppMain.fpsManager)
      ).setTitle("Set Playback Speed To [35] FPS (AKA Doom Tic Units)").castTo(UIClick.class)
    );  

  //=[QUIT BUTTON]==================================================================
    eDim = 48; yPad = 8; xPad = 16;

    UIConfirm.create(
      uim, 
      bbox(vec(AppMain.CANVAS_WIDE-168,8), vec(160,40)),
      new AppQuitAction(app)
    )
    .setButtonLabelsΘ("QUIT APP", "QUIT ?!?", "QUITTING")
    .setTitle("Click Twice To Exit App")
    ;

  }
}


/*=[ CALLBACK DEFS - KEEP THESE, AND WITHIN THIS FILE! ]======================*/
class DropdownSelectAnim implements ISelectAction {
  private AppUtils appUtils;
  public DropdownSelectAnim(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void OnSelection(String selOpt) {
    if(!appUtils.hasCurSpriteGroup()){return;}    
    /*> Equivalent to: "Oi Guv... Ave Ye Got A SpriteClip Of Dis Name?" */
    if(appUtils.getSpriteGroup().getIndexByName(selOpt)>0){
      appUtils.curSpriteGroup.setCurClipByName(selOpt);
    }
    System.out.println("Selected: ["+selOpt+"]"); //> (temporary?) else remove it
  }
}

class PlayerPauseAction implements IToggleCallback {
  private AppUtils appUtils;
  public PlayerPauseAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public boolean getState(){return appUtils.spritePlayer.paused;}
  public void toggleState(){
    if(!appUtils.hasSpritePlayer()){return;}
    appUtils.spritePlayer.onPause();
  }
}

class PlayerPlayAction implements IActionCallback {
  private AppUtils appUtils;
  public PlayerPlayAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasSpritePlayer()){return;}
    appUtils.spritePlayer.onPlay();
  }
}

class PlayerStopAction implements IActionCallback {
  private AppUtils appUtils;
  public PlayerStopAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasSpritePlayer()){return;}
    appUtils.spritePlayer.onStop();
  }
}

class NextClipAction implements IActionCallback {
  private AppUtils appUtils;
  public NextClipAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasCurSpriteGroup()){return;}
    appUtils.curSpriteGroup.nextClip();
  }
}

class PrevClipAction implements IActionCallback {
  private AppUtils appUtils;
  public PrevClipAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasCurSpriteGroup()){return;}
    appUtils.curSpriteGroup.prevClip();
  }
}

class NextFrameAction implements IActionCallback {
  private AppUtils appUtils;
  public NextFrameAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasCurSpriteGroup()){return;}
    appUtils.curSpriteGroup.curClipNextFrame();
  }
}

class PrevFrameAction implements IActionCallback {
  private AppUtils appUtils;
  public PrevFrameAction(AppUtils iAppUtils){appUtils=iAppUtils;}
  public void action(){
    if(!appUtils.hasCurSpriteGroup()){return;}
    appUtils.curSpriteGroup.curClipPrevFrame();
  }
}

class SelectAnimAction implements IActionCallback {
  private String state;
  private AppUtils appUtils;
  public SelectAnimAction(AppUtils iAppUtils, String s){appUtils=iAppUtils; state=s;}
  public void action(){
    if(!appUtils.hasCurSpriteGroup()){return;}
    appUtils.curSpriteGroup.setCurClipByName(state);
  }
}

class ZoomInAction implements IActionCallback {
  private HUDManager hm;  
  public ZoomInAction(HUDManager ihm){hm=ihm;}
  public void action(){hm.setScalar(hm.curScalar+0.5f);}
}

class ZoomOutAction implements IActionCallback {
  private HUDManager hm;  
  public ZoomOutAction(HUDManager ihm){hm=ihm;}  
  public void action(){hm.setScalar(hm.curScalar-0.5f);}
}

class ZoomOneAction implements IActionCallback {
  private HUDManager hm;  
  public ZoomOneAction(HUDManager ihm){hm=ihm;}  
  public void action(){hm.setScalar(1);}
}

class FpsIncAction implements IActionCallback {
  private FPSManager fm;  
  public FpsIncAction(FPSManager ifm){fm=ifm;}  
  public void action(){fm.incTarFPS(5);}
}

class FpsDecAction implements IActionCallback {
  private FPSManager fm;  
  public FpsDecAction(FPSManager ifm){fm=ifm;}  
  public void action(){fm.decTarFPS(5);}
}

class FpsDefAction implements IActionCallback {
  private FPSManager fm;  
  public FpsDefAction(FPSManager fpsManager){fm=fpsManager;} 
  public void action(){fm.setTarFPS(FPSManager.DEF_FPS);}
}


class AppQuitAction implements IConfirmAction {
  private PApplet app;
  private ConfirmState cs;
  public AppQuitAction(PApplet iApp){app = iApp; cs = ConfirmState.ONINIT;}
  public void cancel(){cs = ConfirmState.ONINIT;}
  public ConfirmState getState(){return cs;}
  public void doAction(){app.exit();}
  public void action(){switch (cs){
    case ONINIT: cs = ConfirmState.ONWARN; return;
    case ONWARN: cs = ConfirmState.ONDONE; doAction(); return;
    default: return;
  }}
}



class DispZoomLevAction implements IUpdateCallback {
  private HUDManager hm;  
  public DispZoomLevAction(HUDManager ihm){hm=ihm;}  
  public String getTxt(){return ""+hm.curScalar;}
}

class DispCurClip implements IUpdateCallback {
  private AppUtils appUtils;
  public DispCurClip(AppUtils iAppUtils){appUtils=iAppUtils;}
  public String getTxt(){
    return appUtils.hasCurSpriteGroup() ? appUtils.curSpriteGroup.getCurClipName() : "N/A";
  }
}

class DispCurSprt implements IUpdateCallback {
  private AppUtils appUtils;
  public DispCurSprt(AppUtils iAppUtils){appUtils=iAppUtils;}
  public String getTxt(){
    return appUtils.hasCurSpriteGroup() ? appUtils.curSpriteGroup.getCurSpriteName() : "N/A";
  }
}