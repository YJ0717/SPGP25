package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.world.worldSelectScene;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class Stage3_Scene extends Scene {
    private Context context;

    public Stage3_Scene(Context context) {
        this.context = context;
        setupStageButtons();
        setupBackButton();
    }

    private void setupStageButtons() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            
            // 3-1 버튼 설정
            Button stage3_1Button = activity.findViewById(R.id.stage3_1_button);
            if (stage3_1Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(3, 1);
                stage3_1Button.setEnabled(isUnlocked);
                stage3_1Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    S3_1 stage = new S3_1(context);
                    activity.getGameView().changeScene(stage);
                });
            }
            
            // 3-2 버튼 설정
            Button stage3_2Button = activity.findViewById(R.id.stage3_2_button);
            if (stage3_2Button != null) {
                boolean isUnlocked = StageManager.getInstance().isStageUnlocked(3, 2);
                stage3_2Button.setEnabled(isUnlocked);
                stage3_2Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    S3_2 stage = new S3_2(context);
                    activity.getGameView().changeScene(stage);
                });
            }
        }
    }

    private void setupBackButton() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setOnClickListener(v -> {
                    activity.setContentView(R.layout.world_select);
                    worldSelectScene worldScene = new worldSelectScene(context);
                    activity.getGameView().changeScene(worldScene);
                });
            }
        }
    }

    @Override
    public void update() {
        // 업데이트 로직 (필요시 추가)
    }

    @Override
    public void draw(Canvas canvas) {
        // 그리기 로직 (필요시 추가)
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
            activity.setContentView(R.layout.world3_stage_select);
            setupStageButtons();
            setupBackButton();
        }
    }
} 