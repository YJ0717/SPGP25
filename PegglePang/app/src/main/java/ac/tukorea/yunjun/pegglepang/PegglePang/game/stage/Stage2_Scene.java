package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.graphics.Canvas;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.world.worldSelectScene;
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
                    activity.setContentView(R.layout.world_select);
                    worldSelectScene worldScene = new worldSelectScene(context, 2);
                    activity.getGameView().changeScene(worldScene);
                });
            }
        }
    }

    private void setupStageButtons() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button stage2_1Button = activity.findViewById(R.id.stage2_1_button);
            if (stage2_1Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(2, 1);
                stage2_1Button.setEnabled(isUnlocked);
                stage2_1Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    S2_1 stage = new S2_1(context);
                    activity.getGameView().changeScene(stage);
                });
            }
            
            Button stage2_2Button = activity.findViewById(R.id.stage2_2_button);
            if (stage2_2Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(2, 2);
                stage2_2Button.setEnabled(isUnlocked);
                stage2_2Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    S2_2 stage = new S2_2(context);
                    activity.getGameView().changeScene(stage);
                });
            }
            
            Button stage2_3Button = activity.findViewById(R.id.stage2_3_button);
            if (stage2_3Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(2, 3);
                stage2_3Button.setEnabled(isUnlocked);
                stage2_3Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    S2_3 stage = new S2_3(context);
                    activity.getGameView().changeScene(stage);
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
        // onExit에서 Scene 전환을 하면 무한 루프가 발생하므로 제거
    }
} 