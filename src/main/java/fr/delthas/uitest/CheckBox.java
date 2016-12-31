package fr.delthas.uitest;

import java.awt.*;
import java.util.function.BiConsumer;

public class CheckBox extends Component {
  private String text = "";
  private boolean down = false;
  private boolean checked = false;
  private BiConsumer<Double, Double> listener;

  public CheckBox() {

  }

  public CheckBox(String text) {
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
    double unit = getHeight() / 8;
    drawer.setColor(Color.WHITE);
    drawer.fillRectangle(getWidth() - 7 * unit, unit, 6 * unit, 6 * unit, false);
    drawer.setColor(Color.BLACK);
    drawer.fillRectangle(getWidth() - 7 * unit + 1, unit + 1, 6 * unit - 2, 6 * unit - 2, false);
    if (checked) {
      drawer.setColor(Color.WHITE);
      drawer.fillRectangle(getWidth() - 5 * unit, 3 * unit, 2 * unit, 2 * unit, false);
    }
    drawer.setColor(down ? Color.WHITE : Color.LIGHT_GRAY);
    drawer.drawText((getWidth() - unit) / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
  }

  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    if (button != Ui.MOUSE_LEFT) {
      return false;
    }
    if (down && isInBounds(x, y)) {
      if (!this.down) {
        this.down = true;
        checked = !checked;
        if (listener != null) {
          listener.accept(x, y);
        }
      }
      return true;
    }
    if (!down) {
      this.down = false;
    }
    return false;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  public void setListener(BiConsumer<Double, Double> listener) {
    this.listener = listener;
  }
}
