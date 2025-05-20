package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class PlayerAnimation {
    public enum Type { IDLE, SWORD, MAGIC, HEAL, DEAD }

    private Bitmap idleSheet, swordSheet, magicSheet, healSheet, deadSheet;
    private int idleFrameCount = 6;
    private int swordFrameCount = 2;
    private int magicFrameCount = 4;
    private int healFrameCount = 6;
    private int deadFrameCount = 6;

    private int frame = 0;
    private float animTimer = 0f;
    private float frameDuration = 0.5f;
    private Type currentType = Type.IDLE;
    private boolean playing = false;
    private Runnable onAnimEnd;

    private float x, y, width, height;

    private int idleFrameWidth = 105;
    private int idleFrameHeight = 133;
    private int deadFrameWidth = 105;
    private int deadFrameHeight = 149;

    private Bitmap magicEffectSheet;
    private int magicEffectFrame = 0;
    private int magicEffectFrameCount = 3;
    private float magicEffectAnimTimer = 0f;
    private float magicEffectFrameDuration = 0.15f;
    private boolean magicEffectPlaying = false;
    private float effectX, effectY, effectWidth, effectHeight;
    private Runnable magicEffectOnEnd;

    // Sword effect 관련 필드
    private Bitmap swordEffectSheet;
    private int swordEffectFrame = 0;
    private int swordEffectFrameCount = 5;
    private float swordEffectAnimTimer = 0f;
    private float swordEffectFrameDuration = 0.12f;
    private boolean swordEffectPlaying = false;
    private float swordEffectX, swordEffectY, swordEffectWidth, swordEffectHeight;
    private Runnable swordEffectOnEnd;

    private Paint paint;

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
                    if (currentType == Type.DEAD) {
                        frame = maxFrame - 1;
                        playing = false;
                    } else {
                        playing = false;
                        if (onAnimEnd != null) onAnimEnd.run();
                        play(Type.IDLE, null);
                    }
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
        // magic effect update
        if (magicEffectPlaying) {
            magicEffectAnimTimer += dt;
            if (magicEffectAnimTimer >= magicEffectFrameDuration) {
                magicEffectAnimTimer -= magicEffectFrameDuration;
                magicEffectFrame++;
                if (magicEffectFrame >= magicEffectFrameCount) {
                    magicEffectPlaying = false;
                    if (magicEffectOnEnd != null) magicEffectOnEnd.run();
                }
            }
        }
        if (swordEffectPlaying) {
            swordEffectAnimTimer += dt;
            if (swordEffectAnimTimer >= swordEffectFrameDuration) {
                swordEffectAnimTimer -= swordEffectFrameDuration;
                swordEffectFrame++;
                if (swordEffectFrame >= swordEffectFrameCount) {
                    swordEffectPlaying = false;
                    if (swordEffectOnEnd != null) swordEffectOnEnd.run();
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
            canvas.drawBitmap(sheet, src, dest, paint);
        }
        if (magicEffectPlaying && magicEffectSheet != null) {
            int effFrameW = 180;
            int effFrameH = 350;
            int effFrame = Math.min(magicEffectFrame, magicEffectFrameCount - 1);
            Rect src = new Rect(effFrameW * effFrame, 0, effFrameW * (effFrame + 1), effFrameH);
            RectF dest = new RectF(effectX, effectY, effectX + effectWidth, effectY + effectHeight);
            canvas.drawBitmap(magicEffectSheet, src, dest, paint);
        }
        if (swordEffectPlaying && swordEffectSheet != null) {
            int effFrameW = 140;
            int effFrameH = 150;
            int effFrame = Math.min(swordEffectFrame, swordEffectFrameCount - 1);
            Rect src = new Rect(effFrameW * effFrame, 0, effFrameW * (effFrame + 1), effFrameH);
            RectF dest = new RectF(swordEffectX, swordEffectY, swordEffectX + swordEffectWidth, swordEffectY + swordEffectHeight);
            canvas.drawBitmap(swordEffectSheet, src, dest, paint);
        }
    }

    private Bitmap getSheet(Type type) {
        switch (type) {
            case SWORD: return swordSheet;
            case MAGIC: return magicSheet;
            case HEAL:  return healSheet;
            case DEAD:  return deadSheet;
            default:    return idleSheet;
        }
    }

    private int getFrameCount(Type type) {
        switch (type) {
            case SWORD: return swordFrameCount;
            case MAGIC: return magicFrameCount;
            case HEAL:  return healFrameCount;
            case DEAD:  return deadFrameCount;
            default:    return idleFrameCount;
        }
    }

    private int getFrameWidth(Type type) {
        switch (type) {
            case SWORD: return 90;
            case MAGIC: return 100;
            case HEAL:  return 70;
            case DEAD:  return deadFrameWidth;
            case IDLE:  return idleFrameWidth;
            default:    return idleFrameWidth;
        }
    }

    private int getFrameHeight(Type type) {
        switch (type) {
            case SWORD: return 133;
            case MAGIC: return 135;
            case HEAL:  return 137;
            case DEAD:  return deadFrameHeight;
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

    public void setMagicEffectSheet(Bitmap sheet) {
        this.magicEffectSheet = sheet;
    }
    public void setMagicEffectPosition(float x, float y, float w, float h) {
        this.effectX = x;
        this.effectY = y;
        this.effectWidth = w;
        this.effectHeight = h;
    }
    public void playMagicEffect(Runnable onEnd) {
        this.magicEffectFrame = 0;
        this.magicEffectAnimTimer = 0f;
        this.magicEffectPlaying = true;
        this.magicEffectOnEnd = onEnd;
    }
    public boolean isMagicEffectPlaying() {
        return magicEffectPlaying;
    }

    public void setSwordEffectSheet(Bitmap sheet) {
        this.swordEffectSheet = sheet;
    }
    public void setSwordEffectPosition(float x, float y, float w, float h) {
        this.swordEffectX = x;
        this.swordEffectY = y;
        this.swordEffectWidth = w;
        this.swordEffectHeight = h;
    }
    public void playSwordEffect(Runnable onEnd) {
        this.swordEffectFrame = 0;
        this.swordEffectAnimTimer = 0f;
        this.swordEffectPlaying = true;
        this.swordEffectOnEnd = onEnd;
    }
    public boolean isSwordEffectPlaying() {
        return swordEffectPlaying;
    }

    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    public void setDeadSheet(Bitmap sheet) {
        this.deadSheet = sheet;
    }

    public void play() {
        this.frame = 0;
        this.animTimer = 0f;
        this.playing = true;
    }

    public void setType(Type type) {
        this.currentType = type;
    }

    public boolean isFinished() {
        return !playing;
    }

    public void draw(Canvas canvas, float x, float y) {
        this.x = x;
        this.y = y;
        draw(canvas);
    }

    public void draw(Canvas canvas, float x, float y, int alpha) {
        this.x = x;
        this.y = y;
        int oldAlpha = paint.getAlpha();
        paint.setAlpha(alpha);
        draw(canvas);
        paint.setAlpha(oldAlpha);
    }

    public void setFrameDuration(float duration) {
        this.frameDuration = duration;
    }
}
