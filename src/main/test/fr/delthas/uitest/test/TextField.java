package fr.delthas.uitest.test;

import fr.delthas.uitest.Component;
import fr.delthas.uitest.*;
import fr.delthas.uitest.Font;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;

public class TextField extends Component {
  private String text = "";
  private float[] sizes = new float[8];
  private float[] metrics = new float[2];
  private Consumer<String> listener;
  private int caretPosition;
  private int selectionStart = 0;
  private int selectionEnd = 0;
  private boolean selecting = false;

  @Override
  protected void render(InputState inputState, Drawer drawer) {
    drawer.setColor(Color.WHITE);
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
        drawer.fillRectangle(getWidth() / 2 - sizes[text.length() - 1] / 2 + (selection0 == 0 ? 0 : sizes[selection0 - 1]), getHeight() / 2 + metrics[0] - (metrics[0] + metrics[1]) / 2, sizes[selection1 - 1] - (selection0 == 0 ? 0 : sizes[selection0 - 1]), metrics[1] - metrics[0], false);
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
      drawer.fillRectangle(position - 1, getHeight() / 2 + metrics[0] - (metrics[0] + metrics[1]) / 2, 1, metrics[1] - metrics[0], false);
    }
  }

  @Override
  protected boolean pushKeyButton(double x, double y, int key, boolean down) {
    if (!down) {
      return false;
    }
    if (!isInBounds(x, y)) {
      return false;
    }
    String stringForKey = "";
    if (key == 32 || key == 39 || key >= 44 && key <= 57 || key == 59 || key == 61 || key == 96) {
      stringForKey = Character.toString((char) key);
    } else if (key >= 65 && key <= 93) {
      stringForKey = Character.toString((char) (key + 32));
    } else if (key >= 320 && key <= 329) {
      stringForKey = Character.toString((char) (key - 320 + 48));
    }
    selecting = false;
    if (selectionStart > selectionEnd) {
      int temp = selectionStart;
      selectionStart = selectionEnd;
      selectionEnd = temp;
    }
    if (!stringForKey.isEmpty()) {
      setText(getText().substring(0, selectionStart) + stringForKey + getText().substring(selectionEnd));
    }
    caretPosition = selectionStart + stringForKey.length();
    if (selectionStart == selectionEnd) {
      if (key == GLFW.GLFW_KEY_BACKSPACE && caretPosition > 0) {
        setText(getText().substring(0, caretPosition - 1) + getText().substring(caretPosition));
        caretPosition--;
      } else if (key == GLFW.GLFW_KEY_DELETE && caretPosition < text.length()) {
        setText(getText().substring(0, caretPosition) + getText().substring(caretPosition + 1));
      }
    } else {
      if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
        setText(getText().substring(0, selectionStart) + getText().substring(selectionEnd));
      }
    }
    if (key == GLFW.GLFW_KEY_LEFT && caretPosition > 0) {
      caretPosition--;
    } else if (key == GLFW.GLFW_KEY_RIGHT && caretPosition < text.length()) {
      caretPosition++;
    }
    selectionStart = selectionEnd = caretPosition;
    if (listener != null) {
      listener.accept(stringForKey);
    }
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

  public void setText(String text) {
    if (this.text.equals(text)) {
      return;
    }
    this.text = text;
    if (text.length() > sizes.length) {
      sizes = new float[Integer.max(sizes.length * 2, text.length())];
    }
    Ui.getUi().getTextWidth(text, Font.COMIC, 16, sizes);
    Ui.getUi().getFontMetrics(Font.COMIC, 16, metrics);
  }

  public void setListener(Consumer<String> listener) {
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
