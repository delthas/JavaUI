package fr.delthas.javaui;

import java.awt.*;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * TextField is a component class of the UI, that displays a single line of text that can be edited by the user.
 * <p>
 * For a component with a single, uneditable line of text, use {@link Label} instead.
 * <p>
 * {@link TextField} supports hint texts (see {@link #setHintText(String)}, hidden text (i.e. password fields) (see {@link #setHidden(String)}), and a predicate that can check, and listen to all updates to the text field text (see {@link #setPredicate(Predicate)}).
 *
 * @see Label
 * @see #setText(String)
 * @see #setPredicate(Predicate)
 */
public class TextField extends Component {
  private String hintText = "";
  private String text = "";
  private String drawnText = "";
  private float[] sizes = new float[8];
  private FontMetrics metrics;
  private Predicate<String> predicate;
  private int caretPosition;
  private int selectionStart = 0;
  private int selectionEnd = 0;
  private boolean selecting = false;
  private String hiddenCharacter;
  
  /**
   * Creates a text field (enabled), with an empty text, no hint text, that is not a password field.
   */
  public TextField() {
  
  }
  
  /**
   * Creates a text field (enabled), with the specified text, no hint text, that is not a password field.
   *
   * @param text The text to be set for this text field, cannot be null.
   */
  public TextField(String text) {
    setText(text);
  }
  
  /**
   * Sets the hidden string for this text field.
   * <p>
   * If it is not empty, each character in the text field will be hidden and replaced by the specified string; for example a typical hidden string can be the "*" character, which if set would turn a text field with "mypassword" into "**********".
   * <p>
   * If it is empty, then no character in this text field will be hidden and this won't be a "password" field.
   *
   * @param hiddenCharacter The hidden string to be set for this text field, cannot be null.
   */
  public void setHidden(String hiddenCharacter) {
    Objects.requireNonNull(hiddenCharacter);
    this.hiddenCharacter = hiddenCharacter;
    setText(text);
  }
  
  /**
   * Sets the hint text of the text field, that is a grey text that will be showed when no text is entered, that is not selectable and that will be removed when some text is entered.
   *
   * @param hintText The hint text to be set in the text field, must be non-null.
   */
  public void setHintText(String hintText) {
    Objects.requireNonNull(hintText);
    this.hintText = hintText;
  }
  
  @Override
  protected void render(InputState inputState, Drawer drawer) {
    if (isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      drawer.setColor(Color.WHITE);
    } else {
      drawer.setColor(Color.GRAY);
    }
    drawer.fillRectangle(0, 0, getWidth(), getHeight(), false);
    drawer.setColor(Color.BLACK);
    drawer.fillRectangle(1, 1, getWidth() - 2, getHeight() - 2, false);
    if (drawnText.isEmpty() && !isInBounds(inputState.getMouseX(), inputState.getMouseY())) {
      drawer.setColor(Color.DARK_GRAY);
      drawer.drawText(getWidth() / 2, getHeight() / 2, hintText, Font.COMIC, 16, true, true);
      return;
    }
    if (!drawnText.isEmpty()) {
      if (selectionEnd != selectionStart) {
        int selection0;
        int selection1;
        if (selectionStart > selectionEnd) {
          selection0 = selectionEnd;
          selection1 = selectionStart;
        } else {
          selection0 = selectionStart;
          selection1 = selectionEnd;
        }
        drawer.setColor(Color.WHITE);
        drawer.drawText(getWidth() / 2 - sizes[length(drawnText) - 1] / 2, getHeight() / 2, drawnText.substring(0, selection0), Font.COMIC, 16, false, true);
        drawer.setColor(Color.LIGHT_GRAY);
        drawer.fillRectangle(getWidth() / 2 - sizes[length(drawnText) - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2 + metrics.getDescent() - (metrics.getDescent() + metrics.getAscent()) / 2, sizes[selection1 - 1] - (selection0 == 0 ? 0 : sizes[selection0 - 1]), metrics.getAscent() - metrics.getDescent(), false);
        drawer.setColor(Color.BLACK);
        drawer.drawText(getWidth() / 2 - sizes[length(drawnText) - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2, drawnText.substring(selection0, selection1), Font.COMIC, 16, false, true);
        drawer.setColor(Color.WHITE);
        drawer.drawText(getWidth() / 2 - sizes[length(drawnText) - 1] / 2 + sizes[selection1 - 1], getHeight() / 2, drawnText.substring(selection1), Font.COMIC, 16, false, true);
      } else {
        drawer.setColor(Color.WHITE);
        drawer.drawText(getWidth() / 2, getHeight() / 2, drawnText, Font.COMIC, 16, true, true);
      }
    }
    if (isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      float position = (float) (drawnText.isEmpty() ? getWidth() / 2 : getWidth() / 2 - sizes[length(drawnText) - 1] / 2 + (caretPosition == 0 ? 0 : sizes[caretPosition - 1]));
      drawer.setColor(Color.GRAY);
      drawer.fillRectangle(position - 1, getHeight() / 2 + metrics.getDescent() - (metrics.getDescent() + metrics.getAscent()) / 2, 1, metrics.getAscent() - metrics.getDescent(), false);
    }
  }
  
  private boolean insert(String string) {
    int selection0 = selectionStart;
    int selection1 = selectionEnd;
    if (selectionStart > selectionEnd) {
      selection0 = selectionEnd;
      selection1 = selectionStart;
    }
    String newText = getText().substring(0, selection0) + string + getText().substring(selection1);
    if (!setText(newText)) {
      return false;
    }
    selecting = false;
    selectionStart = selectionEnd = caretPosition = selection0 + length(string);
    return true;
  }
  
  @Override
  protected boolean pushChar(double x, double y, String input, EnumSet<KeyModifier> mods, long time) {
    if (!isInBounds(x, y)) {
      return false;
    }
    insert(input);
    return true;
  }
  
  @Override
  protected boolean pushKeyButton(double x, double y, int key, boolean down, long time) {
    if (!down) {
      return false;
    }
    if (!isInBounds(x, y)) {
      return false;
    }
    int selection0 = selectionStart;
    int selection1 = selectionEnd;
    if (selectionStart > selectionEnd) {
      selection0 = selectionEnd;
      selection1 = selectionStart;
    }
    if (key == Ui.KEY_C && selectionStart != selectionEnd && (Ui.getUi().isKeyDown(Ui.KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(Ui.KEY_RIGHT_CONTROL))) {
      Ui.getUi().setClipboard(getText().substring(selection0, selection1));
      return true;
    }
    if (key == Ui.KEY_V && (Ui.getUi().isKeyDown(Ui.KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(Ui.KEY_RIGHT_CONTROL))) {
      insert(Ui.getUi().getClipboard());
      return true;
    }
    if (key == Key.KEY_A && (Ui.getUi().isKeyDown(Ui.KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(Ui.KEY_RIGHT_CONTROL))) {
      selectionStart = 0;
      selectionEnd = length(drawnText);
      selecting = false;
      return true;
    }
    if (key == Ui.KEY_BACKSPACE) {
      if (selection0 == selection1 && caretPosition > 0) {
        setText(getText().substring(0, caretPosition - 1) + getText().substring(caretPosition));
        caretPosition--;
      } else if (selection0 != selection1) {
        if (!insert("")) {
          return true;
        }
        caretPosition = selection0;
      }
    } else if (key == Ui.KEY_DELETE) {
      if (selection0 == selection1 && caretPosition < length(drawnText)) {
        setText(getText().substring(0, caretPosition) + getText().substring(caretPosition + 1));
      } else if (selection0 != selection1) {
        if (!insert("")) {
          return true;
        }
        caretPosition = selection0;
      }
    } else if (key == Ui.KEY_LEFT && caretPosition > 0) {
      caretPosition--;
    } else if (key == Ui.KEY_RIGHT && caretPosition < length(drawnText)) {
      caretPosition++;
    } else if (key == Ui.KEY_HOME) {
      caretPosition = 0;
    } else if (key == Ui.KEY_END) {
      caretPosition = length(drawnText);
    } else {
      return false;
    }
    selecting = false;
    selectionStart = selectionEnd = caretPosition;
    return true;
  }
  
  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down, long time) {
    if (button != Ui.MOUSE_LEFT) {
      return false;
    }
    if (down && isInBounds(x, y)) {
      caretPosition = getCaretPositionFor(x);
      selectionStart = caretPosition;
      selectionEnd = caretPosition;
      selecting = true;
      return true;
    }
    if (!down) {
      selecting = false;
      return false;
    }
    return false;
  }
  
  @Override
  protected boolean pushMouseMove(double x, double y, long time) {
    if (!selecting) {
      return false;
    }
    caretPosition = getCaretPositionFor(x);
    selectionEnd = caretPosition;
    return false;
  }
  
  /**
   * @return The text of the text field. No/empty text is returned as the empty string (""), not null.
   */
  public String getText() {
    return text;
  }
  
  /**
   * Sets the text of this text field.
   *
   * @param text The text to be set, must be non-null (use the empty string ("") instead if needed).
   * @return Whether the text has been changed (true), or if the set predicate rejected the text, and that thus it has not been changed (false).
   */
  public boolean setText(String text) {
    Objects.requireNonNull(text);
    if (predicate != null && !predicate.test(text)) {
      return false;
    }
    this.text = text;
    drawnText = hiddenCharacter != null ? nTimes(length(text), hiddenCharacter) : text;
    if (length(drawnText) > sizes.length) {
      sizes = new float[Integer.max(sizes.length * 2, length(text))];
    }
    sizes = Ui.getUi().getTextPositions(drawnText, Font.COMIC, 16);
    metrics = Ui.getUi().getFontMetrics(Font.COMIC, 16);
    return true;
  }
  
  /**
   * Sets the predicate that will check and listen to all updates on this text field.
   * <p>
   * The predicate will be checked against the input (actually against the new string that would be produced would the input be inserted into the text field), exactly every time the text is to be updated. <b>It must return true if the text successfully corresponds to the criteria, in which the case the text will effectively be updated with the passed text, and return false if the text does not verify the criteria, in which case the text will not be updated.</b>
   * <p>
   * This predicate is both a way to check all input and reject those that do not satisfy the criteria, and a listener to all text changes.
   * <p>
   * To remove the current predicate, pass null.
   *
   * @param predicate The predicate to be set, to check all changes on this text field, or null to remove the current predicate.
   */
  public void setPredicate(Predicate<String> predicate) {
    this.predicate = predicate;
  }
  
  private int getCaretPositionFor(double x) {
    if (drawnText.isEmpty()) {
      return 0;
    }
    if (x < sizes[0] / 2 + getWidth() / 2 - sizes[length(drawnText) - 1] / 2) {
      return 0;
    }
    for (int i = 1; i < length(drawnText); i++) {
      if (x < (sizes[i] + sizes[i - 1]) / 2 + getWidth() / 2 - sizes[length(drawnText) - 1] / 2) {
        return i;
      }
    }
    return length(drawnText);
  }
  
  private String nTimes(int n, String string) {
    StringBuilder sb = new StringBuilder(string.length() * n);
    for (int i = 0; i < n; i++) {
      sb.append(string);
    }
    return sb.toString();
  }
  
  private int length(String string) {
    return string.codePointCount(0, string.length());
  }
}
