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

    private StageClearScene(Context context) {
        this.context = context;
        clearBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage_clear);
        float imgWidth = 500f;
        float imgHeight = 500f;
        float centerX = Metrics.width / 2f;
        float centerY = Metrics.height / 2f;
        float left = centerX - imgWidth / 2f;
        float top = centerY - imgHeight / 2f;
        clearRect = new RectF(left, top, left + imgWidth, top + imgHeight);

        // 버튼 위치: 이미지 내 상대좌표 → 실제 화면 좌표로 변환
        float selectBtnX = left + 139f;
        float selectBtnY = top + 391f;
        float selectBtnW = 140f;
        float selectBtnH = 60f;
        selectButtonRect = new RectF(selectBtnX, selectBtnY, selectBtnX + selectBtnW, selectBtnY + selectBtnH);

        float nextBtnX = left + 368f;
        float nextBtnY = top + 401f;
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

    public void show() {
        isVisible = true;
        // 스테이지 1-1 클리어 시 스테이지 1-2 해금
        StageManager.getInstance().unlockStage(1, 2);
        // 스테이지 1-1 클리어 표시
        StageManager.getInstance().setStageCleared(1, 1);
        // 몬스터들이 처치된 상태로 표시
        StageManager.getInstance().setMonstersDefeated(1, 1, true);
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
                    activity.setContentView(R.layout.stage1_select);
                    Scene stage1Scene = new Stage1_Scene(context);
                    activity.getGameView().pushScene(stage1Scene);
                }
                return true;
            } else if (nextStageButtonRect.contains(x, y)) {
                hide();
                // 스테이지 1-2 해금
                StageManager.getInstance().unlockStage(1, 2);
                SceneManager.getInstance().changeScene(SceneManager.SceneType.S1_2);
                return true;
            }
        }
        return false;
    }
} 