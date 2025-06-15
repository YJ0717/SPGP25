package ac.tukorea.yunjun.pegglepang.PegglePang.game.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import ac.tukorea.yunjun.pegglepang.R;

public class BackgroundMusicManager {
    private static BackgroundMusicManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isMusicEnabled = true;
    private boolean isPaused = false;

    private BackgroundMusicManager() {
    }

    public static BackgroundMusicManager getInstance() {
        if (instance == null) {
            instance = new BackgroundMusicManager();
        }
        return instance;
    }

    public void startBackgroundMusic(Context context) {
        if (!isMusicEnabled) return;

        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true); // 무한 반복
                    mediaPlayer.setVolume(1.0f, 1.0f); // 볼륨 설정 (0.0 ~ 1.0)
                }
            }

            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                isPaused = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseBackgroundMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    public void resumeBackgroundMusic() {
        if (mediaPlayer != null && isPaused && isMusicEnabled) {
            mediaPlayer.start();
            isPaused = false;
        }
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPaused = false;
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.isMusicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        }
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }
} 