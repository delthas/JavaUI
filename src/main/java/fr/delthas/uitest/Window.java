package fr.delthas.uitest;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.stb.*;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

@SuppressWarnings({"resource", "unused"})
class Window implements Drawer {
  private static int width, height;

  static {
    init();
  }

  private boolean compatibility;
  private long window;
  private int vao, circleVao, texVao, fontVao, program, circleProgram, texProgram, fontProgram;
  private int bufferRectangle;
  private int indexCircleCirle, indexCircleColor;
  private int indexFontFontPosition, indexFontImagePosition, indexFontColor;
  private int indexStdMatrix, indexStdColor;
  private int indexTexScreenPosition, indexTexTexPosition;
  private FloatBuffer bufferMat4x4;
  private Matrix4f mat4x4;
  private ArrayDeque<Double> translateStack = new ArrayDeque<>();
  private double translateX, translateY;
  @SuppressWarnings("FieldCanBeLocal")
  private GLFWKeyCallback keyCallback;
  @SuppressWarnings("FieldCanBeLocal")
  private GLFWCharModsCallback charCallback;
  @SuppressWarnings("FieldCanBeLocal")
  private GLFWCursorPosCallback cursorPosCallback;
  @SuppressWarnings("FieldCanBeLocal")
  private GLFWMouseButtonCallback mouseButtonCallback;
  @SuppressWarnings("FieldCanBeLocal")
  private GLFWScrollCallback scrollCallback;
  private Map<FontKey, FontData> fontData = new HashMap<>();
  private Map<Font, ByteBuffer> fontBuffer = new HashMap<>();
  private Set<Integer> texturesIndexes = new HashSet<>();

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

