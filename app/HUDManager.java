package app;

import PrEis.utils.BBox;
import PrEis.utils.Pgfx;
import PrEis.utils.GridManager;
import processing.core.PApplet;
import processing.core.PVector;

class HUDManager {
  private static final float DEF_HUD_WIDE = 320;
  private static final float DEF_HUD_TALL = 200;
  private static final float DEF_SCALAR = 2f;
  private static final float MIN_SCALAR = 1f;
  private static final float MAX_SCALAR = 4f;
  private BBox dims;
  private PVector vizOff;
  public float curScalar;

  AppMain app;
  GridManager grid;
  
  public HUDManager(AppMain iApp){
    app = iApp;
    dims = new BBox(DEF_HUD_WIDE,DEF_HUD_TALL);
    vizOff = new PVector();
    curScalar = DEF_SCALAR;

    grid = new GridManager(iApp, dims, AppMain.MONFONT)
    .setShowGrid(false)
    .setCellSize(8)
    .setTextSiz(16)
    .setStrkWgt(1)
    .setScalar(DEF_SCALAR)
    .setStrkCol(app.color(255,255,0,128))
    .setTextCol(app.color(255,255,0))
    .setDeltaTic(2)
    ;
  }

//[GETTER FUNCTIONS]------------------------------------------------------------  
  public float getHUD_topLeftX(int pxOff){return (AppMain.CANVAS_WIDH-(dims.wideh()*curScalar))+(pxOff*curScalar)+vizOff.x;}
  
  public float getHUD_topLeftX(){return (AppMain.CANVAS_WIDH-(dims.wideh()*curScalar))+(curScalar)+vizOff.x;}  
  
  public float getHUD_topLeftY(int pxOff){return (AppMain.CANVAS_TALH-(dims.tallh()*curScalar))+(pxOff*curScalar)+vizOff.y;}  
  
  public float getHUD_topLeftY(){return (AppMain.CANVAS_TALH-(dims.tallh()*curScalar))+(curScalar)+vizOff.y;}  
  
  public float getHUD_wide(){return dims.wide()*curScalar;}
  
  public float getHUD_tall(){return dims.tall()*curScalar;}
  
  public float getHUD_spriteX(int offX){return getHUD_topLeftX()-(offX*curScalar);}
  
  public float getHUD_spriteX(){return getHUD_topLeftX();}
  
  public float getHUD_spriteY(int offY){return getHUD_topLeftY()-(offY*curScalar);}
  
  public float getHUD_spriteY(){return getHUD_topLeftY();}

//[SETTER FUNCTIONS]------------------------------------------------------------

  public void setScalar(float nScalar){
    curScalar = PApplet.constrain(nScalar, MIN_SCALAR, MAX_SCALAR);
    grid.setScalar(curScalar);
  }  


  public HUDManager setVizOffΘ(PVector nOff){
    setVizOff(nOff); return this;
  }

  public void setVizOff(PVector nOff){
    if(nOff.mag()==0){return;}
    vizOff=nOff;
  }


//[TOSTRING & TOCONSOLE FUNCTIONS]----------------------------------------------
  public String scalarToString(){return "Current Scalar = ["+curScalar+"]";}  
  public void scalarToConsole(){System.out.println(scalarToString());}  

//[UI FUNCTIONS]----------------------------------------------------------------
  public void onKeyPressed(){
    if(app.key=='g'||app.key=='G'){grid.setShowGrid();}
  }




//[RENDER FUNCTIONS]------------------------------------------------------------
  public void render(){
    app.rectMode(PApplet.CORNER);
    drawGrid();
    drawHUD_bounds();
    drawHUD_mainMidptLines();
    drawHUD_statusBarLines();
  }

  private void drawGrid(){
    app.push();
    app.translate(getHUD_topLeftX(), getHUD_topLeftY());
    grid.render();
    app.pop();
  }

  private void drawHUD_bounds(){
    app.noFill();
    app.stroke(255);
    app.strokeWeight(2);
    app.rect(getHUD_topLeftX(), getHUD_topLeftY(), getHUD_wide(), getHUD_tall());
  }
  
  private void drawHUD_mainMidptLines(){
    app.noFill();
    app.stroke(216);
    app.strokeWeight(1);
    Pgfx.linev(app, getHUD_topLeftX(160),getHUD_topLeftY(),getHUD_topLeftY(158));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(100));}
  
    private void drawHUD_statusBarLines(){
    app.noFill();
    app.stroke(192);
    app.strokeWeight(1);
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(158));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(162));
    Pgfx.lineh(app, getHUD_topLeftX(), getHUD_topLeftX(320), getHUD_topLeftY(168));
  }
} // Ends Class HUDManager