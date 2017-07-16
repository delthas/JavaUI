package fr.delthas.javaui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public final class Sound {
  final int data;
  float gain = 1f;
  boolean loop = false;
  
  Sound(int data) {
    this.data = data;
  }
  
  public static Sound createSound(Path path) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  public static Sound createSound(String path) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  public static Sound createSound(InputStream input) throws IOException {
    ByteBuffer buffer = Utils.getBuffer(input);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  public static Sound createSound(ByteBuffer buffer) {
    return SoundManager.getSoundManager().createSound(buffer);
  }
  
  public float getGain() {
    return gain;
  }
  
  public void setGain(float gain) {
    this.gain = gain;
  }
  
  public boolean isLoop() {
    return loop;
  }
  
  public void setLoop(boolean loop) {
    this.loop = loop;
  }
}
