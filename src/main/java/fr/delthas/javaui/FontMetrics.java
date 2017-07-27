package fr.delthas.javaui;

/**
 * FontMetrics stores metrics relative to a font and font size, that may be useful to components that need to draw and process text.
 * <p>
 * All metrics are in pixels.
 *
 * @see #getLineHeight()
 */
public class FontMetrics {
  private final float ascent;
  private final float descent;
  private final float lineGap;
  
  FontMetrics(float ascent, float descent, float lineGap) {
    this.ascent = ascent;
    this.descent = descent;
    this.lineGap = lineGap;
  }
  
  /**
   * Returns the ascent for this font and font size, that is the vertical extent of characters of this font above the baseline, in pixels.
   * <p>
   * The returned number is positive for values above the baseline, so it is typically positive.
   *
   * @return The ascent for this font and font size, in pixels.
   */
  public float getAscent() {
    return ascent;
  }
  
  /**
   * Returns the descent for this font and font size, that is the vertical extent of characters of this font below the baseline, in pixels.
   * <p>
   * The returned number is positive for values above the baseline, so it is typically negative.
   *
   * @return The descent for this font and font size, in pixels.
   */
  public float getDescent() {
    return descent;
  }
  
  /**
   * Returns the line gap for this font and font size, that is the spacing between this row descent and the next row ascent, in pixels.
   * <p>
   * <b>Most of the time {@link #getLineHeight()} should be used instead as it computes the number of pixels that should be put between two base lines, that is how much to offset the y coordinate when printing several lines of text.</b>
   *
   * @return The line gap for this font and font size, in pixels.
   * @see #getLineHeight()
   */
  public float getLineGap() {
    return lineGap;
  }
  
  /**
   * Returns the line height for this font and font size, in pixels.
   * <p>
   * The line height is the number of vertical pixels that should be put two lines of text for this font and font size. For example, to draw two lines of text, first draw a line of text at {@code y}, then draw a second line at {@code y - getLineHeight(font, size)}.
   * <p>
   * This method is equivalent to the following: {@code return getAscent() - getDescent() + getLineGap();}.
   *
   * @return The line height for this font and font size, in pixels.
   */
  public float getLineHeight() {
    return ascent - descent + lineGap;
  }
}
