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
    private static final float ANIM_SPEED = 0.2f; 
    
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
        }
        rect.set(currentX, currentY, currentX + (right - left), currentY + (bottom - top));
    }

    public void startAnimation(float targetLeft, float targetTop) {
        this.targetX = targetLeft;
        this.targetY = targetTop;
        this.isAnimating = true;
    }

    public void update(float deltaTime) {
        if (isAnimating) {
            float dx = targetX - currentX;
            float dy = targetY - currentY;
            
            currentX += dx * ANIM_SPEED;
            currentY += dy * ANIM_SPEED;
            
            if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                currentX = targetX;
                currentY = targetY;
                isAnimating = false;
            }
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