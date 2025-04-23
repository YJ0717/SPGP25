package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class Stage1_Scene extends Scene {
    private Paint paint; 
    private Context context;

    public Stage1_Scene(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.WHITE); 
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.BLACK); 
        paint.setTextSize(50);
        canvas.drawText("뒤로가기", 400, 400, paint); 
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            // 터치 이벤트 처리
            if (x > 100 && x < 300 && y > 300 && y < 400) { // 특정 영역을 클릭했을 때
                Scene.pop(); // 이전 씬으로 돌아가기
                return true;
            }
        }
        return false; // 이벤트가 처리되지 않음
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}
