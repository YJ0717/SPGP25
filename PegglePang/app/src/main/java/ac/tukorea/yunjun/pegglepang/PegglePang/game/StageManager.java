//  스테이지 해금 시스템의 핵심 로직을 담당

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import java.util.HashMap;
import java.util.Map;

public class StageManager {
    private static StageManager instance;
    private Map<String, StageData> stageDataMap;

    private StageManager() {
        stageDataMap = new HashMap<>();
        stageDataMap.put("1-1", new StageData(true));  
        stageDataMap.put("1-2", new StageData(false)); //추후 클리어 조건을 넣어 앞으로의 스테이지 클리어 선택
        stageDataMap.put("1-3", new StageData(false));
    }

    public static StageManager getInstance() {
        if (instance == null) {
            instance = new StageManager();
        }
        return instance;
    }

    public boolean isStageUnlocked(int stage, int subStage) {
        String key = stage + "-" + subStage;
        return stageDataMap.containsKey(key) && stageDataMap.get(key).isUnlocked();
    }

    public void unlockStage(int stage, int subStage) {
        String key = stage + "-" + subStage;
        if (stageDataMap.containsKey(key)) {
            stageDataMap.get(key).setUnlocked(true);
        }
    }

    public int getHighScore(int stage, int subStage) {
        String key = stage + "-" + subStage;
        return stageDataMap.containsKey(key) ? stageDataMap.get(key).getHighScore() : 0;
    }

    public void setHighScore(int stage, int subStage, int score) {
        String key = stage + "-" + subStage;
        if (stageDataMap.containsKey(key)) {
            stageDataMap.get(key).setHighScore(score);
        }
    }

    private static class StageData {
        private boolean unlocked;
        private int highScore;

        public StageData(boolean unlocked) {
            this.unlocked = unlocked;
            this.highScore = 0;
        }

        public boolean isUnlocked() {
            return unlocked;
        }

        public void setUnlocked(boolean unlocked) {
            this.unlocked = unlocked;
        }

        public int getHighScore() {
            return highScore;
        }

        public void setHighScore(int highScore) {
            this.highScore = highScore;
        }
    }
} 