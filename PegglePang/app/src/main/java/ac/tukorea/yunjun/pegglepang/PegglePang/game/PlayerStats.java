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
    private static final long GAME_DURATION = 5000;

    private int maxHp = 100;
    private int currentHp;
    private boolean isAlive = true;
    private boolean isGameOver = false;

    public PlayerStats() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        currentHp = maxHp;
        
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        
        gameStartTime = System.currentTimeMillis();
    }

    public void takeDamage(float damage) {
        currentHp -= damage;
        if (currentHp < 0) {
            currentHp = 0;
            isAlive = false;
        }
    }

    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
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

    public void draw(Canvas canvas, float screenWidth, float top, float bottom) {
        // HP는 왼쪽에 표시
        float leftMargin = 50;
        float centerY = (top + bottom) / 2;
        float lineHeight = 45;
        float startY = centerY - lineHeight * 1.5f;
        
        textPaint.setColor(0xFFE91E63); 
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("HP: " + currentHp + "/" + maxHp, leftMargin, startY, textPaint);
        
        float rightMargin = screenWidth - 50;
        textPaint.setTextAlign(Paint.Align.RIGHT);
        
        textPaint.setColor(0xFFFFEB3B); // 노란색
        canvas.drawText("물리공격력: " + physicalAttack, rightMargin, startY, textPaint);
        textPaint.setColor(0xFF9C27B0); // 보라색
        canvas.drawText("마법공격력: " + magicAttack, rightMargin, startY + lineHeight, textPaint);
        textPaint.setColor(0xFF4CAF50); // 초록색
        canvas.drawText("힐: " + healing, rightMargin, startY + lineHeight * 2, textPaint);
        textPaint.setColor(0xFFF44336); // 빨간색
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
        if (isGameOver) return true;
        if (System.currentTimeMillis() - gameStartTime >= GAME_DURATION) {
            isGameOver = true;
            return true;
        }
        return false;
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
        isGameOver = false;
    }

    public void resetTimerOnly() {
        gameStartTime = System.currentTimeMillis();
        isGameOver = false;
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

    public void resetStatsAndTimer() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        gameStartTime = System.currentTimeMillis();
        isGameOver = false;
    }
} 