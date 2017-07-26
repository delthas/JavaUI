package fr.delthas.javaui;

/**
 * InputState stores the current state of the input system, that is: the mouse cursor position, which keyboard keys are currently pressed/down, and which mouse buttons are currently pressed/down.
 * <p>
 * InputState is to be used as a snapshot of all inputs at the moment the component receives the object, and <b>may not be stored and used outside of the method it was passed in</b>.
 */
public interface InputState {
  /**
   * Returns the current x position of the mouse cursor, in <b>ABSOLUTE COORDINATES, i.e. not relative to the component rectangle</b>.
   * <p>
   * To get the current x position of the mouse cursor relative to the component rectangle, use {@link #getMouseX(Component)}.
   *
   * @return The current x position of the mouse cursor, in pixels, in absolute coordinates, that is relative to the layer/screen lower-left corner.
   * @throws IllegalStateException If the UI system is not created.
   * @see #getMouseX(Component)
   */
  default double getMouseX() {
    return getMouseX(null);
  }
  
  /**
   * Returns the current x position of the mouse cursor, <b>RELATIVE to the component rectangle</b>.
   * <p>
   * To get the current x position of the mouse cursor in absolute coordinates, use {@link #getMouseX()}.
   *
   * @param component The component to which the coordinates should be relative to; must be non-null.
   * @return The current x position of the mouse cursor, in pixels, relative to the component rectangle lower-left corner.
   * @throws IllegalStateException If the UI system is not created.
   */
  double getMouseX(Component component);
  
  /**
   * Returns the current y position of the mouse cursor, in <b>ABSOLUTE COORDINATES, i.e. not relative to the component rectangle</b>.
   * <p>
   * To get the current y position of the mouse cursor relative to the component rectangle, use {@link #getMouseX(Component)}.
   *
   * @return The current y position of the mouse cursor, in pixels, in absolute coordinates, that is relative to the layer/screen lower-left corner.
   * @see #getMouseY(Component)
   */
  default double getMouseY() {
    return getMouseY(null);
  }
  
  /**
   * Returns the current y position of the mouse cursor, <b>RELATIVE to the component rectangle</b>.
   * <p>
   * To get the current y position of the mouse cursor in absolute coordinates, use {@link #getMouseX()}.
   *
   * @param component The component to which the coordinates should be relative to; must be non-null.
   * @return The current y position of the mouse cursor, in pixels, relative to the component rectangle lower-left corner.
   * @throws IllegalStateException If the UI system is not created.
   */
  double getMouseY(Component component);
  
  /**
   * Returns whether the specified keyboard key (which should be a field of {@link Key}), is currently pressed/down, or released/up.
   *
   * @param keycode The key of which to check the state; should be a field of {@link Key}.
   * @return Whether the key is currently pressed/down (true), or released/up (false).
   * @throws IllegalStateException If the UI system is not created.
   */
  boolean isKeyDown(int keycode);
  
  /**
   * Returns whether the specified mouse button (which should be a field of {@link Mouse}), is currently pressed/down, or released/up.
   *
   * @param button The mouse button of which to check the state; should be a field of {@link Mouse}.
   * @return Whether the button is currently pressed/down (true), or released/up (false).
   * @throws IllegalStateException If the UI system is not created.
   */
  boolean isMouseDown(int button);
}
