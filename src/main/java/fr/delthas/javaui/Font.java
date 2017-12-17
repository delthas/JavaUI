package fr.delthas.javaui;

import java.awt.*;

/**
 * Font represents a common font, which is an object that can specify, for a specified font size, what all the glyphs (graphical representations) for the characters are, and what size they take, as well as some metrics such as line height.
 * <p>
 * All the fonts that can be used to draw text are listed in this enumeration, and are used as parameters to the various {@link Drawer} text related methods, metrics-related, and drawing-related, such as {@link Drawer#text(double, double, String, Font, double)}.
 * <p>
 * The "bold" and "italics" vesions of a font are considered as different fonts objects, since they barely share any glyphs. For example, Times New Roman is a font, Times New Roman Bold is another font.
 *
 * @see Drawer
 * @see Drawer#drawText(double, double, String, Font, double, boolean, boolean, Color)
 */
public enum Font {
  
  /**
   * The COMIC SANS font (most commonly referred to as COMIC SANS MS) in its regular form.
   */
  COMIC("comicsans");
  private final String name;
  
  Font(String name) {
    this.name = name;
  }
  
  String getName() {
    return name;
  }
  
}
