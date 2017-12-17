package fr.delthas.javaui;

import java.awt.*;

/**
 * Drawer is an interface on which to draw a component, to be called from a component {@link Component#render(InputState, Drawer)} method.
 * <p>
 * There are several methods to draw simple geometric shapes, images (actually {@link Texture}) objects, and text. Apart from drawing methods, there is a translation stack, that lets push a x and y amount to be added on all passed coordinates (to be popped before returning). The drawer also stores a default color in its internal state, that can be set with {@link #setColor(Color)}, that it uses to draw geometric shapes and text. There are also utilities method related to text drawing, such as {@link #getLineHeight(Font, float)}.
 * <p>
 * All passed coordinates are <b>RELATIVE to the component rectangle</b> lower-left corner.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // draw an image at the lower-left corner of the component
 * drawer.image(0, 0, texture).draw();
 * // draw an image by stretching it on the whole component
 * drawer.image(0, 0, texture).size(getWidth(), getHeight()).draw();
 *
 * // draw an image by fitting it on the component, centering it
 * drawer.imageIn(0, 0, getWidth(), getHeight(), texture).centered(true);
 *
 * // draw/fill a rectangle using the default color
 * drawer.rectangle(50, 50, 200, 200).draw();
 * // draw/fill a white rectangle, with a 45° angle
 * drawer.rectangle(50, 50, 200, 200).angle(Math.PI / 4).color(Color.WHITE).draw();
 *
 * // draw/fill a white circle
 * drawer.circle(20, 50, 40).color(Color.WHITE).draw();
 *
 * // draw/fill a ring using the default color, centered on the component
 * drawer.ring(getWidth() / 2, getHeight() / 2, 100, 10).centered(true).draw();
 *
 * // draw text using the default color, centered vertically on the component
 * drawer.text(100, getHeight() / 2, ">hello!!<", Font.COMIC, 16).centered(false, true).draw();
 * }
 * </pre>
 */
public abstract class Drawer {
  /*er-l BuiBuillderer-ler-l
  
  // prevent external override
  Drawer() {
  }
  
  /**
   * Pushes a translation onto the translation stack.
   * <p>
   * All method calls on this Drawer object or its inner Builder classes will be translated by the sum of all translations stored on the translation stack.
   * <p>
   * <b>For each {@link #pushTranslate(double, double)} method call during a {@link Component#render(InputState, Drawer)} call, there MUST BE EXACTLY ONE {@link #popTranslate()} CALL, NO MORE, NO LESS.</b>
   *
   * @param x The x amount to push onto the translation stack.
   * @param y The y amount to push onto the translation stack.
   * @see #popTranslate()
   */
  public abstract void pushTranslate(double x, double y);
  
  /**
   * Pops a translation from the translation stack.
   * <p>
   * See {@link #pushTranslate(double, double)} for an explanation of the translation stack. <b>For each {@link #pushTranslate(double, double)} method call during a {@link Component#render(InputState, Drawer)} call, there MUST BE EXACTLY ONE {@link #popTranslate()} CALL, NO MORE, NO LESS.</b>
   *
   * @see #pushTranslate(double, double)
   */
  public abstract void popTranslate();
  
  /**
   * Returns an {@link ImageBuilder} to draw the specified image at the specified coordinates.
   * <p>
   * See the methods on {@link ImageBuilder} to know what various parameters you can set.
   * <p>
   * The coordinates specify the lower-left corner of the image, by default, see {@link ImageBuilder#centered(boolean)}.
   *
   * @param x The x coordinate of the image, adjusted by the translation stack.
   * @param y The y coordinate of the image, adjusted by the translation stack.
   * @param texture The texture to draw, must be non-null.
   * @return An {@link ImageBuilder} to draw this image at these coordinates.
   * @see ImageBuilder
   */
  public ImageBuilder image(double x, double y, Texture texture) {
    return ImageBuilder.get(x, y, texture);
  }
  
  /**
   * Returns an {@link ImageInBuilder} to draw the specified image by fitting it into the specified bounds. The image will be resized so that it fills the largest space inside the bounds, <b>KEEPING the aspect ratio</b>.
   * <p>
   * See the methods on {@link ImageInBuilder} to know what various parameters you can set.
   * <p>
   * The coordinates specify the lower-left corner of the bounds, by default, see {@link ImageInBuilder#centered(boolean)}.
   *
   * @param x The x coordinate of the bounds, adjusted by the translation stack.
   * @param y The y coordinate of the bounds, adjusted by the translation stack.
   * @param width The width of the bounds into which to draw the image.
   * @param height The height of the bounds into which to draw the image
   * @param texture The texture to draw, must be non-null.
   * @return An {@link ImageInBuilder} to draw this image in these bounds.
   * @see ImageInBuilder
   */
  public ImageInBuilder imageIn(double x, double y, double width, double height, Texture texture) {
    return ImageInBuilder.get(x, y, width, height, texture);
  }
  
