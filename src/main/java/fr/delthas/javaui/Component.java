package fr.delthas.javaui;

import java.util.EnumSet;

/**
 * Component is the base class for all components of the UI system. All components to be put in the UI system (to be drawn and receive and events) are subclasses of this class.
 * <p>
 * For a general overview of the UI system, refer to the {@link fr.delthas.javaui} Javadoc.
 * <p>
 * A component is located in a {@link Layer}, itself located within a stack of layers. A component is represented in the UI system as a rectangle, as such it has x, and y properties that represent the position of the lower-left corner of its rectangle position (relative to the lower-left position of the layer/screen, which are at (0,0)), and width and height properties. Additionally it can be enabled or disabled, see {@link #setEnabled(boolean)}.
 * <p>
 * To create a UI component class, such as a Button, subclass this class and override the input methods you need to listen to, (for example, for a button, {@link #pushMouseButton(double, double, int, boolean, long)}, add your logic in it, and specify how to render it by overriding the {@link #render(InputState, Drawer)} method.
 * <p>
 * To add an UI component to a layer, call {@link Layer#addComponent(Component)}, or {@link Layer#addComponent(double, double, double, double, Component)}. A component isn't valid if it isn't attached to a {@link Layer}, which means methods related to the component rectangle would throw {@link IllegalStateException}.
 *
 * @see Ui
 * @see Layer
 * @see Drawer
 */
public class Component {
  private Layer layer;
  private double x;
  private double y;
  private double width;
  private double height;
  private boolean enabled = true;
  
  void reset(Layer layer, double x, double y, double width, double height) {
    this.layer = layer;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    added();
  }
  
  void removeFromLayer() {
    removed(layer, x, y, width, height);
    layer = null;
  }
  
  /**
   * Called after this component is added to a layer.
   * <p>
   * This happens when one of the {@link Layer#addComponent(double, double, double, double, Component)} methods are called, right after its position and other information are set.
   * <p>
   * You may use {@link #getX()}, {@link #getY()}, {@link #getWidth()}, {@link #getHeight()}, {@link #getLayer()} to get information regarding the position, size, and layer onto which it was added.
   */
  protected void added() {}
  
  /**
   * Called after this component is removed from a layer.
   * <p>
   * This happens when the {@link Layer#removeComponent(Component)} method is called, right after its position and other information <b>are UNSET</b>.
   * <p>
   * The usual getter methods ({@link #getX()}, {@link #getY()}, {@link #getWidth()}, {@link #getHeight()}, {@link #getLayer()}) will <b>NOT work</b> since the component has already been removed at the time of the method call.
   * <p>
   * You may call one of the {@link Layer#addComponent(double, double, double, double, Component)} methods on this component to add this component back if wanted.
   *
   * @param layer  The layer containing this compnent before the component was removed, as would have been returned by {@link #getLayer()}.
   * @param x      The x position before the component was removed, as would have been returned by {@link #getX()}.
   * @param y      The y position before the component was removed, as would have been returned by {@link #getY()}.
   * @param width  The width before the component was removed, as would have been returned by {@link #getWidth()}.
   * @param height The height before the component was removed, as would have been returned by {@link #getHeight()}.
   */
  protected void removed(Layer layer, double x, double y, double width, double height) {}
  
  /**
   * Called when a mouse move "event" propagates to this component.
   * <p>
   * This happens when the mouse is moved and the event hasn't been consumed by any layer in front of the layer of this component, and hasn't been consumed by other components in this layer (the order of the propagation of the event in the components in a same layer is undefined).
   * <p>
   * <b>This method will be called regardless of whether the mouse cursor is in the bounds (in the component rectangle) of the component. You may check yourself if it is the case, by using {@link #isInBounds(double, double)}.</b>
   * <p>
   * Your component should consume this event if other components shouldn't receive this event after this component. To consume this event, return true instead of false. The default implementation is to <b>not</b> consume the event, so if a component doesn't need to capture mouse move events, it probably doesn't need an override for this method.
   * <p>
   * The mouse cursor coordinates are relative to the lower-left corner of the component rectangle, so they may be negative or greater than the component width and height.
   * <p>
   * The time of the event is a reasonable approximation ({@literal <}1ms) of the time at which the event was received, as if obtained by executing {@code time = System.nanoTime()} on event receiving; for example you may obtain the time in seconds since the event happened, by running {@code timeDeltaSeconds = (System.nanoTime() - time) / 1e9}.
   *
   * @param x    The new x position of the mouse cursor after it has moved, relative to the lower-left corner of this component rectangle.
   * @param y    The new y position of the mouse cursor after it has moved, relative to the lower-left corner of this component rectangle.
   * @param time The time at which the mouse has been moved, as if obtained by executing {@code time = System.nanoTime()} on event receiving.
   * @return true if this event should be consumed.
   */
  @SuppressWarnings("SameReturnValue")
  protected boolean pushMouseMove(double x, double y, long time) {
    return false;
  }
  
