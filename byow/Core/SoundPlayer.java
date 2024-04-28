package byow.Core;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundPlayer {
    private static Clip clip;

    public static void setFile(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new File(filename).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception ex) {
            System.out.println("Error playing sound.");
            ex.printStackTrace();
        }
    }
    public static void play(String filename) {
        setFile(filename);
        clip.start();
    }

    public static void loop(String filename) {
        setFile(filename);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}