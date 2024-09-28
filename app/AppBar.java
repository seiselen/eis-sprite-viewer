package app;
import PrEis.utils.Pgfx;
import processing.core.PApplet;
import processing.core.PImage;

/** 
 * Realizes App Top-Bar (ᴀᴋᴀ Header) and Bottom-Bar (ᴀᴋᴀ Footer). May eventually
 * further realize Nav-Bar in the header, but that's TBD.
 * @todo kind of cheated with direct invokes of `AppMain` for `CANVAS` dims s.t.
 * will need to be bound (via `BBox`?) when realized as component in `PrEis`.
 */
public class AppBar {

  /** {@link PApplet} of app via {@link #appUtil} for efficiently direct GFX function calls. */
  private PApplet app;

  /** BG fill color of top and bottom app bars. */
  private int appBars_fill;

  /** <b>Top</b> Bar Height. */
  private int tbar_tall;

  /** <b>Bottom</b> Bar Height. */
  private int bbar_tall;

  /** <b>App Logo</b> Image <i>(e.g. "DECORAnimATE")</i>. */
  private PImage app_logo_img;

  /** <b>Eis Logo</b> Image <i>(e.g. "Eiselen Labs")</i>. */
  private PImage eis_logo_img;
  


  private final static String APP_BRND = "©2024 Eiselen Technologies ";

  public AppBar(AppMain iApp, PImage appLogoImg, PImage eisLogoImg){
    app = iApp;
    loadAssets(appLogoImg, eisLogoImg);
    initGFX();
  }

  private void loadAssets(PImage appLogoImg, PImage eisLogoImg){
    app_logo_img = appLogoImg;
    eis_logo_img = eisLogoImg;
  }

  private void initGFX(){
    appBars_fill = app.color(32,96,160);
    tbar_tall = 64;
    bbar_tall = 32;
  }


  /** 
   * Returns Canvas-Space <code>Y</code> coordinate of top bar bottom edge; i.e.
   * <code>Y</code> coordinate where stuff can be drawn without overlapping it.
   * @todo maybe cache these (s.t. maintained); as render calls made both herein
   * and hereout (i.e. GUI) will be asking for them on a per-frame basis.
   */
  public int getTopBarYOff(){
    return tbar_tall;
  }

  /** 
   * Returns Canvas-Space <code>Y</code> coordinate of bottom bar top edge; i.e.
   * <code>Y</code> coordinate where stuff can be drawn without overlapping it.
   * @todo maybe cache these (s.t. maintained); as render calls made both herein
   * and hereout (i.e. GUI) will be asking for them on a per-frame basis.
   */
  public int getBotBarYOff(){
    return AppMain.CANVAS_TALL-bbar_tall;
  }

  void render(){
    app.textFont(AppMain.TXTFONT);
    Pgfx.fillnostroke(app, appBars_fill);    
    app.rectMode(AppMain.CORNER);
    drawTopBar();
    drawBotBar();
  }

  private void drawTopBar(){
    app.rect(0,0,AppMain.CANVAS_WIDE,64);
    app.imageMode(AppMain.CORNER);
    app.image(app_logo_img,4,8);
  }

  /**
   * @todo <b>DEFICIENCY</B>: This references the `FPSManager` of which has yet
   * to be converted to a <code>.java</code> and for which I have plans deeper
   * than simple migration that require full focus (and I'm currently having to
   * expend focus on three such objects as each others' dependencies... I can't
   * afford <code>#4</code>!)
   */
  void drawBotBar(){
    app.rect(0,getBotBarYOff(),AppMain.CANVAS_WIDE,32);
    app.imageMode(AppMain.CORNER);
    app.fill(255);
    app.textAlign(PApplet.RIGHT, PApplet.CENTER);
    app.textSize(24);
    app.text(APP_BRND, AppMain.CANVAS_WIDE-32, AppMain.CANVAS_TALL-16);
    app.image(eis_logo_img,AppMain.CANVAS_WIDE-32, AppMain.CANVAS_TALL-32, 32, 32);
  }
  
  /**
   * @implNote This is the <i>"Poor Man's Tooltip"</i> realization (of which is
   * being replaced soon, see 'todo' note below) As it is called async, it needs
   * the necessary gfx state calls; i.e. no shortcuts per app bar render calls.
   * @todo I'm planning to replace this with <b>actual</b> <code>UIObject</code>
   * tooltips in the next <b>PrEis</b> RC; ergo remove this <i>(and its callback
   * support)</i> when that happens.
   */
  public void drawBotBarLabel(String s){
    app.textFont(AppMain.TXTFONT);
    Pgfx.fillnostroke(app, appBars_fill); 
    app.textAlign(AppMain.RIGHT, AppMain.BASELINE);
    app.textSize(18);
    Pgfx.textWithShadow(app,s,255,0,AppMain.CANVAS_WIDE-8,AppMain.CANVAS_TALL-8);
  }

}