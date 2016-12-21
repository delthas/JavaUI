package fr.delthas.uitest.test;

import fr.delthas.uitest.Icon;
import fr.delthas.uitest.Layer;
import fr.delthas.uitest.Ui;

import java.io.IOException;
import java.util.Random;

public final class UiTest {
  private static final Random random = new Random();
  private static boolean exitRequested = false;

  private UiTest() {}

  public static void main(String[] args) throws IOException {
    Ui.getUi().create("UiTest", Icon.createIcon("icon.png"));

    Layer layer = new Layer();
    Button button = new Button();
    button.setText("ccMMMM");
    button.setListener((x, y) -> button.setText(x + " " + y));
    layer.addComponent(Ui.getWidth() / 3, Ui.getHeight() / 2 - 20, Ui.getWidth() / 3, 40, button);
    TextField field = new TextField();
    field.setText("dille");
    layer.addComponent(Ui.getWidth() / 3, Ui.getHeight() / 2 - 500, Ui.getWidth() / 3, 50, field);
    layer.push();
    while (!exitRequested) {
      Ui.getUi().input();
      Ui.getUi().render();
    }
    Ui.getUi().destroy();
  }
}
