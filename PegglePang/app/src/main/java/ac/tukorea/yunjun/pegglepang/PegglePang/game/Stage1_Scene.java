package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.app.Activity;
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
        TextView backText = ((Activity)context).findViewById(R.id.back_text);
        backText.setOnClickListener(v -> {
            if (context instanceof PegglePangActivity) {
                PegglePangActivity gameActivity = (PegglePangActivity) context;
                gameActivity.setContentView(R.layout.world_select);
                gameActivity.getGameView().popScene();
            }
        });
    }

    private void setupStageButtons() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            
            // 스테이지 1-1 버튼
            Button stage1_1Button = activity.findViewById(R.id.stage1_1_button);
            if (stage1_1Button != null) {
                stage1_1Button.setEnabled(StageManager.getInstance().isStageUnlocked(1, 1));
                stage1_1Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.game_scene);
                    Scene stage = StageFactory.createStage(context, 1, 1);
                    activity.getGameView().pushScene(stage);
                });
            }

            // 스테이지 1-2 버튼 
            // Button stage1_2Button = activity.findViewById(R.id.stage1_2_button);
            // if (stage1_2Button != null) {
            //     stage1_2Button.setEnabled(StageManager.getInstance().isStageUnlocked(1, 2));
            //     stage1_2Button.setOnClickListener(v -> {
            //         activity.setContentView(R.layout.game_scene);
            //         Scene stage = StageFactory.createStage(context, 1, 2);
            //         activity.getGameView().pushScene(stage);
            //     });
            // }

            // 스테이지 1-3 버튼 
            // Button stage1_3Button = activity.findViewById(R.id.stage1_3_button);
            // if (stage1_3Button != null) {
            //     stage1_3Button.setEnabled(StageManager.getInstance().isStageUnlocked(1, 3));
            //     stage1_3Button.setOnClickListener(v -> {
            //         activity.setContentView(R.layout.game_scene);
            //         Scene stage = StageFactory.createStage(context, 1, 3);
            //         activity.getGameView().pushScene(stage);
            //     });
            // }
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
    }
}
