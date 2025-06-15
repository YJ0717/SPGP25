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
    private static final long GAME_DURATION = 10000;
    private long extendedDuration = 0; // 로그라이크로 연장된 시간

    private int maxHp = 100;
    private int currentHp;
    private boolean isAlive = true;
    private boolean isGameOver = false;

    private int roguePhysicalBuff = 0;
    private int rogueMagicBuff = 0;
    private int rogueHealBuff = 0;
    
    // 전투 로그라이크 효과들
    private boolean hasCriticalChance = false;
    private float criticalChance = 0.1f; // 10% 확률
    private float criticalMultiplier = 1.5f; // 1.5배 데미지
    
    private boolean hasDamageReduction = false;
    private float damageReductionRate = 0.5f; // 50% 데미지 감소
    
    private boolean hasStunChance = false;
    private float stunChance = 0.2f; // 20% 확률
    
    // 패시브 아이템 관련
    private boolean hasHourglassItem = false;
    private Bitmap hourglassBitmap;
    private boolean hasPocketItem = false;
    private Bitmap pocketBitmap;
    
    // 퍼즐 로그라이크 효과들
    private boolean hasRockBlockPrevention = false; // 돌블럭 생성 방지
    private boolean hasSwordBlockDouble = false;    // 칼블럭 2배
    private boolean hasMagicBlockDouble = false;    // 마법블럭 2배

    // 공포 상태 관련
    private boolean isFeared = false; // 공포 상태
    private long fearStartTime = 0;   // 공포 시작 시간
    private int fearTurnsRemaining = 0; // 공포 남은 턴 수

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
        
        // 공포 상태 표시 (HP 위에)
        if (isFeared) {
            textPaint.setColor(0xFFFFEB3B); // 노란색으로 변경
            textPaint.setTextSize(40); // 크기도 키우기
            canvas.drawText("공포 (" + fearTurnsRemaining + "턴)", leftMargin, startY - 40, textPaint);
            textPaint.setTextSize(30); // 원래 크기로 복원
        }
        
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
        long gameDuration = isFeared ? GAME_DURATION / 2 : GAME_DURATION; // 공포 상태면 50% 감소
        if (System.currentTimeMillis() - gameStartTime >= gameDuration + extendedDuration) {
            isGameOver = true;
            return true;
        }
        return false;
    }

    public int getRemainingSeconds() {
        long elapsed = System.currentTimeMillis() - gameStartTime;
        long gameDuration = isFeared ? GAME_DURATION / 2 : GAME_DURATION; // 공포 상태면 50% 감소
        return Math.max(0, (int)((gameDuration + extendedDuration - elapsed) / 1000));
    }

    public void reset() {
        physicalAttack = 0;
        magicAttack = 0;
        healing = 0;
        gameStartTime = System.currentTimeMillis();
        isGameOver = false;
        // 공포 효과는 다음 퍼즐 턴에 적용되어야 하므로 여기서는 해제하지 않음
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
        clearFear();
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
    
    // 전투 로그라이크 효과 관련 메서드들
    public void applyCriticalChance() {
        this.hasCriticalChance = true;
    }
    
    public void applyDamageReduction() {
        this.hasDamageReduction = true;
    }
    
    public void applyStunChance() {
        this.hasStunChance = true;
    }
    
    public boolean hasCriticalChance() {
        return hasCriticalChance;
    }
    
    public boolean hasDamageReduction() {
        return hasDamageReduction;
    }
    
    public boolean hasStunChance() {
        return hasStunChance;
    }
    
    public float getCriticalChance() {
        return criticalChance;
    }
    
    public float getCriticalMultiplier() {
        return criticalMultiplier;
    }
    
    public float getDamageReductionRate() {
        return damageReductionRate;
    }
    
    public float getStunChance() {
        return stunChance;
    }
    
    // 크리티컬 데미지 계산
    public int calculateCriticalDamage(int baseDamage) {
        if (hasCriticalChance && Math.random() < criticalChance) {
            return (int)(baseDamage * criticalMultiplier);
        }
        return baseDamage;
    }
    
    // 받는 데미지 계산
    public float calculateReceivedDamage(float incomingDamage) {
        if (hasDamageReduction) {
            return incomingDamage * (1.0f - damageReductionRate);
        }
        return incomingDamage;
    }
    
    // 마비 확률 체크
    public boolean checkStunChance() {
        return hasStunChance && Math.random() < stunChance;
    }

    // 퍼즐 로그라이크 효과 관련 메서드들
    public void applyRockBlockPrevention() {
        this.hasRockBlockPrevention = true;
    }
    
    public void applySwordBlockDouble() {
        this.hasSwordBlockDouble = true;
    }
    
    public void applyMagicBlockDouble() {
        this.hasMagicBlockDouble = true;
    }
    
    public boolean hasRockBlockPrevention() {
        return hasRockBlockPrevention;
    }
    
    public boolean hasSwordBlockDouble() {
        return hasSwordBlockDouble;
    }
    
    public boolean hasMagicBlockDouble() {
        return hasMagicBlockDouble;
    }
    
    // 칼블럭 점수 계산 (2배 적용)
    public int calculateSwordBlockScore(int blockCount) {
        int baseScore = blockCount * 1; // 기본 1점
        if (hasSwordBlockDouble) {
            return baseScore * 2; // 2배
        }
        return baseScore;
    }
    
    // 마법블럭 점수 계산 (2배 적용)
    public int calculateMagicBlockScore(int blockCount) {
        int baseScore = blockCount * 1; // 기본 1점
        if (hasMagicBlockDouble) {
            return baseScore * 2; // 2배
        }
        return baseScore;
    }

    // 공포 상태 관련 메서드들
    public void applyFear() {
        this.isFeared = true;
        this.fearStartTime = System.currentTimeMillis();
        this.fearTurnsRemaining = 2; // 공포 턴 카운터 초기화
    }
    
    public boolean isFeared() {
        return isFeared;
    }
    
    public void clearFear() {
        this.isFeared = false;
        this.fearStartTime = 0;
        this.fearTurnsRemaining = 0;
    }
    
    // 공포 턴 감소 (퍼즐 턴이 끝날 때마다 호출)
    public void reduceFearTurns() {
        if (isFeared && fearTurnsRemaining > 0) {
            fearTurnsRemaining--;
            if (fearTurnsRemaining <= 0) {
                clearFear();
            }
        }
    }
    
    public int getFearTurnsRemaining() {
        return fearTurnsRemaining;
    }
    
    // 공포 상태일 때 퍼즐 시간 계산 (50% 감소)
    public long getFearReducedGameDuration() {
        if (isFeared) {
            return GAME_DURATION / 2; // 50% 감소
        }
        return GAME_DURATION;
    }
} 