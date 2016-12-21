package fr.delthas.uitest;

import java.awt.*;

public interface Drawer {
  void pushTranslate(double x, double y);

  void popTranslate();

  default void fillCircle(double x, double y, double radius, boolean centered) {
    if (centered) {
      fillCircle(x, y, radius);
    } else {
      fillCircle(x + radius / 2, y + radius / 2, radius);
    }
  }

  void fillCircle(double x, double y, double radius);

  default void fillRectangle(double x, double y, double width, double height, boolean centered) {
    if (centered) {
      fillRectangle(x, y, width, height);
    } else {
      fillRectangle(x + width / 2, y + height / 2, width, height);
    }
  }

  void fillRectangle(double x, double y, double width, double height);

  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Image image, boolean centered) {
    if (centered) {
      drawImageIn(x, y, maxWidth, maxHeight, image);
    } else {
      drawImageIn(x + maxWidth / 2, y + maxHeight / 2, maxWidth, maxHeight, image);
    }
  }

  default void drawImageIn(double x, double y, double maxWidth, double maxHeight, Image image) {
    double widthRatio = image.getWidth() / maxWidth;
    double heightRatio = image.getHeight() / maxHeight;
    if (widthRatio > heightRatio) {
      drawImage(x, y, maxWidth, image.getHeight() / widthRatio, image);
    } else {
      drawImage(x, y, image.getWidth() / heightRatio, maxHeight, image);
    }
  }

  default void drawImage(double x, double y, Image image) {
    drawImage(x, y, image.getWidth(), image.getHeight(), image, true);
  }

  default void drawImage(double x, double y, double width, double height, Image image) {
    drawImage(x, y, width, height, image, true);
  }

  default void drawImage(double x, double y, double width, double height, Image image, boolean centered) {
    if (centered) {
      drawImage(x, y, width, height, 0, 0, image.getWidth(), image.getHeight(), image);
    } else {
      drawImage(x + width / 2, y + height / 2, width, height, 0, 0, image.getWidth(), image.getHeight(), image);
    }
  }

  void drawImage(double x, double y, double width, double height, double s1, double t1, double s2, double t2, Image image);

  // TODO
  // default void drawText(double x, double y, String text, Font font, float size) {
  //  drawText(x, y, text, font, size);
  // }

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