  /**
   * Called when a mouse button "event" propagates to this component.
   * <p>
   * This happens when a mouse button is pressed or released and the event hasn't been consumed by any layer in front of the layer of this component, and hasn't been consumed by other components in this layer (the order of the propagation of the event in the components in a same layer is undefined).
   * <p>
   * <b>This method will be called regardless of whether the mouse cursor at the time of the mouse button press or release is in the bounds (in the component rectangle) of the component. You may check yourself if it is the case, by using {@link #isInBounds(double, double)}.</b>
   * <p>
   * Your component should consume this event if other components shouldn't receive this event after this component. To consume this event, return true instead of false. The default implementation is to <b>not</b> consume the event, so if a component doesn't need to capture mouse button events, it probably doesn't need an override for this method.
   * <p>
   * The mouse cursor coordinates are relative to the lower-left corner of the component rectangle, so they may be negative or greater than the component width and height.
   * <p>
   * The time of the event is a reasonable approximation ({@literal <}1ms) of the time at which the event was received, as if obtained by executing {@code time = System.nanoTime()} on event receiving; for example you may obtain the time in seconds since the event happened, by running {@code timeDeltaSeconds = (System.nanoTime() - time) / 1e9}.
   *
   * @param x      The x position of the mouse cursor at the time of the mouse button press or release, relative to the lower-left corner of this component rectangle.
   * @param y      The y position of the mouse cursor at the time of the mouse button press or release, relative to the lower-left corner of this component rectangle.
   * @param button The identifier for the mouse button that has been pressed or released, to be checked for equality with the values in {@link Mouse}.
   * @param down   true if the mouse button was pressed, false if it was released.
   * @param time   The time at which the mouse button has been pressed or released, as if obtained by executing {@code time = System.nanoTime()} on event receiving.
   * @return true if this event should be consumed.
   */
  @SuppressWarnings("SameReturnValue")
  protected boolean pushMouseButton(double x, double y, int button, boolean down, long time) {
    return false;
  }
  
  /**
   * Called when a keyboard key "event" propagates to this component.
   * <p>
   * This callback provides a low-level way to capture raw key pressesn and should not be used for text processing components. For text input, {@link #pushChar(double, double, String, EnumSet, long)} should be used instead, as it provides a high-level callback to handle text input. <b>The keyboard key identifier specifies a LAYOUT-INDEPENDENT, physical key location on the keyboard, and the values in {@link Key} are named after a stands US keyboard layout (QWERTY). For example, the usual physical key used to press forward in FPS games, which is W for a standard US keyboard layout (QWERTY), and Z for a standard French keyboard layout (AZERTY), will always be {@link Key#KEY_W}, regardless of the current keyboard layout of the system.</b>
   * <p>
   * This happens when a keyboard key is pressed or released and the event hasn't been consumed by any layer in front of the layer of this component, and hasn't been consumed by other components in this layer (the order of the propagation of the event in the components in a same layer is undefined).
   * <p>
   * <b>This method will be called regardless of whether the mouse cursor at the time of the keyboard key press or release is in the bounds (in the component rectangle) of the component. You may check yourself if it is the case, by using {@link #isInBounds(double, double)}.</b>
   * <p>
   * Your component should consume this event if other components shouldn't receive this event after this component. To consume this event, return true instead of false. The default implementation is to <b>not</b> consume the event, so if a component doesn't need to capture keyboard key events, it probably doesn't need an override for this method.
   * <p>
   * The mouse cursor coordinates are relative to the lower-left corner of the component rectangle, so they may be negative or greater than the component width and height.
   * <p>
   * The time of the event is a reasonable approximation ({@literal <}1ms) of the time at which the event was received, as if obtained by executing {@code time = System.nanoTime()} on event receiving; for example you may obtain the time in seconds since the event happened, by running {@code timeDeltaSeconds = (System.nanoTime() - time) / 1e9}.
   *
   * @param x    The x position of the mouse cursor at the time of the keyboard key press or release, relative to the lower-left corner of this component rectangle.
   * @param y    The y position of the mouse cursor at the time of the keyboard key press or release, relative to the lower-left corner of this component rectangle.
   * @param key  The layout-independent identifier for the physical keyboard key that has been pressed or released, to be checked for equality with the values in {@link Key}.
   * @param down true if the keyboard key was pressed, false if it was released.
   * @param time The time at which the keyboard key has been pressed or released, as if obtained by executing {@code time = System.nanoTime()} on event receiving.
   * @return true if this event should be consumed.
   */
  @SuppressWarnings("SameReturnValue")
  protected boolean pushKeyButton(double x, double y, int key, boolean down, long time) {
    return false;
  }
  
