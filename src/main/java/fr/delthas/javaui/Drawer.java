package fr.delthas.javaui;

import java.awt.*;
import java.util.Objects;

/**
 * Drawer is an interface on which to draw a component, to be called from a component {@link Component#render(InputState, Drawer)} method.
 * <p>
 * There are several methods to draw simple geometric shapes, images (actually {@link Texture}) objects, and text. Apart from drawing methods, there is a translation stack, that lets push a x and y amount to be added on all passed coordinates (to be popped before returning). The drawer also stores a color in its internal state, that can be set with {@link #setColor(Color)}, that it uses to draw geometric shapes and text. There are also utilities method related to text drawing, such as {@link #getLineHeight(Font, float)}.
 * <p>
 * All passed coordinates are <b>RELATIVE to the component rectangle</b> lower-left corner.
 */
public interface Drawer {
  /**
   * Pushes a translation onto the translation stack.
   * <p>
   * All method calls on this Drawer object will be translated by the sum of all translations stored on the translation stack.
   * <p>
   * <b>For each {@link #pushTranslate(double, double)} method call during a {@link Component#render(InputState, Drawer)} call, there MUST BE EXACTLY ONE {@link #popTranslate()} CALL, NO MORE, NO LESS.</b>
   *
   * @param x The x amount to push onto the translation stack.
   * @param y The y amount to push onto the translation stack.
   * @see #popTranslate()
   */
  void pushTranslate(double x, double y);
  
  /**
   * Pops a translation from the translation stack.
   * <p>
   * See {@link #pushTranslate(double, double)} for an explanation of the translation stack. <b>For each {@link #pushTranslate(double, double)} method call during a {@link Component#render(InputState, Drawer)} call, there MUST BE EXACTLY ONE {@link #popTranslate()} CALL, NO MORE, NO LESS.</b>
   *
   * @see #pushTranslate(double, double)
   */
  void popTranslate();
  
