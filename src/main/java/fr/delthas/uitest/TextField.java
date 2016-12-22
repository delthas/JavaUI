package fr.delthas.uitest;

import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Predicate;

public class TextField extends Component {
  private String text = "";
  private float[] sizes = new float[8];
  private float[] metrics = new float[2];
  private Predicate<String> listener;
  private int caretPosition;
  private int selectionStart = 0;
  private int selectionEnd = 0;
  private boolean selecting = false;

  public TextField() {

  }

  public TextField(String text) {
    setText(text);
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
    if (!text.isEmpty()) {
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
        drawer.drawText(getWidth() / 2 - sizes[text.length() - 1] / 2, getHeight() / 2, text.substring(0, selection0), Font.COMIC, 16, false, true);
        drawer.setColor(Color.LIGHT_GRAY);
        drawer.fillRectangle(getWidth() / 2 - sizes[text.length() - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2 + metrics[1] - (metrics[1] + metrics[0]) / 2, sizes[selection1 - 1] - (selection0 == 0 ? 0 : sizes[selection0 - 1]), metrics[0] - metrics[1], false);
        drawer.setColor(Color.BLACK);
        drawer.drawText(getWidth() / 2 - sizes[text.length() - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2, text.substring(selection0, selection1), Font.COMIC, 16, false, true);
        drawer.setColor(Color.WHITE);
        drawer.drawText(getWidth() / 2 - sizes[text.length() - 1] / 2 + sizes[selection1 - 1], getHeight() / 2, text.substring(selection1), Font.COMIC, 16, false, true);
      } else {
        drawer.setColor(Color.WHITE);
        drawer.drawText(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
      }
    }
    if (isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      float position = (float) (text.isEmpty() ? getWidth() / 2 : getWidth() / 2 - sizes[text.length() - 1] / 2 + (caretPosition == 0 ? 0 : sizes[caretPosition - 1]));
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
    selectionStart = selectionEnd = caretPosition = selection0 + string.length();
    return true;
  }

  @Override
  protected boolean pushChar(double x, double y, int codepoint, int mods) {
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
    if (key == GLFW.GLFW_KEY_C && selectionStart != selectionEnd && (Ui.getUi().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))) {
      Ui.getUi().setClipboard(getText().substring(selection0, selection1));
      return true;
    }
    if (key == GLFW.GLFW_KEY_V && (Ui.getUi().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || Ui.getUi().isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL))) {
      insert(Ui.getUi().getClipboard());
      return true;
    }
    if (key == GLFW.GLFW_KEY_BACKSPACE) {
      if (selection0 == selection1 && caretPosition > 0) {
        setText(getText().substring(0, caretPosition - 1) + getText().substring(caretPosition));
        caretPosition--;
      } else if (selection0 != selection1) {
        if (!insert("")) {
          return true;
        }
        caretPosition = selection0;
      }
    } else if (key == GLFW.GLFW_KEY_DELETE) {
      if (selection0 == selection1 && caretPosition < text.length()) {
        setText(getText().substring(0, caretPosition) + getText().substring(caretPosition + 1));
      } else if (selection0 != selection1) {
        if (!insert("")) {
          return true;
        }
        caretPosition = selection0;
      }
    } else if (key == GLFW.GLFW_KEY_LEFT && caretPosition > 0) {
      caretPosition--;
    } else if (key == GLFW.GLFW_KEY_RIGHT && caretPosition < text.length()) {
      caretPosition++;
    } else if (key == GLFW.GLFW_KEY_HOME) {
      caretPosition = 0;
    } else if (key == GLFW.GLFW_KEY_END) {
      caretPosition = text.length();
    } else {
      return false;
    }
    selecting = false;
    selectionStart = selectionEnd = caretPosition;
    return true;
  }

  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    if (button != 0) {
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
    if (this.text.equals(text)) {
      return true;
    }
    if (listener != null && !listener.test(text)) {
      return false;
    }
    this.text = text;
    if (text.length() > sizes.length) {
      sizes = new float[Integer.max(sizes.length * 2, text.length())];
    }
    Ui.getUi().getTextWidth(text, Font.COMIC, 16, sizes);
    Ui.getUi().getFontMetrics(Font.COMIC, 16, metrics);
    return true;
  }

  public void setListener(Predicate<String> listener) {
    this.listener = listener;
  }

  private int getCaretPositionFor(double x) {
    if (text.isEmpty()) {
      return 0;
    }
    if (x < sizes[0] / 2 + getWidth() / 2 - sizes[text.length() - 1] / 2) {
      return 0;
    }
    for (int i = 1; i < text.length(); i++) {
      if (x < (sizes[i] + sizes[i - 1]) / 2 + getWidth() / 2 - sizes[text.length() - 1] / 2) {
        return i;
      }
    }
    return text.length();
  }
}