  /**
   * Called when a mouse scroll "event" propagates to this component.
   * <p>
   * The {@code scroll} integer parameter represents how much and in which direction the mouse scroll was scrolled. <b>If the user scrolls UPWARDS, then the value will be POSITIVE; if the user scrolls DOWNWARDS, then the value will be NEGATIVE.</b> Most of the time the value will be 1 or -1, but it may be larger, e.g. a value of 5 means the user would have scrolled 5 "units" in a single event.
   * <p>
   * This happens when the mouse scroll is scrolled and the event hasn't been consumed by any layer in front of the layer of this component, and hasn't been consumed by other components in this layer (the order of the propagation of the event in the components in a same layer is undefined).
   * <p>
   * <b>This method will be called regardless of whether the mouse cursor at the time of the mouse scroll is in the bounds (in the component rectangle) of the component. You may check yourself if it is the case, by using {@link #isInBounds(double, double)}.</b>
   * <p>
   * Your component should consume this event if other components shouldn't receive this event after this component. To consume this event, return true instead of false. The default implementation is to <b>not</b> consume the event, so if a component doesn't need to capture mouse scroll events, it probably doesn't need an override for this method.
   * <p>
   * The mouse cursor coordinates are relative to the lower-left corner of the component rectangle, so they may be negative or greater than the component width and height.
   * <p>
   * The time of the event is a reasonable approximation ({@literal <}1ms) of the time at which the event was received, as if obtained by executing {@code time = System.nanoTime()} on event receiving; for example you may obtain the time in seconds since the event happened, by running {@code timeDeltaSeconds = (System.nanoTime() - time) / 1e9}.
   *
   * @param x      The x position of the mouse cursor at the time of the keyboard key press or release, relative to the lower-left corner of this component rectangle.
   * @param y      The y position of the mouse cursor at the time of the keyboard key press or release, relative to the lower-left corner of this component rectangle.
   * @param scroll The amount of scroll received for this event.
   * @param time   The time at which the keyboard key has been pressed or released, as if obtained by executing {@code time = System.nanoTime()} on event receiving.
   * @return true if this event should be consumed.
   */
  @SuppressWarnings("SameReturnValue")
  protected boolean pushMouseScroll(double x, double y, int scroll, long time) {
    return false;
  }
  
  /**
   * Called when a character input "event" propagates to this component.
   * <p>
   * This means a character is input as if in a text field. That is, this callback provides a high-level way to capture text, whereas {@link #pushKeyButton(double, double, int, boolean, long)} captures low-level input of physical key presses. This method should be used for components that handle text inputting, such as a text field.
   * <p>
   * <b>Note that when a simple key, such as R, is pressed, both a {@link #pushKeyButton(double, double, int, boolean, long)} AND a {@link #pushChar(double, double, String, EnumSet, long)} event will be sent.</b>
   * <p>
   * The event contains a String that stores exactly one UTF-8 codepoint represneting the input character, ie {@code input.codePointCount(0, input.length()) == 1}, but <b>note that it may correspond to several Java "char"s, ie {@code input.length()} might be greather than 1</b>. The event also contains a set of all modifiers that were applied to the input, to lead to this codepoint; for example if the user inputs a capital A by pressing shift and the A keyboard key, the set would contain {@link KeyModifier#SHIFT}.
   * <p>
   * The method is called when a character is input is and the event hasn't been consumed by any layer in front of the layer of this component, and hasn't been consumed by other components in this layer (the order of the propagation of the event in the components in a same layer is undefined).
   * <p>
   * <b>This method will be called regardless of whether the mouse cursor at the time of the character input is in the bounds (in the component rectangle) of the component. You may check yourself if it is the case, by using {@link #isInBounds(double, double)}.</b>
   * <p>
   * Your component should consume this event if other components shouldn't receive this event after this component. To consume this event, return true instead of false. The default implementation is to <b>not</b> consume the event, so if a component doesn't need to capture character input events, it probably doesn't need an override for this method.
   * <p>
   * The mouse cursor coordinates are relative to the lower-left corner of the component rectangle, so they may be negative or greater than the component width and height.
   * <p>
   * The time of the event is a reasonable approximation ({@literal <}1ms) of the time at which the event was received, as if obtained by executing {@code time = System.nanoTime()} on event receiving; for example you may obtain the time in seconds since the event happened, by running {@code timeDeltaSeconds = (System.nanoTime() - time) / 1e9}.
   *
   * @param x     The x position of the mouse cursor at the time of the character input, relative to the lower-left corner of this component rectangle.
   * @param y     The y position of the mouse cursor at the time of the character input, relative to the lower-left corner of this component rectangle.
   * @param input The string that stores the UTF-8 codepoint that has been input.
   * @param mods  The set of key modifiers that modified the input of the character, such as Shift, to be checked for equality with the values in {@link KeyModifier}.
   * @param time  The time at which the character has been input, as if obtained by executing {@code time = System.nanoTime()} on event receiving.
   * @return true if this event should be consumed.
   * @see #pushKeyButton(double, double, int, boolean, long)
   */
  @SuppressWarnings("SameReturnValue")
  protected boolean pushChar(double x, double y, String input, EnumSet<KeyModifier> mods, long time) {
    return false;
  }
  
