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
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class RoguelikeChoiceScene {
    private static RoguelikeChoiceScene instance;
    private boolean isVisible = false;
    private Bitmap attackBitmap;
    private Bitmap puzzleBitmap;
    private RectF cardRect;
    private Paint borderPaint;
    private OnRoguelikeDoneListener listener;
    private Context context;
    private Step step = Step.ATTACK;

    private enum Step { ATTACK, PUZZLE, DONE }

    public interface OnRoguelikeDoneListener {
        void onRoguelikeDone();
    }

    private RoguelikeChoiceScene(Context context) {
        this.context = context;
        attackBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.attack_rogue);
        puzzleBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.puzzle_rogue);
        float centerX = Metrics.width / 2f;
        float centerY = Metrics.height / 2f;
        float cardW = 420f;
        float cardH = 500f;
        cardRect = new RectF(centerX - cardW/2, centerY - cardH/2, centerX + cardW/2, centerY + cardH/2);
        borderPaint = new Paint();
        borderPaint.setColor(Color.YELLOW);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8f);
    }

    public static RoguelikeChoiceScene getInstance(Context context) {
        if (instance == null) {
            instance = new RoguelikeChoiceScene(context);
        }
        return instance;
    }

    public void show(OnRoguelikeDoneListener listener) {
        if (isVisible) return;
        isVisible = true;
        this.listener = listener;
        step = Step.ATTACK;
    }

    public void hide() {
        isVisible = false;
    }

    public void draw(Canvas canvas) {
        if (!isVisible) return;
        // 반투명 검은색 배경
        canvas.drawColor(Color.argb(180, 0, 0, 0));
        
        // 중앙에만 반투명 카드(패널) 그리기
        Paint cardPaint = new Paint();
        cardPaint.setColor(Color.argb(200, 40, 40, 40)); // 반투명 다크 그레이
        canvas.drawRoundRect(cardRect, 40, 40, cardPaint);
        canvas.drawRoundRect(cardRect, 40, 40, borderPaint);

        // 카드 중앙에 이미지
        float imgW = 180f, imgH = 180f;
        float imgX = Metrics.width / 2f - imgW / 2;
        float imgY = cardRect.top + 40;
        if (step == Step.ATTACK) {
            canvas.drawBitmap(attackBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        } else if (step == Step.PUZZLE) {
            canvas.drawBitmap(puzzleBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        }

        // 텍스트
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(54);
        String title = (step == Step.ATTACK) ? "전투 로그라이크" : "퍼즐 로그라이크";
        canvas.drawText(title, Metrics.width/2, imgY + imgH + 60, textPaint);
        textPaint.setTextSize(36);
        String desc = (step == Step.ATTACK) ? "전투 특성 효과를 획득합니다!" : "퍼즐 특성 효과를 획득합니다!";
        canvas.drawText(desc, Metrics.width/2, imgY + imgH + 120, textPaint);
        textPaint.setTextSize(28);
        canvas.drawText("(카드를 터치하세요)", Metrics.width/2, imgY + imgH + 170, textPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pt = Metrics.fromScreen(event.getX(), event.getY());
            float x = pt[0], y = pt[1];
            if (cardRect.contains(x, y)) {
                if (step == Step.ATTACK) {
                    // TODO: 전투 로그라이크 효과 적용
                    step = Step.PUZZLE;
                    return true;
                } else if (step == Step.PUZZLE) {
                    // TODO: 퍼즐 로그라이크 효과 적용
                    step = Step.DONE;
                    hide();
                    if (listener != null) listener.onRoguelikeDone();
                    return true;
                }
            }
        }
        return false;
    }
} 