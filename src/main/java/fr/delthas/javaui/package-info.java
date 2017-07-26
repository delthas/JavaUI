/**
 * JavaUI is a fast and lightweight library that is split into three main features:
 * <ul>
 * <li>basic geomtric shapes, text, and images low-level rendering, that are translated to efficient, native OpenGL calls, like a canvas (<b>the drawing API</b>);
 * <li>a lightweight abstraction over this canvas, which is a virtual stack of "layers" (views, such as a title screen, ...) which contain UI components (such as buttons, text fields, ...) which can listen to user input that is propagated to them, and can render themselves onto the screen using the drawing API (<b>the UI API</b>);
 * <li>a sound system that can decode and play back sounds efficiently using native OpenAL calls (<b>the sound API</b>).
 * </ul>
 * The drawing API can only be accessed through a UI component, which why it is essential to understand the UI system (and what its stack of layers is).
 * <p>
 * <b>In JavaUI, all coordinates are specified in PIXELS, the x axis extends from the left to the right, and the y axis extends FROM THE BOTTOM TO THE TOP, and the point at (0,0) is thus the lower-left corner of the considered rectangle. The absolute coordinates range from (0,0) to (width_of_the_screen,height_of_the_screen).</b> Note that the library sometimes uses coordinates relative to a component position, and that you should read the documentation for each method that takes a parameter carefully in order to know whether it accepts relative or absolute parameters.
 * <p></p>
 * <p>
 * <b>The UI API</b> provides an abstraction over the low-level, canvas-like drawing API. It contains a stack of {@link fr.delthas.javaui.Layer layers}, which typically represent a "view"/"screen" in a application (for example a "title screen" could be a layer, an options screen could be a layer, ...). The topmost layer of the stack is the one closest to the screen/user, and layers can be transparent, so the UI API draws the layer at the bottom of the stack, then the one over it in the stack, onto it, and so on util it renders the layer at the top of the stack. Just like a title screen could contain some buttons, some labels, ..., a layer contains a list of components, which can be buttons, text fields, or any user-defined component. Using a stack of layers instead of only one current layer can greatly ease the creation of an UI: if a user clicks the settings option in a layer, you could push a settings layer above the current layer, then pop it from the stack when the user exits the settings layer. For more information see the {@link fr.delthas.javaui.Ui}, {@link fr.delthas.javaui.Layer} and {@link fr.delthas.javaui.Component} Javadoc.
 * <p>
 * Input, be it mouse buttons presses or releases, keyboard key presses and releases, text input, or mouse wheel scrolling, is efficiently captured by OpenGL, then transferred to the UI stack. The UI stack then propagates the input through its layers, from top to bottom of the stack, until it is consumed by a layer, i.e. "used". In fact, layers propagate the input they receive to their components, until one of their components consumes the input. For example, if there is a settings layer above the title screen layer, and the user clicks a button on the settings layer, the input will first be propagated to the settings layer, which will propagate it to the button, which will check that the mouse button press was indeed inside the button, and will consume the input, at which point the event propagation will stop. Input includes reacting to the OS events and thus avoiding that the OS considers the application as a freezed application that does not respond and should be killed; thus you should call this method often, as shown in the example below.
 * <p>
 * Rendering starts at the highest "opaque" layer of the stack (which is a layer property): the UI system calls the layer {@link fr.delthas.javaui.Layer#render(fr.delthas.javaui.InputState, fr.delthas.javaui.Drawer) render} method, passing it an {@link fr.delthas.javaui.Drawer object} that represents an "access" to the low-level drawing API, and then continues upwards and renders all layers until it hits the top of the layer stack. In fact, each layer will call their components render method, passing it the object to "access" the low-level drawing API, first translating it so that coordinates for the component are relative to its position. It is only inside the component render method that the low-level drawing API may be accessed.
 * <p>
 * A code example for a minimal UI can be:
 * <pre><code>
 *    private void main() {
 *      init();
 *      loop();
 *      destroy();
 *    }
 *
 *    private void init() {
 *      Ui.getUi().create("My window!");
 *      Layer layer = new Layer();
 *      Button button = new Button("My button");
 *      button.setListener((butt, x, y) -&gt; System.out.println("Button pressed!"));
 *      layer.addComponent(100, 100, 500, 100, button);
 *      layer.push(); // push the layer onto the layer stack
 *    }
 *
 *    private void loop() {
 *      while(!closeRequested) {
 *        // Process input from the user (will check if there are mouse button clicks on the button, for exemple)
 *        Ui.getUi().input();
 *        // Render the button, then wait for vsync
 *        Ui.getUi().render();
 *        // No need to sleep
 *      }
 *    }
 *
 *    private void destroy() {
 *      Ui.getUi().destroy();
 *    }
 *  </code>
 *  </pre>
 * <p></p>
 * <p>
 * <b>The drawing API</b> is a low-level drawing API that can be called from the components {@link fr.delthas.javaui.Component#render(fr.delthas.javaui.InputState, fr.delthas.javaui.Drawer) render} method. It supports line, rectangle, circle, ring rendering, with transparency, image rendering, and text rendering. Images must first be loaded from an image data source, such as a .jpg file, then uploaded onto the GPU, to become a texture (can and should be called outside the component render method), that can then be drawn from the render method. For more information, see the {@link fr.delthas.javaui.Image} and {@link fr.delthas.javaui.Drawer} Javadoc.
 * <p></p>
 * <p>
 * <b>The sound API</b> lets decode sounds from a compressed sound data source, then play them back at any time. Sound playback can be done at any time, and does not follow a propagation pattern like the render and input parts of the UI system. For more information, see the {@link fr.delthas.javaui.SoundManager} and {@link fr.delthas.javaui.Sound} Javadoc.
 *
 * @see fr.delthas.javaui.Ui
 * @see fr.delthas.javaui.Drawer
 * @see fr.delthas.javaui.SoundManager
 */
package fr.delthas.javaui;
