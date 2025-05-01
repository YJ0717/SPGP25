// 스테이지 1-1의 구현. 게임 화면을 3개 영역으로 나누어 표시
// 퍼즐(55%, 하단), 전투(30%, 상단), 플레이어 정보(15%, 중단) 영역의 비율로 구성

package ac.tukorea.yunjun.pegglepang.PegglePang.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import ac.tukorea.yunjun.pegglepang.framework.view.Metrics;

public class S1_1 extends BaseStageScene {
    private Paint linePaint;

    public S1_1(Context context) {
        super(context, 1, 1);
        
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
    }

    @Override
    protected void setupStageSpecificElements() {
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.LTGRAY);

        // 화면을 55:15:30 비율로 나눔 -> 하단부터 퍼즐:정보:전투
        float puzzleStart = Metrics.height * 0.45f;     
        float playerInfoStart = Metrics.height * 0.30f;  
                                                        

        canvas.drawLine(0, puzzleStart, Metrics.width, puzzleStart, linePaint);
        canvas.drawLine(0, playerInfoStart, Metrics.width, playerInfoStart, linePaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // 각 영역의 중앙에 텍스트 배치
        canvas.drawText("전투 공간", Metrics.width/2, playerInfoStart/2, textPaint);
        canvas.drawText("플레이어 정보", Metrics.width/2, playerInfoStart + (puzzleStart - playerInfoStart)/2, textPaint);
        canvas.drawText("퍼즐 영역", Metrics.width/2, puzzleStart + (Metrics.height - puzzleStart)/2, textPaint);
    }
}
