package ac.tukorea.yunjun.pegglepang.PegglePang.game.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import ac.tukorea.yunjun.pegglepang.R;

public class SoundEffectManager {
    private static SoundEffectManager instance;
    private SoundPool soundPool;
    private int blockBreakSoundId;
    private boolean isSoundEnabled = true;
    private boolean isSoundLoaded = false;
    private Context context;

    private SoundEffectManager() {
        // SoundPool 초기화
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10) // 최대 10개의 동시 재생
                .setAudioAttributes(audioAttributes)
                .build();
        
        // 사운드 로딩 완료 리스너 설정
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0) { // 로딩 성공
                    isSoundLoaded = true;
                }
            }
        });
    }

    public static SoundEffectManager getInstance() {
        if (instance == null) {
            instance = new SoundEffectManager();
        }
        return instance;
    }

    public void initialize(Context context) {
        this.context = context;
        loadSounds();
        
        // 사운드 로딩을 위해 잠시 대기 (백그라운드에서)
        new Thread(() -> {
            try {
                Thread.sleep(100); // 100ms 대기 후 미리 재생해서 준비
                if (isSoundLoaded) {
                    // 볼륨 0으로 미리 재생해서 준비 상태로 만들기
                    soundPool.play(blockBreakSoundId, 0.0f, 0.0f, 0, 0, 1.0f);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadSounds() {
        if (context != null) {
            try {
                // 블록 터지는 효과음 로드 (1초 정도)
                blockBreakSoundId = soundPool.load(context, R.raw.block_break, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playBlockBreakSound() {
        if (isSoundEnabled && soundPool != null && blockBreakSoundId != 0 && isSoundLoaded) {
            try {
                // 높은 우선순위로 즉시 재생
                soundPool.play(blockBreakSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
} 