package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.R;

public class RoguelikeChoiceScene extends Scene {
    public interface OnChoiceListener {
        void onAttackRogue();
        void onPuzzleRogue();
    }

    private Bitmap attackBitmap;
    private Bitmap puzzleBitmap;
    private RectF attackRect;
    private RectF puzzleRect;
    private Paint borderPaint;
    private OnChoiceListener listener;
    private Context context;

    public RoguelikeChoiceScene(Context context, OnChoiceListener listener) {
        this.context = context;
        this.listener = listener;
        attackBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.attack_rogue);
        puzzleBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.puzzle_rogue);
        float centerX = Metrics.width / 2f;
        float centerY = Metrics.height / 2f;
        float imgW = 220f;
        float imgH = 220f;
        float gap = 60f;
        attackRect = new RectF(centerX - imgW - gap/2, centerY - imgH/2, centerX - gap/2, centerY + imgH/2);
        puzzleRect = new RectF(centerX + gap/2, centerY - imgH/2, centerX + imgW + gap/2, centerY + imgH/2);
        borderPaint = new Paint();
        borderPaint.setColor(Color.YELLOW);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8f);
    }

    @Override
    public void draw(Canvas canvas) {
        // 반투명 배경
        canvas.drawColor(Color.argb(180, 0, 0, 0));
        // 타이틀
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("로그라이크 특성 선택", Metrics.width/2, Metrics.height/2 - 180, textPaint);
        // 이미지
        canvas.drawBitmap(attackBitmap, null, attackRect, null);
        canvas.drawBitmap(puzzleBitmap, null, puzzleRect, null);
        // 테두리
        canvas.drawRect(attackRect, borderPaint);
        canvas.drawRect(puzzleRect, borderPaint);
        // 설명
        textPaint.setTextSize(40);
        canvas.drawText("전투 로그라이크", attackRect.centerX(), attackRect.bottom + 50, textPaint);
        canvas.drawText("퍼즐 로그라이크", puzzleRect.centerX(), puzzleRect.bottom + 50, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pt = Metrics.fromScreen(event.getX(), event.getY());
            float x = pt[0], y = pt[1];
            if (attackRect.contains(x, y)) {
                if (listener != null) listener.onAttackRogue();
                return true;
            } else if (puzzleRect.contains(x, y)) {
                if (listener != null) listener.onPuzzleRogue();
                return true;
            }
        }
        return false;
    }
} 