package fr.delthas.uitest;

public class Component {
  private Layer layer;
  private double x;
  private double y;
  private double width;
  private double height;

  @SuppressWarnings("EmptyMethod")
  protected void reset() {
  }

  void reset(Layer layer, double x, double y, double width, double height) {
    this.layer = layer;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    reset();
  }

  @SuppressWarnings("SameReturnValue")
  protected boolean pushMouseMove(double x, double y) {
    return false;
  }

  @SuppressWarnings("SameReturnValue")
  protected boolean pushMouseButton(double x, double y, int button, boolean down) {
    return false;
  }

  @SuppressWarnings("SameReturnValue")
  protected boolean pushKeyButton(double x, double y, int key, boolean down) {
    return false;
  }

  @SuppressWarnings("EmptyMethod")
  protected void render(InputState inputState, Drawer drawer) {

  }

  public Layer getLayer() {
    return layer;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  protected boolean isInBounds(double x, double y) {
    return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
  }
}
