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
                playerStats.reset();
                blockGrid.reset();
            }
        });

        battleSystem.addMonster(slime);
        battleSystem.addMonster(skeleton);
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
        if (!isBattlePhase && playerStats.isGameOver() && !isPuzzleFrozen) {
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

        // 배틀 애니메이션 처리
        if (isBattlePhase && isPlayerTurn && !isWaitingForAnim) {
            isWaitingForAnim = true;
            if (lastSword >= lastMagic && lastSword >= lastHeal) {
                player.playSwordAttack(() -> {
                    int totalDamage = lastSword + lastMagic;
                    slime.takeDamage(totalDamage);
                    skeleton.takeDamage(totalDamage);
                    playerStats.heal(lastHeal);
                    isPlayerTurn = false;
                    isWaitingForAnim = false;
                });
            } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                player.playMagicAttack(() -> {
                    int totalDamage = lastSword + lastMagic;
                    slime.takeDamage(totalDamage);
                    skeleton.takeDamage(totalDamage);
                    playerStats.heal(lastHeal);
                    isPlayerTurn = false;
                    isWaitingForAnim = false;
                });
            } else {
                player.playHeal(() -> {
                    int totalDamage = lastSword + lastMagic;
                    slime.takeDamage(totalDamage);
                    skeleton.takeDamage(totalDamage);
                    playerStats.heal(lastHeal);
                    isPlayerTurn = false;
                    isWaitingForAnim = false;
                });
            }
        }

        // 몬스터 턴 처리
        if (isBattlePhase && !isPlayerTurn && !isWaitingForAnim) {
            if (slime.isAlive()) playerStats.takeDamage(slime.getAttackPower());
            if (skeleton.isAlive()) playerStats.takeDamage(skeleton.getAttackPower());
            isBattlePhase = false;
            isPuzzleFrozen = false;
            playerStats.reset();
            blockGrid.reset();
            if (!slime.isAlive() && !skeleton.isAlive()) {
                StageManager.getInstance().unlockStage(1, 2);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (blockGrid.isAnyBlockAnimating() || isPuzzleFrozen) {
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

        player.draw(canvas);
        slime.draw(canvas);
        skeleton.draw(canvas);
    }

    public void startNewPuzzlePhase() {
        isPuzzleFrozen = false;
        playerStats.reset();
        blockGrid.reset();
    }
}
