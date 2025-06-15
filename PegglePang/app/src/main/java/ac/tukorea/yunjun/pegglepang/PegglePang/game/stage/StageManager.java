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
    private boolean world3Unlocked = false;

    // 퍼즐 로그라이크 설정
    private static boolean bombBlocksEnabled = false;

    // 전투 로그라이크 효과들 (스테이지 3 시리즈에서 지속)
    private static boolean battleCriticalEnabled = false;
    private static boolean battleDamageReductionEnabled = false;
    private static boolean battleStunEnabled = false;

    private StageManager() {
        stageDataMap = new HashMap<>();
        stageDataMap.put("1-1", new StageData(true));  
        stageDataMap.put("1-2", new StageData(false)); //추후 클리어 조건을 넣어 앞으로의 스테이지 클리어 선택
        stageDataMap.put("1-3", new StageData(false));
        stageDataMap.put("2-1", new StageData(false)); // 월드 2 스테이지 1
        stageDataMap.put("2-2", new StageData(false)); // 월드 2 스테이지 2
        stageDataMap.put("2-3", new StageData(false)); // 월드 2 스테이지 3
        stageDataMap.put("3-1", new StageData(false)); // 월드 3 스테이지 1
        stageDataMap.put("3-2", new StageData(false)); // 월드 3 스테이지 2
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
        if (worldNumber == 3) world3Unlocked = true;
    }

    public boolean isWorldUnlocked(int worldNumber) {
        return worldNumber == 1 || (worldNumber == 2 && world2Unlocked) || (worldNumber == 3 && world3Unlocked);
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

    // 전투 로그라이크 효과 관리 메서드들
    public static void enableBattleCritical() {
        battleCriticalEnabled = true;
    }
    
    public static void enableBattleDamageReduction() {
        battleDamageReductionEnabled = true;
    }
    
    public static void enableBattleStun() {
        battleStunEnabled = true;
    }
    
    public static boolean isBattleCriticalEnabled() {
        return battleCriticalEnabled;
    }
    
    public static boolean isBattleDamageReductionEnabled() {
        return battleDamageReductionEnabled;
    }
    
    public static boolean isBattleStunEnabled() {
        return battleStunEnabled;
    }
    
    // 스테이지 3 진입 시 전투 로그라이크 효과를 PlayerStats에 적용
    public static void applyBattleRoguelikeEffects(PlayerStats playerStats) {
        System.out.println("=== 전투 로그라이크 효과 적용 체크 ===");
        System.out.println("크리티컬 활성화: " + battleCriticalEnabled);
        System.out.println("데미지 감소 활성화: " + battleDamageReductionEnabled);
        System.out.println("마비 활성화: " + battleStunEnabled);
        
        if (battleCriticalEnabled) {
            playerStats.applyCriticalChance();
            System.out.println("크리티컬 효과 적용됨");
        }
        if (battleDamageReductionEnabled) {
            playerStats.applyDamageReduction();
            System.out.println("데미지 감소 효과 적용됨");
        }
        if (battleStunEnabled) {
            playerStats.applyStunChance();
            System.out.println("마비 효과 적용됨");
        }
        
        System.out.println("PlayerStats 상태 - 크리티컬: " + playerStats.hasCriticalChance() + 
                          ", 데미지감소: " + playerStats.hasDamageReduction() + 
                          ", 마비: " + playerStats.hasStunChance());
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