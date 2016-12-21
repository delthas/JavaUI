package fr.delthas.uitest.test;

import fr.delthas.uitest.Component;
import fr.delthas.uitest.Drawer;
import fr.delthas.uitest.Font;
import fr.delthas.uitest.InputState;

import java.awt.*;
import java.util.function.BiConsumer;

public class Button extends Component {
  private String text = "";
  private boolean down = false;
  private BiConsumer<Double, Double> listener;

  @Override
  protected void render(InputState inputState, Drawer drawer) {
    drawer.setColor(Color.WHITE);
    drawer.fillRectangle(0, 0, getWidth(), getHeight(), false);
    drawer.setColor(Color.BLACK);
    drawer.fillRectangle(1, 1, getWidth() - 2, getHeight() - 2, false);
    drawer.setColor(down ? Color.WHITE : Color.LIGHT_GRAY);
    drawer.drawText(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
  }

  @Override
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    if (button != 0) {
      return false;
    }
    if (down && isInBounds(x, y)) {
      this.down = true;
      if (listener != null) {
        listener.accept(x, y);
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
