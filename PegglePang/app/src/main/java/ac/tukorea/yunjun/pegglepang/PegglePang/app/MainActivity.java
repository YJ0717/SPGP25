package ac.tukorea.yunjun.pegglepang.PegglePang.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import ac.tukorea.yunjun.pegglepang.R;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 시작 화면에 이미지 설정
        ImageView startScreenImage = findViewById(R.id.start_screen_image);
        startScreenImage.setImageResource(R.mipmap.startscreen); // mipmap 폴더에서 이미지 설정
    }

    public void onBtnStartGame(View view) {
        Intent intent = new Intent(this, PegglePangActivity.class);
        startActivity(intent);
    }
}