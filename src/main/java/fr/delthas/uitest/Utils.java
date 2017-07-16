package fr.delthas.uitest;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class Utils {
  private Utils() {
    throw new IllegalStateException("This class cannot be instantiated!");
  }
  
  static BufferedInputStream getResource(Path path, String pathString, long[] size) throws IOException {
    if (path == null) {
      InputStream is = Utils.class.getResourceAsStream(pathString.startsWith("/") ? pathString : "/" + pathString);
      if (is != null) {
        return new BufferedInputStream(is);
      }
      path = Paths.get(pathString);
    }
    if (Files.isRegularFile(path)) {
      InputStream is = Files.newInputStream(path);
      try {
        size[0] = Files.size(path);
      } catch (IOException ignore) {
      }
      return new BufferedInputStream(is);
    }
    throw new IOException("Resource " + pathString + " not found.");
  }
  
  private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
    ByteBuffer newBuffer = allocate(newCapacity);
    buffer.flip();
    newBuffer.put(buffer);
    return newBuffer;
  }
  
  static ByteBuffer getResourceBuffer(String path) throws IOException {
    long[] size = {-1};
    BufferedInputStream input = getResource(null, path, size);
    if (size[0] >= Integer.MAX_VALUE) {
      throw new RuntimeException("File too large to be read!");
    }
    return getBuffer(input, size[0] <= 0 ? 1024 : (int) size[0] + 1);
  }
  
  static ByteBuffer getResourceBuffer(Path path) throws IOException {
    long[] size = {-1};
    BufferedInputStream input = getResource(path, null, size);
    if (size[0] >= Integer.MAX_VALUE) {
      throw new RuntimeException("File too large to be read!");
    }
    return getBuffer(input, size[0] <= 0 ? 1024 : (int) size[0] + 1);
  }
  
  static ByteBuffer getBuffer(InputStream is) throws IOException {
    return getBuffer(is, 1024);
  }
  
  static ByteBuffer getBuffer(InputStream is, int bufferSize) throws IOException {
    ByteBuffer buffer;
    try (InputStream source = new BufferedInputStream(is); ReadableByteChannel rbc = Channels.newChannel(source)) {
      buffer = allocate(bufferSize);
      while (true) {
        int bytes = rbc.read(buffer);
        if (bytes == -1) {
          break;
        }
        if (buffer.remaining() == 0) {
          ByteBuffer newBuffer = resizeBuffer(buffer, buffer.capacity() * 2);
          free(buffer);
          buffer = newBuffer;
        }
      }
    }
    buffer.flip();
    return buffer;
  }
  
  static ByteBuffer getResourceBuffer(String path, int bufferSize) throws IOException {
    return getBuffer(getResource(null, path, new long[1]), bufferSize);
  }
  
  static ByteBuffer allocate(int capacity) {
    return MemoryUtil.memAlloc(capacity);
  }
  
  static void free(ByteBuffer buffer) {
    MemoryUtil.memFree(buffer);
  }
}
