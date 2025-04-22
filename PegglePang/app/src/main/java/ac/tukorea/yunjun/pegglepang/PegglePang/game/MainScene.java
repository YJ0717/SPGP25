package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.view.MotionEvent;
import android.content.Context;

import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class MainScene extends Scene {
    private Context context; // Context를 저장할 변수

    public MainScene(Context context) {
        this.context = context; // Context 저장
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pts = Metrics.fromScreen(event.getX(), event.getY());
            float x = pts[0], y = pts[1];
            if (x < 100 && y < 100) {
                new SubScene(context).push(); // Context 전달
                return false;
            }
        }
        return false;
    }
}
