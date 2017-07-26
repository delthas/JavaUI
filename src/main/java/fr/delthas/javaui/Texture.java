package fr.delthas.javaui;

/**
 * Texture represents a texture object, that is an image uploaded on the GPU, to be drawn with the {@link Drawer} image drawing methods, in {@link Component#render(InputState, Drawer)} calls.
 * <p>
 * To get a {@link Texture}, first create an {@link Image}, that represents image data read from an image data source, stored on memory, waiting to be uploaded on GPU. Then, upload it on the GPU, either as a simple texture, through {@link Image#upload()}, which is the recommended case if your image isn't a tile, or on the contrary, to a texture atlas, which is a large texture object that can store many images that have the exact same image size, through {@link Atlas#uploadImage(Image)} (see the {@link Atlas} Javadoc).
 * <p>
 * A {@link Texture} doesn't need to have access to the {@link Image} that created it, so you may and should {@link Image#close()} the parent image as soon as you've uploaded it into a {@link Texture} and don't need it anymore.
 * <p>
 * Additionnally, a {@link Texture} can be destroyed, so that it frees up the GPU memory from its image data. After being destroyed, the {@link Texture} cannot be used anymore and a new {@link Texture} must be created and uploaded from an {@link Image}, if needed. <b>Since GPU memory is limited, and a texture takes a lot of space (a 1000x1000 image would typically use 4MB), it SHOULD BE EXPLICITLY DELETED THROUGH THE USE OF {@link #destroy()} as soon as it is not needed anymore.</b>
 *
 * @see Image
 * @see Image#upload()
 * @see Atlas
 * @see Atlas#uploadImage(Image)
 * @see Drawer
 */
public interface Texture {
  /**
   * Destroys the image, so that it frees up all GPU memory it uses.
   * <p>
   * A {@link Texture} cannot be drawn anymore after it is deleted, and a new {@link Texture} must be created and uploaded again from an {@link Image} if needed.
   * <p>
   * <b>Since GPU memory is limited, and a texture takes a lot of space (a 1000x1000 image would typically use 4MB), this method SHOULD BE EXPLICITLY CALLED as soon as the texture is not needed anymore.</b>
   */
  void destroy();
  
  /**
   * @return The width of the {@link Image} that this {@link Texture} has been uploaded from, in pixels.
   */
  int getWidth();
  
  /**
   * @return The height of the {@link Image} that this {@link Texture} has been uploaded from, in pixels.
   */
  int getHeight();
}
