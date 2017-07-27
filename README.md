# JavaUI

## Introduction

JavaUI is a lightweight and fast API for drawing geometric shapes, images, text; rendering a UI with various components and handling input; and decoding and playing back sounds; by internally using lightweight bindings to low-level powerful native libraries: OpenAL and OpenGL.

This API lets you:
- Render basic geometric shapes, images, and text, to the screen, with "canvas-like" methods, in a very efficient way, directly using OpenGL internally (through LWJGL)
- Create and use abstract layers/"views" (title screen, ...), and components (use the preexisting button, label, ..., or create your own), that can react to user input and render themselves onto the screen, in a lightweight way 
- Decode and play sounds in an efficient way, by using OpenAL internally (through LWJGL) **(currently only supports OGG files, the work for a pure Java MP3 decoding library is [currently in progress](https://github.com/Delthas/JavaMP3)**)

## Install

JavaUI requires Java >= 8 to run. You can get this library using Maven by adding this to your ```pom.xml```:

```xml
 <dependencies>
    <dependency>       
           <groupId>fr.delthas</groupId>
           <artifactId>javaui</artifactId>
           <version>1.0.7</version>
    </dependency>
</dependencies>
```


## Quick example

This library includes a three-part API (low-level drawing, UI support, sound support), that is lightweight but quite powerful, and you may find quite useful to study [the documentation](#documentation).

Here's a quick example on how to show a button and say hi to the console when it gets clicked, copied from the extensive library [Javadoc](http://www.javadoc.io/doc/fr.delthas/javaui/).

```java
private void main() {
  init();
  loop();
  destroy();
}

private void init() {
  Ui.getUi().create("My window!");
  Layer layer = new Layer();
  Button button = new Button("My button");
  button.setListener((butt, x, y) -> System.out.println("Button pressed! Hi!"));
  layer.addComponent(100, 100, 500, 100, button);
  layer.push(); // push the layer onto the layer stack
}

private void loop() {
  while(!closeRequested) {
    // Process input from the user 
    // (will check if there are mouse button clicks on the button, for exemple)
    Ui.getUi().input();
    // Render the button, then wait for vsync
    Ui.getUi().render();
    // No need to sleep
  }
}

private void destroy() {
  Ui.getUi().destroy();
}
```

## Documentation

The library javadoc is especially detailed and extensive, and you may refer to the package Javadoc and each class Javadoc for some high-level context and use cases to help you use the library.

The javadoc for the API is located at: http://www.javadoc.io/doc/fr.delthas/javaui/

Some methods of the API can be called from multiple threads, but the Javadoc regarding multithreading is to be added in the near future (see [status](#status) below).

## Building

Simply run ```maven install```.


## Status

- [X] Geometric shapes rendering
- [X] Images decoding
- [X] Images rendering
- [X] Text rendering
- [X] UI support (layers, ...)
- [X] Basic components
- [X] Sound decoding support (OGG)
- [X] Sound playback support
- [X] Extensive documentation
- [ ] Multithreading-specific documentation
- [ ] Sound decoding support (MP3)
- [ ] Better examples
- [ ] Tests

## Misceallenous

### Tech

JavaUI uses a very small set of libraries in order to run:
- [LWJGL 3](https://lwjgl.org) - Very lightweight and efficient Java bindings for several native libraries: GLFW, OpenGL, OpenAL, STB, ...
- [JOML](https://github.com/JOML-CI/JOML) - Very lightweight and efficient pure Java library for linear algebra operations (e.g. matrix-related stuff)

Note that JavaUI doesn't depend on Apache Commons, Google Guava, or other general heavyweight libraries in order to run.

### License

MIT
