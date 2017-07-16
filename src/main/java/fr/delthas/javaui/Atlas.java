package fr.delthas.javaui;

import java.util.HashSet;
import java.util.Set;

public class Atlas {
  final int width;
  final int height;
  final int texture;
  final int n;
  final boolean ignoreAlpha;
  final Set<Integer> children = new HashSet<>();
  boolean destroyed;
  int hint = 0;
  
  public Atlas(int width, int height, int texture, int n, boolean ignoreAlpha) {
    this.width = width;
    this.height = height;
    this.texture = texture;
    this.n = n;
    this.ignoreAlpha = ignoreAlpha;
  }
  
  public static Atlas createAtlas(int width, int height, int n, boolean ignoreAlpha) {
    return Ui.getUi().getWindow().createAtlas(width, height, n, ignoreAlpha);
  }
  
  public AtlasTexture uploadImage(Image image) {
    if (image.ignoreAlpha != ignoreAlpha) {
      throw new RuntimeException("Image and atlas must have the same ignoreAlpha!");
    }
    if (image.width != width || image.height != height) {
      throw new RuntimeException("Image size and atlas images size must be the same!");
    }
    if (children.size() >= n) {
      throw new RuntimeException("Atlas is full!");
    }
    if (hint >= 0) {
      children.add(hint);
      AtlasTexture atlasTexture = Ui.getUi().getWindow().uploadAtlasImage(this, image, hint);
      if (++hint >= n) {
        hint = -1;
      }
      return atlasTexture;
    }
    for (int i = 0; i < n; i++) {
      if (!children.contains(i)) {
        children.add(hint);
        return Ui.getUi().getWindow().uploadAtlasImage(this, image, i);
      }
    }
    throw new RuntimeException("Shouldn't happen!");
  }
  
  public void destroy() {
    if (destroyed) {
      return;
    }
    destroyed = true;
    Ui.getUi().getWindow().destroyAtlas(this);
  }
  
  void destroyImage(int i) {
    children.remove(i);
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
}
