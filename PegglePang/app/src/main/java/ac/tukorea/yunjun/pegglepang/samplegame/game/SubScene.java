package ac.tukorea.yunjun.pegglepang.samplegame.game;

import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class SubScene extends Scene {
    public SubScene() {
        gameObjects.add(new BouncingCircle());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pop();
        return false;
    }
}
