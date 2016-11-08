package fr.delthas.uitest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.lwjgl.BufferUtils;

public class Utils {

  private Utils() {
    throw new IllegalStateException("This class cannot be instantiated!");
  }

  public static BufferedInputStream getResource(String path) throws IOException {
    InputStream is = Utils.class.getResourceAsStream("/" + path);
    if (is != null)
      return new BufferedInputStream(is);
    // TODO mieux
    return null;
  }

  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }

  public static ByteBuffer getResourceBuffer(String path) throws IOException {
    return getResourceBuffer(path, 1024);
  }

  public static ByteBuffer getResourceBuffer(String path, int bufferSize) throws IOException {
    ByteBuffer buffer;
    try (InputStream source = getResource(path); ReadableByteChannel rbc = Channels.newChannel(source)) {
      buffer = BufferUtils.createByteBuffer(bufferSize);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1)
          break;
        if (buffer.remaining() == 0)
          buffer = resizeBuffer(buffer, buffer.capacity() * 2);
      }
    }
    buffer.flip();
    return buffer;
  }

}
