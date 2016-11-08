package fr.delthas.uitest;

import java.awt.Color;

public interface Drawer {

  public void pushTranslate(double x, double y);

  public void popTranslate();

  public void fillCircle(double x, double y, double radius, boolean centered);

  public void fillRectangle(double x, double y, double width, double height, boolean centered);

  public void drawImage(double x, double y, double width, double height, boolean centered);

  public void drawText(double x, double y, double ratio, String text, Font font, boolean centered);

  public void setColor(Color color);
}
