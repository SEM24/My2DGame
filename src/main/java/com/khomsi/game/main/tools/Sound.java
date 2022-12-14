package main.java.com.khomsi.game.main.tools;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;


public class Sound {
    Clip clip;
    private final String resourcePath = "/sounds/";

    URL[] soundURL = new URL[30];

    public Sound() {
        soundURL[0] = getClass().getResource(resourcePath + "Main-Theme.wav");
        soundURL[1] = getClass().getResource(resourcePath + "snd_pombark.wav");
        soundURL[2] = getClass().getResource(resourcePath + "mus_sfx_a_grab.wav");
        soundURL[3] = getClass().getResource(resourcePath + "mus_dununnn.wav");
        soundURL[4] = getClass().getResource(resourcePath + "mus_dooropen.wav");
        soundURL[5] = getClass().getResource(resourcePath + "mus_doorclose.wav");
        soundURL[6] = getClass().getResource(resourcePath + "snd_power.wav");
        soundURL[7] = getClass().getResource(resourcePath + "snd_victory.wav");
        soundURL[8] = getClass().getResource(resourcePath + "hitmonster.wav");
        soundURL[9] = getClass().getResource(resourcePath + "receivedamage.wav");
        soundURL[10] = getClass().getResource(resourcePath + "sword-swipes.wav");
        soundURL[11] = getClass().getResource(resourcePath + "level_up.wav");
        soundURL[12] = getClass().getResource(resourcePath + "cursor.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(stream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error in method setFile in class " + getClass().getSimpleName());
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
