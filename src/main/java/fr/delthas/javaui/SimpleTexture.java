package fr.delthas.javaui;

final class SimpleTexture implements Texture {
  final int width;
  final int height;
  final int texture;
  boolean destroyed;
  
  SimpleTexture(int width, int height, int texture) {
    this.width = width;
    this.height = height;
    this.texture = texture;
  }
  
  public void destroy() {
    if (destroyed) {
      return;
    }
    destroyed = true;
    Ui.getUi().getWindow().destroyImage(this);
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
}