  /**
   * Called by the UI render system when your component is to be rendered to the screen.
   * <p>
   * This method is only called if the component has been added in a {@link Layer}, itself pushed to the UI, and no layer above this layer is opaque.
   * <p>
   * To help render the component, an {@link InputState} representing the state of the input system (mouse and keyboard) at the time of the last input poll before the render, is passed.
   * <p>
   * To draw the component, call the various methods of the {@link Drawer} object. <b>All coordinates passed to the drawer ARE RELATIVE TO THE LOWER-LEFT CORNER, OR THE CENTER, of the component rectangle</b>, depending on which method of drawer you call. They are not absolute coordinates/relative to the lower-left corner of the screen.
   *
   * @param inputState The state of the input system at the time of the last input poll before the render.
   * @param drawer     The drawer on which to draw the component.
   */
  @SuppressWarnings("EmptyMethod")
  protected void render(InputState inputState, Drawer drawer) {
  
  }
  
  /**
   * Returns whether the component is enabled, or disabled.
   * <p>
   * In the UI system, a disabled component means that it should still be rendered, but that it should generally not interact with the user, or capture and consume input events. The exact meaning of a disabled component is component class specific, but for example a disabled button should appear "visually disabled" and should not react to mouse button clicks.
   *
   * @return true if the component is enabled.
   */
  public boolean isEnabled() {
    return enabled;
  }
  
  /**
   * Sets whether the component should be enabled, or disabled.
   * <p>
   * In the UI system, a disabled component means that it should still be rendered, but that it should generally not interact with the user, or capture and consume input events. The exact meaning of a disabled component is component class specific, but for example a disabled button should appear "visually disabled" and should not react to mouse button clicks.
   *
   * @param enabled true if the component to be enabled, false otherwise.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  /**
   * @return The {@link Layer} in which this component has been added, or null if it isn't currently added to a component.
   */
  public Layer getLayer() {
    return layer;
  }
  
  /**
   * @return The x position of the lower-left corner of the component rectangle, in absolute coordinates/relative to the lower-left corner of the screen.
   * @throws IllegalStateException If the component isn't currently attached to a layer.
   */
  public double getX() {
    if (layer == null) {
      throw new IllegalStateException("The component isn't currently attached to a layer.");
    }
    return x;
  }
  
  /**
   * @return The y position of the lower-left corner of the component rectangle, in absolute coordinates/relative to the lower-left corner of the screen.
   * @throws IllegalStateException If the component isn't currently attached to a layer.
   */
  public double getY() {
    if (layer == null) {
      throw new IllegalStateException("The component isn't currently attached to a layer.");
    }
    return y;
  }
  
  /**
   * @return The width of the component rectangle.
   * @throws IllegalStateException If the component isn't currently attached to a layer.
   */
  public double getWidth() {
    if (layer == null) {
      throw new IllegalStateException("The component isn't currently attached to a layer.");
    }
    return width;
  }
  
  /**
   * @return The height of the component rectangle.
   * @throws IllegalStateException If the component isn't currently attached to a layer.
   */
  public double getHeight() {
    if (layer == null) {
      throw new IllegalStateException("The component isn't currently attached to a layer.");
    }
    return height;
  }
  
  /**
   * Returns whether the specified point, relative to the component rectangle, is in the component rectangle bounds.
   * <p>
   * This utility method may be used for example to ignore events for which the mouse position isn't in the component rectangle bounds.
   *
   * @param x The x position of the point, <b>RELATIVE to the component rectangle</b>.
   * @param y The y position of the point, <b>RELATIVE to the component rectangle</b>.
   * @return true if the point is in the component rectangle bounds, false otherwise.
   * @throws IllegalStateException If the component isn't currently attached to a layer.
   */
  protected final boolean isInBounds(double x, double y) {
    if (layer == null) {
      throw new IllegalStateException("The component isn't currently attached to a layer.");
    }
    return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
  }
}
