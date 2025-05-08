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
    private static final float SWIPE_THRESHOLD = 20.0f;   //20px이상 밀었을 때 스와이프 처리 
    
    private Paint linePaint;      
    private Bitmap gridBitmap;    
    private RectF gridRect;       
    private Block[][] blocks;     
    private Bitmap[] blockBitmaps;
    private Random random;        
    
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
        
        blockBitmaps = new Bitmap[3];
        blockBitmaps[Block.HEAL] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.heal_block);
        blockBitmaps[Block.MAGIC] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_block);
        blockBitmaps[Block.SWORD] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sword_block);
        
        blocks = new Block[GRID_SIZE][GRID_SIZE];
        random = new Random();
        initializeBlocks();
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    private void initializeBlocks() {
        do {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    int type = random.nextInt(3);
                    blocks[row][col] = new Block(type, blockBitmaps[type]);
                }
            }
        } while (hasInitialMatches()); 
    }

    private boolean hasInitialMatches() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE - 2; col++) {
                if (blocks[row][col].getType() == blocks[row][col + 1].getType() &&
                    blocks[row][col].getType() == blocks[row][col + 2].getType()) {
                    return true;
                }
            }
        }
        
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 0; row < GRID_SIZE - 2; row++) {
                if (blocks[row][col].getType() == blocks[row + 1][col].getType() &&
                    blocks[row][col].getType() == blocks[row + 2][col].getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAnyBlockAnimating() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null && blocks[row][col].isAnimating()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processMatches() {
        new Thread(() -> {
            try {
                while (isAnyBlockAnimating()) {
                    Thread.sleep(50);
                }
                
                while (checkAndRemoveMatches()) {
                    while (isAnyBlockAnimating()) {
                        Thread.sleep(50);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean checkAndRemoveMatches() {
        boolean[][] toRemove = new boolean[GRID_SIZE][GRID_SIZE];
        boolean hasMatches = false;
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE - 2; col++) {
                if (blocks[row][col] != null) {
                    int matchLength = 1;
                    while (col + matchLength < GRID_SIZE && 
                           blocks[row][col + matchLength] != null &&
                           blocks[row][col].getType() == blocks[row][col + matchLength].getType()) {
                        matchLength++;
                    }
                    
                    if (matchLength >= 3) {
                        hasMatches = true;
                        for (int i = 0; i < matchLength; i++) {
                            toRemove[row][col + i] = true;
                        }
                    }
                }
            }
        }
        
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 0; row < GRID_SIZE - 2; row++) {
                if (blocks[row][col] != null) {
                    int matchLength = 1;
                    while (row + matchLength < GRID_SIZE && 
                           blocks[row + matchLength][col] != null &&
                           blocks[row][col].getType() == blocks[row + matchLength][col].getType()) {
                        matchLength++;
                    }
                    
                    if (matchLength >= 3) {
                        hasMatches = true;
                        for (int i = 0; i < matchLength; i++) {
                            toRemove[row + i][col] = true;
                        }
                    }
                }
            }
        }
        
        if (hasMatches) {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (toRemove[row][col]) {
                        blocks[row][col] = null;
                    }
                }
            }
            dropBlocks();
            fillNewBlocks();
        }
        
        return hasMatches;
    }

    private void dropBlocks() {
        for (int col = 0; col < GRID_SIZE; col++) {
            int insertRow = GRID_SIZE - 1;
            Block[] validBlocks = new Block[GRID_SIZE];
            int validCount = 0;
            
            for (int row = GRID_SIZE - 1; row >= 0; row--) {
                if (blocks[row][col] != null) {
                    validBlocks[validCount++] = blocks[row][col];
                    blocks[row][col] = null;
                }
            }
            
            for (int i = 0; i < validCount; i++) {
                int targetRow = GRID_SIZE - 1 - i;
                blocks[targetRow][col] = validBlocks[i];
                float targetY = puzzleTop + targetRow * blockSize;
                validBlocks[i].startAnimation(puzzleLeft + col * blockSize, targetY, true);
            }
        }
    }

    private int getSmartRandomType(int row, int col) {
        ArrayList<Integer> availableTypes = new ArrayList<>(Arrays.asList(0, 1, 2));
        
        if (row >= 2) {
            if (blocks[row-1][col] != null && blocks[row-2][col] != null &&
                blocks[row-1][col].getType() == blocks[row-2][col].getType()) {
                availableTypes.remove(Integer.valueOf(blocks[row-1][col].getType()));
            }
        }
        
        if (col >= 2) {
            if (blocks[row][col-1] != null && blocks[row][col-2] != null &&
                blocks[row][col-1].getType() == blocks[row][col-2].getType()) {
                availableTypes.remove(Integer.valueOf(blocks[row][col-1].getType()));
            }
        }
        
        if (availableTypes.isEmpty()) {
            return random.nextInt(3);
        } else {
            return availableTypes.get(random.nextInt(availableTypes.size()));
        }
    }

    private void fillNewBlocks() {
        for (int col = 0; col < GRID_SIZE; col++) {
            int emptyCount = 0;
            for (int row = 0; row < GRID_SIZE; row++) {
                if (blocks[row][col] == null) {
                    emptyCount++;
                }
            }
            
            if (emptyCount > 0) {
                Block[] tempBlocks = new Block[GRID_SIZE];
                int validCount = 0;
                
                for (int row = GRID_SIZE - 1; row >= 0; row--) {
                    if (blocks[row][col] != null) {
                        tempBlocks[validCount++] = blocks[row][col];
                        blocks[row][col] = null;
                    }
                }
                
                for (int i = 0; i < validCount; i++) {
                    int targetRow = GRID_SIZE - 1 - i;
                    blocks[targetRow][col] = tempBlocks[i];
                    float targetY = puzzleTop + targetRow * blockSize;
                    tempBlocks[i].startAnimation(puzzleLeft + col * blockSize, targetY, true);
                }
                
                for (int i = 0; i < emptyCount; i++) {
                    int targetRow = emptyCount - 1 - i;
                    int type = getSmartRandomType(targetRow, col);
                    blocks[targetRow][col] = new Block(type, blockBitmaps[type]);
                    
                    float startY = puzzleTop - (i + 1) * blockSize;
                    float targetY = puzzleTop + targetRow * blockSize;
                    
                    blocks[targetRow][col].setPosition(puzzleLeft + col * blockSize, startY, 
                                                     puzzleLeft + col * blockSize + blockSize, startY + blockSize);
                    blocks[targetRow][col].startAnimation(puzzleLeft + col * blockSize, targetY, true);
                }
            }
        }
    }

    private void swapBlocks(int row1, int col1, int row2, int col2) {
        float left1 = puzzleLeft + col1 * blockSize;
        float top1 = puzzleTop + row1 * blockSize;
        float left2 = puzzleLeft + col2 * blockSize;
        float top2 = puzzleTop + row2 * blockSize;
        
        blocks[row1][col1].startAnimation(left2, top2, false);
        blocks[row2][col2].startAnimation(left1, top1, false);
        
        Block temp = blocks[row1][col1];
        blocks[row1][col1] = blocks[row2][col2];
        blocks[row2][col2] = temp;
    }

    @Override
    public void update() {
        super.update();
        
        boolean isAnyBlockAnimating = false;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null) {
                    blocks[row][col].update(0.016f); 
                    if (blocks[row][col].isAnimating()) {
                        isAnyBlockAnimating = true;
                    }
                }
            }
        }
    }

    private void swapBlocksWithAnimation(int row1, int col1, int row2, int col2) {
        float left1 = puzzleLeft + col1 * blockSize;
        float top1 = puzzleTop + row1 * blockSize;
        float left2 = puzzleLeft + col2 * blockSize;
        float top2 = puzzleTop + row2 * blockSize;
        
        blocks[row1][col1].startAnimation(left2, top2, false);
        blocks[row2][col2].startAnimation(left1, top1, false);
        
        Block temp = blocks[row1][col1];
        blocks[row1][col1] = blocks[row2][col2];
        blocks[row2][col2] = temp;

        new Thread(() -> {
            try {
                while (isAnyBlockAnimating()) {
                    Thread.sleep(50);
                }
                
                if (!checkAndRemoveMatches()) {
                    blocks[row1][col1].startAnimation(left1, top1, false);
                    blocks[row2][col2].startAnimation(left2, top2, false);
                    
                    Block tempBlock = blocks[row1][col1];
                    blocks[row1][col1] = blocks[row2][col2];
                    blocks[row2][col2] = tempBlock;
                } else {
                    processMatches();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float[] touchPoint = Metrics.fromScreen(event.getX(), event.getY());
        float x = touchPoint[0];
        float y = touchPoint[1];
        
        int col = (int)((x - puzzleLeft) / blockSize);
        int row = (int)((y - puzzleTop) / blockSize);
        
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:  
                    touchStartX = x;
                    touchStartY = y;
                    selectedBlock = blocks[row][col];
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
                            
                            if (newRow >= 0 && newRow < GRID_SIZE && newCol >= 0 && newCol < GRID_SIZE) {
                                swapBlocksWithAnimation(selectedRow, selectedCol, newRow, newCol);
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

        blockSize = puzzleSize / GRID_SIZE;
        
        canvas.save();
        canvas.clipRect(puzzleLeft, puzzleTop, puzzleLeft + puzzleSize, puzzleTop + puzzleSize);
        
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null) {
                    float left = puzzleLeft + col * blockSize;
                    float top = puzzleTop + row * blockSize;
                    blocks[row][col].setPosition(left, top, left + blockSize, top + blockSize);
                    blocks[row][col].draw(canvas);
                }
            }
        }
        
        canvas.restore();

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("전투 공간", Metrics.width/2, playerInfoStart/2, textPaint);
        canvas.drawText("플레이어 정보", Metrics.width/2, playerInfoStart + (puzzleStart - playerInfoStart)/2, textPaint);
    }
}
