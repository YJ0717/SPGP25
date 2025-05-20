package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.content.Context;
import android.view.MotionEvent;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.Stage2Monster;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;

public class S1_2 extends BaseStageScene {
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
    private Stage2Monster monster1;
    private Bitmap battleBg;
    private Bitmap stateBg;

    private boolean isGameOver = false;
    private boolean isStageClearShown = false;

    public S1_2(Context context) {
        super(context, 1, 2);
        
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
        isPuzzleFrozen = false;

        // 몬스터1 (magicman_idle)
        float monster1DrawHeight = battleHeight * 0.5f;
        float monster1DrawWidth = 80f;
        float monster1Left = Metrics.width - monster1DrawWidth - (Metrics.width * 0.05f);
        float monster1Top = battleHeight - monster1DrawHeight - (battleHeight * 0.05f);
        monster1 = new Stage2Monster(context, R.mipmap.magicman_idle, 3, monster1Left, monster1Top, monster1DrawWidth, monster1DrawHeight);

        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        player.getAnimation().setFrameDuration(0.15f);
        playerStats.resetStatsAndTimer();
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    @Override
    public void update() {
        super.update();
        float dt = GameView.frameTime;
        blockGrid.update(dt);
        player.update(dt);
        monster1.update(dt);

        if (!isBattlePhase && playerStats.isGameOver() && !isPuzzleFrozen && !blockGrid.isAnyBlockAnimating() && !blockGrid.isAnyBlockFalling()) {
            isPuzzleFrozen = true;
            battleDelayTimer = 0f;
        }

        if (isPuzzleFrozen && !isBattlePhase) {
            battleDelayTimer += dt;
        }

        if (isPuzzleFrozen && !isBattlePhase && battleDelayTimer >= BATTLE_DELAY) {
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
                        player.playSwordAttack(() -> {
                            if (monster1.isAlive()) monster1.startBlinking(lastSword + lastMagic);
                            playerStats.heal(lastHeal);
                            isPlayerTurn = false;
                            isWaitingForAnim = false;
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        player.playMagicAttack(() -> {
                            if (monster1.isAlive()) monster1.startBlinking(lastSword + lastMagic);
                            playerStats.heal(lastHeal);
                            isPlayerTurn = false;
                            isWaitingForAnim = false;
                        });
                    } else {
                        player.playHeal(() -> {
                            playerStats.heal(lastHeal);
                            isPlayerTurn = false;
                            isWaitingForAnim = false;
                        });
                    }
                }
            } else {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (monster1.isAlive()) {
                        monster1.attack(() -> {
                            player.takeDamage(monster1.getAttackPower());
                            if (!player.isAlive()) {
                                player.die();
                                isGameOver = true;
                                GameOverScene.getInstance().show();
                                return;
                            }
                            isBattlePhase = false;
                            isPuzzleFrozen = false;
                            playerStats.reset();
                            isWaitingForAnim = false;
                            if (!monster1.isAlive()) {
                                StageManager.getInstance().setStageCleared(1, 2);
                                StageManager.getInstance().setMonstersDefeated(1, 2, true);
                                StageManager.getInstance().unlockStage(1, 3);
                            }
                        });
                    }
                }
            }
        }

        if (isGameOver) {
            return;
        }

        // 몬스터가 모두 죽었을 때만 스테이지 클리어 창 표시
        if (!isStageClearShown && !monster1.isAlive()) {
            StageClearScene.getInstance(context).show(1, 2);
            isStageClearShown = true;
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
        monster1.draw(canvas);

        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }

        StageClearScene.getInstance(context).draw(canvas);
    }
} 