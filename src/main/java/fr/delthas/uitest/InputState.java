package fr.delthas.uitest;

public interface InputState {
  double getMouseX(Component component);

  double getMouseY(Component component);

  boolean isKeyDown(int keycode);

  boolean isMouseDown(int button);
}
