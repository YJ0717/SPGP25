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

public class S1_1 extends BaseStageScene {

    private static final int GRID_SIZE = 6;              
    private static final float SWIPE_THRESHOLD = 20.0f;   
    
    private Paint linePaint;      
    private Bitmap gridBitmap;    
    private RectF gridRect;       
    private BlockGrid blockGrid;
    private PlayerStats playerStats;
    private boolean isPuzzleFrozen;
    
    private float blockSize;      
    private float puzzleLeft;     
    private float puzzleTop;      
    
    private Block selectedBlock;  
    private int selectedRow = -1; 
    private int selectedCol = -1; 
    private float touchStartX;    
    private float touchStartY;    

    public S1_1(Context context) {
        super(context, 1, 1);
        
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);

        gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.grid);
        gridRect = new RectF();
        
        playerStats = new PlayerStats();
        blockGrid = new BlockGrid(context);
        blockGrid.setPlayerStats(playerStats);
        isPuzzleFrozen = false;
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    @Override
    public void update() {
        super.update();
        blockGrid.update(0.016f);
        
        if (playerStats.isGameOver() && !isPuzzleFrozen) {
            isPuzzleFrozen = true;
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
        canvas.drawColor(Color.LTGRAY);

        float puzzleStart = Metrics.height * 0.45f;
        float playerInfoStart = Metrics.height * 0.30f;
        
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
    }

    public void startNewPuzzlePhase() {
        isPuzzleFrozen = false;
        playerStats.reset();
        blockGrid.reset();
    }
}
