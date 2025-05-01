// 퍼즐 게임의 개별 블록을 관리하는 클래스. 블록의 타입(힐, 매직, 검), 위치, 이미지를 처리하고 화면에 그리는 역할 담당
//추후 특수 블럭도 추가할 예정
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
    
    public Block(int type, Bitmap bitmap) {
        this.type = type;
        this.bitmap = bitmap;
        this.rect = new RectF();
    }
    
    public void setPosition(float left, float top, float right, float bottom) {
        rect.set(left, top, right, bottom);
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