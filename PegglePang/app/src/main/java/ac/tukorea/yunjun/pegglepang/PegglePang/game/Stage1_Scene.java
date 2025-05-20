// 스테이지 1의 선택 화면을 관리하는 씬
// 스테이지 1의 서브스테이지(1-1, 1-2, 1-3)로의 진입을 관리
// 스테이지 잠금해제 시스템과 연동됨

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class Stage1_Scene extends Scene {
    private Paint paint; 
    private Context context;

    public Stage1_Scene(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    private void setupBackButton() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setOnClickListener(null);
                backText.setOnClickListener(v -> {
                    if (context instanceof PegglePangActivity) {
                        Scene.pop();
                    }
                });
            }
        }
    }

    private void setupStageButtons() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            
            Button stage1_1Button = activity.findViewById(R.id.stage1_1_button);
            if (stage1_1Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(1, 1);
                stage1_1Button.setEnabled(isUnlocked);
                stage1_1Button.setOnClickListener(v -> {
                    Scene stage = StageFactory.createStage(context, 1, 1);
                    activity.getGameView().pushScene(stage);
                });
            }

            Button stage1_2Button = activity.findViewById(R.id.stage1_2_button);
            if (stage1_2Button != null) {
                stage1_2Button.setEnabled(StageManager.getInstance().isStageUnlocked(1, 2));
                stage1_2Button.setOnClickListener(v -> {
                    Scene stage = StageFactory.createStage(context, 1, 2);
                    activity.getGameView().pushScene(stage);
                });
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.stage1_select);
            setupBackButton();
            setupStageButtons();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.stage1_select);
            setupBackButton();
            setupStageButtons();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.world_select);
            if (Scene.top() instanceof worldSelectScene) {
                ((worldSelectScene)Scene.top()).setupStage1Button();
            }
        }
    }
}
