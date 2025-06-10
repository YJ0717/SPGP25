package ac.tukorea.yunjun.pegglepang.PegglePang.game.battle;

import java.util.ArrayList;
import java.util.List;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage1Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage2Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage3Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;

public class BattleSystem {
    private PlayerStats playerStats;
    private List<Stage1Monster> stage1Monsters;
    private List<Stage2Monster> stage2Monsters;
    private List<Stage3Monster> stage3Monsters;
    private boolean isPlayerTurn = true;
    private float turnTimer = 0f;
    private static final float TURN_DELAY = 1.0f;
    private BattleCallback callback;

    public interface BattleCallback {
        void onBattleEnd(boolean isVictory);
        void onTurnEnd();
    }

    public BattleSystem(PlayerStats playerStats, BattleCallback callback) {
        this.playerStats = playerStats;
        this.stage1Monsters = new ArrayList<>();
        this.stage2Monsters = new ArrayList<>();
        this.stage3Monsters = new ArrayList<>();
        this.callback = callback;
    }

    public void addMonster(Stage1Monster monster) {
        stage1Monsters.add(monster);
    }

    public void addMonster(Stage2Monster monster) {
        stage2Monsters.add(monster);
    }

    public void addMonster(Stage3Monster monster) {
        stage3Monsters.add(monster);
    }

    public void update(float deltaTime) {
        turnTimer += deltaTime;
        if (turnTimer >= TURN_DELAY) {
            turnTimer = 0f;
            processTurn();
        }
    }

    private void processTurn() {
        if (isPlayerTurn) {
            int totalDamage = playerStats.getPhysicalAttack() + playerStats.getMagicAttack();
            
            // Stage1Monster 처리
            for (Stage1Monster monster : stage1Monsters) {
                if (monster.isAlive()) {
                    monster.takeDamage(totalDamage);
                }
            }
            
            // Stage2Monster 처리
            for (Stage2Monster monster : stage2Monsters) {
                if (monster.isAlive()) {
                    monster.takeDamage(totalDamage);
                }
            }
            
            // Stage3Monster 처리
            for (Stage3Monster monster : stage3Monsters) {
                if (monster.isAlive()) {
                    monster.takeDamage(totalDamage);
                }
            }
            
            playerStats.heal(playerStats.getHealing());
            isPlayerTurn = false;
        } else {
            // Stage1Monster 공격
            for (Stage1Monster monster : stage1Monsters) {
                if (monster.isAlive()) {
                    monster.attack(() -> {
                        playerStats.takeDamage(monster.getAttackPower());
                    });
                }
            }
            
            // Stage2Monster 공격
            for (Stage2Monster monster : stage2Monsters) {
                if (monster.isAlive()) {
                    monster.attack(() -> {
                        playerStats.takeDamage(monster.getAttackPower());
                    });
                }
            }
            
            // Stage3Monster 공격
            for (Stage3Monster monster : stage3Monsters) {
                if (monster.isAlive()) {
                    monster.attack(() -> {
                        playerStats.takeDamage(monster.getAttackPower());
                    }, false); // 기본 공격
                }
            }
            
            isPlayerTurn = true;
            callback.onTurnEnd();
            
            if (checkBattleEnd()) {
                callback.onBattleEnd(!playerStats.isAlive());
            }
        }
    }

    private boolean checkBattleEnd() {
        if (!playerStats.isAlive()) {
            return true;
        }
        
        // 모든 몬스터가 죽었는지 확인
        for (Stage1Monster monster : stage1Monsters) {
            if (monster.isAlive()) {
                return false;
            }
        }
        
        for (Stage2Monster monster : stage2Monsters) {
            if (monster.isAlive()) {
                return false;
            }
        }
        
        for (Stage3Monster monster : stage3Monsters) {
            if (monster.isAlive()) {
                return false;
            }
        }
        
        return true;
    }

    public void reset() {
        isPlayerTurn = true;
        turnTimer = 0f;
    }
}
