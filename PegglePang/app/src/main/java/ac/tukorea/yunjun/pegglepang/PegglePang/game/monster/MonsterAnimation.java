package ac.tukorea.yunjun.pegglepang.PegglePang.game.monster;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class MonsterAnimation {
    public enum Type {
        IDLE, ATTACK, DEATH
    }

    private Bitmap idleSheet;
    private Bitmap attackSheet;
    private Bitmap deathSheet;
    private Type currentType = Type.IDLE;
    private int frame = 0;
    private int frameCount;
    private float animTimer = 0f;
    private static final float FRAME_DURATION = 0.5f;
    private float x, y, width, height;
    private boolean isAnimating = false;
    private float deathTimer = 0f;
    private static final float DEATH_DURATION = 0.5f;

    private int idleFrameWidth = 203;
    private int idleFrameHeight = 46;
    private int attackFrameWidth = 203;
    private int attackFrameHeight = 46;
    private int deathFrameWidth = 203;
    private int deathFrameHeight = 46;

    private Paint paint;

    public MonsterAnimation(Bitmap idleSheet, Bitmap attackSheet, Bitmap deathSheet, float x, float y, float width, float height) {
        this.idleSheet = idleSheet;
        this.attackSheet = attackSheet;
        this.deathSheet = deathSheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.frameCount = 3;  // 기본 프레임 수
        this.paint = new Paint();
    }

    public void update(float dt) {
        if (currentType == Type.DEATH) {
            deathTimer += dt;
            if (deathTimer >= DEATH_DURATION) {
                isAnimating = false;
            }
            return;
        }

        if (isAnimating) {
            animTimer += dt;
            if (animTimer >= FRAME_DURATION) {
                animTimer -= FRAME_DURATION;
                frame = (frame + 1) % frameCount;
            }
        }
    }

    public void draw(Canvas canvas) {
        if (currentType == Type.DEATH && deathSheet != null) {
            int frameW = deathSheet.getWidth() / 3;  // 3프레임
            int frameH = deathSheet.getHeight();
            int currentDeathFrame = (int)(deathTimer / (DEATH_DURATION / 3));
            if (currentDeathFrame >= 3) currentDeathFrame = 2;
            
            Rect src = new Rect(deathFrameWidth * currentDeathFrame, 0, deathFrameWidth * (currentDeathFrame + 1), deathFrameHeight);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(deathSheet, src, dest, null);
        } else if (currentType == Type.ATTACK && attackSheet != null) {
            int frameW = attackSheet.getWidth() / frameCount;
            int frameH = attackSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(attackSheet, src, dest, null);
        } else if (idleSheet != null) {
            int frameW = idleSheet.getWidth() / frameCount;
            int frameH = idleSheet.getHeight();
            int left = frameW * frame;
            int right = left + frameW;
            Rect src = new Rect(left, 0, right, frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(idleSheet, src, dest, null);
        }
    }

    public void setType(Type type) {
        if (this.currentType != type) {
            this.currentType = type;
            this.frame = 0;
            this.animTimer = 0f;
            this.deathTimer = 0f;
            this.isAnimating = true;
        }
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }
} 