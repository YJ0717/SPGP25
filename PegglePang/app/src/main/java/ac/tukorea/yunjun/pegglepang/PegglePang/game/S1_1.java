// S1_1.java
// 스테이지 1-1의 구현
// 게임 화면을 3개 영역으로 나누어 표시:
// - 상단 30%: 전투 공간
// - 중단 15%: 플레이어 정보
// - 하단 55%: 퍼즐 영역 (6x6 매치-3 퍼즐)
// [0][0] 부터 [5][5]까지 블록 위치 설정

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.content.Context;
import android.view.MotionEvent;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.Stage1Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.BattleSystem;

public class S1_1 extends BaseStageScene {

    private static final int GRID_SIZE = 6;              
    private static final float SWIPE_THRESHOLD = 20.0f;   
    private static final float TURN_DELAY = 1.0f;
    
    private Paint linePaint;      
    private Bitmap gridBitmap;    
    private RectF gridRect;       
    private BlockGrid blockGrid;
    private PlayerStats playerStats;
    private boolean isPuzzleFrozen;
    private boolean isBattlePhase = false;
    private boolean isPlayerTurn = true;
    private boolean isWaitingForAnim = false;
    private int lastSword, lastMagic, lastHeal;
    private float turnTimer = 0f;
    private float battleDelayTimer = 0f;  
    private static final float BATTLE_DELAY = 2.0f;  
    
    private float blockSize;      
    private float puzzleLeft;     
    private float puzzleTop;      
    
    private Block selectedBlock;  
    private int selectedRow = -1; 
    private int selectedCol = -1; 
    private float touchStartX;    
    private float touchStartY;    

    private Player player; 
    private Stage1Monster slime;
    private Stage1Monster skeleton;
    private Bitmap battleBg;
    private Bitmap stateBg;
    private BattleSystem battleSystem;

    //이펙트/피격 상태 관리
    private boolean isMagicEffectPhase = false;
    private boolean isMonsterBlinkPhase = false;
    private boolean isSwordEffectPhase = false;
    private int pendingMagicDamage = 0;
    private int pendingHeal = 0;
    private int pendingSwordDamage = 0;

    private boolean isMonsterAttackPhase = false;
    private boolean isPlayerBlinkPhase = false;
    private float playerBlinkTimer = 0f;
    private int playerBlinkCount = 0;
    private static final int MAX_PLAYER_BLINK_COUNT = 4;

    private boolean isGameOver = false;
    private float monsterAttackTimer = 0f;
    private float playerAttackTimer = 0f;
    private float magicEffectTimer = 0f;
    private float healEffectTimer = 0f;
    private boolean isMonsterAttacking = false;
    private boolean isPlayerBlinking = false;
    private boolean isMagicEffectPlaying = false;
    private boolean isHealEffectPlaying = false;
    private float playerAttackValue = 0f;
    private float playerMagicValue = 0f;
    private float playerHealValue = 0f;

    private static final float MONSTER_ATTACK_DURATION = 1.0f;
    private static final float PLAYER_BLINK_DURATION = 0.1f;
    private static final float MONSTER_BLINK_DURATION = 0.1f;
    private static final float PLAYER_ATTACK_DURATION = 0.5f;
    private static final float MAGIC_EFFECT_DURATION = 0.8f;
    private static final float HEAL_EFFECT_DURATION = 1.0f;
    private static final float SWORD_EFFECT_DURATION = 0.6f;
    private boolean isPlayerAttacking = false;
    private boolean isMonsterTurn = false;
    private boolean isMonsterBlinking = false;
    private boolean isSwordEffectPlaying = false;
    private Stage1Monster monster;

    private float monsterBlinkTimer = 0f;
    private float swordEffectTimer = 0f;

    public S1_1(Context context) {
        super(context, 1, 1);
        
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);

        gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.grid);
        gridRect = new RectF();
        
        float playerInfoStart = Metrics.height * 0.30f;
        float battleHeight = playerInfoStart;
        float playerDrawHeight = battleHeight * 0.9f;
        float playerDrawWidth = playerDrawHeight * 0.5f;
        float playerLeft = Metrics.width * 0.05f;
        float playerTop = (battleHeight - playerDrawHeight) / 2;
        player = new Player(context, playerLeft, playerTop, playerDrawWidth, playerDrawHeight);
        playerStats = player.getStats();
        blockGrid = new BlockGrid(context);
        blockGrid.setPlayerStats(playerStats);
        isPuzzleFrozen = false;
        float slimeDrawHeight = battleHeight * 0.10f;
        float slimeDrawWidth = slimeDrawHeight * (203f / 46f);
        float slimeLeft = Metrics.width - slimeDrawWidth - (Metrics.width * 0.05f);
        float slimeTop = battleHeight - slimeDrawHeight - (battleHeight * 0.05f);
        slime = new Stage1Monster(context, R.mipmap.slime_idle, 3, slimeLeft, slimeTop, slimeDrawWidth, slimeDrawHeight);
        // skeleton 몬스터 추가 (슬라임 왼쪽에)
        float skeletonDrawHeight = battleHeight * 0.5f;
        float skeletonDrawWidth = 80f;
        float skeletonLeft = slimeLeft - skeletonDrawWidth - (Metrics.width * 0.03f);
        float skeletonTop = battleHeight - skeletonDrawHeight - (battleHeight * 0.05f);
        skeleton = new Stage1Monster(context, R.mipmap.skeleton_idle, 3, skeletonLeft, skeletonTop, skeletonDrawWidth, skeletonDrawHeight);
        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        battleSystem = new BattleSystem(playerStats, new BattleSystem.BattleCallback() {
            @Override
            public void onBattleEnd(boolean isVictory) {
                if (isVictory) {
                    StageManager.getInstance().unlockStage(1, 2);
                }
                isBattlePhase = false;
                isPuzzleFrozen = false;
                playerStats.reset();
                blockGrid.reset();
            }

            @Override
            public void onTurnEnd() {
                isBattlePhase = false;
                isPuzzleFrozen = false;
                blockGrid.reset();
            }
        });

        battleSystem.addMonster(slime);
        battleSystem.addMonster(skeleton);

        // magic effect 위치 설정
        float effectW = 713f / 3f; // 한 프레임 기준, 실제 크기는 아래에서 조정
        float effectH = 350f;
        float effectScale = (slime.getCurrentHp() > 0 ? slime : skeleton).getAttackPower() > 0 ? 0.7f : 0.5f;
        float effectWidth = Metrics.width * 0.4f;
        float effectHeight = effectWidth * (350f / (713f / 3f));
        float effectX = (playerLeft + playerDrawWidth + slimeLeft) / 2 - effectWidth / 2;
        float effectY = playerTop + playerDrawHeight * 0.2f;
        player.setMagicEffectPosition(effectX, effectY, effectWidth, effectHeight);
        // sword effect 위치 설정 (플레이어와 몬스터 중간)
        float swordEffectWidth = Metrics.width * 0.35f;
        float swordEffectHeight = swordEffectWidth * (139f / 190f); // 190=952/5
        float swordEffectX = (playerLeft + playerDrawWidth + slimeLeft) / 2 - swordEffectWidth / 2;
        float swordEffectY = playerTop + playerDrawHeight * 0.3f;
        player.setSwordEffectPosition(swordEffectX, swordEffectY, swordEffectWidth, swordEffectHeight);
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    @Override
    public void update() {
        super.update();
        blockGrid.update(0.016f);
        player.update(0.016f);
        slime.update(0.016f);
        skeleton.update(0.016f);

        // 퍼즐 시간이 끝났을 때 딜레이 시작
        if (!isBattlePhase && playerStats.isGameOver() && !isPuzzleFrozen && !blockGrid.isAnyBlockAnimating() && !blockGrid.isAnyBlockFalling()) {
            isPuzzleFrozen = true;
            battleDelayTimer = 0f;  // 타이머 초기화
        }

        if (isPuzzleFrozen && !isBattlePhase) {
            battleDelayTimer += 0.016f;
        }

        if (isPuzzleFrozen && !isBattlePhase && battleDelayTimer >= BATTLE_DELAY) {
            isBattlePhase = true;
            isPlayerTurn = true;
            isWaitingForAnim = false;
            lastSword = playerStats.getPhysicalAttack();
            lastMagic = playerStats.getMagicAttack();
            lastHeal = playerStats.getHealing();
        }

        // 이펙트/몬스터 피격 중이면 대기
        if (isMagicEffectPhase || isMonsterBlinkPhase || isSwordEffectPhase) {
            // 마법 이펙트가 끝나면 몬스터 피격 시작
            if (isMagicEffectPhase && !player.isMagicEffectPlaying()) {
                isMagicEffectPhase = false;
                isMonsterBlinkPhase = true;
                if (pendingMagicDamage > 0) {
                    if (slime.isAlive()) slime.startBlinking(pendingMagicDamage);
                    if (skeleton.isAlive()) skeleton.startBlinking(pendingMagicDamage);
                }
            }
            // 물리 이펙트가 끝나면 몬스터 피격 시작
            if (isSwordEffectPhase && !player.isSwordEffectPlaying()) {
                isSwordEffectPhase = false;
                isMonsterBlinkPhase = true;
                if (pendingSwordDamage > 0) {
                    if (slime.isAlive()) slime.startBlinking(pendingSwordDamage);
                    if (skeleton.isAlive()) skeleton.startBlinking(pendingSwordDamage);
                }
            }
            // 몬스터 피격이 끝나면 실제 데미지 적용 및 턴 종료
            if (isMonsterBlinkPhase && !slime.isBlinking() && !skeleton.isBlinking()) {
                isMonsterBlinkPhase = false;
                playerStats.heal(pendingHeal);
                isPlayerTurn = false;
                isWaitingForAnim = false;
            }
            return;
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (lastSword >= lastMagic && lastSword >= lastHeal) {
                        player.playSwordAttack(() -> {
                            // 물리 이펙트 시작
                            isSwordEffectPhase = true;
                            pendingSwordDamage = lastSword + lastMagic;
                            pendingHeal = lastHeal;
                            player.playSwordEffect(() -> {
                                if (slime.isAlive()) slime.startBlinking(pendingSwordDamage);
                                if (skeleton.isAlive()) skeleton.startBlinking(pendingSwordDamage);
                                isMonsterBlinkPhase = true;
                                pendingSwordDamage = 0;
                                pendingHeal = lastHeal;
                            });
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        player.playMagicAttack(() -> {
                            // 마법 이펙트 시작
                            isMagicEffectPhase = true;
                            pendingMagicDamage = lastSword + lastMagic;
                            pendingHeal = lastHeal;
                            player.playMagicEffect(() -> {
                                if (slime.isAlive()) slime.startBlinking(pendingMagicDamage);
                                if (skeleton.isAlive()) skeleton.startBlinking(pendingMagicDamage);
                                isMonsterBlinkPhase = true;
                                pendingMagicDamage = 0;
                                pendingHeal = lastHeal;
                            });
                        });
                    } else {
                        player.playHeal(() -> {
                            int totalDamage = lastSword + lastMagic;
                            if (slime.isAlive()) slime.startBlinking(totalDamage);
                            if (skeleton.isAlive()) skeleton.startBlinking(totalDamage);
                            playerStats.heal(lastHeal);
                            isMonsterBlinkPhase = true;
                            pendingMagicDamage = 0;
                            pendingHeal = lastHeal;
                        });
                    }
                }
            } else {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    isMonsterAttackPhase = true;
                    
                    // 스켈레톤이 살아있으면 스켈레톤이 먼저 공격
                    if (skeleton.isAlive()) {
                        skeleton.startAttack(() -> {
                            // 스켈레톤 공격이 끝나면 플레이어가 깜빡거리면서 데미지를 받음
                            player.takeDamage(skeleton.getAttackPower());
                            if (!player.isAlive()) {
                                player.die();
                                isGameOver = true;
                                GameOverScene.getInstance().show();
                                return;
                            }
                            isPlayerBlinkPhase = true;
                            playerBlinkTimer = 0f;
                            playerBlinkCount = 0;
                            
                            // 슬라임이 살아있으면 슬라임이 공격
                            if (slime.isAlive()) {
                                slime.startAttack(() -> {
                                    player.takeDamage(slime.getAttackPower());
                                    if (!player.isAlive()) {
                                        player.die();
                                        isGameOver = true;
                                        GameOverScene.getInstance().show();
                                        return;
                                    }
                                    isPlayerBlinkPhase = true;
                                    playerBlinkTimer = 0f;
                                    playerBlinkCount = 0;
                                    
                                    // 모든 몬스터의 공격이 끝나면 턴 종료
                                    isBattlePhase = false;
                                    isPuzzleFrozen = false;
                                    playerStats.reset();
                                    isWaitingForAnim = false;
                                    
                                    if (!slime.isAlive() && !skeleton.isAlive()) {
                                        StageManager.getInstance().unlockStage(1, 2);
                                    }
                                });
                            } else {
                                // 슬라임이 죽어있으면 바로 턴 종료
                                isBattlePhase = false;
                                isPuzzleFrozen = false;
                                playerStats.reset();
                                isWaitingForAnim = false;
                                
                                if (!skeleton.isAlive()) {
                                    StageManager.getInstance().unlockStage(1, 2);
                                }
                            }
                        });
                    } else if (slime.isAlive()) {
                        // 스켈레톤이 죽어있고 슬라임만 살아있으면 슬라임이 공격
                        slime.startAttack(() -> {
                            player.takeDamage(slime.getAttackPower());
                            if (!player.isAlive()) {
                                player.die();
                                isGameOver = true;
                                GameOverScene.getInstance().show();
                                return;
                            }
                            isPlayerBlinkPhase = true;
                            playerBlinkTimer = 0f;
                            playerBlinkCount = 0;
                            
                            // 슬라임 공격이 끝나면 턴 종료
                            isBattlePhase = false;
                            isPuzzleFrozen = false;
                            playerStats.reset();
                            isWaitingForAnim = false;
                            
                            if (!slime.isAlive()) {
                                StageManager.getInstance().unlockStage(1, 2);
                            }
                        });
                    }
                }
            }
        }

        // 플레이어 깜빡임 처리
        if (isPlayerBlinkPhase) {
            playerBlinkTimer += 0.016f;
            if (playerBlinkTimer >= 0.1f) {
                playerBlinkTimer -= 0.1f;
                playerBlinkCount++;
                if (playerBlinkCount >= MAX_PLAYER_BLINK_COUNT) {
                    isPlayerBlinkPhase = false;
                }
            }
        }

        if (isGameOver) {
            return;
        }

        if (isPuzzleFrozen) {
            if (battleDelayTimer < BATTLE_DELAY) {
                battleDelayTimer += 0.016f;
                return;
            }
            isBattlePhase = true;
            isPuzzleFrozen = false;
            isPlayerTurn = true;
            playerAttackValue = player.getPhysicalAttack();
            playerMagicValue = player.getMagicAttack();
            playerHealValue = player.getHealing();
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (playerAttackValue > playerMagicValue && playerAttackValue > playerHealValue) {
                    if (!isPlayerAttacking) {
                        isPlayerAttacking = true;
                        playerAttackTimer = 0f;
                        player.setAnimationType(PlayerAnimation.Type.SWORD);
                    }
                    handlePlayerAttack();
                } else if (playerMagicValue > playerAttackValue && playerMagicValue > playerHealValue) {
                    if (!isMagicEffectPlaying) {
                        isMagicEffectPlaying = true;
                        magicEffectTimer = 0f;
                        player.setAnimationType(PlayerAnimation.Type.MAGIC);
                    }
                    handleMagicEffect();
                } else if (playerHealValue > playerAttackValue && playerHealValue > playerMagicValue) {
                    if (!isHealEffectPlaying) {
                        isHealEffectPlaying = true;
                        healEffectTimer = 0f;
                        player.setAnimationType(PlayerAnimation.Type.HEAL);
                    }
                    handleHealEffect();
                }
            } else if (isMonsterTurn) {
                if (!isMonsterAttacking) {
                    isMonsterAttacking = true;
                    monsterAttackTimer = 0f;
                    monster.setAnimationType(MonsterAnimation.Type.ATTACK);
                }
                handleMonsterAttack();
            }
        }

        if (isPlayerBlinking) {
            handlePlayerBlink();
        }

        if (isMonsterBlinking) {
            handleMonsterBlink();
        }

        if (isSwordEffectPlaying) {
            handleSwordEffect();
        }

        player.update(0.016f);
        slime.update(0.016f);
        skeleton.update(0.016f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 게임 오버 상태일 때는 GameOverScene의 터치 이벤트 처리
        if (isGameOver) {
            return GameOverScene.getInstance().onTouchEvent(event);
        }

        // 시간이 끝났거나 블록이 애니메이션 중이면 터치 이벤트 무시
        if (playerStats.isGameOver() || blockGrid.isAnyBlockAnimating() || isPuzzleFrozen) {
            return true;
        }

        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];
        
        int col = (int)((x - puzzleLeft) / blockSize);
        int row = (int)((y - puzzleTop) / blockSize);
        
        if (row >= 0 && row < BlockGrid.getGridSize() && col >= 0 && col < BlockGrid.getGridSize()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:  
                    touchStartX = x;
                    touchStartY = y;
                    selectedBlock = blockGrid.getBlock(row, col);
                    selectedRow = row;
                    selectedCol = col;
                    break;
                    
                case MotionEvent.ACTION_UP:    
                    if (selectedBlock != null) {
                        float dx = x - touchStartX;
                        float dy = y - touchStartY;
                        
                        if (Math.abs(dx) > SWIPE_THRESHOLD || Math.abs(dy) > SWIPE_THRESHOLD) {
                            int newRow = selectedRow;
                            int newCol = selectedCol;
                            if (Math.abs(dx) > Math.abs(dy)) {
                                newCol += (dx > 0) ? 1 : -1;  
                            } else {
                                newRow += (dy > 0) ? 1 : -1;  
                            }
                            
                            if (newRow >= 0 && newRow < BlockGrid.getGridSize() && 
                                newCol >= 0 && newCol < BlockGrid.getGridSize()) {
                                blockGrid.swapBlocks(selectedRow, selectedCol, newRow, newCol);
                            }
                        }
                        selectedBlock = null;
                        selectedRow = selectedCol = -1;
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        float puzzleStart = Metrics.height * 0.45f;
        float playerInfoStart = Metrics.height * 0.30f;
        // 전투영역 배경 그리기 (상단 30%)
        if (battleBg != null) {
            RectF bgRect = new RectF(0, 0, Metrics.width, playerInfoStart);
            canvas.drawBitmap(battleBg, null, bgRect, null);
        }
        // 플레이어 정보 영역 배경 그리기
        if (stateBg != null) {
            RectF stateRect = new RectF(0, playerInfoStart, Metrics.width, puzzleStart);
            canvas.drawBitmap(stateBg, null, stateRect, null);
        }
        // 퍼즐 영역(하단 55%) 배경도 state.png로 채우기
        if (stateBg != null) {
            RectF puzzleRect = new RectF(0, puzzleStart, Metrics.width, Metrics.height);
            canvas.drawBitmap(stateBg, null, puzzleRect, null);
        }
        canvas.drawLine(0, puzzleStart, Metrics.width, puzzleStart, linePaint);
        canvas.drawLine(0, playerInfoStart, Metrics.width, playerInfoStart, linePaint);

        float puzzleAreaHeight = Metrics.height - puzzleStart;
        float puzzleSize = Math.min(Metrics.width, puzzleAreaHeight);
        
        puzzleLeft = (Metrics.width - puzzleSize) / 2;
        puzzleTop = puzzleStart;
        
        gridRect.set(puzzleLeft, puzzleTop, puzzleLeft + puzzleSize, puzzleTop + puzzleSize);
        canvas.drawBitmap(gridBitmap, null, gridRect, null);

        blockSize = puzzleSize / BlockGrid.getGridSize();
        blockGrid.setGridMetrics(puzzleLeft, puzzleTop, puzzleSize);
        
        canvas.save();
        canvas.clipRect(puzzleLeft, puzzleTop, puzzleLeft + puzzleSize, puzzleTop + puzzleSize);
        blockGrid.draw(canvas);
        canvas.restore();

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("전투 공간", Metrics.width/2, playerInfoStart/2, textPaint);
        playerStats.draw(canvas, Metrics.width, playerInfoStart, puzzleStart);

        if (isPlayerBlinkPhase && playerBlinkCount % 2 == 0) {
            player.setAlpha(80);
        } else {
            player.setAlpha(255);
        }
        player.draw(canvas);
        
        slime.draw(canvas);
        skeleton.draw(canvas);

        // 게임 오버 화면 표시
        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }
    }

    public void startNewPuzzlePhase() {
        isPuzzleFrozen = false;
        playerStats.reset();
        blockGrid.reset();
    }

    private void handleMonsterAttack() {
        if (monsterAttackTimer >= MONSTER_ATTACK_DURATION) {
            monsterAttackTimer = 0f;
            isMonsterAttacking = false;
            isPlayerBlinking = true;
            playerBlinkTimer = 0f;
            player.takeDamage((int)monster.getAttackPower());
            
            if (!player.isAlive()) {
                player.die();
                isGameOver = true;
                GameOverScene.getInstance().show();
                return;
            }
            
            isPlayerTurn = true;
            isMonsterTurn = false;
            player.reset();
        }
    }

    private void handlePlayerBlink() {
        if (playerBlinkTimer >= PLAYER_BLINK_DURATION) {
            playerBlinkTimer = 0f;
            isPlayerBlinking = false;
        }
    }

    private void handlePlayerAttack() {
        if (playerAttackTimer >= PLAYER_ATTACK_DURATION) {
            playerAttackTimer = 0f;
            isPlayerAttacking = false;
            isMonsterBlinking = true;
            monsterBlinkTimer = 0f;
            if (slime.isAlive()) slime.takeDamage((int)playerAttackValue);
            if (skeleton.isAlive()) skeleton.takeDamage((int)playerAttackValue);
            isPlayerTurn = false;
            isMonsterTurn = true;
        }
    }

    private void handleMagicEffect() {
        if (magicEffectTimer >= MAGIC_EFFECT_DURATION) {
            magicEffectTimer = 0f;
            isMagicEffectPlaying = false;
            isMonsterBlinking = true;
            monsterBlinkTimer = 0f;
            if (slime.isAlive()) slime.takeDamage((int)playerMagicValue);
            if (skeleton.isAlive()) skeleton.takeDamage((int)playerMagicValue);
            isPlayerTurn = false;
            isMonsterTurn = true;
        }
    }

    private void handleHealEffect() {
        if (healEffectTimer >= HEAL_EFFECT_DURATION) {
            healEffectTimer = 0f;
            isHealEffectPlaying = false;
            playerStats.heal((int)playerHealValue);
            isPlayerTurn = false;
            isMonsterTurn = true;
        }
    }

    private void handleSwordEffect() {
        if (swordEffectTimer >= SWORD_EFFECT_DURATION) {
            swordEffectTimer = 0f;
            isSwordEffectPlaying = false;
            isMonsterBlinking = true;
            monsterBlinkTimer = 0f;
            if (slime.isAlive()) slime.takeDamage((int)playerAttackValue);
            if (skeleton.isAlive()) skeleton.takeDamage((int)playerAttackValue);
            isPlayerTurn = false;
            isMonsterTurn = true;
        }
    }

    private void handleMonsterBlink() {
        if (monsterBlinkTimer >= MONSTER_BLINK_DURATION) {
            monsterBlinkTimer = 0f;
            isMonsterBlinking = false;
        }
    }
}
