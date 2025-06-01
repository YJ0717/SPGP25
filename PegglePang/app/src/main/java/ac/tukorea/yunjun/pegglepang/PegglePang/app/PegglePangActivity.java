// 게임의 핵심 엑티비티, 게임 뷰를 관리하고 씬 전환 시 레이아웃 변경을 처리
// 게임뷰의 상태를 보존하면서 레이아웃 전환을 관리하는 것이 핵심 역할

package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.world.worldSelectScene;

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
}
