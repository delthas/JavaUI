package fr.delthas.uitest.test;

import fr.delthas.uitest.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public final class UiTest {
  private static final Random random = new Random();
  private static boolean exitRequested = false;
  private static int toDraw;
  private static List<Image> images;
  private static ConcurrentLinkedQueue<ByteBuffer> fetched = new ConcurrentLinkedQueue<>();

  private UiTest() {}

  public static void main(String[] args) throws IOException {
    Ui.getUi().create("UiTest", Icon.createIcon("icon.png"));

    new Layer().addComponent(new Component() {

      @Override
      protected void render(InputState inputState, Drawer drawer) {
        if (images.isEmpty()) {
          return;
        }
        drawer.drawImageIn(getWidth() / 2, getHeight() / 2, getWidth(), getHeight(), images.get(toDraw));
      }
    }).push();

    images = new ArrayList<>();
    new Thread(() -> {
      while (true) {
        try {
          fetched.add(Utils.getResourceBuffer(Files.list(Paths.get("C:/e/sav")).skip(random.nextInt(500)).limit(1).collect(Collectors.toList()).get(0).toAbsolutePath().toString()));
          Thread.sleep(2500);
        } catch (Exception e) {
        }
      }
    }).start();
    while (!exitRequested) {
      int bound = random.nextInt((int) Math.pow(100, 1.1));
      for (int i = 0; i < bound; i++) {
        Ui.getUi().input();
        Ui.getUi().render();
      }
      toDraw = images.isEmpty() ? 0 : random.nextInt(images.size());
      while (!fetched.isEmpty()) {
        images.add(Image.createImage(fetched.poll()));
      }
    }
    Ui.getUi().destroy();
  }
}
