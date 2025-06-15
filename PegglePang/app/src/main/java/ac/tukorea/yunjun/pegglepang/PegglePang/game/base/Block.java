// 퍼즐 게임의 개별 블록을 관리하는 클래스
// 각 블록은 타입,위치, 이미지 슬라이드 처리

package ac.tukorea.yunjun.pegglepang.PegglePang.game.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.audio.SoundEffectManager;

public class Block {
    public static final int HEAL = 0;   
    public static final int MAGIC = 1;  
    public static final int SWORD = 2;
    public static final int ROCK = 3;
    public static final int BOMB = 4;
    
    private int type;           
    private RectF rect;        
    private Bitmap bitmap;
    private Bitmap rockBitmap;
    private Bitmap bombBitmap;
    private boolean isRock = false;
    private boolean isBomb = false;
    
    private float currentX, currentY;    
    private float targetX, targetY;      
    private boolean isAnimating = false; 
    private boolean isFalling = false;
    private boolean isRemoving = false;
    private float removeScale = 1.0f;
    private boolean soundPlayed = false;
    private static final float SLIDE_SPEED = 0.2f;
    private static final float GRAVITY = 1.5f;
    private static final float REMOVE_SPEED = 0.1f;
    private float velocityY = 0;

    private int row, col;
    
    public Block(int type, Bitmap bitmap) {
        this.type = type;
        this.bitmap = bitmap;
        this.rect = new RectF();
    }

    public void setRockBitmap(Bitmap rockBitmap) {
        this.rockBitmap = rockBitmap;
    }

    public void setBombBitmap(Bitmap bombBitmap) {
        this.bombBitmap = bombBitmap;
    }

    public void convertToRock() {
        this.isRock = true;
    }

    public void convertToBomb() {
        this.isBomb = true;
        this.type = BOMB;
    }

    public boolean isRock() {
        return isRock;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setGridPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    
    public void setPosition(float left, float top, float right, float bottom) {
        if (!isAnimating) {
            currentX = left;
            currentY = top;
            targetX = left;
            targetY = top;
            velocityY = 0;
        }
        rect.set(currentX, currentY, currentX + (right - left), currentY + (bottom - top));
    }

    public void startAnimation(float targetLeft, float targetTop, boolean falling) {
        this.targetX = targetLeft;
        this.targetY = targetTop;
        this.isAnimating = true;
        this.isFalling = falling;
        if (falling) {
            this.velocityY = 0;
        }
    }
    
    public void startRemoveAnimation() {
        this.isRemoving = true;
        this.removeScale = 1.0f;
        this.soundPlayed = false;
    }
    
    public boolean isRemoving() {
        return isRemoving;
    }
    
    public boolean isCompletelyRemoved() {
        return isRemoving && removeScale <= 0.0f;
    }

    public void update(float deltaTime) {
        if (isRemoving) {
            // 제거 애니메이션 - 스케일 축소
            removeScale -= REMOVE_SPEED;
            if (removeScale <= 0.8f && !soundPlayed) {
                // 블록이 80% 축소되었을 때 효과음 재생 (시각적으로 거의 사라질 때)
                SoundEffectManager.getInstance().playBlockBreakSound();
                soundPlayed = true;
            }
            if (removeScale <= 0.0f) {
                removeScale = 0.0f;
            }
        } else if (isAnimating) {
            if (isFalling) {
                velocityY += GRAVITY;
                currentY += velocityY;
                
                float dx = targetX - currentX;
                currentX += dx * SLIDE_SPEED;
                
                if (currentY >= targetY) {
                    currentY = targetY;
                    currentX = targetX;
                    isAnimating = false;
                    isFalling = false;
                    velocityY = 0;
                }
            } else {
                float dx = targetX - currentX;
                float dy = targetY - currentY;
                
                currentX += dx * SLIDE_SPEED;
                currentY += dy * SLIDE_SPEED;
                
                if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                    currentX = targetX;
                    currentY = targetY;
                    isAnimating = false;
                }
            }
            
            float width = rect.width();
            float height = rect.height();
            rect.set(currentX, currentY, currentX + width, currentY + height);
        }
    }
    
    public boolean isAnimating() {
        return isAnimating;
    }

    public boolean isFalling() {
        return isFalling;
    }
    
    public void draw(Canvas canvas) {
        if (isRemoving && removeScale > 0.0f) {
            // 제거 애니메이션 중일 때 스케일 적용
            canvas.save();
            float centerX = rect.centerX();
            float centerY = rect.centerY();
            canvas.scale(removeScale, removeScale, centerX, centerY);
            
            if (isBomb && bombBitmap != null) {
                canvas.drawBitmap(bombBitmap, null, rect, null);
            } else if (isRock && rockBitmap != null) {
                canvas.drawBitmap(rockBitmap, null, rect, null);
            } else if (bitmap != null) {
                canvas.drawBitmap(bitmap, null, rect, null);
            }
            
            canvas.restore();
        } else if (!isRemoving) {
            // 일반 상태일 때
            if (isBomb && bombBitmap != null) {
                canvas.drawBitmap(bombBitmap, null, rect, null);
            } else if (isRock && rockBitmap != null) {
                canvas.drawBitmap(rockBitmap, null, rect, null);
            } else if (bitmap != null) {
                canvas.drawBitmap(bitmap, null, rect, null);
            }
        }
    }
    
    public int getType() {
        return type;
    }

    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
    public float getTargetX() { return targetX; }
    public float getTargetY() { return targetY; }
} 