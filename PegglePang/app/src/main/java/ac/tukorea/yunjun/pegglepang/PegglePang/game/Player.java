package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.R;

public class Player {
    private PlayerStats stats;
    private PlayerAnimation animation;
    private boolean isBlinking = false;
    private float blinkTimer = 0f;
    private static final float BLINK_DURATION = 0.5f;
    private int blinkCount = 0;
    private int maxBlinkCount = 4;
    private int pendingDamage = 0;
    private boolean isDead = false;
    private boolean isAttacking = false;
    private float x, y;

    public Player(Context context, float x, float y, float width, float height, PlayerStats stats) {
        this.stats = stats;
        Bitmap idle = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_idle);
        Bitmap sword = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_swordattac);
        Bitmap magic = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_magicattack);
        Bitmap heal = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_healmotion);
        Bitmap dead = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_dead);
        this.animation = new PlayerAnimation(idle, sword, magic, heal, x, y, width, height);
        this.animation.setDeadSheet(dead);
        Bitmap magicEffect = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_effect);
        this.animation.setMagicEffectSheet(magicEffect);
        Bitmap swordEffect = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sword_effect);
        this.animation.setSwordEffectSheet(swordEffect);
        this.x = x;
        this.y = y;
    }

    public void update(float frameTime) {
        if (isBlinking) {
            blinkTimer += frameTime;
            if (blinkTimer >= BLINK_DURATION) {
                isBlinking = false;
                blinkTimer = 0f;
            }
        }

        if (isDead) {
            animation.update(frameTime);
            return;
        }

        if (isAttacking) {
            animation.update(frameTime);
            if (animation.isFinished()) {
                isAttacking = false;
                animation.setType(PlayerAnimation.Type.IDLE);
            }
        } else {
            animation.update(frameTime);
        }
    }

    public void draw(Canvas canvas) {
        if (isDead) {
            animation.draw(canvas, x, y);
            return;
        }

        if (isBlinking) {
            animation.draw(canvas, x, y, 128);
        } else {
            animation.draw(canvas, x, y);
        }
    }

    public void playSwordAttack(Runnable onEnd) {
        animation.play(PlayerAnimation.Type.SWORD, onEnd);
    }
    public void playMagicAttack(Runnable onEnd) {
        animation.play(PlayerAnimation.Type.MAGIC, onEnd);
    }
    public void playHeal(Runnable onEnd) {
        animation.play(PlayerAnimation.Type.HEAL, onEnd);
    }
    public void playIdle() {
        animation.play(PlayerAnimation.Type.IDLE, null);
    }
    public void playMagicEffect(Runnable onEnd) {
        animation.playMagicEffect(onEnd);
    }
    public void setMagicEffectPosition(float x, float y, float w, float h) {
        animation.setMagicEffectPosition(x, y, w, h);
    }
    public boolean isMagicEffectPlaying() {
        return animation.isMagicEffectPlaying();
    }

    public boolean isAnimPlaying() {
        return animation.isPlaying();
    }

    public PlayerStats getStats() {
        return stats;
    }

    public void setPosition(float x, float y, float width, float height) {
        animation.setPosition(x, y, width, height);
    }

    public void playSwordEffect(Runnable onEnd) {
        animation.playSwordEffect(onEnd);
    }
    public void setSwordEffectPosition(float x, float y, float w, float h) {
        animation.setSwordEffectPosition(x, y, w, h);
    }
    public boolean isSwordEffectPlaying() {
        return animation.isSwordEffectPlaying();
    }

    public void setAlpha(int alpha) {
        animation.setAlpha(alpha);
    }

    public void die() {
        isDead = true;
        animation.setType(PlayerAnimation.Type.DEAD);
        animation.play();
        GameOverScene.getInstance().show();
    }

    public boolean isDead() {
        return isDead;
    }

    public void setAnimationType(PlayerAnimation.Type type) {
        animation.setType(type);
    }

    public int getPhysicalAttack() {
        return stats.getPhysicalAttack();
    }

    public int getMagicAttack() {
        return stats.getMagicAttack();
    }

    public int getHealing() {
        return stats.getHealing();
    }

    public void takeDamage(float damage) {
        isBlinking = true;
        blinkTimer = 0f;
        stats.takeDamage(damage);
        if (stats.getCurrentHp() <= 0 && !isDead) {
            die();
        }
    }

    public boolean isAlive() {
        return stats.isAlive();
    }

    public void reset() {
        stats.reset();
    }

    public PlayerAnimation getAnimation() {
        return animation;
    }

    public void startBlinking(int damage) {
        isBlinking = true;
        blinkTimer = 0f;
        blinkCount = 0;
        pendingDamage = damage;
    }

    private void applyPendingDamage() {
        stats.takeDamage(pendingDamage);
        if (stats.getCurrentHp() <= 0 && !isDead) {
            die();
        }
        pendingDamage = 0;
    }
} 