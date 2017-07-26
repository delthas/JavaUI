package fr.delthas.javaui;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Ui is the singleton that represents the UI system, that is the system that handles window creation and destruction, input handling, drawing, and a stack of {@link Layer layers}.
 * <p>
 * For a general overview of the UI system, refer to the {@link fr.delthas.javaui} Javadoc.
 * <p>
 * <b>Before use of the input and render related methods, the UI MUST be explicitly created with one of the {@code create} methods. After use, that is when nothing is to be rendered anymore, and that the window is to be closed, it MUST be explicitly destroyed with {@link #destroy()}.</b>
 * <p>
 * To use, first create the UI system with one of the {@code create} methods, then push {@link Layer layers} onto the UI stack with {@link #push(Layer)}, then create and add components to the layers with {@link Layer#addComponent(double, double, double, double, Component)}. You can then call {@link #input()} and {@link #render()} regularly to make the UI system propagate inputs to its layers (which in turns propagates them to their components), and to make it render its layers, which in turn renders their components. Once done, <b>explicitly call the {@link #destroy()} method to destroy and free all the memory allocated by the UI system</b>. Some methods will fail and throw {@link IllegalStateException} if the UI system hasn't been created/has been destroyed (specified in their Javadoc).
 * <p>
 * You may create and destroy the UI system multiple times, <b>but all {@link Atlas atlases} and {@link Texture textures} become invalid when you call destroy the UI system, so you have to reupload {@link Image} to the GPU.</b>
 */
public final class Ui implements InputState, Key, Mouse {
  private static final Ui instance;
  
  static {
    instance = new Ui();
  }
  
  private Stack stack = new Stack();
  private Window window = new Window();
  private Set<Integer> keysState = new HashSet<>(20);
  private Set<Integer> mouseState = new HashSet<>(3);
  private boolean created;
  private double mouseX, mouseY;
  
  private Ui() {}
  
  /**
   * @return The width of the window, in pixels.
   * @throws IllegalStateException If the UI system is not created.
   */
  public static int getWidth() {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return Window.getWidth();
  }
  
  /**
   * @return The height of the window, in pixels.
   * @throws IllegalStateException If the UI system is not created.
   */
  public static int getHeight() {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return Window.getHeight();
  }
  
  /**
   * @return The unique instance/singleton of the UI system.
   */
  public static Ui getUi() {
    return instance;
  }
  
  /**
   * Initializes the UI system and creates a window the specified title and fullscreen property, with no window icon.
   * <p>
   * If the UI system is already created, this is a no-op.
   *
   * @param title      The title of the window to create, that may appear e.g. in the taskbar, must be non-null (use the empty string if needed).
   * @param fullscreen Whether the window will be fullscreen (exclusive fullscreen, true), or not (borderless window that has the same dimensiosn as the screen, false).
   */
  public void create(String title, boolean fullscreen) {
    if (created) {
      return;
    }
    created = true;
    window.create(title, null, fullscreen);
  }
  
  /**
   * Initializes the UI system and creates a window the specified title and in fullscreen (fullscreen exclusive) mode, with no window icon.
   * <p>
   * If the UI system is already created, this is a no-op.
   *
   * @param title The title of the window to create, that may appear e.g. in the taskbar, must be non-null (use the empty strng if needed).
   */
  public void create(String title) {
    if (created) {
      return;
    }
    created = true;
    window.create(title, null, true);
  }
  
  /**
   * Initializes the UI system and creates a window the specified title and fullscreen property, and the specified window icon.
   * <p>
   * If the UI system is already created, this is a no-op.
   *
   * @param title      The title of the window to create, that may appear e.g. in the taskbar, must be non-null (use the empty strng if needed).
   * @param fullscreen Whether the window will be fullscreen (exclusive fullscreen, true), or not (borderless window that has the same dimensiosn as the screen, false).
   * @param image      The image representing the window icon to be put (that will e.g. appear in the taskbar, should typically be 64x64), can be null, in which case no icon will be set.
   */
  public void create(String title, Image image, boolean fullscreen) {
    if (created) {
      return;
    }
    created = true;
    window.create(title, image, fullscreen);
  }
  
  /**
   * Initializes the UI system and creates a window the specified title and in fullscreen (fullscreen exclusive) mode, and the specified window icon.
   * <p>
   * If the UI system is already created, this is a no-op.
   *
   * @param title The title of the window to create, that may appear e.g. in the taskbar.
   * @param image The image representing the window icon to be put (that will e.g. appear in the taskbar, should typically be 64x64), can be null, in which case no icon will be set.
   */
  public void create(String title, Image image) {
    if (created) {
      return;
    }
    created = true;
    window.create(title, image, true);
  }
  
  /**
   * Destroys the UI system, closes the window, and frees up all memory allocated by the UI system.
   * <p>
   * <b>All {@link Texture} and {@link Atlas} objects become invalid and must be created again, were the {@link Ui} to be created again.</b>
   * <p>
   * If the UI system is already destroyed, this is a no-op.
   */
  public void destroy() {
    if (!created) {
      return;
    }
    created = false;
    window.destroy();
    while (stack.pop() != null) {
    }
  }
  
  /**
   * Does all the input processing, by propagating all events received and buffered since the last call to {@link #input()}, to all layers and components.
   * <p>
   * This method is blocking for all the duration of the input event propagation and all the input event propagation will happen in the thread that called this method, e.g. this method/stack frame will always appear in the stack of the components input callbacks methods calls.
   *
   * @throws IllegalStateException If the UI system is not created.
   */
  public void input() {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    window.input();
  }
  
  /**
   * Renders the layer stack, by rendering all layers from the uppermost opaque layer in the layer stack to the uppermost layer in the stack, which in turn will call their components {@link Component#render(InputState, Drawer)} method, then waits for the V-SYNC.
   * <p>
   * This method is blocking for all the duration of the rendering and all the rendering will happen in the thread that called this method, and is also blocking until the V-SYNC, that is until the rendering result is sent to the screen.
   *
   * @throws IllegalStateException If the UI system is not created.
   */
  public void render() {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    stack.render(this, window);
    window.flip();
  }
  
  /**
   * Pushes a layer onto the layer stack, so that is is at the top of the layer stack.
   * <p>
   * There is no technical reason to prevent a layer from being present multiple times in the stack, so if the layer is already in the stack it will be added again, instead of removed and added back again.
   *
   * @param layer The layer to push on the layer stack, must be non-null.
   */
  public void push(Layer layer) {
    stack.push(layer);
  }
  
  /**
   * Pops the topmost layer from the layer stack, returning it, or returning null if the stack was already empty before trying to pop it.
   *
   * @return The layer that was at the top of the stack, that was removed from the stack, or null if the stack was empty.
   * @see #push(Layer)
   * @see #top()
   */
  public Layer pop() {
    return stack.pop();
  }
  
  /**
   * @return The topmost element of the layer stack without removing it from the stack, or null if the stack is empty.
   * @see #pop()
   */
  public Layer top() {
    return stack.top();
  }
  
  void pushMouseMove(double x, double y, long time) {
    mouseX = x;
    mouseY = y;
    stack.pushMouseMove(x, y, time);
  }
  
  void pushMouseButton(int button, boolean down, long time) {
    if (down) {
      mouseState.add(button);
    } else {
      mouseState.remove(button);
    }
    stack.pushMouseButton(mouseX, mouseY, button, down, time);
  }
  
  void pushMouseScroll(int scroll, long time) {
    stack.pushMouseScroll(mouseX, mouseY, scroll, time);
  }
  
  void pushKeyButton(int key, boolean down, long time) {
    if (down) {
      keysState.add(key);
    } else {
      keysState.remove(key);
    }
    stack.pushKeyButton(mouseX, mouseY, key, down, time);
  }
  
  void pushChar(String input, EnumSet<KeyModifier> mods, long time) {
    stack.pushChar(mouseX, mouseY, input, mods, time);
  }
  
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
   * @throws IllegalStateException If the UI system is not created.
   */
  public float getLineHeight(Font font, float size) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getLineHeight(font, size);
  }
  
  /**
   * Returns the font metrics for a specified font and font size (ascent, descent, line gap, line height).
   * <p>
   * For a detailed description of the metrics, see the {@link FontMetrics} class and functions Javadoc.
   *
   * @param font The font for which to get the metrics, must be non-null.
   * @param size The font size for which to get the metrics, in pt.
   * @return The font metrics for the specified font and font size.
   * @throws IllegalStateException If the UI system is not created.
   */
  public FontMetrics getFontMetrics(Font font, float size) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getFontMetrics(font, size);
  }
  
  /**
   * Returns a user-friendly, locale-specific name for the specified key, which must be a field of {@link Key}.
   * <p>
   * The returned name (as a String) <b>SHOULD NOT be used as an identifier for the key, since it depends on the user locale, and might be different from computer to computer, OS to OS, ...</b> Its main use case is to show a user-friendly key name to the user for the key the user pressed (for example for use in a "controls" panel).
   *
   * @param key The key identifier for the key to get the name of, must be a field of {@link Key}.
   * @return The key name of the key the user pressed, or the empty string ("") if no appropriate name for the key is found.
   * @throws IllegalStateException If the UI system is not created.
   */
  public String getKeyname(int key) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getKeyname(key);
  }
  
  /**
   * Returns the width, in pixels, of the specified text, if it were to be drawn with the specified font and font size, that is the "x" length from the start position of the first character of the text, to the start position for the (hypothetical) next character after the last character of the text.
   *
   * @param text The text to get the width of, must be non-null (the width of the empty string is always 0).
   * @param font The font for which to get the width of the text, must be non-null.
   * @param size The font size for which to get the width of the text, in pt.
   * @return The width of the specified text, in the specified font and font size, in pixels.
   * @throws IllegalStateException If the UI system is not created.
   */
  public float getTextWidth(String text, Font font, float size) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getTextWidth(text, font, size);
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
   * @throws IllegalStateException If the UI system is not created.
   */
  public float[] getTextPositions(String text, Font font, float size) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getTextPositions(text, font, size);
  }
  
  /**
   * Returns the current clipboard of the system.
   * <p>
   * This is the global clipboard of the system, not a specific library-specific clipboard.
   *
   * @return The current clipboard of the system, as a non-null string (if there's nothing in the clipboard, the empty string ("") will be returned).
   * @throws IllegalStateException If the UI system is not created.
   */
  public String getClipboard() {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return window.getClipboard();
  }
  
  /**
   * Sets the clipboard of the system.
   * <p>
   * This sets the global clipboard of the system, not a specific library-specific clipboard.
   *
   * @param clipboard The string to set as the clipboard of the system, must be non-null (to set it to the empty value, use the empty string ("")).
   * @throws IllegalStateException If the UI system is not created.
   */
  public void setClipboard(String clipboard) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    window.setClipboard(clipboard);
  }
  
  /**
   * Sets the cursor image to be used when the mouse cursor is/hovers over the window.
   * <p>
   * The cursor is specified as an image, and as an offset from the lower-left corner of the image, that represents the actual location of the mouse cursor. For example, for an "arrow-like" cursor, the offset would be such that it is exactly the end of the arrow.
   * <p>
   * This setting will be reset upon UI system {@link #destroy() destruction}.
   *
   * @param image   The image to be set as the mouse cursor.
   * @param xOffset The x offset from the lower-left corner of the image, to the actual mouse cursor position.
   * @param yOffset The y offset from the lower-left corner of the image, to the actual mouse cursor position.
   * @throws IllegalStateException If the UI system is not created.
   */
  public void setCursor(Image image, int xOffset, int yOffset) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    window.setCursor(image, xOffset, yOffset);
  }
  
  @Override
  public double getMouseX(Component component) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return mouseX - (component == null ? 0 : component.getX());
  }
  
  @Override
  public double getMouseY(Component component) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return mouseY - (component == null ? 0 : component.getY());
  }
  
  @Override
  public boolean isKeyDown(int scancode) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return keysState.contains(scancode);
  }
  
  @Override
  public boolean isMouseDown(int button) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    return mouseState.contains(button);
  }
  
  Window getWindow() {
    return window;
  }
  
  /**
   * Sets (immediately) whether the current window should be visible (true) or hidden (false).
   * <p>
   * This setting will be reset upon UI system {@link #destroy() destruction}.
   *
   * @param visible Whether the current window should be visible (true) or hidden (false).
   * @throws IllegalStateException If the UI system is not created.
   */
  public void setVisible(boolean visible) {
    if (!instance.created) {
      throw new IllegalStateException("The UI system isn't created!");
    }
    window.setVisible(visible);
  }
}
