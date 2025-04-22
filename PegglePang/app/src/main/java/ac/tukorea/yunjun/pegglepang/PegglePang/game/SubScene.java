package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class SubScene extends Scene {
    private Paint paint; 

    public SubScene() {
        paint = new Paint();
        paint.setColor(Color.WHITE); 
    }

    @Override
    public void draw(Canvas canvas) {
        // 화면을 흰색으로 채우기
        canvas.drawColor(Color.WHITE);
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pop();
        return false;
    }
}