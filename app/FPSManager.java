package app;
//=[MANAGER DEFINITION]===========================================================

import PrEis.utils.Pgfx;
import processing.core.PApplet;
import processing.core.PFont;

class FPSManager {
  
  public static final int DEF_FPS = 35; //> default fps (`35` per doom tic units)
  public static final int MIN_FPS =  5;
  public static final int MAX_FPS = 60;
  
  private int tarFPS;

  
  /** show FPS textbox? */
  private boolean showFPSTxtBox; 
  
  PApplet app;
  PFont font;

  public FPSManager(PApplet iApp){
    app = iApp;
    setShowFPS(true);
    setTarFPS(DEF_FPS);
  }

  public FPSManager bindFont(PFont iFont){
    font = iFont;
    return this;
  }
  
  public void toggleShowFPS(){showFPSTxtBox=!showFPSTxtBox;}
  public void setShowFPS(boolean v){showFPSTxtBox=v;}

  public void setTarFPS(int nFPS){
    tarFPS = PApplet.constrain(nFPS,MIN_FPS,MAX_FPS);
    app.frameRate(tarFPS);
  }
  
  public void incTarFPS(int dFPS){
    tarFPS = PApplet.constrain(tarFPS+dFPS,MIN_FPS,MAX_FPS);
    app.frameRate(tarFPS);
  }
  
  public void decTarFPS(int dFPS){
    tarFPS = PApplet.constrain(tarFPS-dFPS,MIN_FPS,MAX_FPS);
    app.frameRate(tarFPS);
  }
  
  public String toStringCurFPS(){
    return "["+PApplet.nfc(app.frameRate,2)+"]";
  }

  public String toStringTarFPS(){
    return "["+tarFPS+"]";
  }

  public String toStringBothFPS(){
    return "FPS (Current/Target) = ("+Math.round(app.frameRate)+"/"+tarFPS+")";
  }

  /** 
   * This should only be used if {@link PrEis.gui} is not used, xor not yet set
   * up. Otherwise: the {@link PrEis.gui.UILabel} widget is the <b>PREFERRED</b>
   * means of displaying the FPS and other values of this util object.
   * @implNote unfortunately: realizing this option means that this object must
   * be aware of {@link PFont}; which is unecessary when a label widget is used.
   */
  public void dispFPS(){
    if(!showFPSTxtBox || font==null){return;}
    app.textFont(font);
    app.textAlign(PApplet.LEFT, PApplet.BASELINE);
    app.fill(255);
    app.textSize(24);
    Pgfx.textWithShadow(app, toStringBothFPS(), 255, 0, 8f, app.height-8f);
    }
}
