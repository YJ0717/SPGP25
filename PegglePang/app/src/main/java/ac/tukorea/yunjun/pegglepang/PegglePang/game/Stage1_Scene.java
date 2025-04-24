package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.app.Activity;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class Stage1_Scene extends Scene {
    private Paint paint; 
    private Context context;

    public Stage1_Scene(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.WHITE);
        
        TextView backText = ((Activity)context).findViewById(R.id.back_text);
        backText.setOnClickListener(v -> {
            if (context instanceof PegglePangActivity) {
                PegglePangActivity gameActivity = (PegglePangActivity) context;
                gameActivity.setContentView(R.layout.world_select);
                gameActivity.getGameView().popScene();
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}
