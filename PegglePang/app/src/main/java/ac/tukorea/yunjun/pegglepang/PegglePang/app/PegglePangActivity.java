package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.worldSelectScene;

public class PegglePangActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_select);

        gameView = findViewById(R.id.game_view);
        if (gameView != null) {
            worldSelectScene worldSelectScene = new worldSelectScene(this);
            gameView.pushScene(worldSelectScene);
        } else {
            throw new RuntimeException("GameView is not initialized");
        }
    }

    public GameView getGameView() {
        return gameView;
    }
}

//PegglePang Activity = 게임의 실제 화면들 추가 