  /**
   * Returns a {@link LineBuilder} to draw a straight line between the two specified points.
   * <p>
   * See the methods on {@link LineBuilder} to know what various parameters you can set.
   * <p>
   * The coordinates specify the two ends of the line, in pixels.
   *
   * @param x1 The x coordinate of the first point, adjusted by the translation stack.
   * @param y1 The y coordinate of the first point, adjusted by the translation stack.
   * @param x2 The x coordinate of the second point, adjusted by the translation stack.
   * @param y2 The y coordinate of the second point, adjusted by the translation stack.
   * @return A {@link LineBuilder} to draw the specified line.
   * @see LineBuilder
   */
  public LineBuilder line(double x1, double y1, double x2, double y2) {
    return LineBuilder.get(x1, y1, x2, y2);
  }
  
  /**
   * Returns a {@link LineCenterBuilder} to draw a line of specified length and angle, at the specified center.
   * <p>
   * See the methods on {@link LineCenterBuilder} to know what various parameters you can set.
   *
   * @param x      The x coordinate of the center of the line, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the line, adjusted by the translation stack.
   * @param length The length of the line to draw.
   * @param angle  The positive angle (counter-clockwise) of the line to draw, in radians. (An angle of 0 means the line is parallel to the x axis.)
   * @return A {@link LineCenterBuilder} to draw the specified line.
   * @see LineCenterBuilder
   */
  public LineCenterBuilder lineCenter(double x, double y, double length, double angle) {
    return LineCenterBuilder.get(x, y, length, angle);
  }
  
  /**
   * Returns a {@link RectangleBuilder} to draw/fill a rectangle of specified position and size.
   * <p>
   * See the methods on {@link RectangleBuilder} to know what various parameters you can set.
   * <p>
   * The x and y coordinates specify the lower-left corner of the rectangle, by default, see {@link RectangleBuilder#centered(boolean)}.
   *
   * @param x      The x coordinate of the rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the rectangle, adjusted by the translation stack.
   * @param width  The width of the rectangle.
   * @param height The height of the rectangle.
   * @return A {@link RectangleBuilder} to draw the specified rectangle.
   * @see RectangleBuilder
   */
  public RectangleBuilder rectangle(double x, double y, double width, double height) {
    return RectangleBuilder.get(x, y, width, height);
  }
  
  /**
   * Returns a {@link CircleBuilder} to draw/fill a circle of specified position and radius.
   * <p>
   * See the methods on {@link CircleBuilder} to know what various parameters you can set.
   * <p>
   * The x and y coordinates specify the lower-left corner of the circle square bounds, by default, see {@link CircleBuilder#centered(boolean)}.
   *
   * @param x      The x coordinate of the circle, adjusted by the translation stack.
   * @param y      The y coordinate of the circle, adjusted by the translation stack.
   * @param radius The radius of the circle.
   * @return A {@link CircleBuilder} to draw the specified circle.
   * @see CircleBuilder
   */
  public CircleBuilder circle(double x, double y, double radius) {
    return CircleBuilder.get(x, y, radius);
  }
  
  /**
   * Returns a {@link RingBuilder} to draw/fill a ring of specified outer radius and width. A ring is the difference between an outer circle of radius {@code radius} and a inner circle of radius {@code radius - width}.
   * <p>
   * See the methods on {@link RingBuilder} to know what various parameters you can set.
   * <p>
   * The x and y coordinates specify the lower-left corner of the ring square bounds, by default, see {@link RingBuilder#centered(boolean)}.
   *
   * @param x      The x coordinate of the ring, adjusted by the translation stack.
   * @param y      The y coordinate of the ring, adjusted by the translation stack.
   * @param radius The outer radius of the ring.
   * @param width  The width of the ring.
   * @return A {@link RingBuilder} to draw the specified ring.
   * @see RingBuilder
   */
  public RingBuilder ring(double x, double y, double radius, double width) {
    return RingBuilder.get(x, y, radius, width);
  }
  
