package fr.delthas.javaui;

import java.awt.*;

public class Label extends Component {
  private String text = "";
  
  public Label() {
  
  }
  
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
  
  public String getText() {
    return text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
}
