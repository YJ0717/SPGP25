// 앱의 시작점. 게임 시작 버튼을 통해 PegglePangActivity로 전환하는 메인 화면을 관리

package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import ac.tukorea.yunjun.pegglepang.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //activity main레이아웃으로 설정
    }

    public void onBtnStartGame(View view) {
        Intent intent = new Intent(this, PegglePangActivity.class);
        startActivity(intent);
    }
}