package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.R;

public class Stage1Monster {
    private Bitmap idleSheet;
    private int frame = 0;
    private int frameCount;
    private float animTimer = 0f;
    private static final float FRAME_DURATION = 0.5f;
    private float x, y, width, height;
    private boolean isSkeleton = false;

    private int maxHp;
    private int currentHp;
    private int attackPower;
    private boolean isAlive = true;
    private Paint hpPaint;

    private boolean isBlinking = false;
    private float blinkTimer = 0f;
    private float blinkDuration = 0.5f;
    private int blinkCount = 0;
    private int maxBlinkCount = 4;
    private int pendingDamage = 0;

    public Stage1Monster(Context context, int resId, int frameCount, float x, float y, float width, float height) {
        this.idleSheet = BitmapFactory.decodeResource(context.getResources(), resId);
        this.frameCount = frameCount;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        if (resId == R.mipmap.skeleton_idle) {
            isSkeleton = true;
            this.maxHp = 150;
            this.attackPower = 15;
        } else {
            this.maxHp = 100;
            this.attackPower = 10;
        }
        this.currentHp = this.maxHp;

        hpPaint = new Paint();
        hpPaint.setColor(Color.WHITE);
        hpPaint.setTextSize(30);
        hpPaint.setAntiAlias(true);
        hpPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void update(float dt) {
        animTimer += dt;
        if (animTimer >= FRAME_DURATION) {
            animTimer -= FRAME_DURATION;
            frame = (frame + 1) % frameCount;
        }
        //피격시 깜빡거림
        if (isBlinking) {
            blinkTimer += dt;
            if (blinkTimer >= blinkDuration / maxBlinkCount) {
                blinkTimer -= blinkDuration / maxBlinkCount;
                blinkCount++;
                if (blinkCount >= maxBlinkCount) {
                    isBlinking = false;
                    blinkCount = 0;
                    if (pendingDamage > 0) {
                        applyPendingDamage();
                    }
                }
            }
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
            if (isBlinking && blinkCount % 2 == 0) {
                Paint blinkPaint = new Paint();
                blinkPaint.setAlpha(80);
                canvas.drawBitmap(idleSheet, src, dest, blinkPaint);
            } else {
                canvas.drawBitmap(idleSheet, src, dest, null);
            }
            // HP 표시 (몬스터 위에)
            float hpX = x + width / 2;
            float hpY = y - 10; // 몬스터 위 10픽셀 위에 표시
            hpPaint.setColor(Color.WHITE);
            canvas.drawText(currentHp + "/" + maxHp, hpX, hpY, hpPaint);
        }
    }

    public void setPosition(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
        if (currentHp <= 0) {
            isAlive = false;
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void startBlinking(int damage) {
        isBlinking = true;
        blinkTimer = 0f;
        blinkCount = 0;
        pendingDamage = damage;
    }

    private void applyPendingDamage() {
        currentHp = Math.max(0, currentHp - pendingDamage);
        if (currentHp <= 0) {
            isAlive = false;
        }
        pendingDamage = 0;
    }

    public boolean isBlinking() {
        return isBlinking;
    }
} 