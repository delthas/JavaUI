package fr.delthas.javaui;

public interface InputState {
  default double getMouseX() {
    return getMouseX(null);
  }
  
  double getMouseX(Component component);
  
  default double getMouseY() {
    return getMouseY(null);
  }
  
  double getMouseY(Component component);
  
  boolean isKeyDown(int keycode);
  
  boolean isMouseDown(int button);
}
