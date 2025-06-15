package ac.tukorea.yunjun.pegglepang.PegglePang.game.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import android.os.Handler;

import ac.tukorea.yunjun.pegglepang.PegglePang.game.player.PlayerStats;
import ac.tukorea.yunjun.pegglepang.PegglePang.game.audio.SoundEffectManager;
import ac.tukorea.yunjun.pegglepang.R;

public class BlockGrid {
    private static final int GRID_SIZE = 6;
    private Block[][] blocks;
    private Bitmap[] blockBitmaps;
    private Bitmap rockBitmap;
    private Bitmap bombBitmap;
    private Random random;
    private float puzzleLeft, puzzleTop, blockSize;
    private PlayerStats playerStats;
    private boolean isProcessingMatches = false;
    private Handler mainHandler;
    private int currentStage = 1;
    private int currentSubStage = 1;
    private int rockBottomRow = GRID_SIZE; // 가장 아래 rock이 있는 행 (6부터 시작)
    
    // 퍼즐 로그라이크 관련
    private boolean bombBlockEnabled = false;

    public void setPlayerStats(PlayerStats stats) {
        this.playerStats = stats;
    }

    public void setStageInfo(int stage, int subStage) {
        this.currentStage = stage;
        this.currentSubStage = subStage;
    }

    public void enableBombBlocks() {
        this.bombBlockEnabled = true;
    }

    public void disableBombBlocks() {
        this.bombBlockEnabled = false;
    }

