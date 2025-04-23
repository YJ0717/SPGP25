package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.util.DisplayMetrics;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class worldSelectScene extends Scene {
    private Paint paint; 
    private Context context;
    private int screenHeight; // 화면 높이를 저장할 변수 <디바이스에 따라 화면 높이를 가져오기 위해

    public worldSelectScene(Context context) {
        paint = new Paint();
        paint.setColor(Color.WHITE); 
        this.context = context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenHeight = metrics.heightPixels; 
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    
    //스테이지 1 화면을 전환하기 위한 터치 이벤트 메인화면처럼 버튼객체를 만드는것보다 그냥 터치이벤트로 바로 실행하는게 코드가 더 간편함 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (x > 50 && x < 350 && y > screenHeight - 450 && y < screenHeight - 100) {  
                new Stage1_Scene(context).push(); // Context를 전달하여 스테이지 1 씬으로 전환
                return true;
            }
        }
        return false;
    }



    @Override
    public void onExit() {
        super.onExit();
    }
}