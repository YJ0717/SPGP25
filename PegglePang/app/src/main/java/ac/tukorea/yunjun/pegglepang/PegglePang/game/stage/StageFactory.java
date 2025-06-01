// 스테이지 생성을 담당하는 팩토리 클래스. 스테이지 번호에 따라 적절한 스테이지 씬 객체를 생성하고 반환

package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.content.Context;

import ac.tukorea.yunjun.pegglepang.framework.scene.Scene;

public class StageFactory {
    public static Scene createStage(Context context, int stageNumber, int subStageNumber) {
        switch (stageNumber) {
            case 1:
                return createStage1(context, subStageNumber);
            case 2:
                return createStage2(context, subStageNumber);
            default:
                return null;
        }
    }

    private static Scene createStage1(Context context, int subStageNumber) {
        switch (subStageNumber) {
            case 1:
                return new S1_1(context);
            case 2:
                return new S1_2(context);
            case 3:
                return new S1_3(context);
            default:
                return null;
        }
    }

    private static Scene createStage2(Context context, int subStageNumber) {
        switch (subStageNumber) {
            case 0:
                return new Stage2_Scene(context);
            case 1:
                return new S2_1(context);
            default:
                return null;
        }
    }
} 