// 스테이지 1-1의 구현. 게임 화면을 3개 영역(전투, 플레이어 정보, 퍼즐)으로 나누어 표시
// 각 영역은 화면 높이의 1/3씩 차지

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

        float firstDivider = Metrics.height / 3f;
        float secondDivider = 2 * Metrics.height / 3f;

        canvas.drawLine(0, firstDivider, Metrics.width, firstDivider, linePaint);
        canvas.drawLine(0, secondDivider, Metrics.width, secondDivider, linePaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("전투 공간", Metrics.width/2, firstDivider/2, textPaint);
        canvas.drawText("플레이어 정보", Metrics.width/2, (firstDivider + secondDivider)/2, textPaint);
        canvas.drawText("퍼즐 영역", Metrics.width/2, (secondDivider + Metrics.height)/2, textPaint);
    }
}