    glfwWindowHint(GLFW_DECORATED, GL_FALSE);
  }

  private static String readFile(String name) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Window.class.getResourceAsStream("/" + name)))) {
      StringBuilder file = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        file.append(line).append("\n");
      }
      return file.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  static int getHeight() {
    return height;
  }

  static int getWidth() {
    return width;
  }

  Image createImage(ByteBuffer buffer, boolean ignoreAlpha) {
    int[] x = new int[1];
    int[] y = new int[1];
    ByteBuffer result = stbi_load_from_memory(buffer, x, y, new int[1], ignoreAlpha ? 3 : 4);
    Image image = createImageRaw(result, x[0], y[0], ignoreAlpha);
    stbi_image_free(result);
    return image;
  }

  Image createImageRaw(ByteBuffer buffer, int width, int height, boolean ignoreAlpha) {
    if (buffer.remaining() == 0) // automatically flip if clearly meant to be flipped
    {
      buffer.flip();
    }
    int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, ignoreAlpha ? GL_RGB8 : GL_RGBA8, width, height, 0, ignoreAlpha ? GL_RGB : GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
    texturesIndexes.add(texture);
    return new Image(width, height, texture);
  }

  void create(String title, Icon icon, boolean fullscreen) {
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    boolean supported;
    try {
      window = glfwCreateWindow(width, height, "Context Preloading", NULL, NULL);
      supported = window != NULL;
    } catch (Exception e) {
      supported = false;
    }
    if (!supported) {
      // 4.3 not supported
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
      compatibility = true;
    } else {
      glfwDestroyWindow(window);
    }
    glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
    window = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);

    GLFWImage.Buffer iconImages = GLFWImage.malloc(1);
    iconImages.get(0).set(icon.getWidth(), icon.getHeight(), icon.data);
    glfwSetWindowIcon(window, iconImages);
    iconImages.free();
    // maybe stbi free icon.data

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

    glfwSetCharModsCallback(window, charCallback = new GLFWCharModsCallback() {
      @Override
      public void invoke(long window, int codepoint, int mods) {
        Ui.getUi().pushChar(codepoint, mods);
      }
    });

    glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
      @Override
      public void invoke(long window, double xpos, double ypos) {
        Ui.getUi().pushMouseMove(xpos, height - ypos);
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
          if (id == 131185) {
            return;
          }
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
                  + MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length)) + " " + id);
          if (severity != GL_DEBUG_SEVERITY_NOTIFICATION) {
            Thread.dumpStack();
          }
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

    texVao = glGenVertexArrays();
    glBindVertexArray(texVao);

    vertShader = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vertShader, readFile("tex.vert"));
    glCompileShader(vertShader);
    if (glGetShaderi(vertShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(vertShader));
    }
    fragShader = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fragShader, readFile("tex.frag"));
    glCompileShader(fragShader);
    if (glGetShaderi(fragShader, GL_COMPILE_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetShaderInfoLog(fragShader));
    }
    texProgram = glCreateProgram();
    glAttachShader(texProgram, vertShader);
    glAttachShader(texProgram, fragShader);
    glLinkProgram(texProgram);
    if (glGetProgrami(texProgram, GL_LINK_STATUS) != GL_TRUE) {
      throw new RuntimeException(glGetProgramInfoLog(texProgram));
    }
    glDetachShader(texProgram, vertShader);
    glDetachShader(texProgram, fragShader);
    glDeleteShader(vertShader);
    glDeleteShader(fragShader);
    glUseProgram(texProgram);

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

    indexCircleCirle = glGetUniformLocation(circleProgram, "circle");
    indexCircleColor = glGetUniformLocation(circleProgram, "color");
    indexFontFontPosition = glGetUniformLocation(fontProgram, "fontPosition");
    indexFontImagePosition = glGetUniformLocation(fontProgram, "imagePosition");
    indexFontColor = glGetUniformLocation(fontProgram, "color");
    indexStdMatrix = glGetUniformLocation(program, "matrix");
    indexStdColor = glGetUniformLocation(program, "color");
    indexTexScreenPosition = glGetUniformLocation(texProgram, "screenPosition");
    indexTexTexPosition = glGetUniformLocation(texProgram, "texPosition");


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

    glBindVertexArray(texVao);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    glBindVertexArray(fontVao);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

    bufferMat4x4 = MemoryUtil.memAllocFloat(16);
    mat4x4 = new Matrix4f();

    for (Font font : Font.values()) {
      try {
        ByteBuffer buf = Utils.getResourceBuffer(font.getName() + ".ttf");
        fontBuffer.put(font, buf);
      } catch (IOException e) {
        System.err.println("Couldn't fetch font: " + font + ": " + e.getMessage());
      }
    }
  }

  private FontData getFontData(Font font, float size) {
    return fontData.computeIfAbsent(new FontKey(font, size), key -> {
      ByteBuffer data = fontBuffer.get(font);
      STBTTPackedchar.Buffer[] charData = {STBTTPackedchar.malloc(128 - 32), STBTTPackedchar.malloc(256 - 192)};
      ByteBuffer bitmap = BufferUtils.createByteBuffer(1024 * 1024);
      try (STBTTPackContext pc = STBTTPackContext.malloc()) {
        stbtt_PackBegin(pc, bitmap, 1024, 1024, 0, 1);
        stbtt_PackSetOversampling(pc, 3, 1);
        STBTTPackRange.Buffer ranges = STBTTPackRange.malloc(2);
        memSet(ranges.address(), 0, ranges.capacity() * STBTTPackRange.SIZEOF);
        ranges.get(0).font_size(size).first_unicode_codepoint_in_range(32).num_chars(128 - 32).chardata_for_range(charData[0]);
        ranges.get(1).font_size(size).first_unicode_codepoint_in_range(192).num_chars(256 - 192).chardata_for_range(charData[1]);

        stbtt_PackFontRanges(pc, data, 0, ranges);
        ranges.free();
        stbtt_PackEnd(pc);
      }

      glBindVertexArray(texVao);
      int texture = glGenTextures();
      glBindTexture(GL_TEXTURE_2D, texture);
      glTexImage2D(GL_TEXTURE_2D, 0, GL_R8, 1024, 1024, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

      STBTTFontinfo info = STBTTFontinfo.malloc();
      stbtt_InitFont(info, fontBuffer.get(font));

      return new FontData(texture, charData, info);
    });
  }

  void destroy() {
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
      for (STBTTPackedchar.Buffer buffer : data.charData) {
        buffer.free();
      }
      glDeleteTextures(data.texture);
    });
    texturesIndexes.forEach(GL11::glDeleteTextures);
  }

  void flip() {
    glfwSwapBuffers(window);
    glClear(GL_COLOR_BUFFER_BIT);
  }

  void setVisible(boolean visible) {
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
  public void fillCircle(double x, double y, double radius) {
    glUseProgram(circleProgram);
    glBindVertexArray(circleVao);
    glUniform4f(indexCircleCirle, (float) ((x + translateX) * 2 - 1), (float) ((y + translateY) * 2 - 1), (float) (radius * 2), (float) (radius * 2));
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glUseProgram(0);
  }

  @Override
  public void fillRectangle(double x, double y, double width, double height) {
    glUseProgram(program);
    glBindVertexArray(vao);
    mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1).translate((float) (x + translateX), (float) (y + translateY), 0)
            .scale((float) width, (float) height, 1);
    bufferMat4x4.clear();
    glUniformMatrix4fv(indexStdMatrix, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glUseProgram(0);
  }

  @Override
  public void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Image image) {
    glUseProgram(texProgram);
    glBindVertexArray(texVao);
    glBindTexture(GL_TEXTURE_2D, image.texture);
    mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1).translate((float) (x + translateX), (float) (y + translateY), 0)
            .scale((float) width, (float) height, 1);
    glUniformMatrix4fv(indexTexScreenPosition, false, mat4x4.get(bufferMat4x4));
    mat4x4.scaling(1f / image.getWidth(), -1f / image.getHeight(), 1).translate((float) s1, (float) t1, 0)
            .scale((float) (s2 - s1), (float) (t2 - t1), 1).translate(0.5f, 0.5f, 0.0f);
    glUniformMatrix4fv(indexTexTexPosition, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
    glUseProgram(0);
  }

  @Override
  public float getTextWidth(String text, Font font, float size, float[] sizes) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer xpos = stack.floats(0);
      FloatBuffer ypos = stack.floats(0);

      STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

      FontData fontData = getFontData(font, size);

      for (int i = 0; i < text.length(); i++) {
        if (sizes != null && sizes.length >= i + 1) {
          sizes[i] = xpos.get(0);
        }
        int c = text.codePointAt(i);
        int index;
        int position;
        if (c < 32) {
          continue;
        } else if (c < 128) {
          index = 0;
          position = c - 32;
        } else if (c < 192) {
          continue;
        } else if (c < 256) {
          index = 1;
          position = c - 192;
        } else {
          continue;
        }
        stbtt_GetPackedQuad(fontData.charData[index], 1024, 1024, position, xpos, ypos, q, false);
        if (sizes != null && sizes.length >= i + 1) {
          sizes[i] = xpos.get(0);
        }
      }
      return xpos.get(0);
    }
  }

  @Override
  public void getFontMetrics(Font font, float size, float[] metrics) {
    if (metrics == null || metrics.length == 0) {
      return;
    }
    int[] ascent = new int[1];
    int[] descent = new int[1];
    int[] lineGap = new int[1];
    FontData fontData = getFontData(font, size);
    stbtt_GetFontVMetrics(fontData.info, ascent, descent, lineGap);
    float scale = stbtt_ScaleForPixelHeight(fontData.info, size);
    metrics[0] = ascent[0] * scale;
    if (metrics.length >= 2) {
      metrics[1] = descent[0] * scale;
      if (metrics.length >= 3) {
        metrics[2] = lineGap[0] * scale;
      }
    }
  }

  @Override
  public float drawText(double x, double y, String text, Font font, float size, boolean xCentered, boolean yCentered, float[] sizes) {
    try (MemoryStack stack = stackPush()) {
      FloatBuffer xpos = stack.floats((float) x);
      FloatBuffer ypos = stack.floats((float) (getHeight() - y));

      STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

      FontData fontData = getFontData(font, size);

      glUseProgram(fontProgram);
      glBindVertexArray(fontVao);

      glBindTexture(GL_TEXTURE_2D, fontData.texture);

      float xOffset = 0;
      float yOffset = 0;

      if (xCentered) {
        xOffset = getTextWidth(text, font, size) / 2;
      }

      if (yCentered) {
        int[] ascent = new int[1];
        int[] descent = new int[1];
        int[] lineGap = new int[1];
        stbtt_GetFontVMetrics(fontData.info, ascent, descent, lineGap);
        yOffset = (descent[0] + ascent[0]) * stbtt_ScaleForPixelHeight(fontData.info, size) / 2;
      }

      int codepointCount = text.codePointCount(0, text.length());
      for (int i = 0; i < codepointCount; i++) {
        if (sizes != null && sizes.length >= i + 1) {
          sizes[i] = (float) (xpos.get(0) - x);
        }
        int c = text.codePointAt(i);
        int index;
        int position;
        if (c < 32) {
          continue;
        } else if (c < 128) {
          index = 0;
          position = c - 32;
        } else if (c < 192) {
          continue;
        } else if (c < 256) {
          index = 1;
          position = c - 192;
        } else {
          continue;
        }
        stbtt_GetPackedQuad(fontData.charData[index], 1024, 1024, position, xpos, ypos, q, false);

        if (sizes != null && sizes.length >= i + 1) {
          sizes[i] = (float) (xpos.get(0) - x);
        }

        glUniform4f(indexFontFontPosition, (float) ((translateX + q.x0() - xOffset) * 2 / width) - 1,
                (float) ((translateY + getHeight() - q.y0() - yOffset) * 2 / height) - 1,
                (float) ((translateX + q.x1() - xOffset) * 2 / width) - 1,
                (float) ((translateY + getHeight() - q.y1() - yOffset) * 2 / height) - 1);
        glUniform4f(indexFontImagePosition, q.s0(), q.t0(), q.s1(), q.t1());
        glDrawArrays(GL_TRIANGLES, 0, 6);
      }

      glUseProgram(0);

      return (float) (xpos.get(0) - x);
    }
  }

  @Override
  public void setColor(Color color) {
    glUseProgram(program);
    glUniform3f(indexStdColor, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
    glUseProgram(circleProgram);
    glUniform3f(indexCircleColor, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
    glUseProgram(fontProgram);
    glUniform3f(indexFontColor, color.getRed() / 256f, color.getGreen() / 256f, color.getBlue() / 256f);
  }

  void input() {
    glfwPollEvents();
  }

  public String getClipboard() {
    return glfwGetClipboardString(window);
  }

  public void setClipboard(String clipboard) {
    glfwSetClipboardString(window, clipboard);
  }

  public void setCursor(Icon icon, int xOffset, int yOffset) {
    GLFWImage iconImage = GLFWImage.malloc();
    iconImage.set(icon.getWidth(), icon.getHeight(), icon.data);
    long cursor = glfwCreateCursor(iconImage, xOffset, icon.getHeight() - 1 - yOffset);
    glfwSetCursor(window, cursor);
    iconImage.free();
  }

  private static class FontKey {
    final Font font;
    final float size;

    FontKey(Font font, float size) {
      this.font = font;
      this.size = size;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      FontKey fontKey = (FontKey) o;
      return Float.compare(fontKey.size, size) == 0 &&
              font == fontKey.font;
    }

    @Override
    public int hashCode() {
      return Objects.hash(font, size);
    }
  }

  private static class FontData {
    final int texture;
    final STBTTPackedchar.Buffer[] charData;
    final STBTTFontinfo info;

    FontData(int texture, STBTTPackedchar.Buffer[] charData, STBTTFontinfo info) {
      this.texture = texture;
      this.charData = charData;
      this.info = info;
    }
  }
}
