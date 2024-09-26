package app;
import PrEis.utils.Pgfx;
import processing.core.PApplet;

/**
 * As this is fully cosmetic ergo unecessary, and unoptimized WRT Processing GFX
 * calls: it should be the first thing to prune if/when/as frames start to drop.
 * @todo hoist the 'magic consts' into at least private vars.
 * @todo move to <b>PrEis.utils</b> at some point; as other apps might want it. 
 */
class CustomCursor {
  private int     begCol;
  private int     endCol;
  private float   curLerp;
  private float   pi10th;
  private float   tau10th;
  private PApplet app;

  public CustomCursor(AppMain iApp){
    app = iApp;
    initGFX();
  }

  private void initGFX(){
    pi10th  = (float)Math.PI*.01f;
    tau10th = pi10th*2;
    curLerp = 0f;
    begCol  = app.color(0,160,0);
    endCol  = app.color(0,255,0);    
  }

  public void update(){
    curLerp+=2;
    if(curLerp==100){curLerp=0;}
  }

  void render(){
    app.ellipseMode(PApplet.CENTER);
    app.noFill();
    app.push();
      app.translate(app.mouseX, app.mouseY);    
      app.strokeWeight(2);
      app.stroke(app.lerpColor(begCol, endCol, (float)Math.sin(pi10th*curLerp)));
      app.circle(0,0,16);       
      app.rotate(curLerp*tau10th);
      app.stroke(app.lerpColor(endCol, begCol, (float)Math.sin(pi10th*curLerp)));
      app.strokeWeight(4);
      Pgfx.lineh(app, -10, -6,  0);
      Pgfx.lineh(app,  10,  6,  0);
      Pgfx.linev(app,  0, -10, -6);
      Pgfx.linev(app,  0,  10,  6);    
   app.pop();
  }
}