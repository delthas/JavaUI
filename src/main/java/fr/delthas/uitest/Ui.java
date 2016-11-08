package fr.delthas.uitest;

import java.util.HashSet;
import java.util.Set;

public class Ui implements InputState {

  static {
    instance = new Ui();
  }

  private static final Ui instance;

  private Stack stack = new Stack();
  private Window window = new Window();
  private Set<Integer> keysState = new HashSet<>(20);
  private Set<Integer> mouseState = new HashSet<>(3);
  private int scroll = 0;

  private double mouseX, mouseY;

  private Ui() {}

  public void create(String title) {
    window.create(title, true);
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

  void pushMouseScroll(int scroll) {
    // TODO push instead of storing it
    this.scroll += scroll;
  }

  @Override
  public boolean isKeyDown(int scancode) {
    return keysState.contains(scancode);
  }

  @Override
  public boolean isMouseDown(int button) {
    return mouseState.contains(button);
  }

  public void setVisible(boolean visible) {
    window.setVisible(visible);
  }

  public static int getWidth() {
    return Window.getWidth();
  }

  public static int getHeight() {
    return Window.getHeight();
  }

  public static Ui getUi() {
    return instance;
  }

}
