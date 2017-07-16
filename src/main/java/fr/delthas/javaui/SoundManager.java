package fr.delthas.javaui;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.stb.STBVorbisInfo;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager {
  private static final SoundManager instance;
  
  static {
    Init.init();
    instance = new SoundManager();
  }
  
  private List<Sound> sounds = new ArrayList<>();
  private int[] sources;
  private int sourceId = -1;
  private boolean enabled = true;
  
  public static SoundManager getSoundManager() {
    return instance;
  }
  
  private static void checkALError() {
    int err = alGetError();
    if (err != AL_NO_ERROR) {
      throw new RuntimeException(alGetString(err));
    }
  }
  
  Sound createSound(ByteBuffer buffer) {
    if (!enabled) {
      return null;
    }
    
    try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
      int data = alGenBuffers();
      int[] error = new int[1];
      long decoder = stb_vorbis_open_memory(buffer, error, null);
      if (decoder == NULL) {
        throw new RuntimeException("Failed reading ogg file. Error: " + error[0]);
      }
      stb_vorbis_get_info(decoder, info);
      if (info.channels() != 1 && info.channels() != 2) {
        throw new RuntimeException("Failed reading ogg file. The file should have exactly one or two audio channels.");
      }
      int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);
      ShortBuffer pcm = BufferUtils.createShortBuffer(lengthSamples);
      stb_vorbis_get_samples_short_interleaved(decoder, info.channels(), pcm);
      stb_vorbis_close(decoder);
      alBufferData(data, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcm, info.sample_rate());
      checkALError();
      Sound sound = new Sound(data);
      sounds.add(sound);
      return sound;
    }
  }
  
  public void playSound(Sound sound) {
    if (!enabled) {
      return;
    }
    int availableSource = -1;
    int[] result = new int[1];
    for (int i = sourceId + 1; i < sources.length; i++) {
      alGetSourcei(sources[i], AL_SOURCE_STATE, result);
      if (result[0] != AL_PLAYING) {
        availableSource = i;
        break;
      }
    }
    if (availableSource == -1) {
      for (int i = 0; i <= sourceId; i++) {
        alGetSourcei(sources[i], AL_SOURCE_STATE, result);
        if (result[0] != AL_PLAYING) {
          availableSource = i;
          break;
        }
      }
    }
    if (availableSource == -1) {
      // TODO stop a sound
      return;
      // throw new RuntimeException("Too many sounds running!");
    }
    sourceId = availableSource;
    alSourceStop(sources[sourceId]);
    checkALError();
    alSourcei(sources[sourceId], AL_BUFFER, sound.data);
    checkALError();
    alSourcef(sources[sourceId], AL_GAIN, sound.gain);
    checkALError();
    alSourcei(sources[sourceId], AL_LOOPING, sound.loop ? 1 : 0);
    checkALError();
    alSourcePlay(sources[sourceId]);
    checkALError();
  }
  
  public void create() {
    long device = alcOpenDevice((ByteBuffer) null);
    if (device == NULL) {
      enabled = false;
      return;
    }
    ALCCapabilities deviceCaps = ALC.createCapabilities(device);
    long context = alcCreateContext(device, (IntBuffer) null);
    alcMakeContextCurrent(context);
    AL.createCapabilities(deviceCaps);
    checkALError();
    
    int size = alcGetInteger(device, ALC_ATTRIBUTES_SIZE);
    checkALError();
    int[] attributes = new int[size];
    alcGetIntegerv(device, ALC_ALL_ATTRIBUTES, attributes);
    checkALError();
    int maxSources = 32;
    for (int i = 0; i < size; i++) {
      if (attributes[i] == 0x1010) { // ALC_MONO_SOURCES == 0x1010
        maxSources = attributes[i + 1];
        break;
      }
    }
    sources = new int[maxSources];
    alGenSources(sources);
    checkALError();
  }
  
  public void destroy() {
    if (!enabled) {
      return;
    }
    if (sources != null) {
      alSourceStopv(sources);
      alDeleteSources(sources);
    }
    for (Sound sound : sounds) {
      alDeleteBuffers(sound.data);
    }
    long context = alcGetCurrentContext();
    long device = alcGetContextsDevice(context);
    alcMakeContextCurrent(NULL);
    alcDestroyContext(context);
    alcCloseDevice(device);
  }
}
