package fr.delthas.uitest;

import java.awt.Color;

public class UiTest {
  private static boolean exitRequested = false;

  private static long start = System.nanoTime();

  public static void main(String[] args) {
    Ui.getUi().create("UiTest");
    Layer layer = new Layer();
    layer.addComponent(new Component() {
      @Override
      protected boolean pushMouseButton(double x, double y, int button, boolean down) {
        exitRequested = true;
        return true;
      }

      @Override
      protected void render(InputState inputState, Drawer drawer) {
        drawer.setColor(Color.WHITE);
        for (int i = 0; i < 1; i++) {
          drawer.drawText(0.5 * Ui.getWidth(), 0.5 * Ui.getHeight(), Math.random() * 5, "SAVA", Font.COMIC, true);
          drawer.fillRectangle(0.5 * Ui.getWidth(), 0.5 * Ui.getHeight(), 50, 50, false);
        }
      }
    }, Ui.getWidth() / 4f, Ui.getHeight() / 4f, Ui.getWidth() / 2f, Ui.getHeight() / 2f);
    Ui.getUi().push(layer);

    while (!exitRequested) {
      Ui.getUi().input();
      Ui.getUi().render();
    }
    Ui.getUi().destroy();

  }

}
