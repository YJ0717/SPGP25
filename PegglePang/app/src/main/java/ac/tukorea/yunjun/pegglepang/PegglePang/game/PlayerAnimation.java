package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class PlayerAnimation {
    public enum Type { IDLE, SWORD, MAGIC, HEAL }

    private Bitmap idleSheet, swordSheet, magicSheet, healSheet;
    private int idleFrameCount = 6;
    private int swordFrameCount = 2;
    private int magicFrameCount = 4;
    private int healFrameCount = 6;

    private int frame = 0;
    private float animTimer = 0f;
    private float frameDuration = 0.2f;
    private Type currentType = Type.IDLE;
    private boolean playing = false;
    private Runnable onAnimEnd;

    private float x, y, width, height;

    private int idleFrameWidth = 105;
    private int idleFrameHeight = 133;

    public PlayerAnimation(Bitmap idle, Bitmap sword, Bitmap magic, Bitmap heal,
                          float x, float y, float width, float height) {
        this.idleSheet = idle;
        this.swordSheet = sword;
        this.magicSheet = magic;
        this.healSheet = heal;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
            int offsetY = 90; 
            Rect src = new Rect(frameW * drawFrame, 0, frameW * (drawFrame + 1), frameH);
            RectF dest = new RectF(x, y + offsetY, x + width, y + height + offsetY);
            canvas.drawBitmap(sheet, src, dest, null);
        }
    }

    private Bitmap getSheet(Type type) {
        switch (type) {
            case SWORD: return swordSheet;
            case MAGIC: return magicSheet;
            case HEAL:  return healSheet;
            default:    return idleSheet;
        }
    }

    private int getFrameCount(Type type) {
        switch (type) {
            case SWORD: return swordFrameCount;
            case MAGIC: return magicFrameCount;
            case HEAL:  return healFrameCount;
            default:    return idleFrameCount;
        }
    }

    private int getFrameWidth(Type type) {
        switch (type) {
            case SWORD: return 90;
            case MAGIC: return 100;
            case HEAL:  return 70;
            case IDLE:  return idleFrameWidth;
            default:    return idleFrameWidth;
        }
    }

    private int getFrameHeight(Type type) {
        switch (type) {
            case SWORD: return 133;
            case MAGIC: return 135;
            case HEAL:  return 137;
            case IDLE:  return idleFrameHeight;
            default:    return idleFrameHeight;
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
}
