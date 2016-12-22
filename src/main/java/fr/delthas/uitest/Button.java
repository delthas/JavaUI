package fr.delthas.uitest;

import java.awt.*;
import java.util.function.BiConsumer;

public class Button extends Component {
  private String text = "";
  private boolean down = false;
  private BiConsumer<Double, Double> listener;

  public Button() {

  }

  public Button(String text) {
    setText(text);
  }

  @Override
  protected void render(InputState inputState, Drawer drawer) {
    if (isEnabled() && isInBounds(inputState.getMouseX(this), inputState.getMouseY(this))) {
      drawer.setColor(Color.WHITE);
    } else {
      drawer.setColor(Color.GRAY);
    }
    drawer.fillRectangle(0, 0, getWidth(), getHeight(), false);
    drawer.setColor(Color.BLACK);
    drawer.fillRectangle(1, 1, getWidth() - 2, getHeight() - 2, false);
    drawer.setColor(!isEnabled() ? Color.WHITE : down ? Color.WHITE : Color.LIGHT_GRAY);
    drawer.drawText(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
  }

  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    if (button != 0) {
      return false;
    }
    if (isEnabled() && down && isInBounds(x, y)) {
      if (!this.down) {
        this.down = true;
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

  public void setListener(BiConsumer<Double, Double> listener) {
    this.listener = listener;
  }
}
