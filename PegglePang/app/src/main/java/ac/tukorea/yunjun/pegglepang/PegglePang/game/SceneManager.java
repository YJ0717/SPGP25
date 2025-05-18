package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;

public class SceneManager {
    private static SceneManager instance;
    private GameView gameView;
    private Context context;

    public enum SceneType {
        MAIN,
        S1_1
    }

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(GameView gameView, Context context) {
        this.gameView = gameView;
        this.context = context;
    }

    public void changeScene(SceneType type) {
        switch (type) {
            case MAIN:
                gameView.changeScene(new MainScene(context));
                break;
            case S1_1:
                gameView.changeScene(new S1_1(context));
                break;
        }
    }
} 