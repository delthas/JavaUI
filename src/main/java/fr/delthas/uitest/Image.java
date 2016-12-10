package fr.delthas.uitest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Image {
  final int width;
  final int height;
  final int texture;

  Image(int width, int height, int texture) {
    this.width = width;
    this.height = height;
    this.texture = texture;
  }

  public static ByteBuffer preloadImage(String path) throws IOException {
    return Utils.getResourceBuffer(path);
  }

  public static ByteBuffer preloadImage(InputStream input) throws IOException {
    return Utils.getBuffer(input);
  }

  public static Image createImage(String path) throws IOException {
    return createImage(path, false);
  }

  public static Image createImage(InputStream input) throws IOException {
    return createImage(input, false);
  }

  public static Image createImage(ByteBuffer buffer) {
    return createImage(buffer, false);
  }

  public static Image createImage(String path, boolean ignoreAlpha) throws IOException {
    return createImage(Utils.getResourceBuffer(path), ignoreAlpha);
  }

  public static Image createImage(InputStream input, boolean ignoreAlpha) throws IOException {
    return createImage(Utils.getBuffer(input), ignoreAlpha);
  }

  public static Image createImage(ByteBuffer buffer, boolean ignoreAlpha) {
    return Ui.getUi().getWindow().createImage(buffer, ignoreAlpha);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
