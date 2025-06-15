package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.battle.BaseStageScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.ui.DamageText;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.Block;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.BlockGrid;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.GameOverScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage2Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.GameEndingScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.Player;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class S3_3 extends BaseStageScene {
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
    
    private float blockSize;      
    private float puzzleLeft;     
    private float puzzleTop;      
    
    private Block selectedBlock;
    private int selectedRow = -1; 
    private int selectedCol = -1; 
    private float touchStartX;    
    private float touchStartY;    

    private Player player;
    private Stage2Monster monster;
    private Stage2Monster cloneMonster; // 분신 몬스터
    private boolean isCloneActive = false; // 분신 활성화 여부
    private boolean isAttackingClone = false; // 분신 공격 중인지
    private Bitmap battleBg;
    private Bitmap stateBg;

    private boolean isGameOver = false;
    private boolean isStageClearShown = false;
    private boolean isMonsterBlinkPhase = false;
    private DamageText damageText;
    
    // 출혈 효과 관련 (분신용)
    private boolean isBleedingActive = false;
    private float bleedingTimer = 0f;
    private float bleedingTickTimer = 0f;
    private static final float BLEEDING_DURATION = 10f; // 10초
    private static final float BLEEDING_TICK_INTERVAL = 1f; // 1초마다
    private static final int BLEEDING_DAMAGE = 1; // 1 데미지
    private Paint bleedingTextPaint;

    public S3_3(Context context) {
        super(context, 3, 3);
        
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
        playerStats = StageManager.getPlayerStats();
        player = new Player(context, playerLeft, playerTop, playerDrawWidth, playerDrawHeight, playerStats);
        blockGrid = new BlockGrid(context);
        blockGrid.setPlayerStats(playerStats);
        blockGrid.setStageInfo(3, 3);
        
        if (StageManager.isBombBlocksEnabled()) {
            blockGrid.enableBombBlocks();
        }
        isPuzzleFrozen = false;

        // 스테이지가 이미 클리어되지 않았을 때만 몬스터 생성
        if (!StageManager.getInstance().isStageCleared(3, 3)) {
            // 3-3 스테이지용 몬스터 (demon, HP 40, 공격력 20)
            float monsterDrawHeight = battleHeight * 0.7f;
            float monsterDrawWidth = 150f; // 고정 크기
            float monsterLeft = Metrics.width - monsterDrawWidth - (Metrics.width * 0.02f);
            float monsterTop = battleHeight - monsterDrawHeight - (battleHeight * 0.02f);
            monster = new Stage2Monster(context, R.mipmap.demon_idle, 8, monsterLeft, monsterTop, monsterDrawWidth, monsterDrawHeight, 0f);
            monster.setMaxHp(40);
            monster.setAttackPower(20f);
            monster.setCanCauseFear(true);
            monster.setFearChance(1.0f); // 100% 공포 확률 (본체는 공포만)
        } else {
            monster = null;
        }

        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        player.getAnimation().setFrameDuration(0.15f);
        playerStats.resetStatsAndTimer();

        float effectWidth = Metrics.width * 0.4f;
        float effectHeight = effectWidth * (350f / (713f / 3f));
        float effectX;
        if (monster != null) {
            effectX = (playerLeft + playerDrawWidth + monster.getX()) / 2 - effectWidth / 2;
        } else {
            effectX = (playerLeft + playerDrawWidth + Metrics.width * 0.7f) / 2 - effectWidth / 2;
        }
        float effectY = playerTop + playerDrawHeight * 0.2f;
        player.setMagicEffectPosition(effectX, effectY, effectWidth, effectHeight);

        float swordEffectWidth = Metrics.width * 0.35f;
        float swordEffectHeight = swordEffectWidth * (139f / 190f);
        float swordEffectX;
        if (monster != null) {
            swordEffectX = (playerLeft + playerDrawWidth + monster.getX()) / 2 - swordEffectWidth / 2;
        } else {
            swordEffectX = (playerLeft + playerDrawWidth + Metrics.width * 0.7f) / 2 - swordEffectWidth / 2;
        }
        float swordEffectY = playerTop + playerDrawHeight * 0.3f;
        player.setSwordEffectPosition(swordEffectX, swordEffectY, swordEffectWidth, swordEffectHeight);
        
        damageText = new DamageText(context);
        
        // 출혈 텍스트 페인트 설정
        bleedingTextPaint = new Paint();
        bleedingTextPaint.setColor(Color.RED);
        bleedingTextPaint.setTextSize(40f);
        bleedingTextPaint.setTextAlign(Paint.Align.CENTER);
        bleedingTextPaint.setAntiAlias(true);
        
        setupStageSpecificElements();
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    @Override
    public void update() {
        super.update();
        float dt = GameView.frameTime;
        
        if (isGameOver) {
            return;
        }

        // 출혈 효과 업데이트
        if (isBleedingActive) {
            bleedingTimer += 0.016f; // 대략 60fps 기준
            bleedingTickTimer += 0.016f;
            
            // 1초마다 데미지
            if (bleedingTickTimer >= BLEEDING_TICK_INTERVAL) {
                bleedingTickTimer = 0f;
                playerStats.takeDamage(BLEEDING_DAMAGE);
                
                // 출혈 데미지 텍스트 표시
                float playerCenterX = player.getX() + player.getWidth() / 2;
                float playerTopY = player.getY();
                damageText.showDamage(BLEEDING_DAMAGE, playerCenterX, playerTopY - 50, true);
            }
            
            // 10초 후 출혈 효과 종료
            if (bleedingTimer >= BLEEDING_DURATION) {
                isBleedingActive = false;
                bleedingTimer = 0f;
                bleedingTickTimer = 0f;
            }
        }

        if (monster != null) {
            monster.update(dt);
        }
        
        // 분신 몬스터 업데이트
        if (cloneMonster != null) {
            cloneMonster.update(dt);
        }
        
        blockGrid.update(dt);
        player.update(dt);
        damageText.update(dt);

        if (!isBattlePhase && playerStats.isGameOver() && !isPuzzleFrozen && 
            !blockGrid.isAnyBlockAnimating() && !blockGrid.isAnyBlockFalling() && 
            !blockGrid.hasChainMatches()) {
            isPuzzleFrozen = true;
            isBattlePhase = true;
            isPlayerTurn = true;
            isWaitingForAnim = false;
            lastSword = playerStats.getPhysicalAttack();
            lastMagic = playerStats.getMagicAttack();
            lastHeal = playerStats.getHealing();
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (lastSword >= lastMagic && lastSword >= lastHeal) {
                        int totalDamage = lastSword + lastMagic;
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        player.playSwordAttack(() -> {
                            player.playSwordEffect(() -> {
                                if (monster != null && monster.isAlive()) {
                                    monster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                    isMonsterBlinkPhase = true;
                                    
                                    if (playerStats.checkStunChance()) {
                                        monster.setStunned(1);
                                    }
                                }
                                
                                // 분신도 동시에 공격
                                if (cloneMonster != null && cloneMonster.isAlive()) {
                                    cloneMonster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, cloneMonster.getX() + cloneMonster.getWidth()/2, cloneMonster.getY(), false);
                                    
                                    if (playerStats.checkStunChance()) {
                                        cloneMonster.setStunned(1);
                                    }
                                }
                                
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        int totalDamage = lastSword + lastMagic;
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        player.playMagicAttack(() -> {
                            player.playMagicEffect(() -> {
                                if (monster != null && monster.isAlive()) {
                                    monster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                    isMonsterBlinkPhase = true;
                                    
                                    if (playerStats.checkStunChance()) {
                                        monster.setStunned(1);
                                    }
                                }
                                
                                // 분신도 동시에 공격
                                if (cloneMonster != null && cloneMonster.isAlive()) {
                                    cloneMonster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, cloneMonster.getX() + cloneMonster.getWidth()/2, cloneMonster.getY(), false);
                                    
                                    if (playerStats.checkStunChance()) {
                                        cloneMonster.setStunned(1);
                                    }
                                }
                                
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else {
                        int totalDamage = lastSword + lastMagic;
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        player.playHeal(() -> {
                            if (monster != null && monster.isAlive()) {
                                monster.startBlinking(finalDamage);
                                damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                isMonsterBlinkPhase = true;
                                
                                if (playerStats.checkStunChance()) {
                                    monster.setStunned(1);
                                }
                            }
                            
                            // 분신도 동시에 공격
                            if (cloneMonster != null && cloneMonster.isAlive()) {
                                cloneMonster.startBlinking(finalDamage);
                                damageText.showDamage(finalDamage, cloneMonster.getX() + cloneMonster.getWidth()/2, cloneMonster.getY(), false);
                                
                                if (playerStats.checkStunChance()) {
                                    cloneMonster.setStunned(1);
                                }
                            }
                            
                            playerStats.heal(lastHeal);
                        });
                    }
                }
            } else {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (monster != null && monster.isAlive()) {
                        // 분신 스킬 사용 체크 (HP 50% 미만, 1회성)
                        if (monster.shouldUseCloneSkill()) {
                            monster.useCloneSkill(() -> {
                                // 분신 생성
                                createCloneMonster();
                                isCloneActive = true;
                                
                                // 본체에 공포 효과 추가 (S3_1 스타일)
                                monster.setCanCauseFear(true);
                                
                                // 스킬 완료 후 플레이어 턴으로
                                isBattlePhase = false;
                                isPuzzleFrozen = false;
                                playerStats.reset();
                                isWaitingForAnim = false;
                            });
                        } else if (monster.canAttack()) {
                            // 일반 공격
                            monster.attack(() -> {
                                float baseDamage = monster.getAttackPower();
                                float finalDamage = playerStats.calculateReceivedDamage(baseDamage);
                                player.takeDamage(finalDamage);
                                damageText.showDamage((int)finalDamage, player.getX() + player.getWidth()/2, player.getY(), true);
                                
                                // 본체는 공포 효과만
                                if (monster.canCauseFear() && monster.checkFearEffect()) {
                                    playerStats.applyFear();
                                }
                                
                                if (!player.isAlive()) {
                                    player.die();
                                    isGameOver = true;
                                    GameOverScene.getInstance().show(SceneManager.SceneType.S3_3);
                                    return;
                                }
                                
                                // 분신이 있으면 분신 공격으로 이어짐
                                if (isCloneActive && cloneMonster != null && cloneMonster.isAlive()) {
                                    isAttackingClone = true;
                                    cloneMonster.attack(() -> {
                                        float cloneBaseDamage = cloneMonster.getAttackPower();
                                        float cloneFinalDamage = playerStats.calculateReceivedDamage(cloneBaseDamage);
                                        player.takeDamage(cloneFinalDamage);
                                        damageText.showDamage((int)cloneFinalDamage, player.getX() + player.getWidth()/2, player.getY(), true);
                                        
                                        // 분신의 출혈 효과 체크 (100% 확률)
                                        if (cloneMonster.checkBleedingEffect()) {
                                            isBleedingActive = true;
                                            bleedingTimer = 0f;
                                            bleedingTickTimer = 0f;
                                        }
                                        
                                        if (!player.isAlive()) {
                                            player.die();
                                            isGameOver = true;
                                            GameOverScene.getInstance().show(SceneManager.SceneType.S3_3);
                                            return;
                                        }
                                        
                                        monster.reduceStunTurns();
                                        cloneMonster.reduceStunTurns();
                                        playerStats.reduceFearTurns();
                                        isBattlePhase = false;
                                        isPuzzleFrozen = false;
                                        playerStats.reset();
                                        isWaitingForAnim = false;
                                        isAttackingClone = false;
                                    });
                                } else {
                                    monster.reduceStunTurns();
                                    playerStats.reduceFearTurns();
                                    isBattlePhase = false;
                                    isPuzzleFrozen = false;
                                    playerStats.reset();
                                    isWaitingForAnim = false;
                                }
                            });
                        } else {
                            monster.reduceStunTurns();
                            if (cloneMonster != null) {
                                cloneMonster.reduceStunTurns();
                            }
                            playerStats.reduceFearTurns();
                            isBattlePhase = false;
                            isPuzzleFrozen = false;
                            playerStats.reset();
                            isWaitingForAnim = false;
                        }
                    } else {
                        isBattlePhase = false;
                        isPuzzleFrozen = false;
                        playerStats.reset();
                        isWaitingForAnim = false;
                        if (!isStageClearShown) {
                            // 스테이지 3-3 클리어 시 게임 엔딩 표시
                            GameEndingScene.getInstance(context).show();
                            isStageClearShown = true;
                        }
                    }
                }
            }
        }

        if (isMonsterBlinkPhase && monster != null && !monster.isBlinking()) {
            isMonsterBlinkPhase = false;
            isPlayerTurn = false;
            isWaitingForAnim = false;
        }

        if (!isStageClearShown && (monster == null || (!monster.isAlive() && !monster.isDying()))) {
            // 분신이 있으면 분신도 죽어야 클리어
            boolean allMonstersDefeated = true;
            if (cloneMonster != null && (cloneMonster.isAlive() || cloneMonster.isDying())) {
                allMonstersDefeated = false;
            }
            
            if (allMonstersDefeated && !isStageClearShown) {
                // 스테이지 3-3 클리어 시 게임 엔딩 표시
                GameEndingScene.getInstance(context).show();
                isStageClearShown = true;
            }
        }

        if (!isGameOver && player.isDead()) {
            isGameOver = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        float puzzleStart = Metrics.height * 0.45f;
        float playerInfoStart = Metrics.height * 0.30f;
        
        if (battleBg != null) {
            RectF bgRect = new RectF(0, 0, Metrics.width, playerInfoStart);
            canvas.drawBitmap(battleBg, null, bgRect, null);
        }
        
        if (stateBg != null) {
            RectF stateRect = new RectF(0, playerInfoStart, Metrics.width, puzzleStart);
            canvas.drawBitmap(stateBg, null, stateRect, null);
        }
        
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

        if (monster != null) {
            monster.draw(canvas);
        }
        
        // 분신 몬스터 그리기
        if (cloneMonster != null) {
            cloneMonster.draw(canvas);
        }
        
        player.draw(canvas);
        playerStats.draw(canvas, Metrics.width, playerInfoStart, puzzleStart);
        
        // 출혈 텍스트 표시
        if (isBleedingActive) {
            float textX = player.getX() + player.getWidth() / 2;
            float textY = player.getY() - 20;
            canvas.drawText("출혈", textX, textY, bleedingTextPaint);
        }
        
        damageText.draw(canvas);

        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }

        // 스테이지 3-3에서는 게임 엔딩 씬만 표시
        if (GameEndingScene.getInstance() != null && GameEndingScene.getInstance().isVisible()) {
            GameEndingScene.getInstance().draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            return GameOverScene.getInstance().onTouchEvent(event);
        }

        // 게임 엔딩 씬이 표시되면 엔딩 씬의 터치 이벤트 처리
        if (GameEndingScene.getInstance() != null && GameEndingScene.getInstance().isVisible()) {
            return GameEndingScene.getInstance().onTouchEvent(event);
        }

        // 스테이지 3-3에서는 StageClearScene 터치 이벤트 처리 불필요

        if (playerStats.isGameOver() || blockGrid.isAnyBlockAnimating() || isPuzzleFrozen) {
            return true;
        }

        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];
        
        float puzzleStart = Metrics.height * 0.45f;
        float puzzleAreaHeight = Metrics.height - puzzleStart;
        float puzzleSize = Math.min(Metrics.width, puzzleAreaHeight);
        
        float currentPuzzleLeft = (Metrics.width - puzzleSize) / 2;
        float currentPuzzleTop = puzzleStart;
        float currentBlockSize = puzzleSize / BlockGrid.getGridSize();
        
        // 주머니 아이템 클릭 확인
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float playerInfoStart = Metrics.height * 0.30f;
            if (playerStats.isPocketClicked(x, y, Metrics.width, playerInfoStart, puzzleStart)) {
                blockGrid.destroyRandomBlocks(5);
                new Thread(() -> {
                    try {
                        Thread.sleep(100);
                        while (blockGrid.isAnyBlockAnimating() || blockGrid.isAnyBlockFalling()) {
                            Thread.sleep(50);
                        }
                        if (blockGrid.hasChainMatches()) {
                            blockGrid.forceProcessMatches();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
                return true;
            }
        }
        
        int col = (int)((x - currentPuzzleLeft) / currentBlockSize);
        int row = (int)((y - currentPuzzleTop) / currentBlockSize);
        
        if (row >= 0 && row < BlockGrid.getGridSize() && col >= 0 && col < BlockGrid.getGridSize()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:  
                    // 폭탄 블록 클릭 확인
                    if (blockGrid.getBlock(row, col) != null && blockGrid.getBlock(row, col).isBomb()) {
                        if (blockGrid.handleBombClick(row, col)) {
                            // 폭탄 클릭 후 백그라운드에서 퍼즐 처리
                            new Thread(() -> {
                                try {
                                    Thread.sleep(100);
                                    while (blockGrid.isAnyBlockAnimating() || blockGrid.isAnyBlockFalling()) {
                                        Thread.sleep(50);
                                    }
                                    if (blockGrid.hasChainMatches()) {
                                        blockGrid.forceProcessMatches();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                            return true; // 폭탄이 터졌으므로 이벤트 처리 완료
                        }
                    }
                    
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
                        
                        if (Math.abs(dx) > 20.0f || Math.abs(dy) > 20.0f) {
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
    public void onEnter() {
        super.onEnter();
        
        // 로그라이크 효과 지속성 보장
        StageManager.ensureRoguelikeEffectsPersistence();
        
        // 스테이지 3부터 전투 로그라이크 효과 적용
        StageManager.applyBattleRoguelikeEffects(playerStats);
        
        // 스테이지 3부터 퍼즐 로그라이크 효과 적용
        StageManager.applyPuzzleRoguelikeEffects(playerStats);
        
        if (StageManager.getInstance().isStageCleared(3, 3)) {
            // 스테이지 3-3이 이미 클리어된 상태라면 게임 엔딩 표시
            GameEndingScene.getInstance(context).show();
            isStageClearShown = true;
            
            if (context instanceof PegglePangActivity) {
                PegglePangActivity activity = (PegglePangActivity) context;
                TextView backText = activity.findViewById(R.id.back_text);
                if (backText != null) {
                    backText.setEnabled(false);
                    backText.setAlpha(0.5f);
                }
            }
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            TextView backText = activity.findViewById(R.id.back_text);
            if (backText != null) {
                backText.setEnabled(true);
                backText.setAlpha(1.0f);
            }
        }
    }

    // 분신 몬스터 생성
    private void createCloneMonster() {
        if (monster == null) return;
        
        float battleHeight = Metrics.height * 0.30f;
        float cloneDrawHeight = battleHeight * 0.7f;
        float cloneDrawWidth = 150f; // 본체와 동일한 크기
        
        // 본체 왼쪽에 배치
        float cloneLeft = monster.getX() - cloneDrawWidth - 20f;
        float cloneTop = monster.getY();
        
        cloneMonster = new Stage2Monster(context, R.mipmap.demon_idle, 8, cloneLeft, cloneTop, cloneDrawWidth, cloneDrawHeight, 0f);
        cloneMonster.setMaxHp(monster.getCurrentHp()); // 본체와 동일한 HP
        cloneMonster.setAttackPower(monster.getAttackPower()); // 본체와 동일한 공격력
        cloneMonster.setCanCauseBleeding(true);
        cloneMonster.setBleedingChance(1.0f); // 100% 출혈 확률 (분신은 출혈만)
        cloneMonster.setFlipped(true); // 분신은 가로대칭으로 표시
    }
} 