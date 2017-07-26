package fr.delthas.javaui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * Sound represents an audio sound (mono or stereo), that has been uploaded on the sound card, to be played back with {@link SoundManager#playSound(Sound)}.
 * <p>
 * <b>Before any use of this class, call {@link SoundManager#create()}.</b>
 * <p>
 * To create a Sound from compressed audio data (currently only OGG is supported, but support for MP3 and other formats is planned), use one of the {@code createSound} static functions.
 * <p>
 * A sound also has a gain property (i.e. its relative volume), and a loop property which can be set to make the sound loop and be played back again after it has been played.
 */
public final class Sound {
  final int data;
  float gain = 1f;
  boolean loop = false;
  
  Sound(int data) {
    this.data = data;
  }
  
  /**
   * Creates a sound from a path that represents a file which contains compressed sound data, such as typically a .ogg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * Refer to the {@link Sound} class Javadoc for more general information regarding sounds.
   *
   * @param path The path to the file that contains compressed sound data, must be non-null.
   * @return The {@link Sound} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   */
  public static Sound createSound(Path path) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  /**
   * Creates a sound from a string representing a path that represents a file which contains compressed image data, such as typically a .ogg file. This is a blocking operation that will read the file fully from the disk and decode it fully and only return after both operations are done.
   * <p>
   * The string may either be a path into the JAR (as would be obtained with {@code getClass().getResource(string)}, prepending it with a {@literal /} if necessary, or, if it doesn't correspond to a resource, a path to a file on a filesystem (as would be obtained with {@code Paths.get(string)}), though it may be better to use {@link #createSound(Path)} in this case, which directly supports passing a {@link Path}.
   * <p>
   * Refer to the {@link Sound} class Javadoc for more general information regarding sounds.
   *
   * @param path The string representing the path to the file that contains compressed sound data, must be non-null.
   * @return The {@link Sound} that was created from reading the file, <b>or null if there was an error decoding the file</b>.
   * @throws IOException If an IO exception is raised when reading the file.
   */
  public static Sound createSound(String path) throws IOException {
    ByteBuffer buffer = Utils.getResourceBuffer(path);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  /**
   * Creates a sound from an input stream feeding compressed sound data, such as typically a stream from a .ogg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * The stream doesn't need to be buffered, as it is buffered internally by the library. <b>The stream is NOT closed by the library.</b> If reading from a {@link Path}, you may use {@link #createSound(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createSound(String)} instead.
   * <p>
   * Refer to the {@link Sound} class Javadoc for more general information regarding sounds.
   *
   * @param input The input stream representing the path to the file that contains compressed sound data, must be non-null.
   * @return The {@link Sound} that was created from reading the stream, <b>or null if there was an error decoding the stream</b>.
   * @throws IOException If an IO exception is raised when reading the stream.
   */
  public static Sound createSound(InputStream input) throws IOException {
    ByteBuffer buffer = Utils.getBuffer(input);
    Sound sound = createSound(buffer);
    Utils.free(buffer);
    return sound;
  }
  
  /**
   * Creates a sound from a byte buffer containing compressed sound data, such as typically a buffer obtained from reading a .ogg file. This is a blocking operation that will read the stream fully and decode it fully and only return after both operations are done.
   * <p>
   * <b>THE BUFFER MUST BE FLIPPED, i.e. if you write to this buffer without flipping it afterwards, NO DATA WILL BE READ. The data will be read from the current buffer position to the buffer limit.</b> If reading from a {@link Path}, you may use {@link #createSound(Path)} instead; if reading from a resource file into the currently running JAR, you may use {@link #createSound(String)} instead; if reading from an input stream, you may use {@link #createSound(InputStream)} instead.
   * <p>
   * Refer to the {@link Sound} class Javadoc for more general information regarding sounds.
   *
   * @param buffer The byte buffer containing compressed sound data, must be non-null.
   * @return The {@link Sound} that was created from reading the buffer, <b>or null if there was an error decoding the buffer</b>.
   */
  public static Sound createSound(ByteBuffer buffer) {
    return SoundManager.getSoundManager().createSound(buffer);
  }
  
  /**
   * @return The gain of the sound, i.e. its relative volume, between 0 and 1.
   * @see #setGain(float)
   */
  public float getGain() {
    return gain;
  }
  
  /**
   * Sets the gain of the sound, i.e. its relative volume, as a number between 0 (silent) and 1.
   *
   * @param gain The gain of the sound to be set.
   * @see #getGain()
   */
  public void setGain(float gain) {
    this.gain = gain;
  }
  
  /**
   * @return Whether the sound will be played back again in a loop after being played once (true), or not (false).
   * @see #setLoop(boolean)
   */
  public boolean isLoop() {
    return loop;
  }
  
  /**
   * Sets whether the sound will be played back again in a loop after being played once (true), or not (false).
   *
   * @param loop Whether to loop the sound (true) or not (false).
   * @see #isLoop()
   */
  public void setLoop(boolean loop) {
    this.loop = loop;
  }
}
