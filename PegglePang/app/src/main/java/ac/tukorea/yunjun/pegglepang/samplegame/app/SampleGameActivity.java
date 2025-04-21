package ac.tukorea.yunjun.pegglepang.samplegame.app;

import android.os.Bundle;

import ac.tukorea.yunjun.pegglepang.framework.activity.GameActivity;
import ac.tukorea.yunjun.pegglepang.samplegame.game.MainScene;

public class SampleGameActivity extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainScene().push();
    }
}
