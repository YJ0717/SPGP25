package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import ac.tukorea.yunjun.pegglepang.R;

public class S1_2 extends BaseStageScene {
    private Stage1Monster monster1;
    private Stage1Monster monster2;
    private Paint paint;

    public S1_2(Context context) {
        super(context, 1, 2);
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void setupStageSpecificElements() {
        // 몬스터 생성 (위치 조정 필요)
        monster1 = new Stage1Monster(context, R.mipmap.skeleton_idle, 4, 200, 300, 100, 100);
        monster2 = new Stage1Monster(context, R.mipmap.slime_idle, 4, 400, 300, 100, 100);
    }

    @Override
    public void update() {
        super.update();
        monster1.update(0.016f); // 약 60fps 기준
        monster2.update(0.016f);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        monster1.draw(canvas);
        monster2.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        setupBackButton();
    }
} 