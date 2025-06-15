// 게임의 월드 선택 화면을 관리하는 씬
// 각 월드로의 진입점 역할을 하며, 메인 화면으로의 복귀도 담당

package ac.tukorea.yunjun.pegglepang.PegglePang.game.world;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.app.Activity;
import android.widget.ImageView;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage1_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage2_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.Stage3_Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.stage.StageManager;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.MainActivity;

public class worldSelectScene extends Scene {
    private Paint paint; 
    private Context context;
    private int screenHeight;
    private int worldNumber;

    public worldSelectScene(Context context) {
        this(context, 1);
    }

    public worldSelectScene(Context context, int worldNumber) {
        super();
        this.context = context;
        this.worldNumber = worldNumber;
        paint = new Paint();
        paint.setColor(Color.WHITE); 

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
        // 레이아웃 사용으로 draw()에서는 이미지 그리지 않음
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
            
            // 월드 해금 상태에 따라 배경 이미지 설정
            ImageView backgroundImageView = activity.findViewById(R.id.background_image);
            if (backgroundImageView != null) {
                boolean world3Unlocked = StageManager.getInstance().isWorldUnlocked(3);
                boolean world2Unlocked = StageManager.getInstance().isWorldUnlocked(2);
                
                System.out.println("=== 월드 해금 상태 ===");
                System.out.println("월드2 해금: " + world2Unlocked);
                System.out.println("월드3 해금: " + world3Unlocked);
                
                if (world3Unlocked) {
                    System.out.println("월드3 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world3);
                } else if (world2Unlocked) {
                    System.out.println("월드2 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world2);
                } else {
                    System.out.println("월드1 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world1);
                }
            }
            
            // Back 버튼 숨기기
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setVisibility(View.GONE);
            }
            
            setupStage1Button();
            setupWorld2Button();
            setupWorld3Button();
            setupBackButton();
        }
    }

    public void setupWorld2Button() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button world2Button = activity.findViewById(R.id.world2_button);
            
            if (world2Button != null) {
                if (StageManager.getInstance().isWorldUnlocked(2)) {
                    world2Button.setVisibility(View.VISIBLE);
                    world2Button.setOnClickListener(v -> {
                        Stage2_Scene stage2Scene = new Stage2_Scene(context);
                        activity.getGameView().changeScene(stage2Scene);
                    });
                } else {
                    world2Button.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setupWorld3Button() {
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            Button world3Button = activity.findViewById(R.id.world3_button);
            
            if (world3Button != null) {
                if (StageManager.getInstance().isWorldUnlocked(3)) {
                    world3Button.setVisibility(View.VISIBLE);
                    world3Button.setOnClickListener(v -> {
                        activity.setContentView(R.layout.world3_stage_select);
                        Stage3_Scene stage3Scene = new Stage3_Scene(context);
                        activity.getGameView().changeScene(stage3Scene);
                    });
                } else {
                    world3Button.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.world_select);
            
            // 월드 해금 상태에 따라 배경 이미지 설정
            ImageView backgroundImageView = activity.findViewById(R.id.background_image);
            if (backgroundImageView != null) {
                boolean world3Unlocked = StageManager.getInstance().isWorldUnlocked(3);
                boolean world2Unlocked = StageManager.getInstance().isWorldUnlocked(2);
                
                System.out.println("=== 월드 해금 상태 ===");
                System.out.println("월드2 해금: " + world2Unlocked);
                System.out.println("월드3 해금: " + world3Unlocked);
                
                if (world3Unlocked) {
                    System.out.println("월드3 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world3);
                } else if (world2Unlocked) {
                    System.out.println("월드2 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world2);
                } else {
                    System.out.println("월드1 배경 이미지 설정");
                    backgroundImageView.setImageResource(R.mipmap.world1);
                }
            }
            
            // Back 버튼 숨기기
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setVisibility(View.GONE);
            }
            
            setupStage1Button();
            setupWorld2Button();
            setupWorld3Button();
            setupBackButton();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
    }
}