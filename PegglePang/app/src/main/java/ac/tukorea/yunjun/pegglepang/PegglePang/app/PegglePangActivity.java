package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.os.Bundle;

import ac.tukorea.yunjun.pegglepang.framework.activity.GameActivity;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.MainScene;

public class PegglePangActivity extends GameActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MainScene().push();
    }
}
