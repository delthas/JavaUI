package fr.delthas.javaui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * Layer is the base class for a layer in the stack of layers of the UI system.
 * <p>
 * For a general overview of the UI system, refer to the {@link fr.delthas.javaui} Javadoc.
 * <p>
 * A layer is an object that contains several components, and should typically represent a "view" in an application. For example, if designing a video game, the "title screen" could be a Layer, and the different UI elements on this view would be different {@link Component} added to this Layer, such as a {@link Button}, a {@link TextField}, ..., or a custom {@link Component} class.
 * <p>
 * As such, Layer is but a simple class that holds components and acts as a "proxy" for inputs, that propagates inputs to all its components. By default, a layer can be transparent, but it has an "opaque" property that, if true, means that layers below it don't need to be drawn.
 * <p>
 * To use, simply instantiate a new layer with {@link #Layer()}, and add and remove components wuth {@link #addComponent(double, double, double, double, Component)} and {@link #removeComponent(Component)}; then push the layer onto the UI stack with {@link Ui#push(Layer)}, or the shortcut {@link #push()}. You may set the opaque property with {@link #setOpaque(boolean)}.
 *
 * @see #addComponent(double, double, double, double, Component)
 * @see #removeComponent(Component)
 * @see #push()
 */
public final class Layer {
  private final List<Component> components = new ArrayList<>();
  private boolean opaque;
  
  /**
   * Creates a new empty layer without any component, that is not opaque.
   */
  public Layer() {
  }
  
  /**
   * Creates a new empty layer without any component, that has the specified opactiy.
   * <p>
   * By default, a layer can be transparent, but it has an "opaque" property that, if true, means that layers below it don't need to be drawn.
   *
   * @param opaque Whether this layer is opaque (true), or not (false).
   */
  public Layer(boolean opaque) {
    this.opaque = opaque;
  }
  
  /**
   * Adds the specified component to the layer, by specifying its component rectangle, through the position of its lower-left position, its width, and its height.
   * <p>
   * A component MUST be in at most one layer at a time, so if a component is already added to another layer, this method will throw {@link IllegalArgumentException}, and if it is already added to this layer, it will remoive it first and add it back again.
   *
   * @param x         The x position, in pixels, of the lower-left corner of the component rectangle of the component to be added to this layer.
   * @param y         The y position, in pixels, of the lower-left corner of the component rectangle of the component to be added to this layer.
   * @param width     The width, in pixels, of the component rectangle of the component to be added to this layer.
   * @param height    The height, in pixels, of the component rectangle of the component to be added to this layer.
   * @param component The component to add to this layer, must be non-null and not already added to another layer.
   * @return Itself, for chaining.
   */
  public Layer addComponent(double x, double y, double width, double height, Component component) {
    Objects.requireNonNull(component);
    if (component.getLayer() != null) {
      if (component.getLayer() != this) {
        throw new IllegalArgumentException("The component is already in a Layer! Remove it from its other layer first.");
      }
      components.remove(component);
    }
    component.reset(this, x, y, width, height);
    components.add(component);
    return this;
  }
  
  /**
   * Adds the specified component to the layer, so that its component rectangle is the entirety of the screen.
   * <p>
   * This method is equivalent to the following: {@code addComponent(0, 0, Ui.getWidth(), Ui.getHeight(), component);}.
   *
   * @param component The component to add to this layer, must be non-null and not already added to another layer.
   * @return Itself, for chaining.
   */
  public Layer addComponent(Component component) {
    return addComponent(0, 0, Ui.getWidth(), Ui.getHeight(), component);
  }
  
  /**
   * Removes the specified component from the layer, if it is already added in this layer.
   * <p>
   * If the component is not in this layer, this is a no-op.
   *
   * @param component The component to remove from this layer, must be non-null.
   */
  public void removeComponent(Component component) {
    Objects.requireNonNull(component);
    if (component.getLayer() != this) {
      return;
    }
    component.removeFromLayer();
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
  
  protected boolean pushChar(double x, double y, String input, EnumSet<KeyModifier> mods, long time) {
    for (Component component : components) {
      if (component.pushChar(x - component.getX(), y - component.getY(), input, mods, time)) {
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
  
  /**
   * Pushes the layer onto the UI stack.
   * <p>
   * This method is equivalent to {@code Ui.getUi().push(layer);}.
   *
   * @return Itself, for chaining.
   * @see Ui#push(Layer)
   */
  public Layer push() {
    Ui.getUi().push(this);
    return this;
  }
  
  /**
   * Returns whether the layer is opaque.
   * <p>
   * By default, a layer can be transparent, but it has an "opaque" property that, if true, means that layers below it don't need to be drawn.
   *
   * @return Whether the layer is opaque (true), or not (false).
   */
  public final boolean isOpaque() {
    return opaque;
  }
  
  /**
   * Sets whether the layer is opaque.
   * <p>
   * By default, a layer can be transparent, but it has an "opaque" property that, if true, means that layers below it don't need to be drawn.
   *
   * @param opaque Whether the layer is to be opaque (true), or not (false).
   * @return Itself, for chaining.
   */
  public Layer setOpaque(boolean opaque) {
    this.opaque = opaque;
    return this;
  }
}
