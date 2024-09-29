package app;
import PrEis.gui.PosOri;
import PrEis.utils.Pgfx;
import PrEis.utils.StringUtils;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/** 
 * Defines a (Classic) DooM sprite
 * @see https://zdoom.org/wiki/Sprite
 * @implNote Origin MUST be <code>TOP-LEFT</code> for all rendering, interaction,
 * transformation, and other purposes; per <code>GZD/idTech1</code> convention.
 * @implNote Removed `BBox` realization i.e. keeping things strictly `PVector`s. 
 */
public class Sprite {
  private static int DBUG_RECT_STRK;
  private static int DBUG_RECT_PAD;
  private static boolean RENDER_DBUG_RECT;

  /** {@link PApplet} of application this is being instantiated within.  */
  PApplet app;
  /** Full filepath, including filename with extension suffix. */
  String fullPath;
  /** Full filename, including <code>char[6]</code> suffix integer and extension; e.g. <code>"PLSBZ0.png"</code>. */
  String fileName;
  /** Sprite name <code>Char[1,…,5]</code>; i.e. sans <code>Char[6]</code> suffix integer; e.g. <code>"PLSBZ"</code>. */
  String spriteID; 

  /** Sprite {@link PImage}, 'Nuff Said. */
  PImage pImage;

  /**
   * <b>Sprite Position</b> s.t. sprites rendered at the sum of this and {@link 
   * #off} (i.e. as maintained by {@link #pOff}) should appear oriented hereto.
   */
  PVector pos;

  /** 
   * <b>Sprite Dimensions</b> i.e. {@link #pImage} width and height at <code>1x
   * </code> scale; of syntax: <code>{x:wide, y:tall, z:max(wide,tall)}</code>.
   */
  PVector dim;

  /** 
   * <b>Sprite Offset</b>; s.t. sprites rendered at the sum of this with {@link
   * #pos} (i.e. as maintained by {@link #pOff}) should appear oriented hereby.
   * <ul>
   * <li> Generally: this is the Cartesian Space negation of the image midpoint;
   * e.g. <code>(-32,-18)</code> if {@link #dim} is <code>(64,36)</code>.</li>
   * <li> For <code>idTech1</code> (i.e. Classic DooM) sprites: this is either
   * extracted from the offset embedded in its image's <code>png</code> header;
   * else otherwise specified (e.g. an external <code>JSON</code> file).</li>
   * </ul>
   */
  PVector off; 

  /**
   * <b>Desired Position</b> i.e. in which rendering a sprite positioned thereto 
   * encompasses orienting it to its {@link #pos} via its {@link #off}. This is
   * thus the sum of those two values; as maintained on an <i>event-driven<i>
   * basis via encapsulation i.e. hooking into the setters of both properties.
   */
  PVector des;

  public Sprite(PApplet iApp, String iName, String iFPath, PImage iPImage){
    DBUG_RECT_STRK = iApp.color(0,255,0,128);
    DBUG_RECT_PAD = 8;
    RENDER_DBUG_RECT = true;
    app      = iApp;
    pos      = new PVector();
    off      = new PVector();
    dim      = new PVector();
    des      = new PVector();
    fullPath = iFPath;
    fileName = getFNameFromFPath(fullPath);
    spriteID = iName;
    bindImage(iPImage);
  }

  public Sprite setPos(float inX, float inY){pos.set(inX, inY); setDesiredPos(); return this;}
  public Sprite setPos(PVector inVec){return setPos(inVec.x, inVec.y);}

  public Sprite setOff(float inX, float inY){off.set(inX, inY); setDesiredPos(); return this;}
  public Sprite setOff(PVector inVec){return setOff(inVec.x, inVec.y);}


  public Sprite bindImage(PImage img){
    if(img != null){
      pImage = img;
      dim.set(img.width, img.height, Math.max(img.width, img.height)); 
    }
    return this;
  }

  /** 
   * Sets offset WRT input {@link PosOri}. If type not yet supported (see <code>
   * todo</code> below): default is {@link PosOri#CTR} handling.
   * @todo complete the remaining <code>PosOri</code> opcodes.
   */
  public Sprite setOffTo(PosOri ori){
    switch (ori) {
      case BOT: off.set(-pos.x/2,-pos.y); break;
      case TOP: off.set(-pos.x/2,0); break;    
      case CTR: 
      default:  off.set(-pos.x/2,-pos.y/2);
    }
    return setDesRetThis();
  }


  public Sprite move(float iX, float iY){pos.add(iX,iY);return setDesRetThis();}

  public Sprite move(PVector iV){pos.add(iV); return setDesRetThis();}
  
  private Sprite setDesRetThis(){setDesiredPos(); return this;}

  private void setDesiredPos(){des.set(pos.x-off.x, pos.y-off.y);}
  
  /** Partitioned for Clean Code; i.e. -vs- having 'magic expression' in constructor. */
  private String getFNameFromFPath(String fp){
    return fp.substring(fullPath.length()-10);
  }

  public void render(){
    if(RENDER_DBUG_RECT){renderDebugRect();}
    if(pImage==null){renderNullImgText();}
    else{renderSprite();}
  }

  private void renderDebugRect(){
    Pgfx.strokenofill(app, DBUG_RECT_STRK);
    app.strokeWeight(2);
    app.rectMode(PApplet.CORNER);
    app.rect(des.x-DBUG_RECT_PAD,des.y-DBUG_RECT_PAD,dim.x+DBUG_RECT_PAD,dim.y+DBUG_RECT_PAD);
  }

  private void renderSprite(){
    app.imageMode(PApplet.CORNER);
    app.image(pImage, des.x, des.y);    
  }

  private void renderNullImgText(){
    Pgfx.textWithShadow(app, "NULL PIMAGE", DBUG_RECT_STRK, 0, des);
  }

  public String offToString(){
    return StringUtils.wrapParens(StringUtils.concatAsCSSV(off.x,off.y));
  }


}