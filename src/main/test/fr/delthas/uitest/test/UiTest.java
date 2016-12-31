package fr.delthas.uitest.test;

import fr.delthas.uitest.*;

import java.io.IOException;
import java.util.Random;

public final class UiTest {
  private static final Random random = new Random();
  private static boolean exitRequested = false;
  private static long start;

  private UiTest() {}

  public static void main(String[] args) throws IOException {
    Ui.getUi().create("UiTest", Icon.createIcon("icon.png"));

    Layer layer = new Layer();
    Button button = new Button();
    button.setText("ccMMMM");
    button.setListener((x, y) -> button.setText(x + " " + y));
    Label label = new Label();
    label.setText("cc");
    layer.addComponent(Ui.getWidth() / 10, Ui.getHeight() / 2 - 20, Ui.getWidth() * 3 / 10, 40, label);
    layer.addComponent(Ui.getWidth() * 6 / 10, Ui.getHeight() / 2 - 20, Ui.getWidth() * 3 / 10, 40, button);
    TextField field = new TextField();
    field.setText("dille");
    layer.addComponent(Ui.getWidth() / 3, Ui.getHeight() / 2 - 500, Ui.getWidth() / 3, 50, field);
    CheckBox box = new CheckBox("pine");
    layer.addComponent(50, 800, 800, 40, box);
    layer.addComponent(new Component() {
      private double lastTime;
      private double angle = 0;

      @Override
      protected void render(InputState inputState, Drawer drawer) {
        angle += Math.random() * Math.pow((System.nanoTime() - lastTime) / 2000000000.0, 1);
        lastTime = System.nanoTime();
        drawer.drawLineCenter(getWidth() / 2, getHeight() / 2, 500, angle);
      }
    });
    layer.push();
    start = System.nanoTime();
    while (!exitRequested) {
      Ui.getUi().input();
      Ui.getUi().render();
    }
    Ui.getUi().destroy();
  }
}
