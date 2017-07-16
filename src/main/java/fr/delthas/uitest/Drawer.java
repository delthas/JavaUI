package fr.delthas.uitest;

import java.awt.*;

public interface Drawer {
  void pushTranslate(double x, double y);
  
  void popTranslate();
  
  default void drawLine(double x1, double y1, double x2, double y2) {
    drawLineCenter((x1 + x2) / 2, (y1 + y2) / 2, Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)), Math.atan2(y2 - y1, x2 - x1));
  }
  
  void drawLineCenter(double x, double y, double length, double angle);
  
  default void fillCircle(double x, double y, double radius) {
    fillCircle(x, y, radius, true);
  }
  
  default void fillCircle(double x, double y, double radius, boolean centered) {
    if (centered) {
      fillCircle(x, y, radius, 0);
    } else {
      fillCircle(x + radius / 2, y + radius / 2, radius, 0);
    }
  }
  
  default void fillCircle(double x, double y, double radius, double width, boolean centered) {
    if (centered) {
      fillCircle(x, y, radius, width);
    } else {
      fillCircle(x + radius / 2, y + radius / 2, radius, width);
    }
  }
  
  void fillCircle(double x, double y, double radius, double width);
  
  default void fillRectangle(double x, double y, double width, double height, boolean centered, double angle) {
    if (centered) {
      fillRectangle(x, y, width, height, angle);
    } else {
      fillRectangle(x + width / 2, y + height / 2, width, height, angle);
    }
  }
  
  void fillRectangle(double x, double y, double width, double height, double angle);
  
  default void fillRectangle(double x, double y, double width, double height, boolean centered) {
    fillRectangle(x, y, width, height, centered, 0);
  }
  
  default void fillRectangle(double x, double y, double width, double height) {
    fillRectangle(x, y, width, height, 0);
  }
  
  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Texture texture, boolean centered) {
    if (centered) {
      drawImageIn(x, y, maxWidth, maxHeight, texture);
    } else {
      drawImageIn(x + maxWidth / 2, y + maxHeight / 2, maxWidth, maxHeight, texture);
    }
  }
  
  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Texture texture) {
    double widthRatio = texture.getWidth() / maxWidth;
    double heightRatio = texture.getHeight() / maxHeight;
    if (widthRatio > heightRatio) {
      drawImage(x, y, maxWidth, texture.getHeight() / widthRatio, texture);
    } else {
      drawImage(x, y, texture.getWidth() / heightRatio, maxHeight, texture);
    }
  }
  
  default void drawImage(double x, double y, Texture texture) {
    drawImage(x, y, texture, true);
  }
  
  default void drawImage(double x, double y, Texture texture, boolean centered) {
    drawImage(x, y, texture.getWidth(), texture.getHeight(), texture, centered);
  }
  
  default void drawImage(double x, double y, double width, double height, Texture texture) {
    drawImage(x, y, width, height, texture, true);
  }
  
  default void drawImage(double x, double y, double width, double height, Texture texture, boolean centered) {
    drawImage(x, y, width, height, texture, centered, 0);
  }
  
  default void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture) {
    drawImage(x, y, width, height, s1, t1, s2, t2, texture, 0);
  }
  
  default void drawImage(double x, double y, Texture texture, double angle) {
    drawImage(x, y, texture, true, angle);
  }
  
  default void drawImage(double x, double y, Texture texture, boolean centered, double angle) {
    drawImage(x, y, texture.getWidth(), texture.getHeight(), texture, centered, angle);
  }
  
  default void drawImage(double x, double y, double width, double height, Texture texture, double angle) {
    drawImage(x, y, width, height, texture, true, angle);
  }
  
  default void drawImage(double x, double y, double width, double height, Texture texture, boolean centered, double angle) {
    if (centered) {
      drawImage(x, y, width, height, 0, 0, texture.getWidth(), texture.getHeight(), texture, angle);
    } else {
      drawImage(x + width / 2, y + height / 2, width, height, 0, 0, texture.getWidth(), texture.getHeight(), texture, angle);
    }
  }
  
  void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Texture texture, double angle);
  
  // TODO
  // default void drawText(double x, double y, String text, Font font, float size) {
  //  drawText(x, y, text, font, size);
  // }
  
  default float getLineHeight(Font font, float size) {
    float[] metrics = new float[3];
    getFontMetrics(font, size, metrics);
    return metrics[0] - metrics[1] + metrics[2];
  }
  
  void getFontMetrics(Font font, float size, float[] metrics);
  
  default float getTextWidth(String text, Font font, float size) {
    return getTextWidth(text, font, size, null);
  }
  
  float getTextWidth(String text, Font font, float size, float[] sizes);
  
  default float drawText(double x, double y, String text, Font font, float size, boolean xCentered, boolean yCentered) {
    return drawText(x, y, text, font, size, xCentered, yCentered, null);
  }
  
  float drawText(double x, double y, String text, Font font, float size, boolean xCentered, boolean yCentered, float[] sizes);
  
  void setColor(Color color);
}
