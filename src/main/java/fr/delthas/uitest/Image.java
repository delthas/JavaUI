package fr.delthas.uitest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public final class Image implements AutoCloseable {
  final int width;
  final int height;
  final ByteBuffer data;
  final boolean ignoreAlpha;
  final int allocation; // 0: nothing, 1: utils, 2: stb
  
  private Image(int width, int height, ByteBuffer data, boolean ignoreAlpha, int allocation) {
    this.width = width;
    this.height = height;
    this.data = data;
    this.ignoreAlpha = ignoreAlpha;
    this.allocation = allocation;
  }
  
  public static Image createImage(Path path) throws IOException {
    return createImage(path, false);
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
  
  public static Image createImage(Path path, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  public static Image createImage(String path, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  public static Image createImage(InputStream input, boolean ignoreAlpha) throws IOException {
    ByteBuffer buffer = Utils.getBuffer(input);
    Image image = createImage(buffer, ignoreAlpha);
    Utils.free(buffer);
    return image;
  }
  
  public static Image createImage(ByteBuffer buffer, boolean ignoreAlpha) {
    return Ui.getUi().getWindow().createImage(buffer, ignoreAlpha);
  }
  
  public static Image createImageRaw(ByteBuffer buffer, int width, int height, boolean ignoreAlpha) {
    return createImageRaw(buffer, width, height, ignoreAlpha, 0);
  }
  
  static Image createImageRaw(ByteBuffer buffer, int width, int height, boolean ignoreAlpha, int allocation) {
    return new Image(width, height, buffer, ignoreAlpha, allocation);
  }
  
  public Image getResized(int width, int height) {
    return Ui.getUi().getWindow().resizeImage(this, width, height);
  }
  
  public Texture upload() {
    return Ui.getUi().getWindow().uploadImage(this);
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  @Override
  public void close() {
    if (allocation == 2) {
      Ui.getUi().getWindow().freeImage(this);
      return;
    }
    if (allocation == 1) {
      Utils.free(data);
      return;
    }
  }
}
