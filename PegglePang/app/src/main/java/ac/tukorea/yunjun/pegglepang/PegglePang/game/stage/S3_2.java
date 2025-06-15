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
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.StageClearScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.Player;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;

public class S3_2 extends BaseStageScene {
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
    private Bitmap battleBg;
    private Bitmap stateBg;

    private boolean isGameOver = false;
    private boolean isStageClearShown = false;
    private boolean isMonsterBlinkPhase = false;
    private DamageText damageText;

    // 출혈 효과 관련 변수
    private boolean isBleedingActive = false;
    private float bleedingTimer = 0f;
    private float bleedingTickTimer = 0f;
    private static final float BLEEDING_DURATION = 10f; // 10초
    private static final float BLEEDING_TICK_INTERVAL = 1f; // 1초마다
    private static final int BLEEDING_DAMAGE = 1; // 1 데미지
    private Paint bleedingTextPaint;

    public S3_2(Context context) {
        super(context, 3, 2);
        
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
        blockGrid.setStageInfo(3, 2); // 스테이지 3-2 정보 설정
        
        // 전역 설정에서 폭탄 블록이 활성화되어 있으면 적용
        if (StageManager.isBombBlocksEnabled()) {
            blockGrid.enableBombBlocks();
        }
        isPuzzleFrozen = false;

        // 스테이지가 이미 클리어되지 않았을 때만 몬스터 생성
        if (!StageManager.getInstance().isStageCleared(3, 2)) {
            // 3-2 스테이지용 몬스터 (insect, HP 30, 공격력 15)
            float monsterDrawHeight = battleHeight * 0.5f;
            float monsterDrawWidth = 80f;
            float monsterLeft = Metrics.width - monsterDrawWidth - (Metrics.width * 0.05f);
            float monsterTop = battleHeight - monsterDrawHeight - (battleHeight * 0.05f);
            monster = new Stage2Monster(context, R.mipmap.insect_idle, 5, monsterLeft, monsterTop, monsterDrawWidth, monsterDrawHeight, 1f);
            monster.setMaxHp(30);
        } else {
            monster = null;
        }
        
        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        player.getAnimation().setFrameDuration(0.15f);
        playerStats.resetStatsAndTimer();

        // magic effect 위치 설정
        float effectWidth = Metrics.width * 0.4f;
        float effectHeight = effectWidth * (350f / (713f / 3f));
        float effectX = (playerLeft + playerDrawWidth + (Metrics.width - 80f - (Metrics.width * 0.05f))) / 2 - effectWidth / 2;
        float effectY = playerTop + playerDrawHeight * 0.2f;
        player.setMagicEffectPosition(effectX, effectY, effectWidth, effectHeight);

        // sword effect 위치 설정
        float swordEffectWidth = Metrics.width * 0.35f;
        float swordEffectHeight = swordEffectWidth * (139f / 190f);
        float swordEffectX = (playerLeft + playerDrawWidth + (Metrics.width - 80f - (Metrics.width * 0.05f))) / 2 - swordEffectWidth / 2;
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
        // 3-2 스테이지 특화 설정
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
        
        blockGrid.update(dt);
        player.update(dt);
        damageText.update(dt);

        // 전투 시스템 추가
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
            System.out.println("Battle stats - Sword: " + lastSword + ", Magic: " + lastMagic + ", Heal: " + lastHeal);
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (lastSword >= lastMagic && lastSword >= lastHeal) {
                        int totalDamage = lastSword + lastMagic;
                        // 전투 로그라이크: 크리티컬 데미지 적용
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        System.out.println("Using SWORD attack with total damage: " + finalDamage + " (Base: " + totalDamage + ", Critical: " + (finalDamage > totalDamage) + ")");
                        player.playSwordAttack(() -> {
                            player.playSwordEffect(() -> {
                                if (monster != null && monster.isAlive()) {
                                    monster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                    isMonsterBlinkPhase = true;
                                    
                                    // 전투 로그라이크: 마비 효과 적용
                                    if (playerStats.checkStunChance()) {
                                        monster.setStunned(1); // 1턴 마비
                                    }
                                }
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        int totalDamage = lastSword + lastMagic;
                        // 전투 로그라이크: 크리티컬 데미지 적용
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        System.out.println("Using MAGIC attack with total damage: " + finalDamage + " (Base: " + totalDamage + ", Critical: " + (finalDamage > totalDamage) + ")");
                        player.playMagicAttack(() -> {
                            player.playMagicEffect(() -> {
                                if (monster != null && monster.isAlive()) {
                                    monster.startBlinking(finalDamage);
                                    damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                    isMonsterBlinkPhase = true;
                                    
                                    // 전투 로그라이크: 마비 효과 적용
                                    if (playerStats.checkStunChance()) {
                                        monster.setStunned(1); // 1턴 마비
                                    }
                                }
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else {
                        int totalDamage = lastSword + lastMagic;
                        // 전투 로그라이크: 크리티컬 데미지 적용
                        int finalDamage = playerStats.calculateCriticalDamage(totalDamage);
                        System.out.println("Using HEAL with amount: " + lastHeal + " and total damage: " + finalDamage + " (Base: " + totalDamage + ", Critical: " + (finalDamage > totalDamage) + ")");
                        player.playHeal(() -> {
                            if (monster != null && monster.isAlive()) {
                                monster.startBlinking(finalDamage);
                                damageText.showDamage(finalDamage, monster.getX() + monster.getWidth()/2, monster.getY(), false);
                                isMonsterBlinkPhase = true;
                                
                                // 전투 로그라이크: 마비 효과 적용
                                if (playerStats.checkStunChance()) {
                                    monster.setStunned(1); // 1턴 마비
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
                        // 마비 상태 체크
                        if (monster.canAttack()) {
                            monster.attack(() -> {
                                // 20% 확률로 출혈 효과 적용
                                if (Math.random() < 0.2) {
                                    isBleedingActive = true;
                                    bleedingTimer = 0f;
                                    bleedingTickTimer = 0f;
                                }
                                
                                float baseDamage = monster.getAttackPower();
                                // 전투 로그라이크: 데미지 감소 적용
                                float finalDamage = playerStats.calculateReceivedDamage(baseDamage);
                                player.takeDamage(finalDamage);
                                damageText.showDamage((int)finalDamage, player.getX() + player.getWidth()/2, player.getY(), true);
                                
                                if (!player.isAlive()) {
                                    player.die();
                                    isGameOver = true;
                                    GameOverScene.getInstance().show(SceneManager.SceneType.S3_2);
                                    return;
                                }
                                // 마비 턴 감소
                                monster.reduceStunTurns();
                                // 공포 턴 감소
                                playerStats.reduceFearTurns();
                                isBattlePhase = false;
                                isPuzzleFrozen = false;
                                playerStats.reset();
                                isWaitingForAnim = false;
                            });
                        } else {
                            // 스턴 상태에서는 공격하지 않고 턴만 감소
                            monster.reduceStunTurns();
                            // 공포 턴 감소
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
                            StageClearScene.getInstance(context).show(3, 2);
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

        // 몬스터가 죽었을 때 스테이지 클리어
        if (!isStageClearShown && (monster == null || (!monster.isAlive() && !monster.isDying()))) {
            if (!isStageClearShown) {
                StageClearScene.getInstance(context).show(3, 2);
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

        StageClearScene.getInstance(context).draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            return GameOverScene.getInstance().onTouchEvent(event);
        }

        if (isStageClearShown) {
            return StageClearScene.getInstance(context).onTouchEvent(event);
        }

        if (playerStats.isGameOver() || blockGrid.isAnyBlockAnimating() || isPuzzleFrozen) {
            return true;
        }

        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];
        
        // 주머니 아이템 클릭 확인
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float playerInfoStart = Metrics.height * 0.30f;
            float puzzleStartLocal = Metrics.height * 0.45f;
            if (playerStats.isPocketClicked(x, y, Metrics.width, playerInfoStart, puzzleStartLocal)) {
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
        
        // 퍼즐 영역 계산 (draw 메서드와 동일하게)
        float puzzleStartLocal = Metrics.height * 0.45f;
        float puzzleAreaHeight = Metrics.height - puzzleStartLocal;
        float puzzleSize = Math.min(Metrics.width, puzzleAreaHeight);
        
        float currentPuzzleLeft = (Metrics.width - puzzleSize) / 2;
        float currentPuzzleTop = puzzleStartLocal;
        float currentBlockSize = puzzleSize / BlockGrid.getGridSize();
        
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
        
        // 디버그: 퍼즐 로그라이크 효과 확인
        System.out.println("S3_2 퍼즐 로그라이크 상태:");
        System.out.println("- 돌블럭 방지: " + StageManager.isPuzzleRockPreventionEnabled());
        System.out.println("- 칼블럭 2배: " + StageManager.isPuzzleSwordDoubleEnabled());
        System.out.println("- 마법블럭 2배: " + StageManager.isPuzzleMagicDoubleEnabled());
        System.out.println("- 폭탄 블록 활성화: " + StageManager.isBombBlocksEnabled());
        System.out.println("PlayerStats 상태:");
        System.out.println("- 돌블럭 방지: " + playerStats.hasRockBlockPrevention());
        System.out.println("- 칼블럭 2배: " + playerStats.hasSwordBlockDouble());
        System.out.println("- 마법블럭 2배: " + playerStats.hasMagicBlockDouble());
        
        if (StageManager.getInstance().isStageCleared(3, 2)) {
            StageClearScene.getInstance(context).show(3, 2);
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
} 