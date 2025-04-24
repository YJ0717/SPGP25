package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.util.DisplayMetrics;
import android.widget.Button;

import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class worldSelectScene extends Scene {
    private Paint paint; 
    private Context context;
    private int screenHeight; // 화면 높이를 저장할 변수 <디바이스에 따라 화면 높이를 가져오기 위해

    public worldSelectScene(Context context) {
        paint = new Paint();
        paint.setColor(Color.WHITE); 
        this.context = context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenHeight = metrics.heightPixels; 
    }

    private void setupStage1Button() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button stage1Button = activity.findViewById(R.id.stage1_button);
            
            if (stage1Button != null) {
                stage1Button.setOnClickListener(v -> {
                    activity.setContentView(R.layout.stage1_select);
                    Stage1_Scene stage1Scene = new Stage1_Scene(context);
                    activity.getGameView().pushScene(stage1Scene);
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
        // 터치 이벤트 보다 버튼으로 월드선택이 효율적 -> 스테이지 클리어할때마다 월드 잠금해제 시스템을 넣기위해
        return false;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        setupStage1Button();  // 씬 시작할 때 버튼 설정
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.world_select);
            setupStage1Button();  // 씬 돌아올 때도 동일한 버튼 설정
        }
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}