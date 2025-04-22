package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.content.Context;
import android.util.Log;
import android.util.DisplayMetrics;
import android.app.Activity;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class SubScene extends Scene {
    private Paint paint; 
    private Context context;

    public SubScene(Context context) {
        paint = new Paint();
        paint.setColor(Color.WHITE); 
        this.context = context;
    }

    @Override
    public void draw(Canvas canvas) {
        // 이제 비트맵을 그릴 필요가 없습니다.
        // 필요한 경우 다른 그래픽 요소를 그릴 수 있습니다.
        super.draw(canvas);
    }

    @Override
    public void onExit() {
        super.onExit();
        // 리소스 해제는 필요하지 않음
    }
}