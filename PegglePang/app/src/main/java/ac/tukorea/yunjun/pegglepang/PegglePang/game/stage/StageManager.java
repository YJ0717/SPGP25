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

    // 퍼즐 로그라이크 관련
    private static boolean bombBlocksEnabled = false;

    // 퍼즐 로그라이크 효과 관리
    private static boolean puzzleRockPreventionEnabled = false;
    private static boolean puzzleSwordDoubleEnabled = false;
    private static boolean puzzleMagicDoubleEnabled = false;

    // 전투 로그라이크 효과 관리
    private static boolean battleCriticalEnabled = false;
    private static boolean battleDamageReductionEnabled = false;
    private static boolean battleStunEnabled = false;
    
    // 선택된 로그라이크 효과 저장 (3중 1택 보장)
    private static int selectedStage1RoguelikeChoice = -1; // -1: 미선택, 0: 폭탄, 1: 시간연장, 2: 주머니
    private static int selectedStage2PuzzleRoguelikeChoice = -1; // -1: 미선택, 0: 돌블럭방지, 1: 칼블럭2배, 2: 마법블럭2배

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
        stageDataMap.put("3-3", new StageData(false)); // 월드 3 스테이지 3
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
    
    // 선택된 로그라이크 효과 저장 메서드들
    public static void setStage1RoguelikeChoice(int choice) {
        selectedStage1RoguelikeChoice = choice;
        // 기존 효과들 초기화
        bombBlocksEnabled = false;
        // 선택된 효과만 활성화
        if (choice == 0) {
            bombBlocksEnabled = true;
        }
        // choice 1(시간연장)과 2(주머니)는 PlayerStats에서 직접 관리
    }
    
    public static void setStage2PuzzleRoguelikeChoice(int choice) {
        selectedStage2PuzzleRoguelikeChoice = choice;
        // 기존 효과들 초기화
        puzzleRockPreventionEnabled = false;
        puzzleSwordDoubleEnabled = false;
        puzzleMagicDoubleEnabled = false;
        // 선택된 효과만 활성화
        if (choice == 0) {
            puzzleRockPreventionEnabled = true;
        } else if (choice == 1) {
            puzzleSwordDoubleEnabled = true;
        } else if (choice == 2) {
            puzzleMagicDoubleEnabled = true;
        }
    }
    
    public static int getStage1RoguelikeChoice() {
        return selectedStage1RoguelikeChoice;
    }
    
    public static int getStage2PuzzleRoguelikeChoice() {
        return selectedStage2PuzzleRoguelikeChoice;
    }
    
    // 로그라이크 효과 지속성 보장 메서드
    public static void ensureRoguelikeEffectsPersistence() {
        // 스테이지 1-3이 클리어되었고 선택이 저장되어 있다면 해당 효과만 활성화
        if (getInstance().isStageCleared(1, 3) && selectedStage1RoguelikeChoice >= 0) {
            setStage1RoguelikeChoice(selectedStage1RoguelikeChoice);
        }
        
        // 스테이지 2-3이 클리어되었고 선택이 저장되어 있다면 해당 효과만 활성화
        if (getInstance().isStageCleared(2, 3) && selectedStage2PuzzleRoguelikeChoice >= 0) {
            setStage2PuzzleRoguelikeChoice(selectedStage2PuzzleRoguelikeChoice);
        }
    }

    // 퍼즐 로그라이크 효과 관리 메서드들
    public static void enablePuzzleRockPrevention() {
        puzzleRockPreventionEnabled = true;
    }
    
    public static void enablePuzzleSwordDouble() {
        puzzleSwordDoubleEnabled = true;
    }
    
    public static void enablePuzzleMagicDouble() {
        puzzleMagicDoubleEnabled = true;
    }
    
    public static boolean isPuzzleRockPreventionEnabled() {
        return puzzleRockPreventionEnabled;
    }
    
    public static boolean isPuzzleSwordDoubleEnabled() {
        return puzzleSwordDoubleEnabled;
    }
    
    public static boolean isPuzzleMagicDoubleEnabled() {
        return puzzleMagicDoubleEnabled;
    }
    
    // 스테이지 3 진입 시 퍼즐 로그라이크 효과를 PlayerStats에 적용
    public static void applyPuzzleRoguelikeEffects(PlayerStats playerStats) {
        if (puzzleRockPreventionEnabled) {
            playerStats.applyRockBlockPrevention();
        }
        if (puzzleSwordDoubleEnabled) {
            playerStats.applySwordBlockDouble();
        }
        if (puzzleMagicDoubleEnabled) {
            playerStats.applyMagicBlockDouble();
        }
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
        if (battleCriticalEnabled) {
            playerStats.applyCriticalChance();
        }
        if (battleDamageReductionEnabled) {
            playerStats.applyDamageReduction();
        }
        if (battleStunEnabled) {
            playerStats.applyStunChance();
        }
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