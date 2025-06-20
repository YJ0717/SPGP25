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
import ac.tukorea.yunjun.pegglepang.PegglePang.game.battle.RoguelikeChoiceScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.ui.DamageText;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.Block;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.base.BlockGrid;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.GameOverScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.monster.Stage3Monster;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.main.StageClearScene;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.Player;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;
import ac.tukorea.yunjun.pegglepang.framework.view.GameView;
import android.widget.TextView;
import ac.tukorea.yunjun.pegglepang.PegglePang.app.PegglePangActivity;

public class S2_3 extends BaseStageScene {
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
    private Stage3Monster dragonMonster; // 드래곤 몬스터
    private Bitmap battleBg;
    private Bitmap stateBg;

    private boolean isGameOver = false;
    private boolean isStageClearShown = false;
    private boolean isMonsterBlinkPhase = false;
    private boolean isRoguelikeChoiceShown = false;
    private DamageText damageText;

    public S2_3(Context context) {
        super(context, 2, 3);
        
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
        blockGrid.setStageInfo(2, 3); // 스테이지 2-3 정보 설정
        
        // 전역 설정에서 폭탄 블록이 활성화되어 있으면 적용
        if (StageManager.isBombBlocksEnabled()) {
            blockGrid.enableBombBlocks();
        }
        isPuzzleFrozen = false;

        // 스테이지가 이미 클리어된 상태가 아닐 때만 몬스터 생성
        if (!StageManager.getInstance().isStageCleared(2, 3)) {
            // 드래곤 몬스터 (화면 중앙에 위치)
            dragonMonster = new Stage3Monster(context, R.mipmap.dragon_idle, 1, playerInfoStart, 20f);
        } else {
            // 이미 클리어된 상태라면 몬스터를 null로 설정
            dragonMonster = null;
        }

        battleBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.stage1);
        stateBg = BitmapFactory.decodeResource(context.getResources(), R.mipmap.state);

        player.getAnimation().setFrameDuration(0.15f);
        playerStats.resetStatsAndTimer();

        // magic effect 위치 설정 (드래곤 중앙)
        float effectWidth = Metrics.width * 0.4f;
        float effectHeight = effectWidth * (350f / (713f / 3f));
        float effectX = Metrics.width * 0.5f - effectWidth / 2; // 화면 중앙
        float effectY = playerTop + playerDrawHeight * 0.2f;
        player.setMagicEffectPosition(effectX, effectY, effectWidth, effectHeight);

        // sword effect 위치 설정
        float swordEffectWidth = Metrics.width * 0.35f;
        float swordEffectHeight = swordEffectWidth * (139f / 190f);
        float swordEffectX = Metrics.width * 0.5f - swordEffectWidth / 2; // 화면 중앙
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
        if (dragonMonster != null) {
            dragonMonster.update(dt);
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
            playerStats.resetTimerOnly(); // 폭탄 사용 후 타이머만 리셋
        }

