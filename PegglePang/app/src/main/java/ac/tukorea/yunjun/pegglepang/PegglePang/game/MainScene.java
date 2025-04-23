package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.view.MotionEvent;
import android.content.Context;

import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class MainScene extends Scene {
    private Context context; 

    public MainScene(Context context) {
        this.context = context; 
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pts = Metrics.fromScreen(event.getX(), event.getY());
            float x = pts[0], y = pts[1];
            if (x < 100 && y < 100) {
                new worldSelectScene(context).push(); // Context 전달 스테이지1 클리어 시 월드2선택화면이 열리기 위해 
                return false;
            }
        }
        return false;
    }
}
