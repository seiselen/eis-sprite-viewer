package app;

import PrEis.utils.BBox;
import PrEis.utils.Pgfx;
import processing.core.PApplet;

class HUDManager {
  private static final float DEF_HUD_WIDE = 320;
  private static final float DEF_HUD_TALL = 200;
  private static final float DEF_SCALAR = 2f;
  private static final float MIN_SCALAR = 1f;
  private static final float MAX_SCALAR = 4f;
  private BBox dims;
  public float curScalar;

  AppMain app;
  
  public HUDManager(AppMain iApp){
    app = iApp;
    dims = new BBox(DEF_HUD_WIDE,DEF_HUD_TALL);
    curScalar = DEF_SCALAR;
  }

//[GETTER FUNCTIONS]------------------------------------------------------------  
  public float getHUD_topLeftX(int pxOff){return (AppMain.CANVAS_WIDH-(dims.wideh()*curScalar))+(pxOff*curScalar);}
  public float getHUD_topLeftX(){return (AppMain.CANVAS_WIDH-(dims.wideh()*curScalar))+(curScalar);}  
  public float getHUD_topLeftY(int pxOff){return (AppMain.CANVAS_TALH-(dims.tallh()*curScalar))+(pxOff*curScalar);}  
  public float getHUD_topLeftY(){return (AppMain.CANVAS_TALH-(dims.tallh()*curScalar))+(curScalar);}  
  public float getHUD_wide(){return dims.wide()*curScalar;}
  public float getHUD_tall(){return dims.tall()*curScalar;}
  public float getHUD_spriteX(int offX){return getHUD_topLeftX()-(offX*curScalar);}
  public float getHUD_spriteX(){return getHUD_topLeftX();}
  public float getHUD_spriteY(int offY){return getHUD_topLeftY()-(offY*curScalar);}
  public float getHUD_spriteY(){return getHUD_topLeftY();}
//[SETTER FUNCTIONS]------------------------------------------------------------
  public void setScalar(float nScalar){curScalar = PApplet.constrain(nScalar, MIN_SCALAR, MAX_SCALAR);}  
//[TOSTRING & TOCONSOLE FUNCTIONS]----------------------------------------------
  public String scalarToString(){return "Current Scalar = ["+curScalar+"]";}  
  public void scalarToConsole(){System.out.println(scalarToString());}  

  public void render(){
    app.rectMode(PApplet.CORNER);
    drawHUD_bounds();
    drawHUD_mainMidptLines();
    drawHUD_statusBarLines();
  }
  
  public void drawHUD_bounds(){
    app.noFill();
    app.stroke(255);
    app.strokeWeight(2);
    app.rect(getHUD_topLeftX(), getHUD_topLeftY(), getHUD_wide(), getHUD_tall());
  }
  
  public void drawHUD_mainMidptLines(){
    app.noFill();
    app.stroke(216);
    app.strokeWeight(1);
    Pgfx.linev(app, getHUD_topLeftX(160),getHUD_topLeftY(),getHUD_topLeftY(158));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(100));}
  
  public void drawHUD_statusBarLines(){
    app.noFill();
    app.stroke(192);
    app.strokeWeight(1);
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(158));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(162));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(168));
  }
} // Ends Class HUDManager