  /**
   * Draws a straight line that goes from the point at the specified first coordinates to the point at the specified second coordinates, that is one pixel wide, with the color set by {@link #setColor(Color)}.
   *
   * @param x1 The x coordinate of the first point, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y1 The y coordinate of the first point, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param x2 The x coordinate of the second point, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y2 The y coordinate of the second point, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   */
  default void drawLine(double x1, double y1, double x2, double y2) {
    drawLineCenter((x1 + x2) / 2, (y1 + y2) / 2, Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)), Math.atan2(y2 - y1, x2 - x1));
  }
  
  /**
   * Draws a straight line whose center is the specified point, with the specified angle, that is one pixel wide, with the color set by {@link #setColor(Color)}.
   *
   * @param x      The x coordinate of the center of the line, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the line, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param length The length of the line to draw.
   * @param angle  The positive angle (counter-clockwise) of the line to draw, in radians. (An angle of 0 means the line is parallel to the x axis.)
   */
  void drawLineCenter(double x, double y, double length, double angle);
  
  /**
   * Draws (and fills) a circle, with the specified center and radius, with the color set by {@link #setColor(Color)}.
   *
   * @param x      The x coordinate of the center of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param radius The radius of the circle to draw.
   */
  default void fillCircle(double x, double y, double radius) {
    fillCircle(x, y, radius, true);
  }
  
  /**
   * Draws (and fills) a circle, with the specified center/lower-left corner, and radius, with the color set by {@link #setColor(Color)}.
   *
   * @param x        The x coordinate of the center/lower-left corner of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param radius   The radius of the circle to draw.
   * @param centered Whether the coordinates are those of the center of the circle (true), or of its lower-left corner (false).
   */
  default void fillCircle(double x, double y, double radius, boolean centered) {
    if (centered) {
      fillRing(x, y, radius, 0);
    } else {
      fillRing(x + radius / 2, y + radius / 2, radius, 0);
    }
  }
  
  /**
   * Draws (and fills) a ring, with the specified center/lower-left corner, and (outer) radius, and width, with the color set by {@link #setColor(Color)}; that fills the area between the circles of radius {@code radius} and {@code radius - width}.
   *
   * @param x        The x coordinate of the center/lower-left corner of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param radius   The (outer) radius of the circle to draw.
   * @param width    The width of the ring to draw.
   * @param centered Whether the coordinates are those of the center of the circle (true), or of its lower-left corner (false).
   */
  default void fillRing(double x, double y, double radius, double width, boolean centered) {
    if (centered) {
      fillRing(x, y, radius, width);
    } else {
      fillRing(x + radius / 2, y + radius / 2, radius, width);
    }
  }
  
  /**
   * Draws (and fills) a ring, with the specified center, and (outer) radius, and width, with the color set by {@link #setColor(Color)}; that fills the area between the circles of radius {@code radius} and {@code radius - width}.
   *
   * @param x      The x coordinate of the center of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the circle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param radius The (outer) radius of the circle to draw.
   * @param width  The width of the ring to draw.
   */
  void fillRing(double x, double y, double radius, double width);
  
  /**
   * Draws (and fills) a rectangle, with the specified center/lower-left corner, width and height, and angle, with the color set by {@link #setColor(Color)}.
   *
   * @param x        The x coordinate of the center/lower-left corner of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width    The width of the rectangle to draw.
   * @param height   The height of the rectangle to draw.
   * @param centered Whether the coordinates are those of the center of the rectangle (true), or of its lower-left corner (false).
   * @param angle    The positive angle (counter-clockwise) of the rectangle to draw, in radians.
   */
  default void fillRectangle(double x, double y, double width, double height, boolean centered, double angle) {
    if (centered) {
      fillRectangle(x, y, width, height, angle);
    } else {
      fillRectangle(x + width / 2, y + height / 2, width, height, angle);
    }
  }
  
  /**
   * Draws (and fills) a rectangle, with the specified center, width and height, and angle, with the color set by {@link #setColor(Color)}.
   *
   * @param x      The x coordinate of the center of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width  The width of the rectangle to draw.
   * @param height The height of the rectangle to draw.
   * @param angle  The positive angle (counter-clockwise) of the rectangle to draw, in radians.
   */
  void fillRectangle(double x, double y, double width, double height, double angle);
  
  /**
   * Draws (and fills) a rectangle, with the specified center/lower-left corner, width and height, with the color set by {@link #setColor(Color)}.
   *
   * @param x        The x coordinate of the center/lower-left corner of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width    The width of the rectangle to draw.
   * @param height   The height of the rectangle to draw.
   * @param centered Whether the coordinates are those of the center of the rectangle (true), or of its lower-left corner (false).
   */
  default void fillRectangle(double x, double y, double width, double height, boolean centered) {
    fillRectangle(x, y, width, height, centered, 0);
  }
  
  /**
   * Draws (and fills) a rectangle, with the specified center, width and height with the color set by {@link #setColor(Color)}.
   *
   * @param x      The x coordinate of the center of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y      The y coordinate of the center of the rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width  The width of the rectangle to draw.
   * @param height The height of the rectangle to draw.
   */
  default void fillRectangle(double x, double y, double width, double height) {
    fillRectangle(x, y, width, height, 0);
  }
  
  /**
   * Draws an image, with the specified center/lower-left corner, by resizing it to fit into the specified width and height, called constraint dimensions, while keeping the aspect ratio.
   * <p>
   * The image will be resized (upscaled or downscaled) so that it is the largest possible image that fits into the specified constraint dimensions, while keeping the aspect ratio.
   *
   * @param x         The x coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y         The y coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param maxWidth  The width of the constraint dimensions.
   * @param maxHeight The height of the constraint dimensions.
   * @param texture   The texture to draw, must be non-null.
   * @param centered  Whether the coordinates are those of the center of the image (true), or of its lower-left corner (false).
   */
  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Texture texture, boolean centered) {
    if (centered) {
      drawImageIn(x, y, maxWidth, maxHeight, texture);
    } else {
      drawImageIn(x + maxWidth / 2, y + maxHeight / 2, maxWidth, maxHeight, texture);
    }
  }
  
  /**
   * Draws an image, with the specified center, by resizing it to fit into the specified width and height, called constraint dimensions, while keeping the aspect ratio.
   * <p>
   * The image will be resized (upscaled or downscaled) so that it is the largest possible image that fits into the specified constraint dimensions, while keeping the aspect ratio.
   *
   * @param x         The x coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y         The y coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param maxWidth  The width of the constraint dimensions.
   * @param maxHeight The height of the constraint dimensions.
   * @param texture   The texture to draw, must be non-null.
   */
  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Texture texture) {
    Objects.requireNonNull(texture);
    double widthRatio = texture.getWidth() / maxWidth;
    double heightRatio = texture.getHeight() / maxHeight;
    if (widthRatio > heightRatio) {
      drawImage(x, y, maxWidth, texture.getHeight() / widthRatio, texture);
    } else {
      drawImage(x, y, texture.getWidth() / heightRatio, maxHeight, texture);
    }
  }
  
  /**
   * Draws an image, at the specified center coordinates.
   *
   * @param x       The x coordinate of the center of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the center of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param texture The texture to draw, must be non-null.
   */
  default void drawImage(double x, double y, Texture texture) {
    drawImage(x, y, texture, true);
  }
  
  /**
   * Draws an image, at the specified center/lower-left corner coordinates.
   *
   * @param x        The x coordinate of the center/lower-left corner of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param texture  The texture to draw, must be non-null.
   * @param centered Whether the coordinates are those of the center of the image (true), or of its lower-left corner (false).
   */
  default void drawImage(double x, double y, Texture texture, boolean centered) {
    Objects.requireNonNull(texture);
    drawImage(x, y, texture.getWidth(), texture.getHeight(), texture, centered);
  }
  
  /**
   * Draws an image, with the specified center, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The image will be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height.
   *
   * @param x       The x coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width   The width of the resized image.
   * @param height  The height of the resized image.
   * @param texture The texture to draw, must be non-null.
   */
  default void drawImage(double x, double y, double width, double height, Texture texture) {
    drawImage(x, y, width, height, texture, true);
  }
  
  /**
   * Draws an image, with the specified center/lower-left corner, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The image will be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height.
   *
   * @param x        The x coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width    The width of the resized image.
   * @param height   The height of the resized image.
   * @param texture  The texture to draw, must be non-null.
   * @param centered Whether the coordinates are those of the center of the image (true), or of its lower-left corner (false).
   */
  default void drawImage(double x, double y, double width, double height, Texture texture, boolean centered) {
    drawImage(x, y, width, height, texture, centered, 0);
  }
  
  /**
   * Draws a part of the specified image, with the specified lower-left corner, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The part of the image that will be drawn is specified by two points, that are the lower-left corner of the image part rectangle, in image pixels, relative to its lower-left corner, and the upper-right corner of the rectangle, also relative to the image lower-left corner. The image part will then be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height.
   *
   * @param x       The x coordinate of the lower-left corner of the resized image part, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the lower-left corner of the resized image part, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width   The width of the resized image part.
   * @param height  The height of the resized image part.
   * @param s1      The x coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param t1      The y coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param s2      The x coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param t2      The y coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param texture The texture to draw, must be non-null.
   */
  default void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture) {
    drawImage(x, y, width, height, s1, t1, s2, t2, texture, 0);
  }
  
  /**
   * Draws an image, at the specified center coordinates, with the specified angle.
   *
   * @param x       The x coordinate of the center of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the center of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param texture The texture to draw, must be non-null.
   * @param angle   The positive angle (counter-clockwise) of the image to draw, in radians.
   */
  default void drawImage(double x, double y, Texture texture, double angle) {
    drawImage(x, y, texture, true, angle);
  }
  
  /**
   * Draws an image, at the specified center/lower-left corner coordinates, with the specified angle.
   *
   * @param x        The x coordinate of the center/lower-left corner of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param texture  The texture to draw, must be non-null.
   * @param centered Whether the coordinates are those of the center of the image (true), or of its lower-left corner (false).
   * @param angle    The positive angle (counter-clockwise) of the image to draw, in radians.
   */
  default void drawImage(double x, double y, Texture texture, boolean centered, double angle) {
    Objects.requireNonNull(texture);
    drawImage(x, y, texture.getWidth(), texture.getHeight(), texture, centered, angle);
  }
  
  /**
   * Draws an image, with the specified center, and angle, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The image will be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height. It will then be rotated to the specified angle.
   *
   * @param x       The x coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the center of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width   The width of the resized image.
   * @param height  The height of the resized image.
   * @param texture The texture to draw, must be non-null.
   * @param angle   The positive angle (counter-clockwise) of the image to draw, in radians.
   */
  default void drawImage(double x, double y, double width, double height, Texture texture, double angle) {
    drawImage(x, y, width, height, texture, true, angle);
  }
  
  /**
   * Draws an image, with the specified center/lower-left corner, and angle, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The image will be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height. It will then be rotated to the specified angle.
   *
   * @param x        The x coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y        The y coordinate of the center/lower-left corner of the resized image, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width    The width of the resized image.
   * @param height   The height of the resized image.
   * @param texture  The texture to draw, must be non-null.
   * @param centered Whether the coordinates are those of the center of the image (true), or of its lower-left corner (false).
   * @param angle    The positive angle (counter-clockwise) of the image to draw, in radians.
   */
  default void drawImage(double x, double y, double width, double height, Texture texture, boolean centered, double angle) {
    Objects.requireNonNull(texture);
    if (centered) {
      drawImage(x, y, width, height, 0, 0, texture.getWidth(), texture.getHeight(), texture, angle);
    } else {
      drawImage(x + width / 2, y + height / 2, width, height, 0, 0, texture.getWidth(), texture.getHeight(), texture, angle);
    }
  }
  
  /**
   * Draws a part of the specified image, with the specified lower-left corner, and angle, by resizing it to fit exactly the specified width and height, <b>NOT keeping the aspect ratio.</b>
   * <p>
   * The part of the image that will be drawn is specified by two points, that are the lower-left corner of the image part rectangle, in image pixels, relative to its lower-left corner, and the upper-right corner of the rectangle, also relative to the image lower-left corner. The image part will then be resized without regard for the aspect ratio, so that its resized width and height are exactly those of the specified width and height. It will then be rotated to the specified angle.
   *
   * @param x       The x coordinate of the lower-left corner of the resized image part, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y       The y coordinate of the lower-left corner of the resized image part, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param width   The width of the resized image part.
   * @param height  The height of the resized image part.
   * @param s1      The x coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param t1      The y coordinate of the lower-left corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param s2      The x coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param t2      The y coordinate of the upper-right corner of the image part, relative to the lower-left corner of the image (not adujsted by the translation stack).
   * @param texture The texture to draw, must be non-null.
   * @param angle   The positive angle (counter-clockwise) of the resized image part to draw, in radians.
   */
  void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture, double angle);
  
  // TODO
  // default void drawText(double x, double y, String text, Font font, float size) {
  //  drawText(x, y, text, font, size);
  // }
  
  /**
   * Returns the line height for a specified font and font size, in pixels.
   * <p>
   * The line height is the number of vertical pixels that should be put two lines of text for this font and font size. For example, to draw two lines of text, first draw a line of text at {@code y}, then draw a second line at {@code y - getLineHeight(font, size)}.
   * <p>
   * This method is equivalent to the following: {@code getFontMetrics(font, size).getLineHeight();}.
   *
   * @param font The font for which to get the line height, must be non-null.
   * @param size The font size for which to get the line height, in pt.
   * @return The line height, in pixels, that is the y offset between two line of text.
   */
  default float getLineHeight(Font font, float size) {
    return getFontMetrics(font, size).getLineHeight();
  }
  
  /**
   * Returns the font metrics for a specified font and font size (ascent, descent, line gap, line height).
   * <p>
   * For a detailed description of the metrics, see the {@link FontMetrics} class and functions Javadoc.
   *
   * @param font The font for which to get the metrics, must be non-null.
   * @param size The font size for which to get the metrics, in pt.
   * @return The font metrics for the specified font and font size.
   */
  FontMetrics getFontMetrics(Font font, float size);
  
  /**
   * Returns the width, in pixels, of the specified text, if it were to be drawn with the specified font and font size, that is the "x" length from the start position of the first character of the text, to the start position for the (hypothetical) next character after the last character of the text.
   *
   * @param text The text to get the width of, must be non-null (the width of the empty string is always 0).
   * @param font The font for which to get the width of the text, must be non-null.
   * @param size The font size for which to get the width of the text, in pt.
   * @return The width of the specified text, in the specified font and font size, in pixels.
   */
  default float getTextWidth(String text, Font font, float size) {
    float[] floats = getTextPositions(text, font, size);
    return floats[floats.length - 1];
  }
  
  /**
   * Returns the positions of all characters in the text, would they be drawn with the specified font and font size, starting at x position 0, in pixels, followed by the total width of the text.
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
  float[] getTextPositions(String text, Font font, float size);
  
  /**
   * Draws the specified text at the specified left/center x position and lower/center y position, with the specified font and font size, with the color set by {@link #setColor(Color)}, and returns how much to advance the x position (the width of the text rectangle that was drawn).
   *
   * @param x         The x coordinate of the left/center of the text rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y         The y coordinate of the bottom/center of the text rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param text      The text to draw at the specified location with the specified font and font size, must be non-null (drawing the empty string is a no-op).
   * @param font      The font to use to draw the text, must be non-null.
   * @param size      The font size to use to draw the text, in pt.
   * @param xCentered Whether the specified x coordinate is the left of the text rectangle (false), or the center of the text rectangle (true).
   * @param yCentered Whether the specified y coordinate is the bottom of the text rectangle (false), or the center of the text rectangle (true).
   * @return The width of the text rectangle that was drawn, that is how much to advance the x position, if drawing some text just after the one that was drawn.
   * @see #getTextWidth(String, Font, float)
   * @see #getLineHeight(Font, float)
   */
  default float drawText(double x, double y, String text, Font font, float size, boolean xCentered, boolean yCentered) {
    float[] positions = drawTextPositions(x, y, text, font, size, xCentered, yCentered);
    return positions[positions.length - 1];
  }
  
  /**
   * Draws the specified text at the specified left/center x position and lower/center y position, with the specified font and font size, with the color set by {@link #setColor(Color)}, and returns the positions of each character drawn, followed by the total width of the text, as if obtained by running {@link #getTextPositions(String, Font, float)} on the text.
   * <p>
   * If only the total width of the drawn text is needed, use {@link #getTextWidth(String, Font, float)}. It is more efficient to use this method to draw text and get the drawn characters positions in one go than running {@link #drawText(double, double, String, Font, float, boolean, boolean)} then {@link #getTextPositions(String, Font, float)}.
   * <p>
   * The total width of all drawn characters can be obtained by getting the last element of the array, or 0 if it is empty.
   *
   * @param x         The x coordinate of the left/center of the text rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param y         The y coordinate of the bottom/center of the text rectangle, relative to the lower-left corner of the component rectangle, adjusted by the translation stack.
   * @param text      The text to draw at the specified location with the specified font and font size, must be non-null (drawing the empty string is a no-op).
   * @param font      The font to use to draw the text, must be non-null.
   * @param size      The font size to use to draw the text, in pt.
   * @param xCentered Whether the specified x coordinate is the left of the text rectangle (false), or the center of the text rectangle (true).
   * @param yCentered Whether the specified y coordinate is the bottom of the text rectangle (false), or the center of the text rectangle (true).
   * @return The positions of all characters in the text, for the specified font and font size, followed by the total width of the text, in pixels, as an array of {@code text.length() + 1} numbers, as if returned with {@link #getTextPositions(String, Font, float)}.
   * @see #getTextWidth(String, Font, float)
   * @see #getLineHeight(Font, float)
   * @see #drawText(double, double, String, Font, float, boolean, boolean)
   * @see #getTextPositions(String, Font, float)
   */
  float[] drawTextPositions(double x, double y, String text, Font font, float size, boolean xCentered, boolean yCentered);
  
  /**
   * Sets the color of the {@link Drawer} to be used for the rendering of geometric shapes and text.
   * <p>
   * The alpha value of the color will be taken into account, that is if the transparency isn't opaque, it will not be ignored.
   *
   * @param color The color to set and use for the rendering of geometric shapes and text, must be non-null.
   */
  void setColor(Color color);
}
