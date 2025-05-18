package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class MonsterAnimation {
    public enum Type { IDLE, ATTACK, HIT }

    private Bitmap idleSheet, attackSheet, hitSheet;
    private int idleFrameCount = 3;
    private int attackFrameCount = 2;
    private int hitFrameCount = 2;

    private int frame = 0;
    private float animTimer = 0f;
    private float frameDuration = 0.2f;
    private Type currentType = Type.IDLE;
    private boolean playing = false;
    private Runnable onAnimEnd;

    private float x, y, width, height;

    private int idleFrameWidth = 203;
    private int idleFrameHeight = 46;
    private int attackFrameWidth = 203;
    private int attackFrameHeight = 46;
    private int hitFrameWidth = 203;
    private int hitFrameHeight = 46;

    private Paint paint;

    public MonsterAnimation(Bitmap idle, Bitmap attack, Bitmap hit,
                          float x, float y, float width, float height) {
        this.idleSheet = idle;
        this.attackSheet = attack;
        this.hitSheet = hit;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.paint = new Paint();
    }

    public void play(Type type, Runnable onEnd) {
        this.currentType = type;
        this.frame = 0;
        this.animTimer = 0f;
        this.playing = (type != Type.IDLE);
        this.onAnimEnd = onEnd;
    }

    public void update(float dt) {
        int maxFrame = getFrameCount(currentType);
        if (playing) {
            animTimer += dt;
            if (animTimer >= frameDuration) {
                animTimer -= frameDuration;
                frame++;
                if (frame >= maxFrame) {
                    playing = false;
                    if (onAnimEnd != null) onAnimEnd.run();
                    play(Type.IDLE, null);
                }
            }
        } else {
            if (currentType == Type.IDLE) {
                animTimer += dt;
                if (animTimer >= frameDuration) {
                    animTimer -= frameDuration;
                    frame = (frame + 1) % idleFrameCount;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        Bitmap sheet = getSheet(currentType);
        int frameW = getFrameWidth(currentType);
        int frameH = getFrameHeight(currentType);
        int maxFrame = getFrameCount(currentType);

        int drawFrame = Math.min(frame, maxFrame - 1);
        if (sheet != null) {
            Rect src = new Rect(frameW * drawFrame, 0, frameW * (drawFrame + 1), frameH);
            RectF dest = new RectF(x, y, x + width, y + height);
            canvas.drawBitmap(sheet, src, dest, paint);
        }
    }

    private Bitmap getSheet(Type type) {
        switch (type) {
            case ATTACK: return attackSheet;
            case HIT:    return hitSheet;
            default:     return idleSheet;
        }
    }

    private int getFrameCount(Type type) {
        switch (type) {
            case ATTACK: return attackFrameCount;
            case HIT:    return hitFrameCount;
            default:     return idleFrameCount;
        }
    }

    private int getFrameWidth(Type type) {
        switch (type) {
            case ATTACK: return attackFrameWidth;
            case HIT:    return hitFrameWidth;
            default:     return idleFrameWidth;
        }
    }

    private int getFrameHeight(Type type) {
        switch (type) {
            case ATTACK: return attackFrameHeight;
            case HIT:    return hitFrameHeight;
            default:     return idleFrameHeight;
        }
    }

    public boolean isPlaying() {
        return playing;
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

    public void setType(Type type) {
        this.currentType = type;
    }
} 