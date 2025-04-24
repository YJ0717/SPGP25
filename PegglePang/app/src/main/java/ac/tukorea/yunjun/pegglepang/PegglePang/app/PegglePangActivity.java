package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.worldSelectScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.Stage1_Scene;

public class PegglePangActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_select);
        setupWorldSelectButtons();  // 버튼 설정 메서드 호출

        gameView = findViewById(R.id.game_view);
        if (gameView != null) {
            worldSelectScene worldSelectScene = new worldSelectScene(this);
            gameView.pushScene(worldSelectScene);
        } else {
            throw new RuntimeException("GameView is not initialized");
        }
    }

    // 버튼 설정을 위한 메서드 수정
    public void setupWorldSelectButtons() {
        if (findViewById(R.id.stage1_button) != null) {  // stage1_button으로 수정
            findViewById(R.id.stage1_button).setOnClickListener(v -> {
                setContentView(R.layout.stage1_select);
                Stage1_Scene stage1Scene = new Stage1_Scene(this);
                gameView.pushScene(stage1Scene);
            });
        }
    }

    // getter 메서드 추가해서 
    public GameView getGameView() {
        return gameView;
    }
}

//PegglePang Activity = 게임의 실제 화면들 추가 
