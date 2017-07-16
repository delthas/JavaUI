package fr.delthas.uitest;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

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
import static org.lwjgl.stb.STBImageResize.stbir_resize_uint8;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

@SuppressWarnings({"resource", "unused"})
class Window implements Drawer {
  private static int width, height;
  long time = 0;
  private boolean compatibility;
  private boolean created = false;
  private SynchronousQueue<Object> synchronousQueue = new SynchronousQueue<>();
  private long window;
  private int vao, circleVao, texVao, fontVao, program, circleProgram, texProgram, fontProgram;
  private int bufferRectangle;
  private int indexCircleCircle, indexCircleColor, indexCircleMinLength;
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
  private List<Object> inputs = new ArrayList<>();
  private FontKey lastFontKey;
  private FontData lastFontData;
  
  {
    init();
  }
  
  private static String readFile(String name) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(Window.class.getResourceAsStream("/" + name), StandardCharsets.UTF_8.name()))) {
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
  
  private void _init() {
    Init.init();
    
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
  
  private void init() {
    new Thread(() -> {
      _init();
      while (true) {
        if (created) {
          glfwPollEvents();
        }
        Object object = null;
        try {
          object = synchronousQueue.poll(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignore) {
        }
        if (object == null) {
          continue;
        }
        if (object instanceof CreateRequest) {
          CreateRequest createRequest = (CreateRequest) object;
          _create(createRequest.title, createRequest.image, createRequest.fullscreen);
          glfwMakeContextCurrent(NULL);
          try {
            synchronousQueue.put(new Object());
          } catch (InterruptedException ignore) {
          }
        }
        if (object instanceof DestroyRequest) {
          glfwMakeContextCurrent(window);
          _destroy();
          return;
        }
        if (object instanceof InputRequest) {
          try {
            synchronousQueue.put(inputs);
            inputs = new ArrayList<>();
          } catch (InterruptedException ignore) {
          }
        }
      }
    }).start();
  }

  Image createImage(ByteBuffer buffer, boolean ignoreAlpha) {
    int[] x = new int[1];
    int[] y = new int[1];
    ByteBuffer result = stbi_load_from_memory(buffer, x, y, new int[1], ignoreAlpha ? 3 : 4);
    return Image.createImageRaw(result, x[0], y[0], ignoreAlpha, 2);
  }
  
  void freeImage(Image image) {
    stbi_image_free(image.data);
  }
  
  public Image resizeImage(Image image, int width, int height) {
    ByteBuffer buffer = Utils.allocate(width * height * (image.ignoreAlpha ? 3 : 4));
    stbir_resize_uint8(image.data, image.getWidth(), image.getHeight(), 0, buffer, width, height, 0, image.ignoreAlpha ? 3 : 4);
    return Image.createImageRaw(buffer, width, height, image.ignoreAlpha, 1);
  }
  
  void destroyImage(SimpleTexture simpleTexture) {
    texturesIndexes.remove(simpleTexture.texture);
    glDeleteTextures(simpleTexture.texture);
  }
  
  public Atlas createAtlas(int width, int height, int n, boolean ignoreAlpha) {
    int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, ignoreAlpha ? GL_RGB8 : GL_RGBA8, width * n, height, 0, ignoreAlpha ? GL_RGB : GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
    texturesIndexes.add(texture);
    return new Atlas(width, height, texture, n, ignoreAlpha);
  }
  
  public void destroyAtlas(Atlas atlas) {
    texturesIndexes.remove(atlas.texture);
    glDeleteTextures(atlas.texture);
  }
  
  public AtlasTexture uploadAtlasImage(Atlas atlas, Image image, int i) {
    if (image.data.remaining() == 0) // automatically flip if clearly meant to be flipped
    {
      image.data.flip();
    }
    glBindTexture(GL_TEXTURE_2D, atlas.texture);
    glTexSubImage2D(GL_TEXTURE_2D, 0, atlas.width * i, 0, atlas.width, atlas.height, atlas.ignoreAlpha ? GL_RGB : GL_RGBA, GL_UNSIGNED_BYTE, image.data);
    return new AtlasTexture(atlas, i);
  }
  
  SimpleTexture uploadImage(Image image) {
    if (image.data.remaining() == 0) // automatically flip if clearly meant to be flipped
    {
      image.data.flip();
    }
    int texture = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, image.ignoreAlpha ? GL_RGB8 : GL_RGBA8, image.width, image.height, 0, image.ignoreAlpha ? GL_RGB : GL_RGBA, GL_UNSIGNED_BYTE, image.data);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL, 0);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 0);
    texturesIndexes.add(texture);
    return new SimpleTexture(image.width, image.height, texture);
  }
  
  void create(String title, Image image, boolean fullscreen) {
    try {
      synchronousQueue.put(new CreateRequest(title, image, fullscreen));
    } catch (InterruptedException ignore) {
    }
    try {
      synchronousQueue.take();
    } catch (InterruptedException ignore) {
    }
    glfwMakeContextCurrent(window);
    GL.createCapabilities(true);
  }
  
  void _create(String title, Image image, boolean fullscreen) {
    created = true;
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    boolean supported;
    try {
      window = glfwCreateWindow(width, height, "Context Preloading", glfwGetPrimaryMonitor(), NULL);
      supported = window != NULL;
    } catch (RuntimeException ignore) {
      // sometimes an exception can be thrown instead of returning NULL if the context version is unsupported
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
    
    if (image != null) {
      if (image.ignoreAlpha) {
        throw new RuntimeException("Image has to contain alpha (do not call ignoreAlpha=true)!");
      }
      GLFWImage.Buffer iconImages = GLFWImage.malloc(1);
      iconImages.get(0).set(image.getWidth(), image.getHeight(), image.data);
      glfwSetWindowIcon(window, iconImages);
      iconImages.free();
    }
    
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window. Update your graphics card drivers.");
    }
    
    glfwSetScrollCallback(window, scrollCallback = new GLFWScrollCallback() {
      @Override
      public void invoke(long window, double xoffset, double yoffset) {
        inputs.add(new ScrollInput(System.nanoTime(), -(int) yoffset));
      }
    });
    
    glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
      @Override
      public void invoke(long window, int key, int scancode, int action, int mods) {
        inputs.add(new KeyInput(System.nanoTime(), key, action == GLFW_PRESS));
      }
    });
    
    glfwSetCharModsCallback(window, charCallback = new GLFWCharModsCallback() {
      @Override
      public void invoke(long window, int codepoint, int mods) {
        EnumSet<KeyModifier> enumSet = EnumSet.noneOf(KeyModifier.class);
        if ((mods & GLFW_MOD_CONTROL) != 0) {
          enumSet.add(KeyModifier.CTRL);
        }
        if ((mods & GLFW_MOD_ALT) != 0) {
          enumSet.add(KeyModifier.ALT);
        }
        if ((mods & GLFW_MOD_SHIFT) != 0) {
          enumSet.add(KeyModifier.SHIFT);
        }
        if ((mods & GLFW_MOD_SUPER) != 0) {
          enumSet.add(KeyModifier.SUPER);
        }
        inputs.add(new ModsInput(System.nanoTime(), codepoint, enumSet));
      }
    });
    
    glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {
      @Override
      public void invoke(long window, double xpos, double ypos) {
        inputs.add(new MoveInput(System.nanoTime(), xpos, height - ypos));
      }
    });
    
    glfwSetMouseButtonCallback(window, mouseButtonCallback = new GLFWMouseButtonCallback() {
      @Override
      public void invoke(long window, int button, int action, int mods) {
        // button + 1 b/c we want to start at 1 (all mouse buttons in Mouse.java are offset by 1)
        inputs.add(new MouseInput(System.nanoTime(), button + 1, action == GLFW_PRESS));
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
          if (id == 131185 || id == 131218) {
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
          System.err.println("Source: " + sourceS + " - Type: " + typeS + " - Sévérité: " + severityS + " - Id: " + id + " - Message: "
                  + MemoryUtil.memUTF8(MemoryUtil.memByteBuffer(message, length)));
          if (severity != GL_DEBUG_SEVERITY_NOTIFICATION) {
            Thread.dumpStack();
          }
        }
      }, 0L);
    }
    
    System.out.println(glGetInteger(GL_MAX_TEXTURE_SIZE));
    
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
    
    indexCircleCircle = glGetUniformLocation(circleProgram, "circle");
    indexCircleColor = glGetUniformLocation(circleProgram, "color");
    indexCircleMinLength = glGetUniformLocation(circleProgram, "minLength");
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
    FontKey fontKey = new FontKey(font, size);
    if (fontKey.equals(lastFontKey)) {
      return lastFontData;
    }
    lastFontKey = fontKey;
    lastFontData = fontData.computeIfAbsent(fontKey, key -> {
      ByteBuffer data = fontBuffer.get(font);
      STBTTPackedchar.Buffer[] charData = {STBTTPackedchar.malloc(128 - 32), STBTTPackedchar.malloc(256 - 192), STBTTPackedchar.malloc(1)};
      ByteBuffer bitmap = BufferUtils.createByteBuffer(1024 * 1024);
      try (STBTTPackContext pc = STBTTPackContext.malloc()) {
        stbtt_PackBegin(pc, bitmap, 1024, 1024, 0, 1);
        stbtt_PackSetOversampling(pc, 3, 1);
        STBTTPackRange.Buffer ranges = STBTTPackRange.malloc(3);
        memSet(ranges.address(), 0, ranges.capacity() * STBTTPackRange.SIZEOF);
        ranges.get(0).font_size(size).first_unicode_codepoint_in_range(32).num_chars(128 - 32).chardata_for_range(charData[0]);
        ranges.get(1).font_size(size).first_unicode_codepoint_in_range(192).num_chars(256 - 192).chardata_for_range(charData[1]);
        ranges.get(2).font_size(size).first_unicode_codepoint_in_range(8226).num_chars(1).chardata_for_range(charData[2]);
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
    return lastFontData;
  }
  
  void destroy() {
    glfwMakeContextCurrent(NULL);
    try {
      synchronousQueue.put(new DestroyRequest());
    } catch (InterruptedException ignore) {
    }
  }
  
  void _destroy() {
    created = false;
    fontData.forEach((font, data) -> {
      for (STBTTPackedchar.Buffer buffer : data.charData) {
        buffer.free();
      }
      glDeleteTextures(data.texture);
    });
    texturesIndexes.forEach(GL11::glDeleteTextures);
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
  }
  
  void flip() {
    glfwSwapBuffers(window);
    glClear(GL_COLOR_BUFFER_BIT);
    time = System.nanoTime();
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
  public void fillCircle(double x, double y, double radius, double width) {
    glUseProgram(circleProgram);
    glBindVertexArray(circleVao);
    glUniform1f(indexCircleMinLength, width <= 0 ? 0 : (float) ((1 - width / radius) * (1 - width / radius)));
    glUniform4f(indexCircleCircle, (float) ((x + translateX) * 2 / Window.width - 1), (float) ((y + translateY) * 2 / height - 1), (float) (radius * 2 / Window.width), (float) (radius * 2 / height));
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
  }
  
  @Override
  public void fillRectangle(double x, double y, double width, double height, double angle) {
    glUseProgram(program);
    glBindVertexArray(vao);
    mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1).translate((float) (x + translateX), (float) (y + translateY), 0).rotateZ((float) angle).scale((float) width, (float) height, 1);
    bufferMat4x4.clear();
    glUniformMatrix4fv(indexStdMatrix, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }
  
  @Override
  public void drawLineCenter(double x, double y, double length, double angle) {
    glUseProgram(program);
    glBindVertexArray(vao);
    mat4x4.translation(-1, -1, 0).scale(2f / width, 2f / height, 1).translate((float) (x + translateX), (float) (y + translateY), 0)
            .rotateZ((float) angle).scale((float) length, 1, 1);
    bufferMat4x4.clear();
    glUniformMatrix4fv(indexStdMatrix, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
  }
  
  @Override
  public void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture, double angle) {
    if ((texture instanceof SimpleTexture && ((SimpleTexture) texture).destroyed) || (texture instanceof AtlasTexture && (((AtlasTexture) texture).destroyed || ((AtlasTexture) texture).atlas.destroyed))) {
      throw new RuntimeException("Tried to draw destroyed texture!");
    }
    glUseProgram(texProgram);
    glBindVertexArray(texVao);
    glBindTexture(GL_TEXTURE_2D, texture instanceof SimpleTexture ? ((SimpleTexture) texture).texture : ((AtlasTexture) texture).atlas.texture);
    mat4x4.translation(-1, -1, 0).scale(2f / Window.width, 2f / Window.height, 1).translate((float) (x + translateX), (float) (y + translateY), 0).rotateZ((float) angle).scale((float) width, (float) height, 1);
    glUniformMatrix4fv(indexTexScreenPosition, false, mat4x4.get(bufferMat4x4));
    if (texture instanceof SimpleTexture) {
      mat4x4.scaling(1f / texture.getWidth(), -1f / texture.getHeight(), 1).translate((float) s1, (float) t1, 0).scale((float) (s2 - s1), (float) (t2 - t1), 1).translate(0.5f, 0.5f, 0.0f);
    } else if (texture instanceof AtlasTexture) {
      int n = ((AtlasTexture) texture).atlas.n;
      int i = ((AtlasTexture) texture).i;
      mat4x4.scaling(1f / (n * texture.getWidth()), -1f / texture.getHeight(), 1).translate((float) s1 + i * texture.getWidth(), (float) t1, 0).scale((float) (s2 - s1), (float) (t2 - t1), 1).translate(0.5f, 0.5f, 0.0f);
    }
    glUniformMatrix4fv(indexTexTexPosition, false, mat4x4.get(bufferMat4x4));
    glDrawArrays(GL_TRIANGLES, 0, 6);
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
        } else if (c == 8226) {
          index = 2;
          position = 0;
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
        } else if (c == 8226) {
          index = 2;
          position = 0;
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
  
  @SuppressWarnings("unchecked")
  void input() {
    try {
      synchronousQueue.put(new InputRequest());
    } catch (InterruptedException ignore) {
    }
    try {
      List<Object> inputs = (List<Object>) synchronousQueue.take();
      for (Object input : inputs) {
        if (input instanceof MoveInput) {
          Ui.getUi().pushMouseMove(((MoveInput) input).x, ((MoveInput) input).y, ((MoveInput) input).time);
          continue;
        }
        if (input instanceof KeyInput) {
          Ui.getUi().pushKeyButton(((KeyInput) input).key, ((KeyInput) input).down, ((KeyInput) input).time);
          continue;
        }
        if (input instanceof MouseInput) {
          Ui.getUi().pushMouseButton(((MouseInput) input).button, ((MouseInput) input).down, ((MouseInput) input).time);
          continue;
        }
        if (input instanceof ScrollInput) {
          Ui.getUi().pushMouseScroll(((ScrollInput) input).scroll, ((ScrollInput) input).time);
          continue;
        }
        if (input instanceof ModsInput) {
          Ui.getUi().pushChar(((ModsInput) input).codepoint, ((ModsInput) input).mods, ((ModsInput) input).time);
          continue;
        }
      }
    } catch (InterruptedException ignore) {
    }
  }
  
  public String getClipboard() {
    return glfwGetClipboardString(window);
  }
  
  public void setClipboard(String clipboard) {
    glfwSetClipboardString(window, clipboard);
  }
  
  public void setCursor(Image image, int xOffset, int yOffset) {
    if (image.ignoreAlpha) {
      throw new RuntimeException("Cursor shouldn't have ignoreAlpha set to true!");
    }
    GLFWImage iconImage = GLFWImage.malloc();
    iconImage.set(image.getWidth(), image.getHeight(), image.data);
    long cursor = glfwCreateCursor(iconImage, xOffset, image.getHeight() - 1 - yOffset);
    glfwSetCursor(window, cursor);
    iconImage.free();
  }
  
  public int getCodepoint(int key) {
    String string = glfwGetKeyName(key, 0);
    if (string == null || string.codePointCount(0, string.length()) == 0) {
      return 0;
    }
    return string.codePointAt(0);
  }
  
  private static class CreateRequest {
    public final String title;
    public final Image image;
    public final boolean fullscreen;
    
    public CreateRequest(String title, Image image, boolean fullscreen) {
      this.title = title;
      this.image = image;
      this.fullscreen = fullscreen;
    }
  }
  
  private static class DestroyRequest {
  }
  
  private static class InputRequest {
  }
  
  private static class ScrollInput {
    public final long time;
    public final int scroll;
    
    public ScrollInput(long time, int scroll) {
      this.time = time;
      this.scroll = scroll;
    }
  }
  
  private static class KeyInput {
    public final long time;
    public final int key;
    public final boolean down;
    
    public KeyInput(long time, int key, boolean down) {
      this.time = time;
      this.key = key;
      this.down = down;
    }
  }
  
  private static class MouseInput {
    public final long time;
    public final int button;
    public final boolean down;
    
    public MouseInput(long time, int button, boolean down) {
      this.time = time;
      this.button = button;
      this.down = down;
    }
  }
  
  private static class MoveInput {
    public final long time;
    public final double x;
    public final double y;
    
    public MoveInput(long time, double x, double y) {
      this.time = time;
      this.x = x;
      this.y = y;
    }
  }
  
  private static class ModsInput {
    public final long time;
    public final int codepoint;
    public final EnumSet<KeyModifier> mods;
    
    public ModsInput(long time, int codepoint, EnumSet<KeyModifier> mods) {
      this.time = time;
      this.codepoint = codepoint;
      this.mods = mods;
    }
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
