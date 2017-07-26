package fr.delthas.javaui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * Image represents a two-dimensional RGB/RGBA image, to be yet uploaded onto the GPU through {@link #upload()}, or {@link Atlas#uploadImage(Image)}.
 * <p>
 * An Image can be created from any source of data, that feeds compressed (JPEG/PNG/non animated GIF/...) image data, or uncompressed raw image data, or from another Image, through the use of the {@code createImage}, or the {@code createImageRaw} static functions in this class.
 * <p>
 * An Image is mainly used to draw an image on the screen, but it has to be uploaded first, either to a simple texture, through {@link #upload()}, which is the recommended case if your image isn't a tile, or on the contrary, to a texture atlas, which is a large texture object that can store many images that have the exact same image size, through {@link Atlas#uploadImage(Image)} (see the {@link Atlas} Javadoc).
 * <p>
 * <b>An Image must be closed as soon as it is not needed anymore, as it may be stored in an uncompressed format that takes up a lot of memory (a 1000x1000 image would typically use 4MB). IT MUST EXPLICITLY BE CLOSED THROUGH THE USE OF {@link #close()}.</b> A {@link Texture} uploaded from an {@link Image} doesn't need its {@link Image}, so you may close the Image as soon as you've uploaded it through either of the above-mentioned methods.
 *
 * @see #upload()
 * @see Atlas#uploadImage(Image)
 */
public final class Image implements AutoCloseable {
  final int width;
  final int height;
  final ByteBuffer data;
  final boolean ignoreAlpha;
  private final int allocation; // 0: nothing, 1: utils, 2: stb
  
  private Image(int width, int height, ByteBuffer data, boolean ignoreAlpha, int allocation) {
    this.width = width;
    this.height = height;
    this.data = data;
    this.ignoreAlpha = ignoreAlpha;
    this.allocation = allocation;
  }
  
  /**
   * Creates a RGBA image from a path that represents a file which contains compressed image data, such as typically a .jpg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param path The path to the file that contains compressed image data, must be non-null.
   * @return The {@link Image} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   * @see #createImage(Path, boolean)
   */
  public static Image createImage(Path path) throws IOException {
    return createImage(path, false);
  }
  
  /**
   * Creates a RGBA image from a string representing a path that represents a file which contains compressed image data, such as typically a .jpg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * The string may either be a path into the JAR (as would be obtained with {@code getClass().getResource(string)}, prepending it with a {@literal /} if necessary, or, if it doesn't correspond to a resource, a path to a file on a filesystem (as would be obtained with {@code Paths.get(string)}), though it may be better to use {@link #createImage(Path)} in this case, which directly supports passing a {@link Path}.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param path The string representing the path to the file that contains compressed image data, must be non-null.
   * @return The {@link Image} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   * @see #createImage(String, boolean)
   */
  public static Image createImage(String path) throws IOException {
    return createImage(path, false);
  }
  
  /**
   * Creates a RGBA image from an input stream feeding compressed image data, such as typically a stream from a .jpg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * The stream doesn't need to be buffered, as it is buffered internally by the library. <b>The stream is NOT closed by the library.</b> If reading from a {@link Path}, you may use {@link #createImage(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createImage(String)} instead.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param input The input stream representing the path to the file that contains compressed image data, must be non-null.
   * @return The {@link Image} that was created from reading the stream, <b>or null if there was an error decoding the stream</b>.
   * @throws IOException If an IO exception is raised when reading the stream.
   * @see #createImage(InputStream, boolean)
   */
  public static Image createImage(InputStream input) throws IOException {
    return createImage(input, false);
  }
  
  /**
   * Creates a RGBA image from a byte buffer containing compressed image data, such as typically a buffer obtained from reading a .jpg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * <b>THE BUFFER MUST BE FLIPPED, i.e. if you write to this buffer without flipping it afterwards, NO DATA WILL BE READ. The data will be read from the current buffer position to the buffer limit.</b> If reading from a {@link Path}, you may use {@link #createImage(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createImage(String)} instead; if reading from an input stream, you may use {@link #createImage(InputStream)} instead.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param buffer The byte buffer containing compressed image data, must be non-null.
   * @return The {@link Image} that was created from reading the buffer, <b>or null if there was an error decoding the buffer</b>.
   * @see #createImage(ByteBuffer, boolean)
   */
  public static Image createImage(ByteBuffer buffer) {
    return createImage(buffer, false);
  }
  
  /**
   * Creates a RGB/RGBA image from a path that represents a file which contains compressed image data, such as typically a .jpg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param path        The path to the file that contains compressed image data, must be non-null.
   * @param ignoreAlpha Whether to ignore the transparency of the specified image, thus creating a RGB image (true), or not to ignore it, thus creating a RGBA image, with an alpha channel (false).
   * @return The {@link Image} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   * @see #createImage(Path)
   */
  public static Image createImage(Path path, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  /**
   * Creates a RGB/RGBA image from a string representing a path that represents a file which contains compressed image data, such as typically a .jpg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * The string may either be a path into the JAR (as would be obtained with {@code getClass().getResource(string)}, prepending it with a {@literal /} if necessary, or, if it doesn't correspond to a resource, a path to a file on a filesystem (as would be obtained with {@code Paths.get(string)}), though it may be better to use {@link #createImage(Path)} in this case, which directly supports passing a {@link Path}.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param path        The string representing the path to the file that contains compressed image data, must be non-null.
   * @param ignoreAlpha Whether to ignore the transparency of the specified image, thus creating a RGB image (true), or not to ignore it, thus creating a RGBA image, with an alpha channel (false).
   * @return The {@link Image} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   * @see #createImage(String)
   */
  public static Image createImage(String path, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  /**
   * Creates a RGB/RGBA image from an input stream feeding compressed image data, such as typically a stream from a .jpg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * The stream doesn't need to be buffered, as it is buffered internally by the library. <b>The stream is NOT closed by the library.</b> If reading from a {@link Path}, you may use {@link #createImage(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createImage(String)} instead.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param input       The input stream representing the path to the file that contains compressed image data, must be non-null.
   * @param ignoreAlpha Whether to ignore the transparency of the specified image, thus creating a RGB image (true), or not to ignore it, thus creating a RGBA image, with an alpha channel (false).
   * @return The {@link Image} that was created from reading the stream, <b>or null if there was an error decoding the stream</b>.
   * @throws IOException If an IO exception is raised when reading the stream.
   * @see #createImage(InputStream)
   */
  public static Image createImage(InputStream input, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getBuffer(input);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  /**
   * Creates a RGB/RGBA image from a byte buffer containing compressed image data, such as typically a buffer obtained from reading a .jpg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * <b>THE BUFFER MUST BE FLIPPED, i.e. if you write to this buffer without flipping it afterwards, NO DATA WILL BE READ. The data will be read from the current buffer position to the buffer limit.</b> If reading from a {@link Path}, you may use {@link #createImage(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createImage(String)} instead; if reading from an input stream, you may use {@link #createImage(InputStream)} instead.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param buffer      The byte buffer containing compressed image data, must be non-null.
   * @param ignoreAlpha Whether to ignore the transparency of the specified image, thus creating a RGB image (true), or not to ignore it, thus creating a RGBA image, with an alpha channel (false).
   * @return The {@link Image} that was created from reading the buffer, <b>or null if there was an error decoding the buffer</b>.
   * @see #createImage(ByteBuffer)
   */
  public static Image createImage(ByteBuffer buffer, boolean ignoreAlpha) {
    return Ui.getUi().getWindow().createImage(buffer, ignoreAlpha);
  }
  
  /**
   * Creates a RGB/RGBA image from a byte buffer containing <b>UNCOMPRESSED</b> image data. This is a blocking operation that will read the stream fully and only return after it is done.
   * <p>
   * <b>THE BUFFER MUST BE FLIPPED, i.e. if you write to this buffer without flipping it afterwards, NO DATA WILL BE READ. The data will be read from the current buffer position to the buffer limit.</b> If reading from an uncompressed image source instead, use the {@code createImage*} functions instead.
   * <p>
   * <b>The data layout of the image MUST BE EXACTLY the following</b>: the intensity of the red channel of the pixel, as an unsigned byte (0-255), then that of the green channel, then that of the blue channel, then, IF AND ONLY IF {@code noAlpha} is false, that of the alpha channel; for all pixels in one horizontal line of pixels, starting at the left-most pixel; for all lines of pixels, starting at the top-most line of pixels of the image, WITHOUT ANY PADDING OR OTHER METADATA.
   * <p>
   * Refer to the {@link Image} class Javadoc for more general information regarding images.
   *
   * @param buffer  The byte buffer containing the uncompressed image data, must be non-null.
   * @param width   The width of the image contained in the buffer, in pixels.
   * @param height  The height of the image contained in the buffer, in pixels.
   * @param noAlpha Whether the specified uncompressed image data contains exactly 3 channels per pixel (red, green, blue: true), or it contains exactly 4 channels per piexel (red, green, blue, alpha: false).
   * @return The {@link Image} that was created from reading the buffer.
   * @see #createImage(ByteBuffer, boolean)
   */
  public static Image createImageRaw(ByteBuffer buffer, int width, int height, boolean noAlpha) {
    return createImageRaw(buffer, width, height, noAlpha, 0);
  }
  
  static Image createImageRaw(ByteBuffer buffer, int width, int height, boolean noAlpha, int allocation) {
    return new Image(width, height, buffer, noAlpha, allocation);
  }
  
  /**
   * Returns an {@link Image} that is the resized copy of the specified image, fitting exactly into the specified constraint dimensions, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * Since the returned {@link Image} is a copy of the specified image, the specified image can be closed independently of this image.
   *
   * @param width  The width to which to resize the specified image, in pixels.
   * @param height The height to which to resize the specified image, in pixels.
   * @return The resized copy of the specified image.
   */
  public Image getResized(int width, int height) {
    return Ui.getUi().getWindow().resizeImage(this, width, height);
  }
  
  /**
   * Uploads the image to the GPU, returning a tetxure object representing the image, to be passed to the drawing methods in {@link Drawer} when drawing a component.
   * <p>
   * This creates a single texture for this image, instead of using an {@link Atlas}, which is the recommended solution if there aren't other images of the exact same size and same usage (e.g. a tilemap) to be uploaded later. For the other use case, see {@link Atlas} and {@link Atlas#uploadImage(Image)}.
   * <p>
   * The {@link Image} <b>can and SHOULD be closed as soon as this method returns, as the returned {@link Texture} object doesn't need this {@link Image}.</b>
   *
   * @return The texture objct representing the uploaded Image, to be passed to the {@link Drawer} drawing methods.
   * @see Atlas
   * @see Atlas#uploadImage(Image)
   */
  public Texture upload() {
    return Ui.getUi().getWindow().uploadImage(this);
  }
  
  /**
   * @return The width of the image, in pixels.
   */
  public int getWidth() {
    return width;
  }
  
  /**
   * @return The height of the image, in pixels.
   */
  public int getHeight() {
    return height;
  }
  
  /**
   * Closes the image, thus releasing all memory allocated to the image data.
   * <p>
   * <b>This method should be called as soon as the image is uploaded</b>, as the memory allocated for each image can be quite large (e.g. typically 4MB of memory for a 1000x1000px image). <b>ALL IMAGES MUST EXPLICITLY BE CLOSED THROUGH THE USE OF {@link #close()}</b>, and a {@link Texture} uploaded from an {@link Image} doesn't need its {@link Image}, so you may close the Image as soon as you've uploaded it.
   */
  @Override
  public void close() {
    if (allocation == 2) {
      Ui.getUi().getWindow().freeImage(this);
      return;
    }
    if (allocation == 1) {
      Utils.free(data);
      return;
    }
  }
}
