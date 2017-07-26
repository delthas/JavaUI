package fr.delthas.javaui;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Atlas represents a texture atlas, that is a large texture object used to store many small images of same width and height.
 * <p>
 * It is better to use an atlas when using many small textures, rather than storing each of them in a different texture object. For example in a tile-based game with small tiles, you should use a texture atlas.
 * <p>
 * To create an Atlas, use {@link #createAtlas(int, int, int, boolean)}. To add an image to an atlas (and upload to the GPU), use {@link #uploadImage(Image)}.
 *
 * @see #createAtlas(int, int, int, boolean)
 * @see #uploadImage(Image)
 * @see Image
 * @see Image#upload()
 */
public final class Atlas {
  final int width;
  final int height;
  final int texture;
  final int n;
  final boolean ignoreAlpha;
  final Set<Integer> children = new HashSet<>();
  boolean destroyed;
  int hint = 0;
  
  Atlas(int width, int height, int texture, int n, boolean ignoreAlpha) {
    this.width = width;
    this.height = height;
    this.texture = texture;
    this.n = n;
    this.ignoreAlpha = ignoreAlpha;
  }
  
  /**
   * Creates an Atlas to contain n small images of same fixed width and height.
   *
   * @param width       The width of all the images to be put in this Atlas.
   * @param height      The height of all the images to be put in this Atlas.
   * @param n           The maximum number of images this Atlas will be able to store.
   * @param ignoreAlpha Whether to ignore the alpha channel (e.g. consider fully opaque) of the images to be put in this Atlas (all images to be put in this Atlas must have the same ignoreAlpha).
   * @return The created Atlas.
   * @see #destroy()
   */
  public static Atlas createAtlas(int width, int height, int n, boolean ignoreAlpha) {
    return Ui.getUi().getWindow().createAtlas(width, height, n, ignoreAlpha);
  }
  
  /**
   * Adds an image to this Atlas (and uploads it to the GPU).
   *
   * @param image The image to add to this Atlas (must have the same width, height, and ignoreAlpha as the atlas), must not be null.
   * @return A texture object representing the image in this Atlas, to be used to draw the image with the various draw functions.
   */
  public AtlasTexture uploadImage(Image image) {
    Objects.requireNonNull(image);
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
  
  /**
   * Destroy all of the {@link Texture} objects stored in this Atlas (that is, all images stored in this Atlas can't be drawn anymore), then destroys this Atlas.
   * <p>
   * Use this function when you know you won't need to draw the images stored in this Atlas anymore, to free the GPU memory of the images.
   */
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
  
  /**
   * @return The width of all the images to be put, or already put in this Atlas. All images added to this Atlas <b>MUST</b> have this width.
   */
  public int getWidth() {
    return width;
  }
  
  /**
   * @return The height of all the images to be put, or already put in this Atlas. All images added to this Atlas <b>MUST</b> have this height.
   */
  public int getHeight() {
    return height;
  }
  
  /**
   * @return Whether to ignore the alpha channel (e.g. consider fully opaque regardless of transparency specified in the image) of the images to be put in this Atlas (all images to be added in this Atlas <b>MUST</b> have this ignoreAlpha).
   */
  public boolean isIgnoreAlpha() {
    return ignoreAlpha;
  }
}
