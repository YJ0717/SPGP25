package ac.tukorea.yunjun.pegglepang.PegglePang.game.player;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ac.tukorea.yunjun.pegglepang.R;

public class PlayerStats {
    private int physicalAttack;
    private int magicAttack;
    private int healing;
    private Paint textPaint;
    private long gameStartTime;
    private static final long GAME_DURATION = 5000; // 60초
    private long extendedDuration = 0; // 로그라이크로 연장된 시간

    private int maxHp = 100;
    private int currentHp;
    private boolean isAlive = true;
    private boolean isGameOver = false;

    private int roguePhysicalBuff = 0;
    private int rogueMagicBuff = 0;
    private int rogueHealBuff = 0;
    
    // 패시브 아이템 관련
    private boolean hasHourglassItem = false;
    private Bitmap hourglassBitmap;
    private boolean hasPocketItem = false;
    private Bitmap pocketBitmap;

    public PlayerStats() {
        this(null);
    }
    
    public PlayerStats(Context context) {
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
        
        // 패시브 아이템 비트맵 로드
        if (context != null) {
            hourglassBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.time);
            pocketBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.pocket);
        }
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
        
        // 패시브 아이템들 표시 (HP 아래)
        float itemY = startY + lineHeight * 0.8f; // HP 아래에 위치
        float currentItemX = leftMargin;
        
        // 모래시계 아이템 표시
        if (hasHourglassItem && hourglassBitmap != null) {
            float itemSize = 40; // 모래시계 크기
            canvas.drawBitmap(hourglassBitmap, null, 
                new android.graphics.RectF(currentItemX, itemY, currentItemX + itemSize, itemY + itemSize), null);
            currentItemX += itemSize + 10; // 다음 아이템 위치
        }
        
        // 주머니 아이템 표시
        if (hasPocketItem && pocketBitmap != null) {
            float itemSize = 70; // 주머니 크기 (더 크게)
            canvas.drawBitmap(pocketBitmap, null, 
                new android.graphics.RectF(currentItemX, itemY, currentItemX + itemSize, itemY + itemSize), null);
        }
        
        float rightMargin = screenWidth - 50;
        textPaint.setTextAlign(Paint.Align.RIGHT);
        
        textPaint.setColor(0xFFFFEB3B); // 노란색
        canvas.drawText("물리공격력: " + getPhysicalAttack(), rightMargin, startY, textPaint);
        textPaint.setColor(0xFF9C27B0); // 보라색
        canvas.drawText("마법공격력: " + getMagicAttack(), rightMargin, startY + lineHeight, textPaint);
        textPaint.setColor(0xFF4CAF50); // 초록색
        canvas.drawText("힐: " + getHealing(), rightMargin, startY + lineHeight * 2, textPaint);
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
        if (System.currentTimeMillis() - gameStartTime >= GAME_DURATION + extendedDuration) {
            isGameOver = true;
            return true;
        }
        return false;
    }

    public int getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, (int)((GAME_DURATION + extendedDuration - elapsed) / 1000));
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
        return physicalAttack + roguePhysicalBuff;
    }

    public int getMagicAttack() {
        return magicAttack + rogueMagicBuff;
    }

    public int getHealing() {
        return healing + rogueHealBuff;
    }

    public void resetStatsAndTimer() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        gameStartTime = System.currentTimeMillis();
        isGameOver = false;
    }

    public void applyRoguePhysicalBuff(int amount) {
        this.roguePhysicalBuff = amount;
    }

    public void applyRogueMagicBuff(int amount) {
        this.rogueMagicBuff = amount;
    }

    public void applyRogueHealBuff(int amount) {
        this.rogueHealBuff = amount;
    }

    // 퍼즐 로그라이크 기능들
    public void extendPuzzleTime(int seconds) {
        this.extendedDuration = seconds * 1000; // 밀리초로 변환
        this.hasHourglassItem = true; // 모래시계 아이템 활성화
    }
    
    // 모래시계 아이템 관련 메소드들
    public void activateHourglassItem() {
        this.hasHourglassItem = true;
    }
    
    public boolean hasHourglassItem() {
        return hasHourglassItem;
    }
    
    public void setHourglassBitmap(Bitmap bitmap) {
        this.hourglassBitmap = bitmap;
    }
    
    public Bitmap getHourglassBitmap() {
        return hourglassBitmap;
    }
    
    // 주머니 아이템 관련 메소드들
    public void activatePocketItem() {
        this.hasPocketItem = true;
    }
    
    public boolean hasPocketItem() {
        return hasPocketItem;
    }
    
    public void setPocketBitmap(Bitmap bitmap) {
        this.pocketBitmap = bitmap;
    }
    
    public Bitmap getPocketBitmap() {
        return pocketBitmap;
    }
    
    // 주머니 클릭 감지를 위한 메소드
    public boolean isPocketClicked(float touchX, float touchY, float screenWidth, float top, float bottom) {
        if (!hasPocketItem || pocketBitmap == null) {
            return false;
        }
        
        float leftMargin = 50;
        float centerY = (top + bottom) / 2;
        float lineHeight = 45;
        float startY = centerY - lineHeight * 1.5f;
        float itemY = startY + lineHeight * 0.8f;
        float currentItemX = leftMargin;
        
        // 모래시계가 있으면 그 다음 위치
        if (hasHourglassItem) {
            currentItemX += 40 + 10; // 모래시계 크기 + 간격
        }
        
        float itemSize = 70; // 주머니 크기
        
        return touchX >= currentItemX && touchX <= currentItemX + itemSize &&
               touchY >= itemY && touchY <= itemY + itemSize;
    }
} 