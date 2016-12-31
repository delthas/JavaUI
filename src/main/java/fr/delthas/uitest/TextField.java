package fr.delthas.uitest;


import java.awt.*;
import java.util.EnumSet;
import java.util.function.Predicate;

public class TextField extends Component {
  private String hintText = "";
  private String text = "";
  private String drawnText = "";
  private float[] sizes = new float[8];
  private float[] metrics = new float[2];
  private Predicate<String> predicate;
  private int caretPosition;
  private int selectionStart = 0;
  private int selectionEnd = 0;
  private boolean selecting = false;
  private String hiddenCharacter;

  public TextField() {

  }

  public TextField(String text) {
    setText(text);
  }

  public void setHidden(String hiddenCharacter) {
    this.hiddenCharacter = hiddenCharacter;
    setText(text);
  }

  public void setHintText(String hintText) {
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
        drawer.fillRectangle(getWidth() / 2 - sizes[length(drawnText) - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2 + metrics[1] - (metrics[1] + metrics[0]) / 2, sizes[selection1 - 1] - (selection0 == 0 ? 0 : sizes[selection0 - 1]), metrics[0] - metrics[1], false);
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
      drawer.fillRectangle(position - 1, getHeight() / 2 + metrics[1] - (metrics[1] + metrics[0]) / 2, 1, metrics[0] - metrics[1], false);
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
  protected boolean pushChar(double x, double y, int codepoint, EnumSet<KeyModifier> mods) {
    if (!isInBounds(x, y)) {
      return false;
    }
    insert(new String(new int[]{codepoint}, 0, 1));
    return true;
  }

  @Override
  protected boolean pushKeyButton(double x, double y, int key, boolean down) {
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
    if (Ui.getUi().getCodepoint(key) == 'A' && (Ui.getUi().isKeyDown(Ui.KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(Ui.KEY_RIGHT_CONTROL))) {
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
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
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
  protected boolean pushMouseMove(double x, double y) {
    if (!selecting) {
      return false;
    }
    caretPosition = getCaretPositionFor(x);
    selectionEnd = caretPosition;
    return false;
  }

  public String getText() {
    return text;
  }

  public boolean setText(String text) {
    if (predicate != null && !predicate.test(text)) {
      return false;
    }
    this.text = text;
    drawnText = hiddenCharacter != null ? nTimes(length(text), hiddenCharacter) : text;
    if (length(drawnText) > sizes.length) {
      sizes = new float[Integer.max(sizes.length * 2, length(text))];
    }
    Ui.getUi().getTextWidth(drawnText, Font.COMIC, 16, sizes);
    Ui.getUi().getFontMetrics(Font.COMIC, 16, metrics);
    return true;
  }

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
