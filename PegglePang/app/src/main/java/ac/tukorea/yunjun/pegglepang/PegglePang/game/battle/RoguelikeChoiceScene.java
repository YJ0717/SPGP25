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
    private Bitmap attackBitmap2;
    private Bitmap puzzleBitmap2;
    private RectF cardRect;
    private Paint borderPaint;
    private OnRoguelikeDoneListener listener;
    private Context context;
    private Step step = Step.ATTACK;
    private PlayerStats playerStats;
    private boolean useSecondImages = false;

    private enum Step { ATTACK, PUZZLE, DONE }

    public interface OnRoguelikeDoneListener {
        void onRoguelikeDone(int puzzleChoice); // puzzleChoice: 0=폭탄, 1=시간연장, 2=무작위블록
    }

    private RoguelikeChoiceScene(Context context) {
        this.context = context;
        this.attackBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.attack_rogue);
        this.puzzleBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.puzzle_rogue);
        this.attackBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.attack_rogue2);
        this.puzzleBitmap2 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.puzzle_rogue2);
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
        useSecondImages = false;
    }

    public void showBattleRogue(OnRoguelikeDoneListener listener) {
        if (isVisible) return;
        isVisible = true;
        this.listener = listener;
        step = Step.ATTACK;
        useSecondImages = true;
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
            Bitmap currentAttackBitmap = useSecondImages ? attackBitmap2 : attackBitmap;
            canvas.drawBitmap(currentAttackBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        } else if (step == Step.PUZZLE) {
            Bitmap currentPuzzleBitmap = useSecondImages ? puzzleBitmap2 : puzzleBitmap;
            canvas.drawBitmap(currentPuzzleBitmap, null, new RectF(imgX, imgY, imgX+imgW, imgY+imgH), null);
        }

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
                        if (useSecondImages) {
                            // 전투 로그라이크: 크리티컬 확률
                            playerStats.applyCriticalChance();
                            StageManager.enableBattleCritical();
                        } else {
                            // 기존 로그라이크: 물리 공격력 버프
                        playerStats.applyRoguePhysicalBuff(10);
                        }
                        step = Step.PUZZLE;
                        return true;
                    } else if (magicRect.contains(x, y)) {
                        if (useSecondImages) {
                            // 전투 로그라이크: 데미지 감소
                            playerStats.applyDamageReduction();
                            StageManager.enableBattleDamageReduction();
                        } else {
                            // 기존 로그라이크: 마법 공격력 버프
                            playerStats.applyRogueMagicBuff(10);
                        }
                        step = Step.PUZZLE;
                        return true;
                    } else if (healRect.contains(x, y)) {
                        if (useSecondImages) {
                            // 전투 로그라이크: 마비 확률
                            playerStats.applyStunChance();
                            StageManager.enableBattleStun();
                        } else {
                            // 기존 로그라이크: 힐링 버프
                            playerStats.applyRogueHealBuff(10);
                        }
                        step = Step.PUZZLE;
                        return true;
                    }
                } else if (step == Step.PUZZLE) {
                    if (physicalRect.contains(x, y)) {
                        if (useSecondImages) {
                            // 스테이지 3 퍼즐 로그라이크: 돌블럭 생성 방지 (망치 아이콘)
                            playerStats.applyRockBlockPrevention();
                            StageManager.enablePuzzleRockPrevention();
                            StageManager.setStage2PuzzleRoguelikeChoice(0); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(0);
                        } else {
                            // 스테이지 1 퍼즐 로그라이크: 폭탄 블록
                            StageManager.enableBombBlocks();
                            StageManager.setStage1RoguelikeChoice(0); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(0);
                        }
                        return true;
                    } else if (magicRect.contains(x, y)) {
                        if (useSecondImages) {
                            // 스테이지 3 퍼즐 로그라이크: 칼블럭 2배 (칼 아이콘)
                            playerStats.applySwordBlockDouble();
                            StageManager.enablePuzzleSwordDouble();
                            StageManager.setStage2PuzzleRoguelikeChoice(1); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(1);
                        } else {
                            // 스테이지 1 퍼즐 로그라이크: 시간 연장
                            playerStats.extendPuzzleTime(30);
                            Bitmap hourglassBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.time);
                            playerStats.setHourglassBitmap(hourglassBitmap);
                            StageManager.setStage1RoguelikeChoice(1); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(1);
                        }
                        return true;
                    } else if (healRect.contains(x, y)) {
                        if (useSecondImages) {
                            // 스테이지 3 퍼즐 로그라이크: 마법블럭 2배 (마법 아이콘)
                            playerStats.applyMagicBlockDouble();
                            StageManager.enablePuzzleMagicDouble();
                            StageManager.setStage2PuzzleRoguelikeChoice(2); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(2);
                        } else {
                            // 스테이지 1 퍼즐 로그라이크: 주머니 아이템
                            playerStats.activatePocketItem();
                            Bitmap pocketBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.pocket);
                            playerStats.setPocketBitmap(pocketBitmap);
                            StageManager.setStage1RoguelikeChoice(2); // 선택 저장
                            step = Step.DONE;
                            hide();
                            if (listener != null) listener.onRoguelikeDone(2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
} 