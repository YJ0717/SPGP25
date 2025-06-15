package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.content.Context;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.MainScene;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;

public class SceneManager {
    private static SceneManager instance;
    private GameView gameView;
    private Context context;

    public enum SceneType {
        MAIN,
        S1_1,
        STAGE_SELECT,
        S1_2,
        S1_3,
        S3_2,
        S3_3
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
            case STAGE_SELECT:
                gameView.changeScene(new Stage1_Scene(context));
                break;
            case S1_2:
                gameView.changeScene(StageFactory.createStage(context, 1, 2));
                break;
            case S1_3:
                gameView.changeScene(StageFactory.createStage(context, 1, 3));
                break;
            case S3_2:
                gameView.changeScene(StageFactory.createStage(context, 3, 2));
                break;
            case S3_3:
                gameView.changeScene(StageFactory.createStage(context, 3, 3));
                break;
        }
    }
} 