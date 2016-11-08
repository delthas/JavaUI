package fr.delthas.uitest;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTBakedChar.Buffer;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@SuppressWarnings({"resource", "unused"})
public class Window implements Drawer {

  private static class FontData {

    public int texture;
    public STBTTBakedChar.Buffer bakedChars;

    public FontData(int texture, Buffer bakedChars) {
      this.texture = texture;
      this.bakedChars = bakedChars;
    }

  }

  static {
    init();
  }

  private boolean compatibility;
  private long window;
  private int vao, circleVao, texVao, fontVao, program, circleProgram, texProgram, fontProgram;
  private int indexMatrix;
  private int indexColor, indexColorCircle;
  private int indexCircle;
  private int indexTexPosition, indexTexImagePosition;
  private int indexFontPosition, indexFontImagePosition, indexFontColor;
  private int bufferRectangle;
  private FloatBuffer bufferMat4x4;
  private Matrix4f mat4x4;

  private static int width, height;

  private ArrayDeque<Double> translateStack = new ArrayDeque<>();
  private double translateX, translateY;

  private GLFWKeyCallback keyCallback;
  private GLFWCursorPosCallback cursorPosCallback;
  private GLFWMouseButtonCallback mouseButtonCallback;
  private GLFWScrollCallback scrollCallback;

  private Map<Font, FontData> fontData = new HashMap<>();

