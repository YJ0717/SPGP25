package ac.tukorea.yunjun.pegglepang.samplegame.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import ac.tukorea.yunjun.pegglepang.R;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnStartGame(View view) {
        Intent intent = new Intent(this, SampleGameActivity.class);
        startActivity(intent);
    }
}