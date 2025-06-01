package ac.tukorea.yunjun.pegglepang.PegglePang.game.main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.SceneManager;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;

public class GameOverScene {
    private static GameOverScene instance;
    private Paint textPaint;
    private Paint buttonPaint;
    private RectF retryButton;
    private RectF mainMenuButton;
    private boolean isVisible = false;

    private GameOverScene() {
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(100, 100, 100));
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setAntiAlias(true);

        float buttonWidth = Metrics.width * 0.4f;
        float buttonHeight = 120;
        float centerX = Metrics.width / 2;
        float centerY = Metrics.height / 2;

        retryButton = new RectF(
            centerX - buttonWidth/2,
            centerY,
            centerX + buttonWidth/2,
            centerY + buttonHeight
        );

        mainMenuButton = new RectF(
            centerX - buttonWidth/2,
            centerY + buttonHeight + 50,
            centerX + buttonWidth/2,
            centerY + buttonHeight * 2 + 50
        );
    }

    public static GameOverScene getInstance() {
        if (instance == null) {
            instance = new GameOverScene();
        }
        return instance;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public void draw(Canvas canvas) {
        if (!isVisible) return;

        // 반투명 검은색 배경
        canvas.drawColor(Color.argb(200, 0, 0, 0));

        // Game Over 텍스트
        canvas.drawText("GAME OVER", Metrics.width/2, Metrics.height/3, textPaint);

        // 버튼 그리기
        canvas.drawRoundRect(retryButton, 20, 20, buttonPaint);
        canvas.drawRoundRect(mainMenuButton, 20, 20, buttonPaint);

        // 버튼 텍스트
        textPaint.setTextSize(60);
        canvas.drawText("다시하기", retryButton.centerX(), retryButton.centerY() + 20, textPaint);
        canvas.drawText("메인메뉴", mainMenuButton.centerX(), mainMenuButton.centerY() + 20, textPaint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;

        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (retryButton.contains(x, y)) {
                // 다시하기 버튼 클릭
                hide();
                SceneManager.getInstance().changeScene(SceneManager.SceneType.S1_1);
                return true;
            } else if (mainMenuButton.contains(x, y)) {
                // 메인메뉴 버튼 클릭
                hide();
                SceneManager.getInstance().changeScene(SceneManager.SceneType.MAIN);
                return true;
            }
        }
        return false;
    }
} 