        if (isBattlePhase) {
            if (isPlayerTurn) {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    if (lastSword >= lastMagic && lastSword >= lastHeal) {
                        player.playSwordAttack(() -> {
                            player.playSwordEffect(() -> {
                                // 드래곤은 물리+마법 합산 데미지
                                if (dragonMonster != null && dragonMonster.isAlive()) {
                                    int totalDamage = lastSword + lastMagic;
                                    dragonMonster.startBlinking(totalDamage);
                                    damageText.showDamage(totalDamage, dragonMonster.getX() + dragonMonster.getWidth()/2, dragonMonster.getY() + dragonMonster.getHeight()/2, false);
                                }
                                isMonsterBlinkPhase = true;
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else if (lastMagic >= lastSword && lastMagic >= lastHeal) {
                        player.playMagicAttack(() -> {
                            player.playMagicEffect(() -> {
                                // 드래곤은 물리+마법 합산 데미지
                                if (dragonMonster != null && dragonMonster.isAlive()) {
                                    int totalDamage = lastSword + lastMagic;
                                    dragonMonster.startBlinking(totalDamage);
                                    damageText.showDamage(totalDamage, dragonMonster.getX() + dragonMonster.getWidth()/2, dragonMonster.getY() + dragonMonster.getHeight()/2, false);
                                }
                                isMonsterBlinkPhase = true;
                                playerStats.heal(lastHeal);
                            });
                        });
                    } else {
                        player.playHeal(() -> {
                            // 드래곤은 물리+마법 합산 데미지
                            if (dragonMonster != null && dragonMonster.isAlive()) {
                                int totalDamage = lastSword + lastMagic;
                                dragonMonster.startBlinking(totalDamage);
                                damageText.showDamage(totalDamage, dragonMonster.getX() + dragonMonster.getWidth()/2, dragonMonster.getY() + dragonMonster.getHeight()/2, false);
                            }
                            isMonsterBlinkPhase = true;
                            playerStats.heal(lastHeal);
                        });
                    }
                }
            } else {
                if (!isWaitingForAnim) {
                    isWaitingForAnim = true;
                    
                    // 드래곤 공격
                    if (dragonMonster != null && dragonMonster.isAlive()) {
                        dragonMonster.attack(() -> {
                            float damage = dragonMonster.getAttackPower();
                            player.takeDamage(damage);
                            damageText.showDamage((int)damage, player.getX() + player.getWidth()/2, player.getY(), true);
                            if (!player.isAlive()) {
                                player.die();
                                isGameOver = true;
                                GameOverScene.getInstance().show();
                                return;
                            }
                            
                            // 공격 완료
                            isBattlePhase = false;
                            isPuzzleFrozen = false;
                            playerStats.reset();
                            isWaitingForAnim = false;
                        }, false); // 물리 공격으로 설정
                    } else {
                        // 드래곤이 죽은 경우
                        isBattlePhase = false;
                        isPuzzleFrozen = false;
                        playerStats.reset();
                        isWaitingForAnim = false;
                        // 로그라이크 창이 먼저 뜨도록 스테이지 클리어 창은 여기서 표시하지 않음
                    }
                }
            }
        }

        if (isMonsterBlinkPhase && (dragonMonster == null || !dragonMonster.isBlinking())) {
            isMonsterBlinkPhase = false;
            isPlayerTurn = false;
            isWaitingForAnim = false;
        }

        if (isGameOver) {
            return;
        }

        if (!isRoguelikeChoiceShown && !isStageClearShown && (dragonMonster == null || (!dragonMonster.isAlive() && !dragonMonster.isDying()))) {
            isRoguelikeChoiceShown = true;
            RoguelikeChoiceScene.getInstance(context).showBattleRogue(new RoguelikeChoiceScene.OnRoguelikeDoneListener() {
                @Override
                public void onRoguelikeDone(int puzzleChoice) {
                    // 로그라이크 선택 완료 후 스테이지 클리어 창 표시
                    if (!isStageClearShown) {
                        // 월드3 해금 및 3-1 스테이지 해금
                        StageManager.getInstance().unlockWorld(3);
                        StageManager.getInstance().unlockStage(3, 1);
                        
                        StageClearScene.getInstance(context).show(2, 3);
                        isStageClearShown = true;
                    }
                }
            });
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

        if (RoguelikeChoiceScene.getInstance(context).onTouchEvent(event)) {
            return true;
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
                // 주머니 사용 후 매치 처리를 위해 약간의 지연 후 체크
                new Thread(() -> {
                    try {
                        Thread.sleep(100); // 블록 떨어지는 시간 대기
                        while (blockGrid.isAnyBlockAnimating() || blockGrid.isAnyBlockFalling()) {
                            Thread.sleep(50);
                        }
                        // 매치가 있으면 처리
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
                    // 폭탄 블록 클릭 확인
                    if (blockGrid.getBlock(row, col) != null && blockGrid.getBlock(row, col).isBomb()) {
                        if (blockGrid.handleBombClick(row, col)) {
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
        if (dragonMonster != null) {
            dragonMonster.draw(canvas);
        }

        // 데미지 텍스트 그리기 (몬스터 바로 다음에)
        damageText.draw(canvas);
        
        if (isGameOver) {
            GameOverScene.getInstance().draw(canvas);
        }

        RoguelikeChoiceScene.getInstance(context).draw(canvas);
        StageClearScene.getInstance(context).draw(canvas);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        if (StageManager.getInstance().isStageCleared(2, 3)) {
            // 이미 스테이지가 클리어된 상태라면 클리어 창을 바로 표시
            StageClearScene.getInstance(context).show(2, 3);
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