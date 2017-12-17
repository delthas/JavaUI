package fr.delthas.javaui;

import java.awt.*;

/**
 * Checkbox is a component class of the UI, that displays a checkbox and a single line of text and reacts to left mouse button clicks on its checkbox.
 * <p>
 * To retrieve events/state, you may set a {@link Checkbox.Listener} with {@link #setListener(Listener)}, that will be called when the checkbox is enabled and the checkbox is clicked with the left mouse button, and you may also check the state of the checkbox with {@link #isChecked()}.
 */
public final class Checkbox extends Component {
  private String text = "";
  private boolean down = false;
  private boolean checked = false;
  private Listener listener;
  
  /**
   * Creates a checkbox (enabled), with an empty text, that is not checked.
   */
  public Checkbox() {
  
  }
  
  /**
   * Creates a checkbox (enabled), with the specified text, that is not checked.
   *
   * @param text The text of the checkbox, to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public Checkbox(String text) {
    setText(text);
  }
  
  @Override
  protected void render(InputState inputState, Drawer drawer) {
    if (isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      drawer.setColor(Color.WHITE);
    } else {
      drawer.setColor(Color.GRAY);
    }
    drawer.rectangle(0, 0, getWidth(), getHeight()).draw();
    drawer.rectangle(1, 1, getWidth() - 2, getHeight() - 2).color(Color.BLACK).draw();
    double unit = getHeight() / 8;
    drawer.setColor(Color.WHITE);
    drawer.rectangle(getWidth() - 7 * unit, unit, 6 * unit, 6 * unit).color(Color.WHITE).draw();
    drawer.rectangle(getWidth() - 7 * unit + 1, unit + 1, 6 * unit - 2, 6 * unit - 2).color(Color.BLACK).draw();
    if (checked) {
      drawer.rectangle(getWidth() - 5 * unit, 3 * unit, 2 * unit, 2 * unit).color(Color.WHITE).draw();
    }
    drawer.text((getWidth() - unit) / 2, getHeight() / 2, text, Font.COMIC, 16).centered(true, true).color(down ? Color.WHITE : Color.LIGHT_GRAY).draw();
  }
  
  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down, long time) {
    if (button != Ui.MOUSE_LEFT) {
      return false;
    }
    if (down && isInBounds(x, y)) {
      if (!this.down) {
        this.down = true;
        checked = !checked;
        if (listener != null) {
          listener.checkboxPressed(this, x, y);
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
   * @return The text of the checkbox. No/empty text is returned as the empty string (""), not null.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Sets the text of this checkbox.
   *
   * @param text The text to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public void setText(String text) {
    this.text = text;
  }
  
  /**
   * @return true if the checkbox is checked.
   */
  public boolean isChecked() {
    return checked;
  }
  
  /**
   * Sets the text of this checkbox.
   *
   * @param checked true if the checkbox is to be set to the checked state, false otherwise.
   */
  public void setChecked(boolean checked) {
    this.checked = checked;
  }
  
  /**
   * Sets the listener of this checkbox. To remove the listener, pass null.
   *
   * @param listener The listener to be set, to listen for checkbox check events, or null to remove the listener.
   * @see Listener
   */
  public void setListener(Listener listener) {
    this.listener = listener;
  }
  
  /**
   * Listener is a listener to a checkbox, that will be called when the checkbox is enabled and pressed, to be set with {@link Checkbox#setListener(Listener)}.
   */
  @SuppressWarnings("InterfaceNeverImplemented")
  @FunctionalInterface
  public interface Listener {
    /**
     * Called when a checkbox on which the listener has been set, is enabled and pressed.
     *
     * @param checkbox The checkbox which originated the event.
     * @param x        The x position of the mouse relative to the checkbox component (<b>NOT</b> the checkbox inner checkbox), when it pressed the inner checkbox.
     * @param y        The y position of the mouse relative to the checkbox component (<b>NOT</b> the checkbox inner checkbox), when it pressed the inner checkbox.
     */
    void checkboxPressed(Checkbox checkbox, double x, double y);
  }
}
