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
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.StageClearScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.Player;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;

public class S3_1 extends BaseStageScene {
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

    public S3_1(Context context) {
        super(context, 3, 1);
        
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
        blockGrid.setStageInfo(3, 1); // 스테이지 3-1 정보 설정
        
        // 전역 설정에서 폭탄 블록이 활성화되어 있으면 적용
        if (StageManager.isBombBlocksEnabled()) {
            blockGrid.enableBombBlocks();
        }
        isPuzzleFrozen = false;

        boolean is3_2Unlocked = StageManager.getInstance().isStageUnlocked(3, 2);
        
        if (!is3_2Unlocked) {
            // 3-1 스테이지용 몬스터 (고스트, HP 25, 공격력 12)
            float monsterDrawHeight = battleHeight * 0.5f;
            float monsterDrawWidth = 80f;
            float monsterLeft = Metrics.width - monsterDrawWidth - (Metrics.width * 0.05f);
            float monsterTop = battleHeight - monsterDrawHeight - (battleHeight * 0.05f);
            monster = new Stage2Monster(context, R.mipmap.ghost_idle, 3, monsterLeft, monsterTop, monsterDrawWidth, monsterDrawHeight, 0f);
            monster.setMaxHp(25);
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
        
        setupStageSpecificElements();
    }

    @Override
    protected void setupStageSpecificElements() {
        // 3-1 스테이지 특화 설정
    }

    @Override
    public void update() {
        super.update();
        float dt = GameView.frameTime;
        blockGrid.update(dt);
        player.update(dt);
        if (monster != null) {
            monster.update(dt);
        }
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
                                float baseDamage = monster.getAttackPower();
                                // 전투 로그라이크: 데미지 감소 적용
                                float finalDamage = playerStats.calculateReceivedDamage(baseDamage);
                                player.takeDamage(finalDamage);
                                damageText.showDamage((int)finalDamage, player.getX() + player.getWidth()/2, player.getY(), true);
                                
                                // 고스트의 공포 효과 체크
                                if (monster.canCauseFear() && monster.checkFearEffect()) {
                                    playerStats.applyFear();
                                }
                                
                                if (!player.isAlive()) {
                                    player.die();
                                    isGameOver = true;
                                    GameOverScene.getInstance().show();
                                    return;
                                }
                                // 마비 턴 감소
                                monster.reduceStunTurns();
                                isBattlePhase = false;
                                isPuzzleFrozen = false;
                                playerStats.reset();
                                isWaitingForAnim = false;
                            });
                        } else {
                            // 몬스터가 마비 상태 - 공격하지 않고 바로 다음 턴으로
                            monster.reduceStunTurns();
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
                            StageClearScene.getInstance(context).show(3, 1);
                            isStageClearShown = true;
                            StageManager.getInstance().unlockStage(3, 2);
                        }
                    }
                }
            }

                    if (isMonsterBlinkPhase && monster != null && !monster.isBlinking()) {
            isMonsterBlinkPhase = false;
            isPlayerTurn = false;
            isWaitingForAnim = false;
        }
        }

        if (!isStageClearShown && (monster == null || (!monster.isAlive() && !monster.isDying()))) {
            StageClearScene.getInstance(context).show(3, 1);
            isStageClearShown = true;
            StageManager.getInstance().unlockStage(3, 2);
        }

        if (!isGameOver && player.isDead()) {
            isGameOver = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            return GameOverScene.getInstance().onTouchEvent(event);
        }

        if (StageClearScene.getInstance(context).onTouchEvent(event)) {
            return true;
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
            float puzzleStart = Metrics.height * 0.45f;
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
        
        int col = (int)((x - puzzleLeft) / blockSize);
        int row = (int)((y - puzzleTop) / blockSize);
        
        if (row >= 0 && row < BlockGrid.getGridSize() && col >= 0 && col < BlockGrid.getGridSize()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:  
                    if (blockGrid.getBlock(row, col) != null && blockGrid.getBlock(row, col).isBomb()) {
                        if (blockGrid.handleBombClick(row, col)) {
                            return true;
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
        playerStats.draw(canvas, Metrics.width, playerInfoStart, puzzleStart);

        player.draw(canvas);
        if (monster != null) {
            monster.draw(canvas);
        }

        damageText.draw(canvas);
        
        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }

        StageClearScene.getInstance(context).draw(canvas);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        if (context instanceof PegglePangActivity) {
            PegglePangActivity activity = (PegglePangActivity) context;
            activity.setContentView(R.layout.game_scene);
            setupStageSpecificElements();
        }
        
        // 스테이지 3부터 전투 로그라이크 효과 적용
        StageManager.applyBattleRoguelikeEffects(playerStats);
        
        // 스테이지 3부터 퍼즐 로그라이크 효과 적용
        StageManager.applyPuzzleRoguelikeEffects(playerStats);
        
        if (StageManager.getInstance().isStageCleared(3, 1)) {
            StageClearScene.getInstance(context).show(3, 1);
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