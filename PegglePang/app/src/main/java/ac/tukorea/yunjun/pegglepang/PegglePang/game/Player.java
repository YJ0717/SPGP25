package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.R;

public class Player {
    private PlayerStats stats;
    private Bitmap idleSheet;
    private int frame = 0;
    private int frameCount = 6;
    private float animTimer = 0f;
    private static final float FRAME_DURATION = 0.22f;
    private float x, y, width, height;

    public Player(Context context, float x, float y, float width, float height) {
        this.stats = new PlayerStats();
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_idle);
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
            Rect src = new Rect(frameW * frame, 0, frameW * (frame + 1), frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(idleSheet, src, dest, null);
        }
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
} 