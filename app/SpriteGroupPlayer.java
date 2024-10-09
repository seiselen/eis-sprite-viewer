package app;
import PrEis.utils.Cons;
import PrEis.utils.Cons.Err;
import processing.core.PApplet;

public class SpriteGroupPlayer {
  SpriteGroup spriteGroup;
  boolean playing;
  boolean paused;
  boolean stopped;
  boolean dispSpriteBounds;

  /** log whenever mode changes i.e. {PLAY/PAUSE/STOP} */
  boolean logBehaviorChange = false; 
  
  //> Maybe Temp, Maybe I Keep For Debug Purposes...
  Sprite curSprite;
  
  PApplet app;
    
  public SpriteGroupPlayer(PApplet iApp){
    app = iApp;
    dispSpriteBounds = false;
    onStop();
  }
  
  public SpriteGroupPlayer injectSpriteGroup(SpriteGroup group){
    onStop();
    if(group==null){Cons.err(Err.NULL_INPUT); Cons.err("sprite group is null");}
    else{spriteGroup=group;} return this;
  }


  public void toggleDispSpriteBounds(){
    dispSpriteBounds = !dispSpriteBounds;
  }
  
  public void onKeyPressed(){
    if(paused){
      switch(app.keyCode){
      case PApplet.LEFT : spriteGroup.curClipNextFrame(); return;
      case PApplet.RIGHT : spriteGroup.curClipPrevFrame(); return;  
    }
    }
    switch(app.keyCode){
      case PApplet.UP: spriteGroup.prevClip(); return;
      case PApplet.DOWN: spriteGroup.nextClip(); return;
    }
  }
  
  public SpriteGroupPlayer run() {onPlay(); return this;}

  public void onPlay() {changeBehavior(true,false,false);}

  public void onPause(){if(paused){onPlay();return;}changeBehavior(false,true,false);}

  public void onStop() {changeBehavior(false,false,true); if(spriteGroup!=null){spriteGroup.curClipReset();}}
  
  private void changeBehavior(boolean bPlay, boolean bPause, boolean bStop){
    playing=bPlay;
    paused=bPause;
    stopped=bStop;
    if(logBehaviorChange){Cons.log(behaviorStateToString());}
  }
  
  public void update(){
    if(playing){spriteGroup.curClipNextFrame();}
  }
  
  public String behaviorStateToString(){
    return "{playing=["+playing+"], paused=["+paused+"] stopped=["+stopped+"]}";
  }
  
  public void render(){  
    if(spriteGroup==null||spriteGroup.sprDict==null||spriteGroup.curClip==null){return;}
    curSprite = spriteGroup.getCurSpriteObj();

    app.push();
      app.translate(AppMain.hudManager.getHUD_spriteX(),AppMain.hudManager.getHUD_spriteY());
      if(dispSpriteBounds){curSprite.renderDebugRect(AppMain.hudManager.curScalar);}
      app.scale(AppMain.hudManager.curScalar);
      curSprite.render();
    app.pop();
  }

}