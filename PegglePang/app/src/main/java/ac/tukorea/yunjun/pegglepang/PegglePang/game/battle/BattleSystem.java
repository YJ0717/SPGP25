package ac.tukorea.yunjun.pegglepang.PegglePang.game.battle;

import java.util.ArrayList;
import java.util.List;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage1Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;

public class BattleSystem {
    private PlayerStats playerStats;
    private List<Stage1Monster> monsters;
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
        this.monsters = new ArrayList<>();
        this.callback = callback;
    }

    public void addMonster(Stage1Monster monster) {
        monsters.add(monster);
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
            for (Stage1Monster monster : monsters) {
                if (monster.isAlive()) {
                    monster.takeDamage(totalDamage);
                }
            }
            playerStats.heal(playerStats.getHealing());
            isPlayerTurn = false;
        } else {
            for (Stage1Monster monster : monsters) {
                if (monster.isAlive()) {
                    playerStats.takeDamage(monster.getAttackPower());
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
        
        for (Stage1Monster monster : monsters) {
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