  /**
   * Returns a {@link TextBuilder} to draw a single line of text.
   * <p>
   * See the methods on {@link TextBuilder} to know what various parameters you can set.
   * <p>
   * The x and y coordinates specify the lower-left corner of the text bounds, by default, see {@link TextBuilder#centered(boolean, boolean)}.
   *
   * @param x    The x coordinate of the text, adjusted by the translation stack.
   * @param y    The y coordinate of the text, adjusted by the translation stack.
   * @param text The text string to draw, must be non-null, drawing the empty string is a no-op.
   * @param font The font of the text, must be non-null.
   * @param size The font size of the text, in pt.
   * @return A {@link TextBuilder} to draw the specified text.
   * @see TextBuilder
   */
  public TextBuilder text(double x, double y, String text, Font font, double size) {
    return TextBuilder.get(x, y, text, font, size);
  }
  
  protected abstract void drawLineCenter(double x, double y, double length, double angle, Color color);
  
  protected abstract void fillRing(double x, double y, double radius, double width, Color color);
  
  protected abstract void fillRectang
  
  /**
   * ImageBuilder is a Builder-like interface that lets you specify various image-related parameters and draw an image with {@link #draw()}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#image(double, double, Texture)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#image(double, double, Texture)}.
   */
  public static final class ImageBuilder {
    private static final ImageBuilder INSTANCE = new ImageBuilder();
    private double x;
    private double y;
    private double width;
    private double height;
    private double s1;
    private double t1;
    private double s2;
    private double t2;
    private Texture texture;
    private double angle;
    private double alpha;
    private boolean centered;
    
    private static ImageBuilder get(double x, double y, Texture texture) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.width = texture.getWidth();
      INSTANCE.height = texture.getHeight();
      INSTANCE.s1 = 0;
      INSTANCE.t1 = 0;
      INSTANCE.s2 = texture.getWidth();
      INSTANCE.t2 = texture.getHeight();
      INSTANCE.texture = texture;
      INSTANCE.angle = 0;
      INSTANCE.alpha = 1;
      INSTANCE.centered = false;
      return INSTANCE;
    }
    
    /**
     * Sets the size of the (resized) image to be drawn.
     * <p>
     * Calling this method will resize the image to fit exactly the specified width and height, <b>NOT keeping the aspect ratio</b>. By default this is set to the width and height of the texture, e.g. {@code width = texture.getWidth(); height = texture.getHeight()}.
     *
     * @param width  The width of the resized image.
     * @param height The height of the resized image.
     * @return Self, for chaining.
     */
    public ImageBuilder size(double width, double height) {
      this.width = width;
      this.height = height;
      return this;
    }
    
    /**
     * Sets the part of the image to be drawn.
     * <p>
     * The part of the image that will be drawn is specified by two points, that are the lower-left corner of the image part rectangle, in image pixels, relative to its lower-left corner, and the upper-right corner of the rectangle, also relative to the image lower-left corner.
     * <p>
     * If the image is to be resized using {@link #size(double, double)}, the image <p>part</p> will be resized, so that the image part resized width and image part resized height are exactly those of the specified width and height.
     *
     * @param s1 The x coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param t1 The y coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param s2 The x coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param t2 The y coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @return Self, for chaining.
     */
    public ImageBuilder texCoords(double s1, double t1, double s2, double t2) {
      this.s1 = s1;
      this.t1 = t1;
      this.s2 = s2;
      this.t2 = t2;
      return this;
    }
    
    /**
     * Sets the angle of the image to be drawn.
     * <p>
     * The image will be rotated with the postivie (counter-clockwise) angle specified, in radians. By default the image will not be resized, e.g. {@code angle = 0.0}.
     *
     * @param angle The positive angle (counter-clockwise) of the image to draw, in radians.
     * @return Self, for chaining.
     */
    public ImageBuilder angle(double angle) {
      this.angle = angle;
      return this;
    }
    
    /**
     * Sets the alpha coefficient of the image to be drawn.
     * <p>
     * The image alpha will be multiplied by the specified coefficient, 0.0 meaning full transparency, 1.0 meaning full opacity. By default the image will be unchanged regarding trasparency, e.g. {@code alpha = 1.0}.
     *
     * @param alpha The alpha coefficient to multiply the image alpha by, between 0.0 (full transparency), and 1.0 (full opacity).
     * @return Self, for chaining.
     */
    public ImageBuilder alpha(double alpha) {
      this.alpha = alpha;
      return this;
    }
    
