package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

import ac.tukorea.yunjun.pegglepang.R;

public class BlockGrid {
    private static final int GRID_SIZE = 6;
    private Block[][] blocks;
    private Bitmap[] blockBitmaps;
    private Random random;
    private float puzzleLeft, puzzleTop, blockSize;
    private PlayerStats playerStats;

    public void setPlayerStats(PlayerStats stats) {
        this.playerStats = stats;
    }

    public BlockGrid(Context context) {
        blocks = new Block[GRID_SIZE][GRID_SIZE];
        random = new Random();
        
        blockBitmaps = new Bitmap[3];
        blockBitmaps[Block.HEAL] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.heal_block);
        blockBitmaps[Block.MAGIC] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_block);
        blockBitmaps[Block.SWORD] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sword_block);
        
        initializeBlocks();
    }

    public void setGridMetrics(float left, float top, float size) {
        this.puzzleLeft = left;
        this.puzzleTop = top;
        this.blockSize = size / GRID_SIZE;
        updateBlockPositions();
    }

    private void updateBlockPositions() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Block block = blocks[row][col];
                if (block != null) {
                    float left = puzzleLeft + col * blockSize;
                    float top = puzzleTop + row * blockSize;
                    block.setPosition(left, top, left + blockSize, top + blockSize);
                }
            }
        }
    }

    private void initializeBlocks() {
        do {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    int type = random.nextInt(3);
                    blocks[row][col] = new Block(type, blockBitmaps[type]);
                    blocks[row][col].setGridPosition(row, col);
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

    public boolean isAnyBlockAnimating() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null && blocks[row][col].isAnimating()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAnyBlockFalling() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null && blocks[row][col].isFalling()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getSmartRandomType(int row, int col) {
        ArrayList<Integer> availableTypes = new ArrayList<>(Arrays.asList(0, 1, 2));
        
        if (row >= 1) {
            if (blocks[row-1][col] != null) {
                availableTypes.remove(Integer.valueOf(blocks[row-1][col].getType()));
            }
        }
        
        if (col >= 1) {
            if (blocks[row][col-1] != null) {
                availableTypes.remove(Integer.valueOf(blocks[row][col-1].getType()));
            }
        }
        
        if (availableTypes.isEmpty()) {
            return random.nextInt(3);
        } else {
            return availableTypes.get(random.nextInt(availableTypes.size()));
        }
    }

    public void swapBlocks(int row1, int col1, int row2, int col2) {
        float left1 = puzzleLeft + col1 * blockSize;
        float top1 = puzzleTop + row1 * blockSize;
        float left2 = puzzleLeft + col2 * blockSize;
        float top2 = puzzleTop + row2 * blockSize;
        
        blocks[row1][col1].startAnimation(left2, top2, false);
        blocks[row2][col2].startAnimation(left1, top1, false);
        
        Block temp = blocks[row1][col1];
        blocks[row1][col1] = blocks[row2][col2];
        blocks[row2][col2] = temp;

        blocks[row1][col1].setGridPosition(row1, col1);
        blocks[row2][col2].setGridPosition(row2, col2);

        new Thread(() -> {
            try {
                while (isAnyBlockAnimating()) {
                    Thread.sleep(50);
                }
                
                if (!checkMatch(row1, col1) && !checkMatch(row2, col2)) {
                    blocks[row1][col1].startAnimation(left1, top1, false);
                    blocks[row2][col2].startAnimation(left2, top2, false);
                    
                    Block tempBlock = blocks[row1][col1];
                    blocks[row1][col1] = blocks[row2][col2];
                    blocks[row2][col2] = tempBlock;
                    
                    blocks[row1][col1].setGridPosition(row1, col1);
                    blocks[row2][col2].setGridPosition(row2, col2);
                } else {
                    processMatches();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean checkMatch(int row, int col) {
        if (blocks[row][col] == null) return false;
        int type = blocks[row][col].getType();

        int horizontalCount = 1;
        for (int i = col - 1; i >= 0 && blocks[row][i] != null && blocks[row][i].getType() == type; i--) {
            horizontalCount++;
        }
        for (int i = col + 1; i < GRID_SIZE && blocks[row][i] != null && blocks[row][i].getType() == type; i++) {
            horizontalCount++;
        }

        int verticalCount = 1;
        for (int i = row - 1; i >= 0 && blocks[i][col] != null && blocks[i][col].getType() == type; i--) {
            verticalCount++;
        }
        for (int i = row + 1; i < GRID_SIZE && blocks[i][col] != null && blocks[i][col].getType() == type; i++) {
            verticalCount++;
        }

        return horizontalCount >= 3 || verticalCount >= 3;
    }

    private void processMatches() {
        boolean[][] toRemove = new boolean[GRID_SIZE][GRID_SIZE];
        boolean hasMatches = false;
        int swordCount = 0;
        int magicCount = 0;
        int healCount = 0;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE - 2; col++) {
                if (blocks[row][col] != null && blocks[row][col + 1] != null && blocks[row][col + 2] != null) {
                    int type = blocks[row][col].getType();
                    if (blocks[row][col + 1].getType() == type && blocks[row][col + 2].getType() == type) {
                        int matchLength = 3;
                        while (col + matchLength < GRID_SIZE && blocks[row][col + matchLength] != null && 
                               blocks[row][col + matchLength].getType() == type) {
                            matchLength++;
                        }
                        for (int i = 0; i < matchLength; i++) {
                            toRemove[row][col + i] = true;
                        }
                        switch(type) {
                            case Block.SWORD: swordCount += matchLength; break;
                            case Block.MAGIC: magicCount += matchLength; break;
                            case Block.HEAL: healCount += matchLength; break;
                        }
                        hasMatches = true;
                        col += matchLength - 1;
                    }
                }
            }
        }

        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 0; row < GRID_SIZE - 2; row++) {
                if (blocks[row][col] != null && blocks[row + 1][col] != null && blocks[row + 2][col] != null) {
                    int type = blocks[row][col].getType();
                    if (blocks[row + 1][col].getType() == type && blocks[row + 2][col].getType() == type) {
                        int matchLength = 3;
                        while (row + matchLength < GRID_SIZE && blocks[row + matchLength][col] != null && 
                               blocks[row + matchLength][col].getType() == type) {
                            matchLength++;
                        }
                        for (int i = 0; i < matchLength; i++) {
                            toRemove[row + i][col] = true;
                        }
                        switch(type) {
                            case Block.SWORD: swordCount += matchLength; break;
                            case Block.MAGIC: magicCount += matchLength; break;
                            case Block.HEAL: healCount += matchLength; break;
                        }
                        hasMatches = true;
                        row += matchLength - 1;
                    }
                }
            }
        }

        if (hasMatches && playerStats != null) {
            playerStats.addPhysicalAttack(swordCount);
            playerStats.addMagicAttack(magicCount);
            playerStats.addHealing(healCount);

            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (toRemove[row][col]) {
                        blocks[row][col] = null;
                    }
                }
            }

            dropBlocks();
            fillNewBlocks();

            new Thread(() -> {
                try {
                    while (isAnyBlockAnimating() || isAnyBlockFalling()) {
                        Thread.sleep(50);
                    }
                    boolean hasMoreMatches = false;
                    for (int row = 0; row < GRID_SIZE; row++) {
                        for (int col = 0; col < GRID_SIZE; col++) {
                            if (blocks[row][col] != null && checkMatch(row, col)) {
                                hasMoreMatches = true;
                                break;
                            }
                        }
                        if (hasMoreMatches) break;
                    }
                    if (hasMoreMatches) {
                        processMatches();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
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
                blocks[targetRow][col].setGridPosition(targetRow, col);
                float targetY = puzzleTop + targetRow * blockSize;
                validBlocks[i].startAnimation(puzzleLeft + col * blockSize, targetY, true);
            }
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
                for (int i = 0; i < emptyCount; i++) {
                    int targetRow = emptyCount - 1 - i;
                    int type;
                    do {
                        type = random.nextInt(3);
                        if (targetRow < GRID_SIZE - 1 && blocks[targetRow + 1][col] != null &&
                            blocks[targetRow + 1][col].getType() == type) {
                            continue;
                        }
                        if (col > 0 && blocks[targetRow][col - 1] != null &&
                            blocks[targetRow][col - 1].getType() == type) {
                            continue;
                        }
                        break;
                    } while (true);
                    blocks[targetRow][col] = new Block(type, blockBitmaps[type]);
                    blocks[targetRow][col].setGridPosition(targetRow, col);
                    float startY = puzzleTop - (i + 1) * blockSize;
                    float targetY = puzzleTop + targetRow * blockSize;
                    blocks[targetRow][col].setPosition(puzzleLeft + col * blockSize, startY, 
                                                     puzzleLeft + col * blockSize + blockSize, startY + blockSize);
                    blocks[targetRow][col].startAnimation(puzzleLeft + col * blockSize, targetY, true);
                }
            }
        }
    }

    public void update(float deltaTime) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null) {
                    blocks[row][col].update(deltaTime);
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null) {
                    blocks[row][col].draw(canvas);
                }
            }
        }
    }

    public Block getBlock(int row, int col) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            return blocks[row][col];
        }
        return null;
    }

    public static int getGridSize() {
        return GRID_SIZE;
    }

    public boolean isGameOver() {
        return playerStats.isGameOver();
    }

    public void reset() {
        playerStats.reset();
        initializeBlocks();
    }
} 