package fr.delthas.javaui;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class Ui implements InputState, Key, Mouse {
  private static final Ui instance;
  
  static {
    instance = new Ui();
  }
  
  private Stack stack = new Stack();
  private Window window = new Window();
  private Set<Integer> keysState = new HashSet<>(20);
  private Set<Integer> mouseState = new HashSet<>(3);
  private double mouseX, mouseY;
  
  private Ui() {}
  
  public static int getWidth() {
    return Window.getWidth();
  }
  
  public static int getHeight() {
    return Window.getHeight();
  }
  
  public static Ui getUi() {
    return instance;
  }
  
  public void create(String title, boolean fullscreen) {
    window.create(title, null, fullscreen);
  }
  
  public void create(String title) {
    window.create(title, null, true);
  }
  
  public void create(String title, Image image, boolean fullscreen) {
    window.create(title, image, fullscreen);
  }
  
  public void create(String title, Image image) {
    window.create(title, image, true);
  }
  
  public void destroy() {
    window.destroy();
    while (stack.pop() != null) {
    }
  }
  
  public void input() {
    window.input();
  }
  
  public void render() {
    stack.render(this, window);
    window.flip();
  }
  
  public void push(Layer layer) {
    stack.push(layer);
  }
  
  public Layer pop() {
    return stack.pop();
  }
  
  public Layer top() {
    return stack.top();
  }
  
  void pushMouseMove(double x, double y, long time) {
    mouseX = x;
    mouseY = y;
    stack.pushMouseMove(x, y, time);
  }
  
  void pushMouseButton(int button, boolean down, long time) {
    if (down) {
      mouseState.add(button);
    } else {
      mouseState.remove(button);
    }
    stack.pushMouseButton(mouseX, mouseY, button, down, time);
  }
  
  void pushMouseScroll(int scroll, long time) {
    stack.pushMouseScroll(mouseX, mouseY, scroll, time);
  }
  
  void pushKeyButton(int key, boolean down, long time) {
    if (down) {
      keysState.add(key);
    } else {
      keysState.remove(key);
    }
    stack.pushKeyButton(mouseX, mouseY, key, down, time);
  }
  
  void pushChar(int codepoint, EnumSet<KeyModifier> mods, long time) {
    stack.pushChar(mouseX, mouseY, codepoint, mods, time);
  }
  
  public float getLineHeight(Font font, float size) {
    return window.getLineHeight(font, size);
  }
  
  public void getFontMetrics(Font font, float size, float[] metrics) {
    window.getFontMetrics(font, size, metrics);
  }
  
  public int getCodepoint(int key) {
    return window.getCodepoint(key);
  }
  
  public float getTextWidth(String text, Font font, float size) {
    return window.getTextWidth(text, font, size);
  }
  
  public float getTextWidth(String text, Font font, float size, float[] sizes) {
    return window.getTextWidth(text, font, size, sizes);
  }
  
  public String getClipboard() {
    return window.getClipboard();
  }
  
  public void setClipboard(String clipboard) {
    window.setClipboard(clipboard);
  }
  
  public void setCursor(Image image, int xOffset, int yOffset) {
    window.setCursor(image, xOffset, yOffset);
  }
  
  @Override
  public double getMouseX(Component component) {
    return mouseX - (component == null ? 0 : component.getX());
  }
  
  @Override
  public double getMouseY(Component component) {
    return mouseY - (component == null ? 0 : component.getY());
  }
  
  @Override
  public boolean isKeyDown(int scancode) {
    return keysState.contains(scancode);
  }
  
  @Override
  public boolean isMouseDown(int button) {
    return mouseState.contains(button);
  }
  
  Window getWindow() {
    return window;
  }
  
  public void setVisible(boolean visible) {
    window.setVisible(visible);
  }
}
