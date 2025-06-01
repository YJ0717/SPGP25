// 모든 스테이지 씬의 기본 클래스. 공통 기능(뒤로가기, 레이아웃 설정 등)을 구현
// 각 스테이지는 이 클래스를 상속받아 stageSpecificElements 매서드를 통해  각 스테이지마다 표시되는 요소를 구현

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
                if (stageNumber == 1) {
                    gameActivity.setContentView(R.layout.stage1_select);
                    gameActivity.getGameView().changeScene(new Stage1_Scene(context));
                } else if (stageNumber == 2) {
                    gameActivity.setContentView(R.layout.world2_stage_select);
                    gameActivity.getGameView().changeScene(new Stage2_Scene(context));
                }
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