package fr.delthas.javaui;

import java.util.EnumSet;

/**
 * KeyModifier is a key modifier that is pressed/down when a character is input; used in {@link Component#pushChar(double, double, String, EnumSet, long)}.
 * <p>
 * KeyModifier do not respresent a physical key location, rather they simply represent a layout-independent abstract modifier. For example, if pressing the left shift key, then pressing the a key while the left shift key is down, the {@link KeyModifier#SHIFT} would be passed.
 */
public enum KeyModifier {
  
  /**
   * The Control key modifier, for example the modifier used in all Ctrl+S, Ctrl+C, ..., shortcuts.
   */
  CTRL,
  /**
   * The Control key modifier, for example the modifier used in all Alt+0, Alt+Tab, ..., shortcuts.
   */
  ALT,
  /**
   * The Control key modifier, for example the modifier used in all Shift+a, ..., key presses.
   */
  SHIFT,
  /**
   * The Super key modifier (which is called the Win/Windows key modifier on Windows, and the Command key on Mac OS), for example the modifier used in all Win+R, ..., shortcuts.
   */
  SUPER
  
}
