package main;

import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

  static Clip clip;
  URL SoundURL[] = new URL[30];

  public Sound() {
    SoundURL[0] = getClass().getResource("/res/sound/BlueBoyAdventure.wav");
    SoundURL[1] = getClass().getResource("/res/sound/coin.wav");
    SoundURL[2] = getClass().getResource("/res/sound/fanfare.wav");
    SoundURL[3] = getClass().getResource("/res/sound/powerup.wav");
    SoundURL[4] = getClass().getResource("/res/sound/unlock.wav");
  }

  public void setFile(int i) {
    try {
      AudioInputStream ais = AudioSystem.getAudioInputStream(SoundURL[i]);
      clip = AudioSystem.getClip();
      clip.open(ais);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void play() {
    clip.start();
  }

  public void loop() {
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

  public void stop() {
    clip.stop();
  }
}