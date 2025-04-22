package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class MainScene extends Scene {
    public MainScene() {
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pts = Metrics.fromScreen(event.getX(), event.getY());
            float x = pts[0], y = pts[1];
            if (x < 100 && y < 100) {
                new SubScene().push();
                return false;
            }
        }
        return false;
    }
}