    public BlockGrid(Context context) {
        blocks = new Block[GRID_SIZE][GRID_SIZE];
        random = new Random();
        mainHandler = new Handler(context.getMainLooper());
        
        blockBitmaps = new Bitmap[3];
        blockBitmaps[Block.HEAL] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.heal_block);
        blockBitmaps[Block.MAGIC] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_block);
        blockBitmaps[Block.SWORD] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sword_block);
        rockBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.rock);
        bombBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bomb);
        
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

    public boolean hasChainMatches() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE - 2; col++) {
                if (blocks[row][col] != null && !blocks[row][col].isRock() && !blocks[row][col].isBomb() && 
                    blocks[row][col + 1] != null && !blocks[row][col + 1].isRock() && !blocks[row][col + 1].isBomb() && 
                    blocks[row][col + 2] != null && !blocks[row][col + 2].isRock() && !blocks[row][col + 2].isBomb()) {
                    int type = blocks[row][col].getType();
                    if (blocks[row][col + 1].getType() == type && blocks[row][col + 2].getType() == type) {
                        return true;
                    }
                }
            }
        }
        
        for (int col = 0; col < GRID_SIZE; col++) {
            for (int row = 0; row < GRID_SIZE - 2; row++) {
                if (blocks[row][col] != null && !blocks[row][col].isRock() && !blocks[row][col].isBomb() && 
                    blocks[row + 1][col] != null && !blocks[row + 1][col].isRock() && !blocks[row + 1][col].isBomb() && 
                    blocks[row + 2][col] != null && !blocks[row + 2][col].isRock() && !blocks[row + 2][col].isBomb()) {
                    int type = blocks[row][col].getType();
                    if (blocks[row + 1][col].getType() == type && blocks[row + 2][col].getType() == type) {
                        return true;
                    }
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
        // rock 블록이나 bomb 블록은 스왑할 수 없음
        if ((blocks[row1][col1] != null && (blocks[row1][col1].isRock() || blocks[row1][col1].isBomb())) ||
            (blocks[row2][col2] != null && (blocks[row2][col2].isRock() || blocks[row2][col2].isBomb()))) {
            return;
        }

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
                    // 매치 실패 - 원래 위치로 되돌리기
                    blocks[row1][col1].startAnimation(left1, top1, false);
                    blocks[row2][col2].startAnimation(left2, top2, false);
                    
                    Block tempBlock = blocks[row1][col1];
                    blocks[row1][col1] = blocks[row2][col2];
                    blocks[row2][col2] = tempBlock;
                    
                    blocks[row1][col1].setGridPosition(row1, col1);
                    blocks[row2][col2].setGridPosition(row2, col2);
                    
                    // 스테이지 2-1부터 rock 추가
                    if (shouldAddRockOnFailure()) {
                        addRockFromBottom();
                    }
                } else {
                    processMatches();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private boolean shouldAddRockOnFailure() {
        return currentStage >= 2 && currentSubStage >= 1;
    }

    private void addRockFromBottom() {
        // 퍼즐 로그라이크: 돌블럭 생성 방지 효과 체크
        if (playerStats != null && playerStats.hasRockBlockPrevention()) {
            return;
        }
        
        if (rockBottomRow > 0) {
            rockBottomRow--;
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[rockBottomRow][col] != null) {
                    blocks[rockBottomRow][col].setRockBitmap(rockBitmap);
                    blocks[rockBottomRow][col].convertToRock();
                }
            }
        }
    }

    private boolean checkMatch(int row, int col) {
        if (blocks[row][col] == null || blocks[row][col].isRock() || blocks[row][col].isBomb()) return false;
        int type = blocks[row][col].getType();

        int horizontalCount = 1;
        for (int i = col - 1; i >= 0 && blocks[row][i] != null && !blocks[row][i].isRock() && blocks[row][i].getType() == type; i--) {
            horizontalCount++;
        }
        for (int i = col + 1; i < GRID_SIZE && blocks[row][i] != null && !blocks[row][i].isRock() && blocks[row][i].getType() == type; i++) {
            horizontalCount++;
        }

        int verticalCount = 1;
        for (int i = row - 1; i >= 0 && blocks[i][col] != null && !blocks[i][col].isRock() && blocks[i][col].getType() == type; i--) {
            verticalCount++;
        }
        for (int i = row + 1; i < GRID_SIZE && blocks[i][col] != null && !blocks[i][col].isRock() && blocks[i][col].getType() == type; i++) {
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
                if (blocks[row][col] != null && !blocks[row][col].isRock() && !blocks[row][col].isBomb() && 
                    blocks[row][col + 1] != null && !blocks[row][col + 1].isRock() && !blocks[row][col + 1].isBomb() && 
                    blocks[row][col + 2] != null && !blocks[row][col + 2].isRock() && !blocks[row][col + 2].isBomb()) {
                    int type = blocks[row][col].getType();
                    if (blocks[row][col + 1].getType() == type && blocks[row][col + 2].getType() == type) {
                        int matchLength = 3;
                        while (col + matchLength < GRID_SIZE && blocks[row][col + matchLength] != null && 
                               !blocks[row][col + matchLength].isRock() &&
                               !blocks[row][col + matchLength].isBomb() &&
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
                if (blocks[row][col] != null && !blocks[row][col].isRock() && !blocks[row][col].isBomb() && 
                    blocks[row + 1][col] != null && !blocks[row + 1][col].isRock() && !blocks[row + 1][col].isBomb() && 
                    blocks[row + 2][col] != null && !blocks[row + 2][col].isRock() && !blocks[row + 2][col].isBomb()) {
                    int type = blocks[row][col].getType();
                    if (blocks[row + 1][col].getType() == type && blocks[row + 2][col].getType() == type) {
                        int matchLength = 3;
                        while (row + matchLength < GRID_SIZE && blocks[row + matchLength][col] != null && 
                               !blocks[row + matchLength][col].isRock() &&
                               !blocks[row + matchLength][col].isBomb() &&
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

        if (hasMatches) {
            // 블록 터지는 효과음 즉시 재생
            SoundEffectManager.getInstance().playBlockBreakSound();
        }
        
        if (hasMatches && playerStats != null) {
            // 퍼즐 로그라이크: 칼블럭과 마법블럭 2배 효과 적용
            int finalSwordCount = playerStats.calculateSwordBlockScore(swordCount);
            int finalMagicCount = playerStats.calculateMagicBlockScore(magicCount);
            
            playerStats.addPhysicalAttack(finalSwordCount);
            playerStats.addMagicAttack(finalMagicCount);
            playerStats.addHealing(healCount);

            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (toRemove[row][col] && blocks[row][col] != null && !blocks[row][col].isRock()) {
                        blocks[row][col] = null;
                    }
                }
            }

            dropBlocks();
            fillNewBlocks(false); // 매치로 인한 생성이므로 폭탄 블록 생성 가능

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
                        mainHandler.post(() -> processMatches());
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
            
            // 아래부터 확인하면서 rock이 아닌 블록들만 수집 (원래 로직)
            for (int row = GRID_SIZE - 1; row >= 0; row--) {
                if (blocks[row][col] != null && !blocks[row][col].isRock()) {
                    validBlocks[validCount++] = blocks[row][col];
                    blocks[row][col] = null;
                }
            }
            
            // 아래부터 배치하되 rock 블록이 있는 자리는 건너뛰기
            for (int i = 0; i < validCount; i++) {
                // rock 블록이 있는 위치는 건너뛰기
                while (insertRow >= 0 && blocks[insertRow][col] != null && blocks[insertRow][col].isRock()) {
                    insertRow--;
                }
                
                if (insertRow >= 0) {
                    int targetRow = GRID_SIZE - 1 - i;
                    // rock 블록 때문에 더 위로 올라가야 하는 경우
                    if (insertRow < targetRow) {
                        targetRow = insertRow;
                    }
                    
                    blocks[targetRow][col] = validBlocks[i];
                    blocks[targetRow][col].setGridPosition(targetRow, col);
                    float targetY = puzzleTop + targetRow * blockSize;
                    validBlocks[i].startAnimation(puzzleLeft + col * blockSize, targetY, true);
                    insertRow = targetRow - 1;
                }
            }
        }
    }

    private void fillNewBlocks() {
        fillNewBlocks(false); // 기본적으로 일반 매치로 간주
    }

    private void fillNewBlocks(boolean isBombDestroy) {
        ArrayList<int[]> newBlockPositions = new ArrayList<>();
        
        for (int col = 0; col < GRID_SIZE; col++) {
            int emptyCount = 0;
            // 빈 공간 개수 세기 (원래 로직)
            for (int row = 0; row < GRID_SIZE; row++) {
                if (blocks[row][col] == null) {
                    emptyCount++;
                }
            }
            
            if (emptyCount > 0) {
                // 원래 로직대로 위에서부터 새 블록 생성
                for (int i = 0; i < emptyCount; i++) {
                    int targetRow = emptyCount - 1 - i;
                    int type;
                    do {
                        type = random.nextInt(3);
                        // 아래쪽 블록과 다른 타입으로 생성 (단순화)
                        if (targetRow < GRID_SIZE - 1 && blocks[targetRow + 1][col] != null &&
                            blocks[targetRow + 1][col].getType() == type) {
                            continue;
                        }
                        // 왼쪽 블록과 다른 타입으로 생성 (단순화)
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
                    
                    // 새로 생성된 블록 위치 저장
                    newBlockPositions.add(new int[]{targetRow, col});
            }
        }
    }
        
        // 매치로 인한 생성이고 폭탄 블록이 활성화된 경우, 새로 생성된 블록 중 하나를 폭탄 블록으로 변경
        if (!isBombDestroy && bombBlockEnabled && currentStage >= 2 && currentSubStage >= 1 && 
            !newBlockPositions.isEmpty() && random.nextInt(100) < 50) {
            
            int randomIndex = random.nextInt(newBlockPositions.size());
            int[] pos = newBlockPositions.get(randomIndex);
            int row = pos[0];
            int col = pos[1];
            
            // 기존 블록의 애니메이션 정보 저장
            Block originalBlock = blocks[row][col];
            float currentX = originalBlock.getCurrentX();
            float currentY = originalBlock.getCurrentY();
            float targetX = originalBlock.getTargetX();
            float targetY = originalBlock.getTargetY();
            boolean isAnimating = originalBlock.isAnimating();
            boolean isFalling = originalBlock.isFalling();
            
            // 기존 블록을 폭탄 블록으로 변경
            blocks[row][col] = new Block(Block.BOMB, blockBitmaps[0]); // 임시 비트맵
            blocks[row][col].setBombBitmap(bombBitmap);
            blocks[row][col].convertToBomb();
            blocks[row][col].setGridPosition(row, col);
            
            // 기존 애니메이션 상태 복원
            blocks[row][col].setPosition(currentX, currentY, currentX + blockSize, currentY + blockSize);
            if (isAnimating) {
                blocks[row][col].startAnimation(targetX, targetY, isFalling);
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

    // 폭탄 블록 클릭 시 해당 행과 열의 모든 블록 제거
    public boolean handleBombClick(int row, int col) {
        if (blocks[row][col] != null && blocks[row][col].isBomb()) {
            // 폭탄 터지는 효과음 재생
            SoundEffectManager.getInstance().playBlockBreakSound();
            
            int swordCount = 0;
            int magicCount = 0;
            int healCount = 0;
            
            // 같은 행의 모든 블록 제거하면서 스텟 계산
            for (int c = 0; c < GRID_SIZE; c++) {
                if (blocks[row][c] != null && !blocks[row][c].isRock() && !blocks[row][c].isBomb()) {
                    int type = blocks[row][c].getType();
                    switch(type) {
                        case Block.SWORD: swordCount++; break;
                        case Block.MAGIC: magicCount++; break;
                        case Block.HEAL: healCount++; break;
                    }
                }
                if (blocks[row][c] != null) {
                    blocks[row][c] = null;
                }
            }
            
            // 같은 열의 모든 블록 제거하면서 스텟 계산 (중복 제거를 위해 폭탄 위치 제외)
            for (int r = 0; r < GRID_SIZE; r++) {
                if (r != row && blocks[r][col] != null && !blocks[r][col].isRock() && !blocks[r][col].isBomb()) {
                    int type = blocks[r][col].getType();
                    switch(type) {
                        case Block.SWORD: swordCount++; break;
                        case Block.MAGIC: magicCount++; break;
                        case Block.HEAL: healCount++; break;
                    }
                }
                if (r != row && blocks[r][col] != null) {
                    blocks[r][col] = null;
                }
            }
            
            // 플레이어 스텟에 추가
            if (playerStats != null) {
                playerStats.addPhysicalAttack(swordCount);
                playerStats.addMagicAttack(magicCount);
                playerStats.addHealing(healCount);
            }
            
            // 블록들 떨어뜨리고 새로운 블록 생성 (폭탄으로 인한 생성이므로 새로운 폭탄 블록 생성 안함)
            dropBlocks();
            fillNewBlocks(true);
            
            // 연쇄 매칭 확인을 위한 Thread 추가
            new Thread(() -> {
                try {
                    while (isAnyBlockAnimating() || isAnyBlockFalling()) {
                        Thread.sleep(50);
                    }
                    boolean hasMoreMatches = false;
                    for (int r = 0; r < GRID_SIZE; r++) {
                        for (int c = 0; c < GRID_SIZE; c++) {
                            if (blocks[r][c] != null && checkMatch(r, c)) {
                                hasMoreMatches = true;
                                break;
                            }
                        }
                        if (hasMoreMatches) break;
                    }
                    if (hasMoreMatches) {
                        mainHandler.post(() -> processMatches());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
            return true; // 폭탄이 터졌음을 알림
        }
        return false; // 폭탄이 아니거나 없음
    }

    // 무작위 블록 5개 터뜨리기 (로그라이크 기능)
    public void destroyRandomBlocks(int count) {
        // 무작위 블록 터지는 효과음 재생
        SoundEffectManager.getInstance().playBlockBreakSound();
        
        ArrayList<int[]> availableBlocks = new ArrayList<>();
        
        // 터뜨릴 수 있는 블록 찾기 (rock과 bomb 제외)
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (blocks[row][col] != null && !blocks[row][col].isRock() && !blocks[row][col].isBomb()) {
                    availableBlocks.add(new int[]{row, col});
                }
            }
        }
        
        int swordCount = 0;
        int magicCount = 0;
        int healCount = 0;
        
        // 무작위로 count개 선택해서 제거하면서 스텟 계산
        for (int i = 0; i < Math.min(count, availableBlocks.size()); i++) {
            int randomIndex = random.nextInt(availableBlocks.size());
            int[] pos = availableBlocks.remove(randomIndex);
            
            // 터트리기 전에 블록 타입 확인하여 스텟 계산
            int type = blocks[pos[0]][pos[1]].getType();
            switch(type) {
                case Block.SWORD: swordCount++; break;
                case Block.MAGIC: magicCount++; break;
                case Block.HEAL: healCount++; break;
            }
            
            blocks[pos[0]][pos[1]] = null;
        }
        
        // 플레이어 스텟에 추가
        if (playerStats != null) {
            playerStats.addPhysicalAttack(swordCount);
            playerStats.addMagicAttack(magicCount);
            playerStats.addHealing(healCount);
        }
        
        // 블록들 떨어뜨리고 새로운 블록 생성 (로그라이크 기능이므로 폭탄 블록 생성 가능)
        dropBlocks();
        fillNewBlocks(false);
        
        // 연쇄 매칭 확인을 위한 Thread 추가
        new Thread(() -> {
            try {
                while (isAnyBlockAnimating() || isAnyBlockFalling()) {
                    Thread.sleep(50);
                }
                boolean hasMoreMatches = false;
                for (int r = 0; r < GRID_SIZE; r++) {
                    for (int c = 0; c < GRID_SIZE; c++) {
                        if (blocks[r][c] != null && checkMatch(r, c)) {
                            hasMoreMatches = true;
                            break;
                        }
                    }
                    if (hasMoreMatches) break;
                }
                if (hasMoreMatches) {
                    mainHandler.post(() -> processMatches());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // 외부에서 매치 처리를 호출할 수 있는 public 메서드
    public void forceProcessMatches() {
        processMatches();
    }
} 