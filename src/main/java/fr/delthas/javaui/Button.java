package fr.delthas.javaui;

import java.awt.*;
import java.util.Objects;

/**
 * Button is a component class of the UI, that displays a single line of text and reacts to left mouse button clicks.
 * <p>
 * To use, specify a {@link Button.Listener} with {@link #setListener(Listener)}, that will be called when the button is enabled and is clicked with the left mouse button.
 *
 * @see Listener
 */
public final class Button extends Component {
  private String text = "";
  private boolean down = false;
  private Listener listener;
  /**
   * Creates a button (enabled), with an empty text.
   */
  public Button() {
  
  }
  
  /**
   * Creates a button (enabled), with the specified text.
   *
   * @param text The text of the button, to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public Button(String text) {
    setText(Objects.requireNonNull(text));
  }
  
  @Override
  protected void render(InputState inputState, Drawer drawer) {
    if (isEnabled() && isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      drawer.setColor(Color.WHITE);
    } else {
      drawer.setColor(Color.GRAY);
    }
    drawer.rectangle(0, 0, getWidth(), getHeight()).draw();
    drawer.rectangle(1, 1, getWidth() - 2, getHeight() - 2).color(Color.BLACK).draw();
    drawer.setColor(!isEnabled() ? Color.GRAY : down ? Color.WHITE : Color.LIGHT_GRAY);
    drawer.text(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16).centered(true, true).draw();
  }
  
  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down, long time) {
    if (button != Ui.MOUSE_LEFT) {
      return false;
    }
    if (isEnabled() && down && isInBounds(x, y)) {
      if (!this.down) {
        this.down = true;
        if (listener != null) {
          listener.buttonPressed(this, x, y);
        }
      }
      return true;
    }
    if (!down) {
      this.down = false;
    }
    return false;
  }
  
  /**
   * @return The text of the button. No/empty text is returned as the empty string (""), not null.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Sets the text of this button.
   *
   * @param text The text to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public void setText(String text) {
    this.text = Objects.requireNonNull(text);
  }
  
  /**
   * Sets the listener of this button. To remove the listener, pass null.
   *
   * @param listener The listener to be set, to listen for button press events, or null to remove the listener.
   * @see Listener
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }
  
  /**
   * Listener is a listener to a button, that will be called when the button is enabled and pressed, to be set with {@link Button#setListener(Listener)}.
   */
  @SuppressWarnings("InterfaceNeverImplemented")
  @FunctionalInterface
  public interface Listener {
    /**
     * Called when a button on which the listener has been set, is enabled and pressed.
     *
     * @param button The button which originated the event.
     * @param x      The x position of the mouse relative to the button, when it pressed the button.
     * @param y      The y position of the mouse relative to the button, when it pressed the button.
     */
    void buttonPressed(Button button, double x, double y);
  }
}
