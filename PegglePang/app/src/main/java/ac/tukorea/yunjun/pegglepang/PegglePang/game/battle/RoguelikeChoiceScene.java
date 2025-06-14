package ac.tukorea.yunjun.pegglepang.PegglePang.game.battle;

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
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.StageManager;

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
    private PlayerStats playerStats;

    private enum Step { ATTACK, PUZZLE, DONE }

    public interface OnRoguelikeDoneListener {
        void onRoguelikeDone(int puzzleChoice); // puzzleChoice: 0=폭탄, 1=시간연장, 2=무작위블록
    }

    private RoguelikeChoiceScene(Context context) {
        this.context = context;
        this.attackBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.attack_rogue);
        this.puzzleBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.puzzle_rogue);
        float centerX = Metrics.width / 2f;
        float centerY = Metrics.height / 2f;
        float cardW = 820f;
        float cardH = 700f;
        cardRect = new RectF(centerX - cardW/2, centerY - cardH/2, centerX + cardW/2, centerY + cardH/2);
        borderPaint = new Paint();
        borderPaint.setColor(Color.YELLOW);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(8f);
        this.playerStats = StageManager.getPlayerStats();
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
        float imgW = Metrics.width * 0.8f;  // 화면 너비의 80%
        float imgH = Metrics.height * 0.8f; // 화면 높이의 80%
        float imgX = Metrics.width / 2f - imgW / 2;
        float imgY = Metrics.height / 2f - imgH / 2;
        if (step == Step.ATTACK) {
            canvas.drawBitmap(attackBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        } else if (step == Step.PUZZLE) {
            canvas.drawBitmap(puzzleBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        }
        // 디버깅용: 각 아이콘 클릭 영역 빨간색 테두리로 표시
        float iconWidth = cardRect.width() / 3f;
        RectF physicalRect = new RectF(cardRect.left, cardRect.top, cardRect.left + iconWidth, cardRect.bottom);
        RectF magicRect = new RectF(cardRect.left + iconWidth, cardRect.top, cardRect.left + iconWidth * 2, cardRect.bottom);
        RectF healRect = new RectF(cardRect.left + iconWidth * 2, cardRect.top, cardRect.right, cardRect.bottom);
        Paint debugPaint = new Paint();
        debugPaint.setColor(Color.RED);
        debugPaint.setStyle(Paint.Style.STROKE);
        debugPaint.setStrokeWidth(6f);
        canvas.drawRect(physicalRect, debugPaint);
        canvas.drawRect(magicRect, debugPaint);
        canvas.drawRect(healRect, debugPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pt = Metrics.fromScreen(event.getX(), event.getY());
            float x = pt[0], y = pt[1];
            if (cardRect.contains(x, y)) {
                float iconWidth = cardRect.width() / 3f;
                RectF physicalRect = new RectF(cardRect.left, cardRect.top, cardRect.left + iconWidth, cardRect.bottom);
                RectF magicRect = new RectF(cardRect.left + iconWidth, cardRect.top, cardRect.left + iconWidth * 2, cardRect.bottom);
                RectF healRect = new RectF(cardRect.left + iconWidth * 2, cardRect.top, cardRect.right, cardRect.bottom);
                if (step == Step.ATTACK) {
                    if (physicalRect.contains(x, y)) {
                        playerStats.applyRoguePhysicalBuff(10);
                        step = Step.PUZZLE;
                        return true;
                    } else if (magicRect.contains(x, y)) {
                        playerStats.applyRogueMagicBuff(8);
                        step = Step.PUZZLE;
                        return true;
                    } else if (healRect.contains(x, y)) {
                        playerStats.applyRogueHealBuff(5);
                        step = Step.PUZZLE;
                        return true;
                    }
                } else if (step == Step.PUZZLE) {
                    if (physicalRect.contains(x, y)) {
                        // 폭탄 선택 - 폭탄 블록 활성화
                        step = Step.DONE;
                        hide();
                        if (listener != null) listener.onRoguelikeDone(0);
                        return true;
                    } else if (magicRect.contains(x, y)) {
                        // 모래시계 선택 - 퍼즐 시간 연장
                        step = Step.DONE;
                        hide();
                        if (listener != null) listener.onRoguelikeDone(1);
                        return true;
                    } else if (healRect.contains(x, y)) {
                        // 주머니 선택 - 무작위 블록 5개 터뜨리기
                        step = Step.DONE;
                        hide();
                        if (listener != null) listener.onRoguelikeDone(2);
                        return true;
                    }
                }
            }
        }
        return false;
    }
} 