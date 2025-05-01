// 스테이지 1-1의 구현. 게임 화면을 3개 영역으로 나누어 표시
// 퍼즐(55%, 하단), 전투(30%, 상단), 플레이어 정보(15%, 중단) 영역의 비율로 구성

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.content.Context;
import java.util.Random;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;
import ac.tukorea.yunjun.pegglepang.R;

public class S1_1 extends BaseStageScene {
    private static final int GRID_SIZE = 6;
    private Paint linePaint;
    private Bitmap gridBitmap;
    private RectF gridRect;
    private Block[][] blocks;
    private Bitmap[] blockBitmaps;
    private Random random;

    public S1_1(Context context) {
        super(context, 1, 1);
        
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);

        gridBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.grid);
        gridRect = new RectF();
        
        // 블록 이미지 로드
        blockBitmaps = new Bitmap[3];
        blockBitmaps[Block.HEAL] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.heal_block);
        blockBitmaps[Block.MAGIC] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.magic_block);
        blockBitmaps[Block.SWORD] = BitmapFactory.decodeResource(context.getResources(), R.mipmap.sword_block);
        
        blocks = new Block[GRID_SIZE][GRID_SIZE];
        random = new Random();
        initializeBlocks();
    }

    private void initializeBlocks() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int type = random.nextInt(3);
                blocks[row][col] = new Block(type, blockBitmaps[type]);
            }
        }
    }

    @Override
    protected void setupStageSpecificElements() {
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
        
        float puzzleLeft = (Metrics.width - puzzleSize) / 2;
        float puzzleTop = puzzleStart + (puzzleAreaHeight - puzzleSize) / 2;
        
        gridRect.set(puzzleLeft, puzzleTop, puzzleLeft + puzzleSize, puzzleTop + puzzleSize);
        canvas.drawBitmap(gridBitmap, null, gridRect, null);

        // 블록 그리기
        float blockSize = puzzleSize / GRID_SIZE;
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

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("전투 공간", Metrics.width/2, playerInfoStart/2, textPaint);
        canvas.drawText("플레이어 정보", Metrics.width/2, playerInfoStart + (puzzleStart - playerInfoStart)/2, textPaint);
    }
}
