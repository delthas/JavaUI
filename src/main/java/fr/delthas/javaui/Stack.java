package fr.delthas.javaui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("ProtectedMemberInFinalClass")
final class Stack {
  protected List<Layer> layers = new ArrayList<>();
  
  public void push(Layer layer) {
    layers.add(layer);
  }
  
  public Layer pop() {
    if (layers.isEmpty()) {
      return null;
    }
    return layers.remove(layers.size() - 1);
  }
  
  public Layer top() {
    if (layers.isEmpty()) {
      return null;
    }
    return layers.get(layers.size() - 1);
  }
  
  protected void pushMouseMove(double x, double y, long time) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushMouseMove(x, y, time)) {
        return;
      }
    }
  }
  
  protected void pushMouseButton(double x, double y, int button, boolean down, long time) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushMouseButton(x, y, button, down, time)) {
        return;
      }
    }
  }
  
  protected void pushMouseScroll(double x, double y, int scroll, long time) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushMouseScroll(x, y, scroll, time)) {
        return;
      }
    }
  }
  
  protected void pushKeyButton(double x, double y, int key, boolean down, long time) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushKeyButton(x, y, key, down, time)) {
        return;
      }
    }
  }
  
  protected void pushChar(double x, double y, String input, EnumSet<KeyModifier> mods, long time) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushChar(x, y, input, mods, time)) {
        return;
      }
    }
  }
  
  protected void render(InputState inputState, Drawer drawer) {
    if (layers.isEmpty()) {
      return;
    }
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious() && !it.previous().isOpaque()) {
    }
    while (it.hasNext()) {
      it.next().render(inputState, drawer);
    }
  }
}
