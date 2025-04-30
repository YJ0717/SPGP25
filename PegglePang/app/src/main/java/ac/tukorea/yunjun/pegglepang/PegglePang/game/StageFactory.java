package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class StageFactory {
    public static Scene createStage(Context context, int stageNumber, int subStageNumber) {
        switch (stageNumber) {
            case 1:
                return createStage1(context, subStageNumber);
            // 스테이지 선택창을 효율적으로 관리
            default:
                return null;
        }
    }

    private static Scene createStage1(Context context, int subStageNumber) {
        switch (subStageNumber) {
            case 1:
                return new S1_1(context);
            // case 2:
            //     return new Stage1_2(context);
            // case 3:
            //     return new Stage1_3(context);
            default:
                return null;
        }
    }
} 