  private static void init() {
    Configuration.EGL_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.OPENAL_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.OPENCL_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.OPENGLES_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.VULKAN_EXPLICIT_INIT.set(Boolean.TRUE);

    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    glfwSetErrorCallback(GLFWErrorCallback.createThrow());

    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

    // do NOT change this to a try-with block
    GLFWVidMode vidmode;
    vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
    glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
    glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
    glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
    glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());
    width = vidmode.width();
    height = vidmode.height();
  }

  public void create(String title, boolean fullscreen) {
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    window = glfwCreateWindow(width, height, "Context Preloading", NULL, NULL);
    glfwDestroyWindow(window);
    glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
    if (window == NULL) {
      // 4.3 not supported
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
      compatibility = true;
    }
    window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

    BufferedImage image;
    try (BufferedInputStream is = new BufferedInputStream(Window.class.getResourceAsStream("/icon.png"))) {
      image = ImageIO.read(is);
      // do NOT change this to a try-with block
      GLFWImage iconImage = GLFWImage.create();
      ByteBuffer windowIconImage = readImage(image, true, false);
      iconImage.set(image.getWidth(), image.getHeight(), windowIconImage);
      glfwSetWindowIcon(window, GLFWImage.create(iconImage.sizeof()).put(iconImage).flip());
      memFree(windowIconImage);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window. Update your graphics card drivers.");
    }

    glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {
      @Override
      public void invoke(long window, double xoffset, double yoffset) {
        Ui.getUi().pushMouseScroll(-(int) yoffset);
      }
    });

    glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
      @Override
      public void invoke(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
          Ui.getUi().pushKeyButton(key, true);
        } else if (action == GLFW_RELEASE) {
          Ui.getUi().pushKeyButton(key, false);
        }
      }
    });

    glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
      @Override
      public void invoke(long window, double xpos, double ypos) {
        Ui.getUi().pushMouseMove(xpos / width, (height - ypos) / height);
      }
    });

    glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
      @Override
      public void invoke(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
          Ui.getUi().pushMouseButton(button, true);
        } else if (action == GLFW_RELEASE) {
          Ui.getUi().pushMouseButton(button, false);
        }
      }
    });

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);

    GL.createCapabilities(true);

    if (!compatibility) {
      glEnable(GL_DEBUG_OUTPUT);
      glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
      glDebugMessageCallback(new GLDebugMessageCallback() {
        @Override
        public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
          String sourceS;
          switch (source) {
            case GL_DEBUG_SOURCE_API:
              sourceS = "API";
              break;
            case GL_DEBUG_SOURCE_APPLICATION:
              sourceS = "Application";
              break;
            case GL_DEBUG_SOURCE_SHADER_COMPILER:
              sourceS = "Shader_compiler";
              break;
            case GL_DEBUG_SOURCE_THIRD_PARTY:
              sourceS = "Third_party";
              break;
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
              sourceS = "Window_system";
              break;
            case GL_DEBUG_SOURCE_OTHER:
            default:
              sourceS = "Autre";
              break;
          }
          String typeS;
          switch (type) {
            case GL_DEBUG_TYPE_ERROR:
              typeS = "Error";
              break;
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
              typeS = "Deprecated_bhvr";
              break;
            case GL_DEBUG_TYPE_MARKER:
              typeS = "Marker";
              break;
            case GL_DEBUG_TYPE_PERFORMANCE:
              typeS = "Performance";
              break;
            case GL_DEBUG_TYPE_POP_GROUP:
              typeS = "Pop_group";
              break;
            case GL_DEBUG_TYPE_PUSH_GROUP:
              typeS = "Push_group";
              break;
            case GL_DEBUG_TYPE_PORTABILITY:
              typeS = "Portability";
              break;
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
              typeS = "Undefined_bhvr";
              break;
            case GL_DEBUG_TYPE_OTHER:
            default:
              typeS = "Autre";
              break;
          }
          String severityS;
          switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH:
              severityS = "Haute";
              break;
            case GL_DEBUG_SEVERITY_MEDIUM:
              severityS = "Moyen";
              break;
            case GL_DEBUG_SEVERITY_LOW:
              severityS = "Bas";
              break;
            case GL_DEBUG_SEVERITY_NOTIFICATION:
              severityS = "Notification";
              break;
            default:
              severityS = "Autre";
              break;
          }
          System.err.println("Source: " + sourceS + " - Type: " + typeS + " - Sévérité: " + severityS + " - Message: "
              + MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length)));
          if (severity != GL_DEBUG_SEVERITY_NOTIFICATION)
            Thread.dumpStack();
        }
      }, 0L);
    }

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    glDisable(GL_CULL_FACE);
    glDisable(GL_DEPTH_TEST);
    glClearColor(0, 0, 0, 0);

    circleVao = glGenVertexArrays();
    glBindVertexArray(circleVao);

    int vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, readFile("circle.vert"));
    glCompileShader(vertShader);
    if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(vertShader));
    }
    int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, readFile("circle.frag"));
    glCompileShader(fragShader);
    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(fragShader));
    }
    circleProgram = glCreateProgram();
    glAttachShader(circleProgram, vertShader);
    glAttachShader(circleProgram, fragShader);
    glLinkProgram(circleProgram);
    if (glGetProgrami(circleProgram, GL_LINK_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetProgramInfoLog(circleProgram));
    }
    glDetachShader(circleProgram, vertShader);
    glDetachShader(circleProgram, fragShader);
    glDeleteShader(vertShader);
    glDeleteShader(fragShader);
    glUseProgram(circleProgram);

    indexCircle = glGetUniformLocation(circleProgram, "circle");
    indexColorCircle = glGetUniformLocation(circleProgram, "color");

    // texVao = glGenVertexArrays();
    // glBindVertexArray(texVao);
    //
    // vertShader = glCreateShader(GL_VERTEX_SHADER);
    // glShaderSource(vertShader, readFile("tex.vert"));
    // glCompileShader(vertShader);
    // if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE) {
    // throw new RuntimeException(glGetShaderInfoLog(vertShader));
    // }
    // fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    // glShaderSource(fragShader, readFile("tex.frag"));
    // glCompileShader(fragShader);
    // if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE) {
    // throw new RuntimeException(glGetShaderInfoLog(fragShader));
    // }
    // texProgram = glCreateProgram();
    // glAttachShader(texProgram, vertShader);
    // glAttachShader(texProgram, fragShader);
    // glLinkProgram(texProgram);
    // if (glGetProgrami(texProgram, GL_LINK_STATUS) != GL_TRUE) {
    // throw new RuntimeException(glGetProgramInfoLog(texProgram));
    // }
    // glDetachShader(texProgram, vertShader);
    // glDetachShader(texProgram, fragShader);
    // glDeleteShader(vertShader);
    // glDeleteShader(fragShader);
    // glUseProgram(texProgram);
    //
    // indexTexPosition = glGetUniformLocation(texProgram, "position");
    // indexTexImagePosition = glGetUniformLocation(texProgram, "imagePosition");

    fontVao = glGenVertexArrays();
    glBindVertexArray(fontVao);

    vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, readFile("font.vert"));
    glCompileShader(vertShader);
    if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(vertShader));
    }
    fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, readFile("font.frag"));
    glCompileShader(fragShader);
    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(fragShader));
    }
    fontProgram = glCreateProgram();
    glAttachShader(fontProgram, vertShader);
    glAttachShader(fontProgram, fragShader);
    glLinkProgram(fontProgram);
    if (glGetProgrami(fontProgram, GL_LINK_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetProgramInfoLog(fontProgram));
    }
    glDetachShader(fontProgram, vertShader);
    glDetachShader(fontProgram, fragShader);
    glDeleteShader(vertShader);
    glDeleteShader(fragShader);
    glUseProgram(fontProgram);

    indexFontPosition = glGetUniformLocation(fontProgram, "fontPosition");
    indexFontImagePosition = glGetUniformLocation(fontProgram, "imagePosition");
    indexFontColor = glGetUniformLocation(fontProgram, "color");

    vao = glGenVertexArrays();
    glBindVertexArray(vao);

    vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, readFile("std.vert"));
    glCompileShader(vertShader);
    if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(vertShader));
    }
    fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, readFile("std.frag"));
    glCompileShader(fragShader);
    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(fragShader));
    }
    program = glCreateProgram();
    glAttachShader(program, vertShader);
    glAttachShader(program, fragShader);
    glLinkProgram(program);
    if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetProgramInfoLog(program));
    }
    glDetachShader(program, vertShader);
    glDetachShader(program, fragShader);
    glDeleteShader(vertShader);
    glDeleteShader(fragShader);
    glUseProgram(program);

    indexMatrix = glGetUniformLocation(program, "matrix");
    indexColor = glGetUniformLocation(program, "color");

    bufferRectangle = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, bufferRectangle);
    float[] rawPositions = {-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f};
    try (MemoryStack stack = stackPush()) {
      FloatBuffer fb = stack.mallocFloat(16);
      fb.put(rawPositions);
      fb.flip();
      glBufferData(GL_ARRAY_BUFFER, fb, GL_STATIC_DRAW);
    }

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    // glBindVertexArray(texVao);
    // glEnableVertexAttribArray(0);
    // glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    glBindVertexArray(fontVao);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    bufferMat4x4 = MemoryUtil.memAllocFloat(16);
    mat4x4 = new Matrix4f();

    for (Font font : Font.values()) {
      try {
        ByteBuffer buf = Utils.getResourceBuffer(font.getName() + ".ttf");
        ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
        STBTTBakedChar.Buffer chars = STBTTBakedChar.malloc(96);
        stbtt_BakeFontBitmap(buf, 72, bitmap, 512, 512, 32, chars);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R8, 512, 512, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        fontData.put(font, new FontData(texture, chars));
      } catch (IOException e) {
        System.err.println("Couldn't fetch font: " + font.toString() + ": " + e.getMessage());
      }
    }
  }

  public void destroy() {
    glDeleteBuffers(bufferRectangle);
    glDeleteVertexArrays(vao);
    glDeleteVertexArrays(circleVao);
    glDeleteVertexArrays(texVao);
    glDeleteVertexArrays(fontVao);
    glDeleteProgram(program);
    glDeleteProgram(circleProgram);
    glDeleteProgram(texProgram);
    glDeleteProgram(fontProgram);
    memFree(bufferMat4x4);
    glfwDestroyWindow(window);
    glfwTerminate();
    fontData.forEach((font, data) -> {
      data.bakedChars.free();
      glDeleteTextures(data.texture);
    });
  }

  public void flip() {
    glfwSwapBuffers(window);
    glClear(GL_COLOR_BUFFER_BIT);
  }

  public void setVisible(boolean visible) {
    if (visible) {
      glfwMaximizeWindow(window);
    } else {
      glfwIconifyWindow(window);
    }
  }

  @Override
  public void pushTranslate(double x, double y) {
    translateStack.push(x);
    translateStack.push(y);
    translateX += x;
    translateY += y;
  }

  @Override
  public void popTranslate() {
    translateY -= translateStack.pop();
    translateX -= translateStack.pop();
    if (translateStack.isEmpty()) {
      // recalibrate if empty to reduce floating point imprecision
      translateX = 0;
      translateY = 0;
    }
  }

  @Override
  public void fillCircle(double x, double y, double radius, boolean centered) {
    glUseProgram(circleProgram);
    glBindVertexArray(circleVao);
    if (centered) {
      glUniform4f(indexCircle, (float) ((x + translateX) * 2 - 1), (float) ((y + translateY) * 2 - 1), (float) (radius * 2), (float) (radius * 2));
    } else {
      glUniform4f(indexCircle, (float) ((x + translateX + radius / 2) * 2 - 1), (float) ((y + translateY + radius / 2) * 2 - 1), (float) (radius * 2),
          (float) (radius * 2));
    }
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
  }

  @Override
  public void fillRectangle(double x, double y, double width, double height, boolean centered) {
    glUseProgram(program);
    glBindVertexArray(vao);
    if (centered) {
      mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1).translate((float) (x + translateX), (float) (y + translateY), 0)
          .scale((float) width, (float) height, 1);
    } else {
      mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1)
          .translate((float) (x + translateX + width / 2), (float) (y + translateY + height / 2), 0).scale((float) width, (float) height, 1);
    }
    bufferMat4x4.clear();
    glUniformMatrix4fv(indexMatrix, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  @Override
  public void drawImage(double x, double y, double width, double height, boolean centered) {
    glUseProgram(texProgram);
    glBindVertexArray(texVao);

    // TODO

    glDrawArrays(GL_TRIANGLES, 0, 6);
  }

  @Override
  public void drawText(double x, double y, double ratio, String text, Font font, boolean centered) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer xpos = stack.floats((float) x);
      FloatBuffer ypos = stack.floats((float) y);

      STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

      FontData fontData = this.fontData.get(font);

      glUseProgram(fontProgram);
      glBindVertexArray(fontVao);

      glBindTexture(GL_TEXTURE_2D, fontData.texture);

      float offset = 0;

      if (centered) {
        for (int i = 0; i < text.length(); i++) {
          char c = text.charAt(i);
          if (c < 32 || 128 <= c)
            continue;
          stbtt_GetBakedQuad(fontData.bakedChars, 512, 512, c - 32, xpos, ypos, q, true);
        }
        offset = (float) (ratio * (xpos.get(0) - x) / 2);
        xpos.put(0, (float) x);
      }

      for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i);
        if (c < 32 || 128 <= c)
          continue;

        float oldX = xpos.get(0);
        float oldY = ypos.get(0);

        stbtt_GetBakedQuad(fontData.bakedChars, 512, 512, c - 32, xpos, ypos, q, true);

        glUniform4f(indexFontPosition, (float) ((translateX + oldX + (q.x0() - oldX) * ratio - offset) * 2 / width) - 1,
            (float) ((translateY + oldY + (y + y - oldY - q.y0()) * ratio) * 2 / height) - 1,
            (float) ((translateX + oldX + (q.x1() - oldX) * ratio - offset) * 2 / width) - 1,
            (float) ((translateY + oldY + (y + y - oldY - q.y1()) * ratio) * 2 / height) - 1);
        glUniform4f(indexFontImagePosition, q.s0(), q.t0(), q.s1(), q.t1());
        glDrawArrays(GL_TRIANGLES, 0, 6);

        xpos.put(0, oldX + (xpos.get(0) - oldX) * (float) ratio);
        ypos.put(0, oldY + (ypos.get(0) - oldY) * (float) ratio);
      }
    }
  }

  @Override
  public void setColor(Color color) {
    glUseProgram(program);
    glUniform3f(indexColor, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
    glUseProgram(circleProgram);
    glUniform3f(indexColorCircle, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
    glUseProgram(fontProgram);
    glUniform3f(indexFontColor, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
  }

  public void input() {
    glfwPollEvents();
  }

  private static String readFile(String name) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Window.class.getResourceAsStream("/" + name)))) {
      StringBuilder file = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        file.append(line + "\n");
      }
      return file.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static int getHeight() {
    return height;
  }

  public static int getWidth() {
    return width;
  }

  private static ByteBuffer readImage(BufferedImage image, boolean alpha, boolean inverted) {
    // extract the pixel colors from the image and put it in a buffer for opengl
    // heavy optimization to avoid allocating 3 times the size of an image
    // based on BufferedImage#getRGB

    int imageHeight = image.getHeight();
    int imageWidth = image.getWidth();
    ByteBuffer buffer = MemoryUtil.memAlloc(imageWidth * imageHeight * (alpha ? 4 : 3));
    Raster raster = image.getRaster();
    int nbands = raster.getNumBands();
    int dataType = raster.getDataBuffer().getDataType();
    Object data;
    ColorModel colorModel = image.getColorModel();
    switch (dataType) {
      case DataBuffer.TYPE_BYTE:
        data = new byte[nbands];
        break;
      case DataBuffer.TYPE_USHORT:
        data = new short[nbands];
        break;
      case DataBuffer.TYPE_INT:
        data = new int[nbands];
        break;
      case DataBuffer.TYPE_FLOAT:
        data = new float[nbands];
        break;
      case DataBuffer.TYPE_DOUBLE:
        data = new double[nbands];
        break;
      default:
        throw new IllegalArgumentException("Unknown data buffer type: " + dataType);
    }
    for (int y = 0; y < imageHeight; y++) {
      for (int x = 0; x < imageWidth; x++) {
        if (inverted) {
          raster.getDataElements(x, imageHeight - 1 - y, data);
        } else {
          raster.getDataElements(x, y, data);
        }
        buffer.put((byte) colorModel.getRed(data));
        buffer.put((byte) colorModel.getGreen(data));
        buffer.put((byte) colorModel.getBlue(data));
        if (alpha) {
          buffer.put((byte) colorModel.getAlpha(data));
        }
      }
    }

    buffer.flip();

    return buffer;
  }

}
