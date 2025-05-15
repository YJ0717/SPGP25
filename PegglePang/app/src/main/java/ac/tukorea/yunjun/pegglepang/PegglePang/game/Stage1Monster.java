package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.R;

public class Stage1Monster {
    private Bitmap idleSheet;
    private int frame = 0;
    private int frameCount = 3;
    private float animTimer = 0f;
    private static final float FRAME_DURATION = 0.25f;
    private float x, y, width, height;

    public Stage1Monster(Context context, float x, float y, float width, float height) {
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.slime_idle);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(float dt) {
        animTimer += dt;
        if (animTimer >= FRAME_DURATION) {
            animTimer -= FRAME_DURATION;
            frame = (frame + 1) % frameCount;
        }
    }

    public void draw(Canvas canvas) {
        if (idleSheet != null) {
            int frameW = idleSheet.getWidth() / frameCount;
            int frameH = idleSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(idleSheet, src, dest, null);
        }
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Bitmap getIdleSheet() {
        return idleSheet;
    }
} 