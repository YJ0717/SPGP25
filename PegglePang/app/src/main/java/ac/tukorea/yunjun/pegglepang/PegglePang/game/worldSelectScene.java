// 게임의 월드 선택 화면을 관리하는 씬
// 각 월드로의 진입점 역할을 하며, 메인 화면으로의 복귀도 담당

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.app.Activity;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.MainActivity;

public class worldSelectScene extends Scene {
    private Paint paint; 
    private Context context;
    private int screenHeight;

    public worldSelectScene(Context context) {
        paint = new Paint();
        paint.setColor(Color.WHITE); 
        this.context = context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenHeight = metrics.heightPixels; 
    }

    public void setupStage1Button() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button stage1Button = activity.findViewById(R.id.stage1_button);
            
            if (stage1Button != null) {
                stage1Button.setOnClickListener(null);
                stage1Button.setOnClickListener(v -> {
                    Stage1_Scene stage1Scene = new Stage1_Scene(context);
                    activity.getGameView().pushScene(stage1Scene);
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
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
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
            activity.setContentView(R.layout.world_select);
            setupStage1Button();
            setupBackButton();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.world_select);
            setupStage1Button();
            setupBackButton();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}