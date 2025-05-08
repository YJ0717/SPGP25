// 퍼즐 게임의 개별 블록을 관리하는 클래스
// 각 블록은 타입,위치, 이미지 슬라이드 처리

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Block {
    public static final int HEAL = 0;   
    public static final int MAGIC = 1;  
    public static final int SWORD = 2;  
    
    private int type;           
    private RectF rect;        
    private Bitmap bitmap;     
    
    private float currentX, currentY;    
    private float targetX, targetY;      
    private boolean isAnimating = false; 
    private boolean isFalling = false;
    private static final float SLIDE_SPEED = 0.2f;
    private static final float GRAVITY = 1.5f;
    private float velocityY = 0;
    
    public Block(int type, Bitmap bitmap) {
        this.type = type;
        this.bitmap = bitmap;
        this.rect = new RectF();
    }
    
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

    public void update(float deltaTime) {
        if (isAnimating) {
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
    
    public void draw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, null, rect, null);
        }
    }
    
    public int getType() {
        return type;
    }
} 