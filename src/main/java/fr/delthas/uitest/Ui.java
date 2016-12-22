package fr.delthas.uitest;

import java.util.HashSet;
import java.util.Set;

public final class Ui implements InputState {
  private static final Ui instance;

  static {
    instance = new Ui();
  }

  private Stack stack = new Stack();
  private Window window = new Window();
  private Set<Integer> keysState = new HashSet<>(20);
  private Set<Integer> mouseState = new HashSet<>(3);
  private int scroll = 0;
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

  public void create(String title, Icon icon) {
    window.create(title, icon, true);
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

  void pushMouseMove(double x, double y) {
    mouseX = x;
    mouseY = y;
    stack.pushMouseMove(x, y);
  }

  void pushMouseButton(int button, boolean down) {
    if (down) {
      mouseState.add(button);
    } else {
      mouseState.remove(button);
    }
    stack.pushMouseButton(mouseX, mouseY, button, down);
  }

  void pushKeyButton(int key, boolean down) {
    if (down) {
      keysState.add(key);
    } else {
      keysState.remove(key);
    }
    stack.pushKeyButton(mouseX, mouseY, key, down);
  }

  void pushChar(int codepoint, int mods) {
    stack.pushChar(mouseX, mouseY, codepoint, mods);
  }

  void pushMouseScroll(int scroll) {
    // TODO push instead of storing it
    this.scroll += scroll;
  }

  public float getLineHeight(Font font, float size) {
    return window.getLineHeight(font, size);
  }

  public void getFontMetrics(Font font, float size, float[] metrics) {
    window.getFontMetrics(font, size, metrics);
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
