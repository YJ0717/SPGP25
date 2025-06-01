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
        float cardW = 820f;
        float cardH = 700f;
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
        // 중앙에 로그라이크 이미지만 크게 표시 (배경, 카드, 텍스트 모두 제거)
        float imgW = Metrics.width * 0.8f;  // 화면 너비의 80%
        float imgH = Metrics.height * 0.8f; // 화면 높이의 80%
        float imgX = Metrics.width / 2f - imgW / 2;
        float imgY = Metrics.height / 2f - imgH / 2;
        if (step == Step.ATTACK) {
            canvas.drawBitmap(attackBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        } else if (step == Step.PUZZLE) {
            canvas.drawBitmap(puzzleBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        }
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