package ac.tukorea.yunjun.pegglepang.PegglePang.game.stage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.content.Context;
import android.view.MotionEvent;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.battle.BaseStageScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.ui.DamageText;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.Block;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.BlockGrid;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.GameOverScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage2Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.StageClearScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.Player;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import android.widget.TextView;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class S1_2 extends BaseStageScene {
    private static final int GRID_SIZE = 6;              
    private static final float SWIPE_THRESHOLD = 20.0f;   
    
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
    private Stage2Monster monster1;
    private Bitmap battleBg;
    private Bitmap stateBg;

    private boolean isGameOver = false;
    private boolean isStageClearShown = false;
    private boolean isMonsterBlinkPhase = false;

    private float puzzleTransitionTimer = 0f;
    private DamageText damageText;

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

        // 스테이지가 이미 클리어된 상태가 아닐 때만 몬스터 생성
        if (!StageManager.getInstance().isStageUnlocked(1, 3)) {
            // 몬스터1 (magicman_idle)
            float monster1DrawHeight = battleHeight * 0.5f;
            float monster1DrawWidth = 80f;
            float monster1Left = Metrics.width - monster1DrawWidth - (Metrics.width * 0.05f);
            float monster1Top = battleHeight - monster1DrawHeight - (battleHeight * 0.05f);
            monster1 = new Stage2Monster(context, R.mipmap.magicman_idle, 2, monster1Left, monster1Top, monster1DrawWidth, monster1DrawHeight, 10f);
        } else {
            // 이미 클리어된 상태라면 몬스터를 null로 설정
            monster1 = null;
        }

        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        player.getAnimation().setFrameDuration(0.15f);
        playerStats.resetStatsAndTimer();

        // magic effect 위치 설정
        float effectW = 713f / 3f;
        float effectH = 350f;
        float effectScale = 0.7f;
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
        if (monster1 != null) {
            monster1.update(dt);
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
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (lastSword >= lastMagic && lastSword >= lastHeal) {
                        player.playSwordAttack(() -> {
                            player.playSwordEffect(() -> {
                                if (monster1 != null && monster1.isAlive()) {
                                    monster1.startBlinking(lastSword);
                                    damageText.showDamage(lastSword, monster1.getX() + monster1.getWidth()/2, monster1.getY(), false);
                                    isMonsterBlinkPhase = true;
                                }
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        player.playMagicAttack(() -> {
                            player.playMagicEffect(() -> {
                                if (monster1 != null && monster1.isAlive()) {
                                    monster1.startBlinking(lastSword);
                                    damageText.showDamage(lastSword, monster1.getX() + monster1.getWidth()/2, monster1.getY(), false);
                                    isMonsterBlinkPhase = true;
                                }
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else {
                        player.playHeal(() -> {
                            if (monster1 != null && monster1.isAlive()) {
                                monster1.startBlinking(lastSword);
                                damageText.showDamage(lastSword, monster1.getX() + monster1.getWidth()/2, monster1.getY(), false);
                                isMonsterBlinkPhase = true;
                            }
                            playerStats.heal(lastHeal);
                        });
                    }
                }
            } else {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (monster1 != null && monster1.isAlive()) {
                        monster1.attack(() -> {
                            float damage = monster1.getAttackPower();
                            player.takeDamage(damage);
                            damageText.showDamage((int)damage, player.getX() + player.getWidth()/2, player.getY(), true);
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
                        });
                    } else {
                        isBattlePhase = false;
                        isPuzzleFrozen = false;
                        playerStats.reset();
                        isWaitingForAnim = false;
                        if (!isStageClearShown) {
                            StageClearScene.getInstance(context).show(1, 2);
                            isStageClearShown = true;
                            StageManager.getInstance().unlockStage(1, 3);
                        }
                    }
                }
            }
        }

        if (isMonsterBlinkPhase && monster1 != null && !monster1.isBlinking()) {
            isMonsterBlinkPhase = false;
            isPlayerTurn = false;
            isWaitingForAnim = false;
        }

        if (isGameOver) {
            return;
        }

        if (!isStageClearShown && (monster1 == null || (!monster1.isAlive() && !monster1.isDying()))) {
            StageClearScene.getInstance(context).show(1, 2);
            isStageClearShown = true;
            StageManager.getInstance().unlockStage(1, 3);
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
        canvas.drawColor(Color.BLACK); // 전체 화면을 검은색으로 채움
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
        if (monster1 != null) {
            monster1.draw(canvas);
        }

        // 데미지 텍스트 그리기 (몬스터 바로 다음에)
        damageText.draw(canvas);
        
        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }

        StageClearScene.getInstance(context).draw(canvas);
    }

    protected void startPuzzleTransition() {
        puzzleTransitionTimer = 0f;
    }

    protected boolean updatePuzzleTransition(float dt) {
        if (puzzleTransitionTimer < 0f) {
            return false;  // 딜레이가 시작되지 않음
        }
        puzzleTransitionTimer += dt;
        if (puzzleTransitionTimer >= 1.0f) {  // 1초 딜레이
            puzzleTransitionTimer = -1f;  // 딜레이 종료
            return true;
        }
        return false;
    }

    @Override
    public void onEnter() {
        super.onEnter();
        if (StageManager.getInstance().isStageUnlocked(1, 3)) {
            // 이미 스테이지가 클리어된 상태라면 클리어 창을 바로 표시
            StageClearScene.getInstance(context).show(1, 2);
            isStageClearShown = true;
            
            // Back 버튼 비활성화
            if (context instanceof PegglePangActivity) {
                PegglePangActivity activity = (PegglePangActivity) context;
                TextView backText = activity.findViewById(R.id.back_text);
                if (backText != null) {
                    backText.setEnabled(false);
                    backText.setAlpha(0.5f); // 반투명하게 표시
                }
            }
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        // Back 버튼 다시 활성화
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