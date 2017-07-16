package fr.delthas.javaui;

final class AtlasTexture implements Texture {
  final Atlas atlas;
  final int i;
  boolean destroyed = false;
  
  AtlasTexture(Atlas atlas, int i) {
    this.atlas = atlas;
    this.i = i;
  }
  
  public void destroy() {
    if (destroyed) {
      return;
    }
    destroyed = true;
    atlas.destroyImage(i);
  }
  
  public int getWidth() {
    return atlas.width;
  }
  
  public int getHeight() {
    return atlas.height;
  }
}
