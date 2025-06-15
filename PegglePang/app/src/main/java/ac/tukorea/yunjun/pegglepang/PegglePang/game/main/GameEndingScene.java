package ac.tukorea.yunjun.pegglepang.PegglePang.game.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.SceneManager;

public class GameEndingScene {
    private static GameEndingScene instance;
    private boolean isVisible = false;
    private Bitmap endingBitmap;
    private RectF endingRect;
    private Context context;

    private GameEndingScene(Context context) {
        this.context = context;
        this.endingBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ending);
        
        // 화면 전체에 엔딩 이미지 표시
        this.endingRect = new RectF(0, 0, Metrics.width, Metrics.height);
    }

    public static GameEndingScene getInstance(Context context) {
        if (instance == null) {
            instance = new GameEndingScene(context);
        }
        return instance;
    }

    public static GameEndingScene getInstance() {
        return instance;
    }

    public void show() {
        isVisible = true;
    }

    public void hide() {
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void draw(Canvas canvas) {
        if (!isVisible || endingBitmap == null) return;
        
        canvas.drawBitmap(endingBitmap, null, endingRect, null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!isVisible) return false;
        
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 엔딩 화면 클릭 시 월드선택창으로 이동
            hide();
            SceneManager.getInstance().changeScene(SceneManager.SceneType.STAGE_SELECT);
            return true;
        }
        
        return false;
    }
} 