package fr.delthas.uitest;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Stack {
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

  protected void pushMouseMove(double x, double y) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushMouseMove(x, y)) {
        return;
      }
    }
  }

  protected void pushMouseButton(double x, double y, int button, boolean down) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushMouseButton(x, y, button, down)) {
        return;
      }
    }
  }

  protected void pushKeyButton(double x, double y, int key, boolean down) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushKeyButton(x, y, key, down)) {
        return;
      }
    }
  }

  protected void pushChar(double x, double y, int codepoint, int mods) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious()) {
      if (it.previous().pushChar(x, y, codepoint, mods)) {
        return;
      }
    }
  }

  protected void render(InputState inputState, Drawer drawer) {
    ListIterator<Layer> it = layers.listIterator(layers.size());
    while (it.hasPrevious() && !it.previous().isOpaque()) {
    }
    if (it.hasPrevious()) {
      it.previous();
    }
    while (it.hasNext()) {
      it.next().render(inputState, drawer);
    }
  }
}
