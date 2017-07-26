package fr.delthas.javaui;

/**
 * Key represents a layout independent identifier for a physcial key on a keyboard, used in {@link Component#pushKeyButton(double, double, int, boolean, long)} and {@link InputState#isKeyDown(int)}.
 * <p>
 * <b>The keyboard key identifier specifies a LAYOUT-INDEPENDENT, physical key location on the keyboard, and the values in {@link Key} are named after a stands US keyboard layout (QWERTY). For example, the usual physical key used to press forward in FPS games, which is W for a standard US keyboard layout (QWERTY), and Z for a standard French keyboard layout (AZERTY), will always be {@link Key#KEY_W}, regardless of the current keyboard layout of the system.</b>
 *
 * @see Component#pushKeyButton(double, double, int, boolean, long)
 * @see InputState#isKeyDown(int)
 * @see KeyModifier
 * @see Mouse
 */
public interface Key {
  /**
   * The physical keyboard key that is the space bar in the standard US keyboard layout.
   */
  int KEY_SPACE = 32;
  /**
   * The physical keyboard key that is apostrophe (') in the standard US keyboard layout.
   */
  int KEY_APOSTROPHE = 39;
  /**
   * The physical keyboard key that is comma (,) in the standard US keyboard layout.
   */
  int KEY_COMMA = 44;
  /**
   * The physical keyboard key that is minus (-) in the standard US keyboard layout.
   */
  int KEY_MINUS = 45;
  /**
   * The physical keyboard key that is period (.) in the standard US keyboard layout.
   */
  int KEY_PERIOD = 46;
  /**
   * The physical keyboard key that is slash (/) in the standard US keyboard layout.
   */
  int KEY_SLASH = 47;
  /**
   * The physical keyboard key that is 0 in the standard US keyboard layout.
   */
  int KEY_0 = 48;
  /**
   * The physical keyboard key that is 1 in the standard US keyboard layout.
   */
  int KEY_1 = 49;
  /**
   * The physical keyboard key that is 2 in the standard US keyboard layout.
   */
  int KEY_2 = 50;
  /**
   * The physical keyboard key that is 3 in the standard US keyboard layout.
   */
  int KEY_3 = 51;
  /**
   * The physical keyboard key that is 4 in the standard US keyboard layout.
   */
  int KEY_4 = 52;
  /**
   * The physical keyboard key that is 5 in the standard US keyboard layout.
   */
  int KEY_5 = 53;
  /**
   * The physical keyboard key that is 6 in the standard US keyboard layout.
   */
  int KEY_6 = 54;
  /**
   * The physical keyboard key that is 7 in the standard US keyboard layout.
   */
  int KEY_7 = 55;
  /**
   * The physical keyboard key that is 8 in the standard US keyboard layout.
   */
  int KEY_8 = 56;
  /**
   * The physical keyboard key that is 9 in the standard US keyboard layout.
   */
  int KEY_9 = 57;
  /**
   * The physical keyboard key that is semicolon (;) in the standard US keyboard layout.
   */
  int KEY_SEMICOLON = 59;
  /**
   * The physical keyboard key that is semicolon (=) in the standard US keyboard layout.
   */
  int KEY_EQUAL = 61;
  /**
   * The physical keyboard key that is a in the standard US keyboard layout.
   */
  int KEY_A = 65;
  /**
   * The physical keyboard key that is b in the standard US keyboard layout.
   */
  int KEY_B = 66;
  /**
   * The physical keyboard key that is c in the standard US keyboard layout.
   */
  int KEY_C = 67;
  /**
   * The physical keyboard key that is d in the standard US keyboard layout.
   */
  int KEY_D = 68;
  /**
   * The physical keyboard key that is e in the standard US keyboard layout.
   */
  int KEY_E = 69;
  /**
   * The physical keyboard key that is f in the standard US keyboard layout.
   */
  int KEY_F = 70;
  /**
   * The physical keyboard key that is g in the standard US keyboard layout.
   */
  int KEY_G = 71;
  /**
   * The physical keyboard key that is h in the standard US keyboard layout.
   */
  int KEY_H = 72;
  /**
   * The physical keyboard key that is i in the standard US keyboard layout.
   */
  int KEY_I = 73;
  /**
   * The physical keyboard key that is j in the standard US keyboard layout.
   */
  int KEY_J = 74;
  /**
   * The physical keyboard key that is k in the standard US keyboard layout.
   */
  int KEY_K = 75;
  /**
   * The physical keyboard key that is l in the standard US keyboard layout.
   */
  int KEY_L = 76;
  /**
   * The physical keyboard key that is m in the standard US keyboard layout.
   */
  int KEY_M = 77;
  /**
   * The physical keyboard key that is n in the standard US keyboard layout.
   */
  int KEY_N = 78;
  /**
   * The physical keyboard key that is o in the standard US keyboard layout.
   */
  int KEY_O = 79;
  /**
   * The physical keyboard key that is p in the standard US keyboard layout.
   */
  int KEY_P = 80;
  /**
   * The physical keyboard key that is q in the standard US keyboard layout.
   */
  int KEY_Q = 81;
  /**
   * The physical keyboard key that is r in the standard US keyboard layout.
   */
  int KEY_R = 82;
  /**
   * The physical keyboard key that is s in the standard US keyboard layout.
   */
  int KEY_S = 83;
  /**
   * The physical keyboard key that is t in the standard US keyboard layout.
   */
  int KEY_T = 84;
  /**
   * The physical keyboard key that is u in the standard US keyboard layout.
   */
  int KEY_U = 85;
  /**
   * The physical keyboard key that is v in the standard US keyboard layout.
   */
  int KEY_V = 86;
  /**
   * The physical keyboard key that is w in the standard US keyboard layout.
   */
  int KEY_W = 87;
  /**
   * The physical keyboard key that is x in the standard US keyboard layout.
   */
  int KEY_X = 88;
  /**
   * The physical keyboard key that is y in the standard US keyboard layout.
   */
  int KEY_Y = 89;
  /**
   * The physical keyboard key that is z in the standard US keyboard layout.
   */
  int KEY_Z = 90;
  /**
   * The physical keyboard key that is the left bracket (<code>{</code>) in the standard US keyboard layout.
   */
  int KEY_LEFT_BRACKET = 91;
  /**
   * The physical keyboard key that is backslash (\) in the standard US keyboard layout.
   */
  int KEY_BACKSLASH = 92;
  /**
   * The physical keyboard key that is the right bracket (<code>{</code>) in the standard US keyboard layout.
   */
  int KEY_RIGHT_BRACKET = 93;
  /**
   * The physical keyboard key that is the grave accent (`) in the standard US keyboard layout.
   */
  int KEY_GRAVE_ACCENT = 96;
  /**
   * A physical keyboard key that doesn't exist in the standard US keyboard layout. It is unclear what it maps to in other layouts; but for now the key events on this key aren't ignored, even though the mapping is unclear. Corresponds to GLFW "GLFW_KEY_WORLD_1" value (for which there isn't much documentation.
   */
  int KEY_WORLD_1 = 161;
  /**
   * A physical keyboard key that doesn't exist in the standard US keyboard layout. It is unclear what it maps to in other layouts; but for now the key events on this key aren't ignored, even though the mapping is unclear. Corresponds to GLFW "GLFW_KEY_WORLD_2" value (for which there isn't much documentation.
   */
  int KEY_WORLD_2 = 162;
  /**
   * The physical keyboard key that is the escape key in the standard US keyboard layout.
   */
  int KEY_ESCAPE = 256;
  /**
   * The physical keyboard key that is the enter key (⏎) in the standard US keyboard layout.
   */
  int KEY_ENTER = 257;
  /**
   * The physical keyboard key that is the tabulation key (⇥) in the standard US keyboard layout.
   */
  int KEY_TAB = 258;
  /**
   * The physical keyboard key that is the backspace key (⌫) in the standard US keyboard layout.
   */
  int KEY_BACKSPACE = 259;
  /**
   * The physical keyboard key that is the insert key in the standard US keyboard layout.
   */
  int KEY_INSERT = 260;
  /**
   * The physical keyboard key that is the delete key (⌦) in the standard US keyboard layout.
   */
  int KEY_DELETE = 261;
  /**
   * The physical keyboard key that is the right arrow key (→) in the standard US keyboard layout.
   */
  int KEY_RIGHT = 262;
  /**
   * The physical keyboard key that is the left arrow key (←) in the standard US keyboard layout.
   */
  int KEY_LEFT = 263;
  /**
   * The physical keyboard key that is the down arrow key (↓) in the standard US keyboard layout.
   */
  int KEY_DOWN = 264;
  /**
   * The physical keyboard key that is the up arrow key (↑) in the standard US keyboard layout.
   */
  int KEY_UP = 265;
  /**
   * The physical keyboard key that is the page up key (⇞) in the standard US keyboard layout.
   */
  int KEY_PAGE_UP = 266;
  /**
   * The physical keyboard key that is the page down key (⇟) in the standard US keyboard layout.
   */
  int KEY_PAGE_DOWN = 267;
  /**
   * The physical keyboard key that is the home/origin key (↖) in the standard US keyboard layout.
   */
  int KEY_HOME = 268;
  /**
   * The physical keyboard key that is the end key (↘) in the standard US keyboard layout.
   */
  int KEY_END = 269;
  /**
   * The physical keyboard key that is the caps lock key (⇪) in the standard US keyboard layout.
   */
  int KEY_CAPS_LOCK = 280;
  /**
   * The physical keyboard key that is the scroll lock key in the standard US keyboard layout.
   */
  int KEY_SCROLL_LOCK = 281;
  /**
   * The physical keyboard key that is the numlock key in the standard US keyboard layout.
   */
  int KEY_NUM_LOCK = 282;
  /**
   * The physical keyboard key that is the print screen key in the standard US keyboard layout.
   */
  int KEY_PRINT_SCREEN = 283;
  /**
   * The physical keyboard key that is the pause key in the standard US keyboard layout.
   */
  int KEY_PAUSE = 284;
  /**
   * The physical keyboard key that is the F1 (function key 1) in the standard US keyboard layout.
   */
  int KEY_F1 = 290;
  /**
   * The physical keyboard key that is the F2 (function key 2) in the standard US keyboard layout.
   */
  int KEY_F2 = 291;
  /**
   * The physical keyboard key that is the F3 (function key 3) in the standard US keyboard layout.
   */
  int KEY_F3 = 292;
  /**
   * The physical keyboard key that is the F4 (function key 4) in the standard US keyboard layout.
   */
  int KEY_F4 = 293;
  /**
   * The physical keyboard key that is the F5 (function key 5) in the standard US keyboard layout.
   */
  int KEY_F5 = 294;
  /**
   * The physical keyboard key that is the F6 (function key 6) in the standard US keyboard layout.
   */
  int KEY_F6 = 295;
  /**
   * The physical keyboard key that is the F7 (function key 7) in the standard US keyboard layout.
   */
  int KEY_F7 = 296;
  /**
   * The physical keyboard key that is the F8 (function key 8) in the standard US keyboard layout.
   */
  int KEY_F8 = 297;
  /**
   * The physical keyboard key that is the F9 (function key 9) in the standard US keyboard layout.
   */
  int KEY_F9 = 298;
  /**
   * The physical keyboard key that is the F10 (function key 10) in the standard US keyboard layout.
   */
  int KEY_F10 = 299;
  /**
   * The physical keyboard key that is the F11 (function key 11) in the standard US keyboard layout.
   */
  int KEY_F11 = 300;
  /**
   * The physical keyboard key that is the F12 (function key 12) in the standard US keyboard layout.
   */
  int KEY_F12 = 301;
  /**
   * The physical keyboard key that is the F13 (function key 13) in the standard US keyboard layout.
   */
  int KEY_F13 = 302;
  /**
   * The physical keyboard key that is the F14 (function key 14) in the standard US keyboard layout.
   */
  int KEY_F14 = 303;
  /**
   * The physical keyboard key that is the F15 (function key 15) in the standard US keyboard layout.
   */
  int KEY_F15 = 304;
  /**
   * The physical keyboard key that is the F16 (function key 16) in the standard US keyboard layout.
   */
  int KEY_F16 = 305;
  /**
   * The physical keyboard key that is the F17 (function key 17) in the standard US keyboard layout.
   */
  int KEY_F17 = 306;
  /**
   * The physical keyboard key that is the F18 (function key 18) in the standard US keyboard layout.
   */
  int KEY_F18 = 307;
  /**
   * The physical keyboard key that is the F19 (function key 19) in the standard US keyboard layout.
   */
  int KEY_F19 = 308;
  /**
   * The physical keyboard key that is the F20 (function key 20) in the standard US keyboard layout.
   */
  int KEY_F20 = 309;
  /**
   * The physical keyboard key that is the F21 (function key 21) in the standard US keyboard layout.
   */
  int KEY_F21 = 310;
  /**
   * The physical keyboard key that is the F22 (function key 22) in the standard US keyboard layout.
   */
  int KEY_F22 = 311;
  /**
   * The physical keyboard key that is the F23 (function key 23) in the standard US keyboard layout.
   */
  int KEY_F23 = 312;
  /**
   * The physical keyboard key that is the F24 (function key 24) in the standard US keyboard layout.
   */
  int KEY_F24 = 313;
  /**
   * The physical keyboard key that is the F25 (function key 25) in the standard US keyboard layout.
   */
  int KEY_F25 = 314;
  /**
   * The physical keyboard key that is the numeric keypad 0 key (not the normal {@link Key#KEY_0}) in the standard US keyboard layout.
   */
  int KEY_KP_0 = 320;
  /**
   * The physical keyboard key that is the numeric keypad 1 key (not the normal {@link Key#KEY_1}) in the standard US keyboard layout.
   * <p>
   * int KEY_KP_1 = 321;
   * /**
   * The physical keyboard key that is the numeric keypad 2 key (not the normal {@link Key#KEY_2}) in the standard US keyboard layout.
   */
  int KEY_KP_2 = 322;
  /**
   * The physical keyboard key that is the numeric keypad 3 key (not the normal {@link Key#KEY_3}) in the standard US keyboard layout.
   */
  int KEY_KP_3 = 323;
  /**
   * The physical keyboard key that is the numeric keypad 4 key (not the normal {@link Key#KEY_4}) in the standard US keyboard layout.
   */
  int KEY_KP_4 = 324;
  /**
   * The physical keyboard key that is the numeric keypad 5 key (not the normal {@link Key#KEY_5}) in the standard US keyboard layout.
   */
  int KEY_KP_5 = 325;
  /**
   * The physical keyboard key that is the numeric keypad 6 key (not the normal {@link Key#KEY_6}) in the standard US keyboard layout.
   */
  int KEY_KP_6 = 326;
  /**
   * The physical keyboard key that is the numeric keypad 7 key (not the normal {@link Key#KEY_7}) in the standard US keyboard layout.
   */
  int KEY_KP_7 = 327;
  /**
   * The physical keyboard key that is the numeric keypad 8 key (not the normal {@link Key#KEY_8}) in the standard US keyboard layout.
   */
  int KEY_KP_8 = 328;
  /**
   * The physical keyboard key that is the numeric keypad 9 key (not the normal {@link Key#KEY_9}) in the standard US keyboard layout.
   */
  int KEY_KP_9 = 329;
  /**
   * The physical keyboard key that is the numeric keypad decimal (.) key (not the normal {@link Key#KEY_PERIOD}) in the standard US keyboard layout.
   */
  int KEY_KP_DECIMAL = 330;
  /**
   * The physical keyboard key that is the numeric keypad divide (/) key (not the normal {@link Key#KEY_SLASH}) in the standard US keyboard layout.
   */
  int KEY_KP_DIVIDE = 331;
  /**
   * The physical keyboard key that is the numeric keypad muiltply (*) key in the standard US keyboard layout.
   */
  int KEY_KP_MULTIPLY = 332;
  /**
   * The physical keyboard key that is the numeric keypad subtract (-) key (not the normal {@link Key#KEY_MINUS}) in the standard US keyboard layout.
   */
  int KEY_KP_SUBTRACT = 333;
  /**
   * The physical keyboard key that is the numeric keypad add (+) key in the standard US keyboard layout.
   */
  int KEY_KP_ADD = 334;
  /**
   * The physical keyboard key that is the numeric keypad enter (⏎) key (not the normal {@link Key#KEY_ENTER}) in the standard US keyboard layout.
   */
  int KEY_KP_ENTER = 335;
  /**
   * The physical keyboard key that is the numeric keypad equal (=) key (not the normal {@link Key#KEY_EQUAL}) in the standard US keyboard layout.
   */
  int KEY_KP_EQUAL = 336;
  /**
   * The physical keyboard key that is the left shift key (⇧) (not the same as the {@link Key#KEY_RIGHT_SHIFT}) in the standard US keyboard layout.
   */
  int KEY_LEFT_SHIFT = 340;
  /**
   * The physical keyboard key that is the left control key (not the same as the {@link Key#KEY_RIGHT_CONTROL}) in the standard US keyboard layout.
   */
  int KEY_LEFT_CONTROL = 341;
  /**
   * The physical keyboard key that is the left alt key (not the same as the {@link Key#KEY_RIGHT_ALT}) in the standard US keyboard layout.
   */
  int KEY_LEFT_ALT = 342;
  /**
   * The physical keyboard key that is the left super key (often called the Win/Windows key on Windows, and the Command key on Mac OS) (not the same as the {@link Key#KEY_RIGHT_SUPER}) in the standard US keyboard layout.
   */
  int KEY_LEFT_SUPER = 343;
  /**
   * The physical keyboard key that is the right shift key (⇧) (not the same as the {@link Key#KEY_LEFT_SHIFT}) in the standard US keyboard layout.
   */
  int KEY_RIGHT_SHIFT = 344;
  /**
   * The physical keyboard key that is the right control key (not the same as the {@link Key#KEY_LEFT_CONTROL}) in the standard US keyboard layout.
   */
  int KEY_RIGHT_CONTROL = 345;
  /**
   * The physical keyboard key that is the right alt key (not the same as the {@link Key#KEY_LEFT_ALT}) in the standard US keyboard layout.
   */
  int KEY_RIGHT_ALT = 346;
  /**
   * The physical keyboard key that is the right super key (often called the Win/Windows key on Windows, and the Command key on Mac OS) (not the same as the {@link Key#KEY_LEFT_SUPER}) in the standard US keyboard layout.
   */
  int KEY_RIGHT_SUPER = 347;
  /**
   * The physical keyboard key that is the menu key (the key which opens a contextual menu in Windows) in the standard US keyboard layout.
   */
  int KEY_MENU = 348;
}
