package ac.tukorea.yunjun.pegglepang.samplegame.game;

import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.framework.objects.JoyStick;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.BuildConfig;
import ac.tukorea.yunjun.pegglepang.R;

public class MainScene extends Scene {
    private static final String TAG = MainScene.class.getSimpleName();
    private Fighter fighter;
    private JoyStick joyStick;

    public MainScene() {
        Metrics.setGameSize(900, 1600);
        GameView.drawsDebugStuffs = BuildConfig.DEBUG;

        for (int i = 0; i < 5; i++) {
            add(new BouncingCircle());
        }
        for (int i = 0; i < 10; i++) {
            add(Ball.random());
        }
        joyStick = new JoyStick(R.mipmap.joystick_bg, R.mipmap.joystick_thumb, 200, 1400,  200, 60, 150);
        fighter = new Fighter(joyStick);
        add(fighter);
        add(joyStick);
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
        return joyStick.onTouch(event);
    }
}
