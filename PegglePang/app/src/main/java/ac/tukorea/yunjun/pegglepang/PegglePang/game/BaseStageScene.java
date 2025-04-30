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

public abstract class BaseStageScene extends Scene {
    protected Paint paint;
    protected Context context;
    protected int stageNumber;
    protected int subStageNumber;

    public BaseStageScene(Context context, int stageNumber, int subStageNumber) {
        this.context = context;
        this.stageNumber = stageNumber;
        this.subStageNumber = subStageNumber;
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    protected void setupBackButton() {
        TextView backText = ((Activity)context).findViewById(R.id.back_text);
        backText.setOnClickListener(v -> {
            if (context instanceof PegglePangActivity) {
                PegglePangActivity gameActivity = (PegglePangActivity) context;
                gameActivity.setContentView(R.layout.stage1_select);
                gameActivity.getGameView().popScene();
            }
        });
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.game_scene);
            setupBackButton();
            setupStageSpecificElements();
        }
    }

    protected abstract void setupStageSpecificElements();
} 