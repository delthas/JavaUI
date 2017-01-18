package fr.delthas.uitest.test;

import fr.delthas.uitest.Component;
import fr.delthas.uitest.*;

import java.awt.*;
import java.io.IOException;

public final class UiTest {
  private static boolean exitRequested = false;

  private UiTest() {}

  public static void main(String[] args) throws IOException {
    Ui.getUi().create("UiTest", Icon.createIcon("icon.png"));

    Layer layer = new Layer();
    layer.addComponent(new Component() {
      @Override
      protected void render(InputState inputState, Drawer drawer) {
        drawer.setColor(Color.CYAN);
        drawer.fillCircle(500, 500, 50);
      }
    });
    layer.push();
    while (!exitRequested) {
      Ui.getUi().input();
      Ui.getUi().render();
    }
    Ui.getUi().destroy();
  }
}
