package app;

import PrEis.utils.FileSysUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.io.File;

public class AppMain  extends PApplet {

  public  static int CANVAS_WIDE = 1280;
  public  static int CANVAS_TALL = 768;
  public  static int CANVAS_WIDH = CANVAS_WIDE/2;
  public  static int CANVAS_TALH = CANVAS_TALL/2;

  public  static int    FILL_CANVAS;
  public  static PFont  TXTFONT;
  public  static PFont  SYMFONT;
  public  static PImage APPICON;
  public  static PImage APPLOGO;
  public  static PImage EISLOGO;

  public  static String assetPath;  
  public  static AppUtils appUtil;
  public  static AppGUI appGUI;
  public  static HUDManager hudManager;
  public  static FPSManager fpsManager;
  public  static AppBar appBar;
  public  static CustomCursor custCursor;
  public  static SpriteGroupPlayer player;

  public static void main(String[] args) {
    PApplet.main("app.AppMain");
    System.out.println("\n \n"); //> corrects debug launch blurb lack of newline
  }

  public void settings(){
    this.size(CANVAS_WIDE, CANVAS_TALL); 
  }


  /** @implNote <b>REMEMBER: ORDER ABSOLUTELY MATTERS HERE!!!</b> */
  public void setup(){
    noCursor();
    FILL_CANVAS = color(0);

    initAssetPath();
    loadAppAssets();
    appUtil    = new AppUtils(this);
    fpsManager = new FPSManager(this).bindFont(TXTFONT);
    hudManager = new HUDManager(this);
    appBar     = new AppBar(this, APPLOGO, EISLOGO);
    custCursor = new CustomCursor(this);
    player     = new SpriteGroupPlayer(this);
    appGUI     = new AppGUI(appUtil, TXTFONT, SYMFONT);
    println("Processing Function [setup] Successfully Executed");
  }


  /** Calls {@link #update}, then {@link #render}. */
  public void draw(){
    update();
    render();
  }

  /** @implNote per usual... <b>ORDER <i>(likely)</i> COUNTS!</b> */
  public void update(){
    appGUI.update();
    player.update();
    custCursor.update();
  }

  /** @implNote per usual... <b>ORDER COUNTS!</b> */
  public void render(){
    background(FILL_CANVAS);
    appBar.render();
    hudManager.render();
    player.render();
    fpsManager.dispFPS();
    appGUI.render();
    custCursor.render();
  }

  public void mousePressed(){
    appGUI.onMousePressed();    
  }

  public void keyPressed(){
    switch(key){case 'q' : case 'Q': exit(); return;}
    player.onKeyPressed();
  }

  public void mouseWheel(MouseEvent e){
    appGUI.onMouseWheel(e.getCount());
  }


  public void onLoadTargetSelect(File f){
    appUtil.onSelectionMade(f);
  }

  /** Loads GUI fonts, images, etc.; i.e. NOT app targets! */
  public void loadAppAssets(){
    TXTFONT = loadFont(fullpathOf(ResPath.TXTFONT));
    SYMFONT = loadFont(fullpathOf(ResPath.SYMFONT));
    APPICON = loadImage(fullpathOf(ResPath.APPICON));
    APPLOGO = loadImage(fullpathOf(ResPath.APPLOGO));
    EISLOGO = loadImage(fullpathOf(ResPath.EISLOGO));
  }


  public void initAssetPath(){
    File f;
    String ad = ResPath.ASSETDIR.get();
    String bd = ResPath.BUILDIR.get();

    //=[ 'PRODUCTION MODE' CASE (I.E. STANDALONE LAUNCHED BY USER) ]============
    f = new File(FileSysUtils.pathConcat(sketchPath(), ad));
    if (f.exists() && f.isDirectory()){assetPath = f.getAbsolutePath();}
    
    //=[ 'DEVELOPMENT MODE' CASE (I.E. DEBUG LAUNCHED BY VSCODE) ]==============
    f = new File(FileSysUtils.pathConcat(sketchPath(), bd, ad));
    if (f.exists() && f.isDirectory()){assetPath = f.getAbsolutePath();}
  }

  /** Returns path concat of {@link #assetPath} with input subpath therefrom. */
  public static String fullpathOf(ResPath sp){
    return FileSysUtils.pathConcat(assetPath,sp.get());
  }

}