package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;

public class PlayerStats {
    private int physicalAttack;
    private int magicAttack;
    private int healing;
    private Paint textPaint;
    private long gameStartTime;
    private static final long GAME_DURATION = 60000;

    public PlayerStats() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        
        gameStartTime = System.currentTimeMillis();
    }

    public void draw(Canvas canvas, float screenWidth, float top, float bottom) {
        float rightMargin = screenWidth - 50;
        float centerY = (top + bottom) / 2;
        float lineHeight = 45;
        float startY = centerY - lineHeight * 1.5f;
        
        canvas.drawText("물리공격력: " + physicalAttack, rightMargin, startY, textPaint);
        canvas.drawText("마법공격력: " + magicAttack, rightMargin, startY + lineHeight, textPaint);
        canvas.drawText("힐: " + healing, rightMargin, startY + lineHeight * 2, textPaint);
        canvas.drawText("Time: " + getRemainingSeconds() + "초", rightMargin, startY + lineHeight * 3, textPaint);
    }

    public void addPhysicalAttack(int count) {
        physicalAttack += count;
    }

    public void addMagicAttack(int count) {
        magicAttack += count;
    }

    public void addHealing(int count) {
        healing += count;
    }

    public boolean isGameOver() {
        return System.currentTimeMillis() - gameStartTime >= GAME_DURATION;
    }

    public int getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, (int)((GAME_DURATION - elapsed) / 1000));
    }

    public void reset() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        gameStartTime = System.currentTimeMillis();
    }

    public int getPhysicalAttack() {
        return physicalAttack;
    }

    public int getMagicAttack() {
        return magicAttack;
    }

    public int getHealing() {
        return healing;
    }
} 