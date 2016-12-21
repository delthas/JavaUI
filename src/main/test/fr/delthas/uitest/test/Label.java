package fr.delthas.uitest.test;

import fr.delthas.uitest.Component;
import fr.delthas.uitest.Drawer;
import fr.delthas.uitest.Font;
import fr.delthas.uitest.InputState;

import java.awt.*;

public class Label extends Component {
  private String text = "";

  @Override
  protected void render(InputState inputState, Drawer drawer) {
    drawer.setColor(Color.WHITE);
    drawer.drawText(getWidth() / 2, getHeight() / 2, text, Font.COMIC, 16, true, true);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
