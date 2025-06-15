// 게임의 핵심 엑티비티, 게임 뷰를 관리하고 씬 전환 시 레이아웃 변경을 처리
// 게임뷰의 상태를 보존하면서 레이아웃 전환을 관리하는 것이 핵심 역할

package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.world.worldSelectScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.audio.BackgroundMusicManager;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.audio.SoundEffectManager;

public class PegglePangActivity extends AppCompatActivity {
    private GameView gameView;
    private int currentLayout = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_select);

        gameView = findViewById(R.id.game_view);
        if (gameView == null) {
            throw new RuntimeException("게임 뷰 생성되지 않음");
        }

        worldSelectScene worldSelectScene = new worldSelectScene(this);
        gameView.pushScene(worldSelectScene);
        
        // 배경음악 시작
        BackgroundMusicManager.getInstance().startBackgroundMusic(this);
        
        // 효과음 매니저 초기화
        SoundEffectManager.getInstance().initialize(this);
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (currentLayout == layoutResID) {
            return;  
        }
        currentLayout = layoutResID;
        
        GameView oldGameView = gameView;  
        super.setContentView(layoutResID);
        
        gameView = findViewById(R.id.game_view);
        
        if (oldGameView != null && gameView != null) {
            gameView.restoreFromGameView(oldGameView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 앱이 다시 활성화될 때 배경음악 재개
        BackgroundMusicManager.getInstance().resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 앱이 백그라운드로 갈 때 배경음악 일시정지
        BackgroundMusicManager.getInstance().pauseBackgroundMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 앱이 종료될 때 배경음악 완전 정지
        BackgroundMusicManager.getInstance().stopBackgroundMusic();
        // 효과음 매니저 해제
        SoundEffectManager.getInstance().release();
    }
}
