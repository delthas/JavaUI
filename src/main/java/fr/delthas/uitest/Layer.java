package fr.delthas.uitest;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Layer {
  protected List<Component> components = new ArrayList<>();
  private boolean opaque;
  
  protected void reset() {
    for (Component component : components) {
      component.reset();
    }
  }
  
  public Layer addComponent(double x, double y, double width, double height, Component component) {
    component.reset(this, x, y, width, height);
    components.add(component);
    return this;
  }
  
  public Layer addComponent(Component component) {
    return addComponent(0, 0, Ui.getWidth(), Ui.getHeight(), component);
  }
  
  public void removeComponent(Component component) {
    components.remove(component);
  }
  
  protected boolean pushMouseMove(double x, double y, long time) {
    for (Component component : components) {
      if (component.pushMouseMove(x - component.getX(), y - component.getY(), time)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean pushMouseButton(double x, double y, int button, boolean down, long time) {
    for (Component component : components) {
      if (component.pushMouseButton(x - component.getX(), y - component.getY(), button, down, time)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean pushMouseScroll(double x, double y, int scroll, long time) {
    for (Component component : components) {
      if (component.pushMouseScroll(x - component.getX(), y - component.getY(), scroll, time)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean pushKeyButton(double x, double y, int key, boolean down, long time) {
    for (Component component : components) {
      if (component.pushKeyButton(x - component.getX(), y - component.getY(), key, down, time)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean pushChar(double x, double y, int codepoint, EnumSet<KeyModifier> mods, long time) {
    for (Component component : components) {
      if (component.pushChar(x - component.getX(), y - component.getY(), codepoint, mods, time)) {
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
  
  public Layer push() {
    Ui.getUi().push(this);
    return this;
  }
  
  public boolean isOpaque() {
    return opaque;
  }
  
  public Layer setOpaque(boolean opaque) {
    this.opaque = opaque;
    return this;
  }
}
