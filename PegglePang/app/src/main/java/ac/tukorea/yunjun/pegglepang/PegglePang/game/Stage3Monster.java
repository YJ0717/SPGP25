package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.content.Context;
import ac.tukorea.yunjun.pegglepang.R;

public class Stage3Monster extends Stage2Monster {
    private static final int FRAME_COUNT = 5;
    private static final float FRAME_DURATION = 0.15f;
    private static final int MONSTER_WIDTH = 284;
    private static final int MONSTER_HEIGHT = 68;

    public Stage3Monster(Context context, int resId, int hp, float x, float y, float width, float height, float attackPower) {
        super(context, resId, FRAME_COUNT, x, y, width, height, attackPower);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
} 