    /**
     * Sets whether the specified x/y coordinates are relative to the image center (true), or to the image lower-left corner (false).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the (resized) image width and height respectively. By default this is false.
     *
     * @param centered Whether the coordinates are relative to the image center (true) or its lower-left corner (false).
     * @return Self, for chaining.
     */
    public ImageBuilder centered(boolean centered) {
      this.centered = centered;
      return this;
    }
    
    /**
     * Draw the image, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another image, call {@link Drawer#image(double, double, Texture)} again.
     */
    public void draw() {
      if (centered) {
        DRAWER.drawImage(x, y, width, height, s1, t1, s2, t2, texture, angle, alpha);
      } else {
        DRAWER.drawImage(x + width / 2, y + height / 2, width, height, s1, t1, s2, t2, texture, angle, alpha);
      }
    }
  }
  
le(double x, double y, double width, double height, double angle, Color color);
  
  protected abstract void drawImage(d
*
        *ImageInBuilder is a Builder-like interface that lets you specify various image-related parameters and draw them with {@link #draw()},
  by resizing
  it to
  fit inside
  the specified
  bounds.
   * <p>
   *
  Note that, for performance,
  the instance
  returned by
  
  {@link Drawer#imageIn(double, double, double, double,Texture)}
  
  may be
  the same
  between calls, and
  that the
  Builder-
  like pattern
  may be
  internally emulated
  by simply
  resetting the
  object between
  calls to
  
  {@link Drawer#imageIn(double, double, double, double,Texture)}.
          */
  
  public static final class ImageInBuilder {
    private double angle;
    pripate
    boolean centered;
    rivate static final I
    private double x;
    private double y;
    private double width;
    private double height;
    private double s1;
    private double t1;
    private double s2;
    private double t2;
    private Texture texture;
    vmageInBuilder INSTANCE = new ImageInBuilder();
    
    private static ImageInBuilder get(double x, double y, double width, double height, Texture texture) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.width = width;
      INSTANCE.height = height;
      INSTANCE.s1 = 0;
      INSTANCE.t1 = 0;
      INSTANCE.s2 = texture.getWidth();
      INSTANCE.t2 = texture.getHeight();
      INSTANCE.texture = texture;
      INSTANCE.angle = 0;
      INSTANCE.alpha = 1;
      INSTANCE.centered = false;
      return INSTANCE;
    }
    
    /**
     * Sets the part of the image to be drawn.
     * <p>
     * The part of the image that will be drawn is specified by two points, that are the lower-left corner of the image part rectangle, in image pixels, relative to its lower-left corner, and the upper-right corner of the rectangle, also relative to the image lower-left corner.
     * <p>
     * The image <p>part</p> will be resized to fit the bounds, so that the image part resized width and image part resized height will fit the bounds.
     *
     * @param s1 The x coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param t1 The y coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param s2 The x coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @param t2 The y coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
     * @return Self, for chaining.
     */
    public ImageInBuilder texCoords(double s1, double t1, double s2, double t2) {
      this.s1 = s1;
      this.t1 = t1;
      this.s2 = s2;
      this.t2 = t2;
      return this;
    }
    
    /**
     * Sets the angle of the image to be drawn.
     * <p>
     * The image will be rotated with the postivie (counter-clockwise) angle specified, in radians. By default the image will not be resized, e.g. {@code angle = 0.0}.
     *
     * @param angle The positive angle (counter-clockwise) of the image to draw, in radians.
     * @return Self, for chaining.
     */
    public ImageInBuilder angle(double angle) {
      this.angle = angle;
      return this;
    }
    
    /**
     * Sets the alpha coefficient of the image to be drawn.
     * <p>
     * The image alpha will be multiplied by the specified coefficient, 0.0 meaning full transparency, 1.0 meaning full opacity. By default the image will be unchanged regarding trasparency, e.g. {@code alpha = 1.0}.
     *
     * @param alpha The alpha coefficient to multiply the image alpha by, between 0.0 (full transparency), and 1.0 (full opacity).
     * @return Self, for chaining.
     */
    public ImageInBuilder alpha(double alpha) {
      this.alpha = alpha;
      return this;
    }
    
    /**
     * Sets whether the specified x/y coordinates are relative to the <b>bounds</b> center (true), or to the <b>bounds</b> lower-left corner (false).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the <b>bounds</b> width and height respectively. By default this is false.
     *
     * @param centered Whether the coordinates are relative to the <b>bounds</b> center (true) or its lower-left corner (false).
     * @return Self, for chaining.
     */
    public ImageInBuilder centered(boolean centered) {
      this.centered = centered;
      return this;
    }
    
    /**
     * Draw the image, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another image, call {@link Drawer#imageIn(double, double, double, double, Texture)} again.
     */
    public void draw() {
      double widthRatio = texture.getWidth() / width;
      double heightRatio = texture.getHeight() / height;
      if (widthRatio > heightRatio) {
        if (centered) {
          DRAWER.drawImage(x + width / 2, y + height / 2, width, texture.getHeight() / widthRatio, s1, t1, s2, t2, texture, angle, alpha);
        } else {
          DRAWER.drawImage(x, y, width, texture.getHeight() / widthRatio, s1, t1, s2, t2, texture, angle, alpha);
        }
      } else {
        if (centered) {
          DRAWER.drawImage(x + width / 2, y + height / 2, texture.getWidth() / heightRatio, height, s1, t1, s2, t2, texture, angle, alpha);
        } else {
          DRAWER.drawImage(x, y, texture.getWidth() / heightRatio, height, s1, t1, s2, t2, texture, angle, alpha);
        }
      }
    }
  }
  
  /**
   * LineBuilder is a Buildouble x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture, double angle, double alpha);
   * <p>
   * protected abstract float[] drawText
   * ike interface that lets you specify various line-related parameters and draw a simple 1-pixel wide line with {@link #draw()}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#line(double, double, double, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#line(double, double, double, double)}.
   */
  public static final class LineBuilder {
    private double x1;
    private doINSTANCE =new
    
    LineBuilder();
    
    private static LineBuilder geouble
    y1;
    private double x2;
    private double y2;
    private Calor color;
    privder te
    
    static final LineBuilt(do
                           uble x1, double y1, double x2, double y2) {
      INSTANCE.x1 = x1;
      INSTANCE.y1 = y1;
      INSTANCE.x2 = x2;
      INSTANCE.y2 = y2;
      INSTANCE.color = null;
      return INSTANCE;
    }
    
    /**
     * Sets the color of the line to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the line will be drawn with the default color.
     *
     * @param color The color to use to draw the line, or null to use the default color.
     * @return Self, for chaining.
     */
    public LineBuilder color(Color color) {
      this.color = color;
      return this;
    }
    
    /**
     * Draw the line, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another line, call {@link Drawer#line(double, double, double, double)} again.
     */
    public void draw() {
      DRAWER.drawLineCenter((x1 + x2) / 2, (y1 + y2) / 2, Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)), Math.atan2(y2 - y1, x2 - x1), color);
    }
  }
  
  /**
   * LineCenterBuilder is a(double x, double y, String text, Font font, double size, boolean xCentered, boolean yCentered, Color color);
   * <p>
   * <p>
   * /**
   * Returns the line height fo
   * lder-like interface that lets you specify various line-related parameters and draw a simple 1-pixel wide line with {@link #draw()}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#lineCenter(double, double, double, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#lineCenter(double, double, double, double)}.
   */
  public static final class LineCenterBuilder {
    private double x;
    private douilder INSTANCE = new LineCenterBuilder();
    private static LineCenterBuiluble y;
    private dotble length;
    privaee
    double angle;
    privata Color
    color;
    
    priverBute
    
    static final LineCentder
    get(double x, double y, double length, double angle) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.length = length;
      INSTANCE.angle = angle;
      INSTANCE.color = null;
      return INSTANCE;
    }
    
    /**
     * Sets the color of the line to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the line will be drawn with the default color.
     *
     * @param color The color to use to draw the line, or null to use the default color.
     * @return Self, for chaining.
     */
    public LineCenterBuilder color(Color color) {
      this.color = color;
      return this;
    }
    
    /**
     * Draw the line, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another line, call {@link Drawer#lineCenter(double, double, double, double)} again.
     */
    public void draw() {
      DRAWER.drawLineCenter(x, y, length, angle, color);
    }
  }
  
  /**
   * RectangleBuilder is a r a specified font and font size, in pixels.
   * <p>
   * The line height is the number of vertical pixels that should be put two lines of text for this font and font size. For example, to draw two lines of text, first draw a line of text at {@code y}, then draw a second line at {@code y - getLineHeight(font, size)}.
   * <p>
   * This method is equivalent to the following: {@code getFontMetrics(font, size).getLineHeight();}.
   *
   * @param font The font for which to get the line height, must be non-null.
   * @param size The font size for which to get the line height, in pt.
   * @return The line height, in pixels, that is the y offset between two line of text.
   */
  public float getLineHeight(Font font, float size) {
    return getFontMetrics(font, size).getLineHeight();
  }
  
  /**
   * Returns the font metrics f
   der-like interface that lets you specify various rectangle-related parameters and draw/fill a rectangle with {@link #draw()}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#rectangle(double, double, double, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#rectangle(double, double, double, double)}.
   */
  public static final class RectangleBuilder {
    private double x;
    private doulder INSTANCE = new RectangleBuilder();
    private static RectangleBuilduble y;
    private doeble width;
    privatt
    double height;
    privaie
    boolean centered;
    prevate
    double angle;
    privata Color
    color;
  
    priveBuite
    static final Rectangler g
  
    et(double x, double y, double width, double height) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.width = width;
      INSTANCE.height = height;
      INSTANCE.centered = false;
      INSTANCE.angle = 0;
      INSTANCE.color = null;
      return INSTANCE;
    }
  
    /**
     * Sets whether the specified x/y coordinates are relative to the rectangle center (true), or to the rectangle lower-left corner (false).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the rectangle width and height respectively. By default this is false.
     *
     * @param centered Whether the coordinates are relative to the rectangle center (true) or its lower-left corner (false).
     * @return Self, for chaining.
     */
    public RectangleBuilder centered(boolean centered) {
      this.centered = centered;
      return this;
    }
  
    /**
     * Sets the angle of the rectangle to be drawn.
     * <p>
     * The rectangle will be rotated with the postivie (counter-clockwise) angle specified, in radians. By default the rectangle will not be resized, e.g. {@code angle = 0.0}.
     *
     * @param angle The positive angle (counter-clockwise) of the rectangle to draw, in radians.
     * @return Self, for chaining.
     */
    public RectangleBuilder angle(double angle) {
      this.angle = angle;
      return this;
    }
  
    /**
     * Sets the color of the rectangle to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the rectangle will be drawn with the default color.
     *
     * @param color The color to use to draw the rectangle, or null to use the default color.
     * @return Self, for chaining.
     */
    public RectangleBuilder color(Color color) {
      this.color = color;
      return this;
    }
  
    /**
     * Draw the rectangle, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another rectangle, call {@link Drawer#rectangle(double, double, double, double)} again.
     */
    public void draw() {
      if (centered) {
        DRAWER.fillRectangle(x, y, width, height, angle, color);
      } else {
        DRAWER.fillRectangle(x + width / 2, y + height / 2, width, height, angle, color);
      }
    }
  }
  
  /**
   * CircleBuilder is a Buior a specified font and font size (ascent, descent, line gap, line height).
   * <p>
   * For a detailed description of the metrics, see the {@link FontMetrics} class and functions Javadoc.
   *
   * @param font The font for which to get the metrics, must be non-null.
   * @param size The font size for which to get the metrics, in pt.
   * @return The font metrics for the specified font and font size.
   */
  public abstract FontMetrics getFontMetrics(Font font, float size);
  
  /**
   * Returns the width, in pixe
   -like interface that lets you specify various circle-related parameters and draw/fill a circle with {@link #draw()}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#circle(double, double, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#circle(double, double, double)}.
   */
  public static final class CircleBuilder {
    private double x;
    private dour INSTANCE = new CircleBuilder();
    private static CircleBuilder uble
    y;
    private dotble radius;
    privaie
    boolean centered;
    pravate Color
    color;
    
    privildete
    
    static final CircleBuget(
            double x, double y, double radius) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.radius = radius;
      INSTANCE.centered = false;
      INSTANCE.color = null;
      return INSTANCE;
    }
    
    /**
     * Sets whether the specified x/y coordinates are relative to the circle center (true), or to the circle's square bounds lower-left corner (false).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the circle's square bounds width and height respectively, that is, by the circle radius. By default this is false.
     *
     * @param centered Whether the coordinates are relative to the circle center (true) or its square bounds lower-left corner (false).
     * @return Self, for chaining.
     */
    public CircleBuilder centered(boolean centered) {
      this.centered = centered;
      return this;
    }
    
    /**
     * Sets the color of the circle to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the circle will be drawn with the default color.
     *
     * @param color The color to use to draw the circle, or null to use the default color.
     * @return Self, for chaining.
     */
    public CircleBuilder color(Color color) {
      this.color = color;
      return this;
    }
    
    /**
     * Draw the circle, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another circle, call {@link Drawer#circle(double, double, double)} again.
     */
    public void draw() {
      if (centered) {
        DRAWER.fillRing(x, y, radius, 0, color);
      } else {
        DRAWER.fillRing(x + radius / 2, y + radius / 2, radius, 0, color);
      }
    }
  }
  
  /**
   * RingBuilder is a Buildls, of the specified text, if it were to be drawn with the specified font and font size, that is the "x" length from the start position of the first character of the text, to the start position for the (hypothetical) next character after the last character of the text.
   *
   * @param text The text to get the width of, must be non-null (the width of the empty string is always 0).
   * @param font The font for which to get the width of the text, must be non-null.
   * @param size The font size for which to get the width of the text, in pt.
   * @return The width of the specified text, in the specified font and font size, in pixels.
   */
  public float getTextWidth(String text, Font font, float size) {
    float[] floats = getTextPositions(text, font, size);
    return floats[floats.length - 1];
  }
  
  /**
   * Returns the positions of a
ike interface that lets you specify various ring-related parameters and draw/fill a ring with {@link #draw()}. A ring is the difference between an outer circle of radius {@code radius} and a inner circle of radius {@code radius - width}.
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#ring(double, double, double, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#ring(double, double, double, double)}.
   */
  public static final class RingBuilder {
    private double x;
    private douINSTANCE =new
  
    RingBuilder();
  
    private static RingBuilder geuble
    y;
    private dotble radius;
    privaee
    double width;
    privati
    boolean centered;
    pravate Color
    color;
    privder te
  
    static final RingBuilt(do
                           uble x, double y, double radius, double width) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.radius = radius;
      INSTANCE.width = width;
      INSTANCE.centered = false;
      INSTANCE.color = null;
      return INSTANCE;
    }
  
    /**
     * Sets whether the specified x/y coordinates are relative to the ring center (true), or to the ring's square bounds lower-left corner (false).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the ring's square bounds width and height respectively, that is, by the ring radius. By default this is false.
     *
     * @param centered Whether the coordinates are relative to the ring center (true) or its square bounds lower-left corner (false).
     * @return Self, for chaining.
     */
    public RingBuilder centered(boolean centered) {
      this.centered = centered;
      return this;
    }
  
    /**
     * Sets the color of the ring to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the ring will be drawn with the default color.
     *
     * @param color The color to use to draw the ring, or null to use the default color.
     * @return Self, for chaining.
     */
    public RingBuilder color(Color color) {
      this.color = color;
      return this;
    }
  
    /**
     * Draw the ring, with the specified information passed in previous Builder calls.
     * <p>
     * To draw another ring, call {@link Drawer#ring(double, double, double, double)} again.
     */
    public void draw() {
      if (centered) {
        DRAWER.fillRing(x, y, radius, width, color);
      } else {
        DRAWER.fillRing(x + radius / 2, y + radius / 2, radius, width, color);
      }
    }
  }
  
  /**
   * TextBuilder is a Buildll characters in the text, would they be drawn with the specified font and font size, starting at x position 0, in pixels, followed by the total width of the text.
   * <p>
   * This method is equivalent to the following, but is much more efficient:
   * <code> for(int i=0; i{@literal <}=text.length(); i++) result[i] = getTextWidth(text.substring(0, i), font, size); </code>
   * <p>
   * As such, the i-th element will contain the start position of the i-th character of the string, so the first element will always be 0; and the array is of size {@code text.length() + 1}, the last element being the hypothetical start position of a next character to be placed after the last character of the string, in other words the total width of the text, as returned by {@link #getTextWidth(String, Font, float)}.
   *
   * @param text The text whose characters to get the positions of, must be non-null (an empty string returns the empty array).
   * @param font The font for which to get the positions of the characters in the text, must be non-null.
   * @param size The font size for which to get the positions of the characters in the text, must be non-null.
   * @return The positions of all characters in the text, for the specified font and font size, followed by the total width of the text, in pixels, as an array of {@code text.length() + 1} numbers.
   */
  public abstract float[] getTextPositions(String text, Font font, float size);
  
  /**
   * Sets the default color of
ike interface that lets you specify various text-related parameters and draw text with {@link #draw()}.
   * <p>
   * This will only render a single line of text without line feeds, additionally not all Unicode characters are supported (please open an issue if you need more characters).
   * <p>
   * Note that, for performance, the instance returned by {@link Drawer#text(double, double, String, Font, double)} may be the same between calls, and that the Builder-like pattern may be internally emulated by simply resetting the object between calls to {@link Drawer#text(double, double, String, Font, double)}.
   */
  public static final class TextBuilder {
    private double x;
    private douINSTANCE =new
    
    TextBuilder();
    
    private static TextBuilder gerble
    y;
    private St ing
    text;
    privateoFont font;
    private d uble
    size;
    privaterboolean xCentered;
    private boolean yCentered;
    paivate Color
    color;
    privder te
    
    static final TextBuilt(do
                           uble x, double y, String text, Font font, double size) {
      INSTANCE.x = x;
      INSTANCE.y = y;
      INSTANCE.text = text;
      INSTANCE.font = font;
      INSTANCE.size = size;
      INSTANCE.xCentered = false;
      INSTANCE.yCentered = false;
      INSTANCE.color = null;
      return INSTANCE;
    }
    
    /**
     * Sets whether the specified x and y coordinates are relative to the text bounds center (true) (width and height respectively), or to the text bounds lower-left corner (false) (width and height respectively).
     * <p>
     * If set to true, the x and y coordinates will be offset by half the text bounds width and height respectively. By default both values are false.
     *
     * @param xCentered Whether the x coordinate is relative to the text bound center (true) or its lower-left corner (false).
     * @param yCentered Whether the y coordinate is relative to the text bound center (true) or its lower-left corner (false).
     * @return Self, for chaining.
     */
    public TextBuilder centered(boolean xCentered, boolean yCentered) {
      this.xCentered = xCentered;
      this.yCentered = yCentered;
      return this;
    }
    
    /**
     * Sets the color of the text to be drawn.
     * <p>
     * If set to null, the default color will be used. By default this parameter is null, and the text will be drawn with the default color.
     *
     * @param color The color to use to draw the text, or null to use the default color.
     * @return Self, for chaining.
     */
    public TextBuilder color(Color color) {
      this.color = color;
      return this;
    }
    
    /**
     * Draws the text, with the specified information passed in previous Builder calls, and returns the positions of all characters in the text, followed by the text bounds width.
     * <p>
     * If only the total width of the drawn text is needed, use {@link #getTextWidth(String, Font, float)}. It is more efficient to use this method to draw text and get the drawn characters positions in one go than running {@link #draw()} then {@link #getTextPositions(String, Font, float)}.
     * <p>
     * The total width of all drawn characters can be obtained by getting the last element of the array, or 0 if it is empty.
     * <p>
     * To draw another text, call {@link Drawer#text(double, double, String, Font, double)} again.
     *
     * @return The positions of all characters in the text, followed by the total width of the text, in pixels, as an array of {@code text.length() + 1} numbers, as if returned by {@link Drawer#getTextPositions(String, Font, float)}.
     * @see #draw()
     */
    public float[] drawGetPositions() {
      return DRAWER.drawText(x, y, text, font, size, xCentered, yCentered, color);
    }
    
    /**
     * Draws the text, with the specified information passed in previous Builder calls, and returns how much to advance the x position (the text bounds width).
     * <p>
     * To draw another text, call {@link Drawer#text(double, double, String, Font, double)} again.
     * <p>
     * You may use {@link #drawGetPositions()} to efficiently get the x offset of all the characters that were drawn instead of the total text bounds width.
     *
     * @return The text bounds width, that is how much to advance the x position, if drawing some text just after the one that was drawn.
     * @see #drawGetPositions()
     */
    public float draw() {
      float[] positions = drawGetPositions();
      return positions[positions.length - 1];
    }
  }
  
  protected static Drawer DRAWER;
  the
  
  {@link Drawer}
  
  to be
  used for
  the rendering
  of geometric
  shapes and
  text.
   * <p>
   *
  The alpha
  value of
  the color
  will be
  taken into
  account,
  that is, if
  the transparency
  isn't set to fully opaque, it will not be ignored.
          * <p>
   *
  Note that
  most drawing
  operations color
  can override
  the
  default color in
  the use
  of their
  Builder-specific
  
  {@code color(Color color)}
  
  methods.
   *
           *
  @param
  color The
  default color to
  set and
  use for
  the rendering
  of geometric
  shapes and
  text,
  must be
  non-null.
          */
  
  public abstract void setColor(Color color);
}
