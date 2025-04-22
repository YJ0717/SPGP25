package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.SubScene;

public class PegglePangActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_select);

        gameView = findViewById(R.id.game_view);

        if (gameView != null) {
            SubScene subScene = new SubScene(this);
            gameView.pushScene(subScene);
        } else {
            throw new RuntimeException("GameView is not initialized");
        }
    }
}
