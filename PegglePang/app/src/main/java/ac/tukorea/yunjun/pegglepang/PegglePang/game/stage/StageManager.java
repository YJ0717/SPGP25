//  스테이지 해금 시스템의 핵심 로직을 담당

package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import java.util.HashMap;
import java.util.Map;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;

public class StageManager {
    private static StageManager instance;
    private Map<String, StageData> stageDataMap;
    private static PlayerStats playerStats = new PlayerStats();
    private boolean world2Unlocked = false;

    // 퍼즐 로그라이크 설정
    private static boolean bombBlocksEnabled = false;

    private StageManager() {
        stageDataMap = new HashMap<>();
        stageDataMap.put("1-1", new StageData(true));  
        stageDataMap.put("1-2", new StageData(false)); //추후 클리어 조건을 넣어 앞으로의 스테이지 클리어 선택
        stageDataMap.put("1-3", new StageData(false));
        stageDataMap.put("2-1", new StageData(false)); // 월드 2 스테이지 1
        stageDataMap.put("2-2", new StageData(false)); // 월드 2 스테이지 2
        stageDataMap.put("2-3", new StageData(false)); // 월드 2 스테이지 3
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

    public boolean isStageCleared(int stage, int subStage) {
        String key = stage + "-" + subStage;
        return stageDataMap.containsKey(key) && stageDataMap.get(key).isCleared();
    }

    public void setStageCleared(int stage, int subStage) {
        String key = stage + "-" + subStage;
        if (stageDataMap.containsKey(key)) {
            stageDataMap.get(key).setCleared(true);
        }
    }

    public boolean areMonstersDefeated(int stage, int subStage) {
        String key = stage + "-" + subStage;
        return stageDataMap.containsKey(key) && stageDataMap.get(key).areMonstersDefeated();
    }

    public void setMonstersDefeated(int stage, int subStage, boolean defeated) {
        String key = stage + "-" + subStage;
        if (stageDataMap.containsKey(key)) {
            stageDataMap.get(key).setMonstersDefeated(defeated);
        }
    }

    public static PlayerStats getPlayerStats() {
        return playerStats;
    }

    public void unlockWorld(int worldNumber) {
        if (worldNumber == 2) world2Unlocked = true;
    }

    public boolean isWorldUnlocked(int worldNumber) {
        return worldNumber == 1 || (worldNumber == 2 && world2Unlocked);
    }

    // 퍼즐 로그라이크 관련 메소드들
    public static void enableBombBlocks() {
        bombBlocksEnabled = true;
    }

    public static boolean isBombBlocksEnabled() {
        return bombBlocksEnabled;
    }

    public static void disableBombBlocks() {
        bombBlocksEnabled = false;
    }

    private static class StageData {
        private boolean unlocked;
        private int highScore;
        private boolean cleared;  // 스테이지 클리어 여부
        private boolean monstersDefeated;  // 몬스터들이 모두 처치되었는지 여부

        public StageData(boolean unlocked) {
            this.unlocked = unlocked;
            this.highScore = 0;
            this.cleared = false;
            this.monstersDefeated = false;
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

        public boolean isCleared() {
            return cleared;
        }

        public void setCleared(boolean cleared) {
            this.cleared = cleared;
        }

        public boolean areMonstersDefeated() {
            return monstersDefeated;
        }

        public void setMonstersDefeated(boolean defeated) {
            this.monstersDefeated = defeated;
        }
    }
} 