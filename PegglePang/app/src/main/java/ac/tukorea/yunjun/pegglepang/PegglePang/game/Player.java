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
    private PlayerAnimation animation;

    public Player(Context context, float x, float y, float width, float height) {
        this.stats = new PlayerStats();
        Bitmap idle = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_idle);
        Bitmap sword = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_swordattac);
        Bitmap magic = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_magicattack);
        Bitmap heal = BitmapFactory.decodeResource(context.getResources(), R.mipmap.player_healmotion);
        this.animation = new PlayerAnimation(idle, sword, magic, heal, x, y, width, height);
        Bitmap magicEffect = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_effect);
        this.animation.setMagicEffectSheet(magicEffect);
    }

    public void update(float dt) {
        animation.update(dt);
    }

    public void draw(Canvas canvas) {
        animation.draw(canvas);
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
} 