package fr.delthas.javaui;

import java.awt.*;

/**
 * Label is a component class of the UI, that displays a single line of text, but does not react to user input.
 * <p>
 * To use, construct a label with some text (or the default empty string), and set it again as needed with {@link #setText(String)}.
 */
public final class Label extends Component {
  private String text = "";
  
  /**
   * Creates a label (enabled), with an empty text.
   */
  public Label() {
  
  }
  
  /**
   * Creates a label (enabled), with the specified text.
   *
   * @param text The text of the label, to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public Label(String text) {
    setText(text);
  }
  
  @Override
  protected void render(InputState inputState, Drawer drawer) {
    drawer.setColor(Color.WHITE);
    drawer.fillRectangle(0, 0, getWidth(), getHeight(), false);
    drawer.setColor(Color.BLACK);
    drawer.fillRectangle(1, 1, getWidth() - 2, getHeight() - 2, false);
    drawer.setColor(Color.WHITE);
    drawer.drawText(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
  }
  
  /**
   * @return The text of the label. No/empty text is returned as the empty string (""), not null.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Sets the text of this label.
   *
   * @param text The text to be set, cannot be null (use the empty string ("") instead if needed).
   */
  public void setText(String text) {
    this.text = text;
  }
}
