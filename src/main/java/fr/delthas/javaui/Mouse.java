package fr.delthas.javaui;

/**
 * Mouse represents a mouse button, and is used in {@link Component#pushMouseButton(double, double, int, boolean, long)}.
 * <p>
 * The mouse left, right, and middle buttons are {@link #MOUSE_LEFT}, {@link #MOUSE_RIGHT}, and {@link #MOUSE_MIDDLE}, which are aliases for the first 3 buttons: {@link #MOUSE_1}, {@link #MOUSE_2}, {@link #MOUSE_3}. There are 8 supported mouse buttons.
 *
 * @see Key
 * @see KeyModifier
 */
public interface Mouse {
  /**
   * The first mouse button, which is also the {@link #MOUSE_LEFT left mouse button}.
   */
  int MOUSE_1 = 1;
  /**
   * The second mouse button, which is also the {@link #MOUSE_RIGHT right mouse button}.
   */
  int MOUSE_2 = 2;
  /**
   * The third mouse button, which is also the {@link #MOUSE_MIDDLE middle mouse button} (most commonly the scroll wheel button).
   */
  int MOUSE_3 = 3;
  /**
   * The fourth mouse button.
   */
  int MOUSE_4 = 4;
  /**
   * The fifth mouse button.
   */
  int MOUSE_5 = 5;
  /**
   * The sixth mouse button.
   */
  int MOUSE_6 = 6;
  /**
   * The seventh mouse button.
   */
  int MOUSE_7 = 7;
  /**
   * The eighth mouse button.
   */
  int MOUSE_8 = 8;
  /**
   * The left mouse button, which is also the {@link #MOUSE_1 first mouse button}.
   */
  int MOUSE_LEFT = MOUSE_1;
  /**
   * The right mouse button, which is also the {@link #MOUSE_2 second mouse button}.
   */
  int MOUSE_RIGHT = MOUSE_2;
  /**
   * The middle mouse button (most commonly the scroll wheel mouse button), which is also the {@link #MOUSE_3 third mouse button}.
   */
  int MOUSE_MIDDLE = MOUSE_3;
}
