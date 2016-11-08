package fr.delthas.uitest;

import java.util.ArrayList;
import java.util.List;

public class Layer {

  private boolean opaque;

  protected List<Component> components = new ArrayList<>();

  protected void reset() {
    for (Component component : components) {
      component.reset();
    }
  }

  public void addComponent(Component component, double x, double y, double width, double height) {
    component.reset(this, x, y, width, height);
    components.add(component);
  }

  protected boolean pushMouseMove(double x, double y) {
    for (Component component : components) {
      if (component.pushMouseMove(x - component.getX(), y - component.getY())) {
        return true;
      }
    }
    return false;
  }

  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    for (Component component : components) {
      if (component.pushMouseButton(x - component.getX(), y - component.getY(), button, down)) {
        return true;
      }
    }
    return false;
  }

  protected boolean pushKeyButton(double x, double y, int key, boolean down) {
    for (Component component : components) {
      if (component.pushKeyButton(x - component.getX(), y - component.getY(), key, down)) {
        return true;
      }
    }
    return false;
  }

  protected void render(InputState inputState, Drawer drawer) {
    for (Component component : components) {
      drawer.pushTranslate(component.getX(), component.getY());
      component.render(inputState, drawer);
      drawer.popTranslate();
    }
  }

  public boolean isOpaque() {
    return opaque;
  }

  protected void setOpaque(boolean opaque) {
    this.opaque = opaque;
  }

}
