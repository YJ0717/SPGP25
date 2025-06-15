package ac.tukorea.yunjun.pegglepang.PegglePang.game.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ac.tukorea.yunjun.pegglepang.R;

public class DamageText {
    private static final float DISPLAY_DURATION = 2.0f; // 3초 동안 표시
    private static final float MOVE_SPEED = 40f; // 위로 이동 속도
    private static final float FADE_START_TIME = 2.0f; // 2초 후부터 페이드 시작
    
    private Bitmap numberSheet;
    private List<DamageInstance> damageInstances;
    
    public static class DamageInstance {
        public int damage;
        public float x, y;
        public float startY;
        public float timer;
        public boolean isPlayerDamage; // true면 플레이어 데미지, false면 몬스터 데미지
        
        public DamageInstance(int damage, float x, float y, boolean isPlayerDamage) {
            this.damage = damage;
            this.x = x;
            this.y = y;
            this.startY = y;
            this.timer = 0f;
            this.isPlayerDamage = isPlayerDamage;
        }
    }
    
    public DamageText(Context context) {
        numberSheet = BitmapFactory.decodeResource(context.getResources(), R.mipmap.number);
        damageInstances = new ArrayList<>();
    }
    
    public void showDamage(int damage, float x, float y, boolean isPlayerDamage) {
        damageInstances.add(new DamageInstance(damage, x, y, isPlayerDamage));
    }
    
    public void update(float dt) {
        Iterator<DamageInstance> iterator = damageInstances.iterator();
        while (iterator.hasNext()) {
            DamageInstance instance = iterator.next();
            instance.timer += dt;
            
            // 위로 이동
            instance.y = instance.startY - (instance.timer * MOVE_SPEED);
            
            // 3초 후 제거
            if (instance.timer >= DISPLAY_DURATION) {
                iterator.remove();
            }
        }
    }
    
    public void draw(Canvas canvas) {
        for (DamageInstance instance : damageInstances) {
            drawNumber(canvas, instance.damage, instance.x, instance.y, instance.timer, instance.isPlayerDamage);
        }
    }
    
    private void drawNumber(Canvas canvas, int number, float x, float y, float timer, boolean isPlayerDamage) {
        String numberStr = String.valueOf(number);
        float digitWidth = 18f;
        float digitHeight = 34f;
        
        // 숫자 크기 설정 (플레이어와 몬스터 데미지 크기 동일)
        float scale = 2.5f; // 크게 표시
        float scaledWidth = digitWidth * scale;
        float scaledHeight = digitHeight * scale;
        
        // 전체 숫자 너비 계산
        float totalWidth = scaledWidth * numberStr.length();
        float startX = x - totalWidth / 2; // 중앙 정렬
        
        // 화면 경계 체크 및 조정
        if (startX < 0) startX = 10; // 왼쪽 경계
        if (startX + totalWidth > 1080) startX = 1080 - totalWidth - 10; // 오른쪽 경계 (화면 너비 가정)
        if (y < scaledHeight) y = scaledHeight + 10; // 상단 경계
        
        // 페이드 효과 계산
        int alpha = 255;
        if (timer > FADE_START_TIME) {
            float fadeProgress = (timer - FADE_START_TIME) / (DISPLAY_DURATION - FADE_START_TIME);
            alpha = (int)(255 * (1 - fadeProgress));
        }
        
        // 각 자릿수 그리기
        for (int i = 0; i < numberStr.length(); i++) {
            int digit = Character.getNumericValue(numberStr.charAt(i));
            
            // 소스 영역 (스프라이트 시트에서) - 0,1,2,3,4,5,6,7,8,9 순서
            int left = (int)(digit * digitWidth);
            int right = (int)(left + digitWidth);
            Rect src = new Rect(left, 0, right, (int)digitHeight);
            
            // 대상 영역 (화면에서)
            float digitX = startX + (i * scaledWidth);
            RectF dest = new RectF(digitX, y, digitX + scaledWidth, y + scaledHeight);
            
            // 알파값 적용해서 그리기
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setAlpha(alpha);
            canvas.drawBitmap(numberSheet, src, dest, paint);
        }
    }
    
    public void clear() {
        damageInstances.clear();
    }
} 