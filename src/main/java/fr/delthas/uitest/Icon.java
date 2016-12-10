package fr.delthas.uitest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Icon {
  final int width;
  final int height;
  final ByteBuffer data;

  private Icon(int width, int height, ByteBuffer data) {
    this.width = width;
    this.height = height;
    this.data = data;
  }

  public static Icon createIcon(String path) throws IOException {
    return createIcon(Utils.getResourceBuffer(path));
  }

  public static Icon createIcon(InputStream input) throws IOException {
    return createIcon(Utils.getBuffer(input));
  }

  public static Icon createIcon(ByteBuffer buffer) {
    int[] x = new int[1];
    int[] y = new int[1];
    ByteBuffer result = stbi_load_from_memory(buffer, x, y, new int[1], 4);
    return new Icon(x[0], y[0], result);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
