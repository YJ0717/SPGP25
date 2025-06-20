package ac.tukorea.yunjun.pegglepang.PegglePang.game.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.S2_1;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.S2_2;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.S2_3;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage1_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage2_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage3_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.StageFactory;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.StageManager;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.world.worldSelectScene;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class StageClearScene {
    private static StageClearScene instance;
    private boolean isVisible = false;
    private Bitmap clearBitmap;
    private RectF clearRect;
    private RectF selectButtonRect;
    private RectF nextStageButtonRect;
    private Paint buttonPaint;
    private Context context;
    private int currentStage = 1;
    private int currentSubStage = 1;

    private StageClearScene(Context context) {
        this.context = context;
        clearBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage_clear);
        float imgWidth = 470f;
        float imgHeight = 500f;
        float centerX = Metrics.width / 2f;
        float centerY = Metrics.height / 2f;
        float left = centerX - imgWidth / 2f;
        float top = centerY - imgHeight / 2f;
        clearRect = new RectF(left, top, left + imgWidth, top + imgHeight);

        // 버튼 위치: 이미지 내 상대좌표 → 실제 화면 좌표로 변환
        float selectBtnX = left + 120f;
        float selectBtnY = top + 391f;
        float selectBtnW = 140f;
        float selectBtnH = 60f;
        selectButtonRect = new RectF(selectBtnX, selectBtnY, selectBtnX + selectBtnW, selectBtnY + selectBtnH);

        float nextBtnX = left + 270f;
        float nextBtnY = top + 380f;
        float nextBtnW = 140f;
        float nextBtnH = 60f;
        nextStageButtonRect = new RectF(nextBtnX, nextBtnY, nextBtnX + nextBtnW, nextBtnY + nextBtnH);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.TRANSPARENT); // 버튼은 이미지로만 보임, 터치 판정용
    }

    public static StageClearScene getInstance(Context context) {
        if (instance == null) {
            instance = new StageClearScene(context);
        }
        return instance;
    }

    public void show(int stage, int subStage) {
        if (isVisible) return;
        isVisible = true;
        currentStage = stage;
        currentSubStage = subStage;
        StageManager.getInstance().setStageCleared(stage, subStage);
        StageManager.getInstance().setMonstersDefeated(stage, subStage, true);
        if (stage == 1 && subStage == 3) {
            // 월드2 해금 및 스테이지 2-1 해금
            StageManager.getInstance().unlockWorld(2);
            StageManager.getInstance().unlockStage(2, 1);
        } else if (stage == 2 && subStage == 3) {
            // 월드3 해금 및 스테이지 3-1 해금
            System.out.println("=== S2_3 클리어 ===");
            System.out.println("월드3 해금 시작");
            StageManager.getInstance().unlockWorld(3);
            StageManager.getInstance().unlockStage(3, 1);
            System.out.println("월드3 해금 완료");
        } else if (stage == 1 && subStage == 2) {
            StageManager.getInstance().unlockStage(1, 3);
        } else if (stage == 3 && subStage == 1) {
            // 3-1 클리어 시 3-2 해금
            StageManager.getInstance().unlockStage(3, 2);
        } else if (stage == 3 && subStage == 2) {
            // 3-2 클리어 시 3-3 해금
            StageManager.getInstance().unlockStage(3, 3);
        } else {
            StageManager.getInstance().unlockStage(stage, subStage + 1);
        }
    }

    public void hide() {
        isVisible = false;
    }

    public void draw(Canvas canvas) {
        if (!isVisible) return;
        // 반투명 검은색 배경
        canvas.drawColor(Color.argb(180, 0, 0, 0));
        // 스테이지 클리어 이미지
        canvas.drawBitmap(clearBitmap, null, clearRect, null);
        // (디버그용) 버튼 영역 표시
        //canvas.drawRect(selectButtonRect, buttonPaint);
        //canvas.drawRect(nextStageButtonRect, buttonPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;
        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (selectButtonRect.contains(x, y)) {
                hide();
                if (context instanceof PegglePangActivity) {
                    PegglePangActivity activity = (PegglePangActivity) context;
                    if (currentStage == 1) {
                        // 월드1 스테이지 선택화면으로
                        activity.setContentView(R.layout.stage1_select);
                        Scene stage1Scene = new Stage1_Scene(context);
                        activity.getGameView().changeScene(stage1Scene);
                    } else if (currentStage == 2) {
                        // 월드2 스테이지 선택화면으로
                        activity.setContentView(R.layout.world2_stage_select);
                        Scene stage2Scene = new Stage2_Scene(context);
                        activity.getGameView().changeScene(stage2Scene);
                    } else if (currentStage == 3) {
                        // 월드3 스테이지 선택화면으로
                        activity.setContentView(R.layout.world3_stage_select);
                        Scene stage3Scene = new Stage3_Scene(context);
                        activity.getGameView().changeScene(stage3Scene);
                    }
                }
                return true;
            } else if (nextStageButtonRect.contains(x, y)) {
                hide();
                if (context instanceof PegglePangActivity) {
                    PegglePangActivity activity = (PegglePangActivity) context;
                    
                    // 다음 스테이지로 이동
                    if (currentStage == 1 && currentSubStage == 3) {
                        // 1-3 클리어 후 2-1로
                        activity.setContentView(R.layout.game_scene);
                        S2_1 stage = new S2_1(context);
                        activity.getGameView().changeScene(stage);
                    } else if (currentStage == 2 && currentSubStage == 1) {
                        // 2-1 클리어 후 2-2로
                        activity.setContentView(R.layout.game_scene);
                        S2_2 stage = new S2_2(context);
                        activity.getGameView().changeScene(stage);
                    } else if (currentStage == 2 && currentSubStage == 2) {
                        // 2-2 클리어 후 2-3으로
                        activity.setContentView(R.layout.game_scene);
                        S2_3 stage = new S2_3(context);
                        activity.getGameView().changeScene(stage);
                    } else if (currentStage == 2 && currentSubStage == 3) {
                        // 2-3 클리어 후 3-1로
                        Scene nextStage = StageFactory.createStage(context, 3, 1);
                        if (nextStage != null) {
                            activity.setContentView(R.layout.game_scene);
                            activity.getGameView().changeScene(nextStage);
                        }
                    } else if (currentStage == 3 && currentSubStage == 1) {
                        // 3-1 클리어 후 3-2로
                        Scene nextStage = StageFactory.createStage(context, 3, 2);
                        if (nextStage != null) {
                            activity.setContentView(R.layout.game_scene);
                            activity.getGameView().changeScene(nextStage);
                        }
                    } else if (currentStage == 3 && currentSubStage == 2) {
                        // 3-2 클리어 후 3-3으로
                        Scene nextStage = StageFactory.createStage(context, 3, 3);
                        if (nextStage != null) {
                            activity.setContentView(R.layout.game_scene);
                            activity.getGameView().changeScene(nextStage);
                        }
                    } else {
                        // 일반적인 다음 스테이지
                        Scene nextStage = StageFactory.createStage(context, currentStage, currentSubStage + 1);
                        if (nextStage != null) {
                            activity.setContentView(R.layout.game_scene);
                            activity.getGameView().changeScene(nextStage);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
} 