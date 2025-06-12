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
        void onRoguelikeDone();
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
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float[] pt = Metrics.fromScreen(event.getX(), event.getY());
            float x = pt[0], y = pt[1];
            if (cardRect.contains(x, y)) {
                if (step == Step.ATTACK) {
                    // 전투 로그라이크 효과 적용
                    float selectX = x - cardRect.left;
                    float selectWidth = cardRect.width() / 3;
                    
                    if (selectX < selectWidth) {
                        // 물리 공격력 +10
                        playerStats.applyRoguePhysicalBuff(10);
                    } else if (selectX < selectWidth * 2) {
                        // 마법 공격력 +8
                        playerStats.applyRogueMagicBuff(8);
                    } else {
                        // 힐링 +5
                        playerStats.applyRogueHealBuff(5);
                    }
                    step = Step.PUZZLE;
                    return true;
                } else if (step == Step.PUZZLE) {
                    // 퍼즐 로그라이크 효과 적용
                    float selectX = x - cardRect.left;
                    float selectWidth = cardRect.width() / 3;
                    
                    if (selectX < selectWidth) {
                        // 물리 공격력 추가 +10
                        playerStats.applyRoguePhysicalBuff(playerStats.getPhysicalAttack() + 10);
                    } else if (selectX < selectWidth * 2) {
                        // 마법 공격력 추가 +8
                        playerStats.applyRogueMagicBuff(playerStats.getMagicAttack() + 8);
                    } else {
                        // 힐링 추가 +5
                        playerStats.applyRogueHealBuff(playerStats.getHealing() + 5);
                    }
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