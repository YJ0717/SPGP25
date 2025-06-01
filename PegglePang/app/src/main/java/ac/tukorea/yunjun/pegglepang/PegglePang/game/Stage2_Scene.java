package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class Stage2_Scene extends Scene {
    private Context context;

    public Stage2_Scene(Context context) {
        this.context = context;
    }

    private void setupBackButton() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setOnClickListener(v -> {
                    activity.getGameView().popScene();
                });
            }
        }
    }

    private void setupStageButtons() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button stage2_1Button = activity.findViewById(R.id.stage2_1_button);
            if (stage2_1Button != null) {
                stage2_1Button.setEnabled(StageManager.getInstance().isStageUnlocked(2, 1));
                stage2_1Button.setOnClickListener(v -> {
                    S2_1 stage = new S2_1(context);
                    activity.getGameView().changeScene(stage);
                });
            }
            // 2-2, 2-3 등은 추후 추가
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
            activity.setContentView(R.layout.world2_stage_select);
            setupBackButton();
            setupStageButtons();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.world2_stage_select);
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
            // 월드2가 해금된 상태로 월드 선택 화면 생성
            worldSelectScene worldScene = new worldSelectScene(context, 2);
            activity.getGameView().changeScene(worldScene);
        }
    }
} 