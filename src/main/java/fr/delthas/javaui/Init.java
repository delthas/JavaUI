package fr.delthas.javaui;

import org.lwjgl.system.Configuration;

final class Init {
  private static volatile boolean init = false;
  
  private Init() {
  }
  
  static synchronized void init() {
    if (init) {
      return;
    }
    init = true;
    Configuration.EGL_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.OPENCL_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.OPENGLES_EXPLICIT_INIT.set(Boolean.TRUE);
    Configuration.VULKAN_EXPLICIT_INIT.set(Boolean.TRUE);
  }
}
