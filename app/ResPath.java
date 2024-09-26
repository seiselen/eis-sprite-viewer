package app;

public enum ResPath {
  /** Directory (in src codebase) with built <code>JAR</code> and app assets. */
  BUILDIR("build"),
  /** Directory (in <code>build/</code>) containing assets. */
  ASSETDIR("assets"),
  /** Filename of <b>TEMP</b> examples dir (i.e. containing target `animDef`). */
  EXAMPATH("examples"),

  //-[ FONT ASSETS ]-----------------------------------------------------------#
  /** Filename of app text font (i.e. within {@link #ASSETDIR}). */
  TXTFONT("tit_web_bold_32.vlw"),
  /** Filename of app glyph font (i.e. within {@link #ASSETDIR}). */
  SYMFONT("font_awe_48.vlw"),
  /** Filename of app glyph font char map (i.e. within {@link #ASSETDIR}). */
  SYMCMAP("font_awe_char_codes.json"),

  //-[ IMAGE ASSETS ]----------------------------------------------------------#
  /** Filename of app icon (i.e. within {@link #ASSETDIR}). */
  APPICON("app_icon.png"),
  /** Filename of app logo (i.e. within {@link #ASSETDIR}). */
  APPLOGO("app_logo.png"),
  /** Filename of Eis logo (i.e. within {@link #ASSETDIR}). */
  EISLOGO("eis_logo.png"),

  STUB; //> exists only for semicolon, lol ;-)

  private String sPath;
  ResPath(){sPath=null;}
  ResPath(String in_sPath){sPath = in_sPath;}
  public String get(){return sPath==null ? this.toString() : sPath;}  
}
