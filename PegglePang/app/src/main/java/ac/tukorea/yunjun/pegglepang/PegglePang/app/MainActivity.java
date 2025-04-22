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
        setContentView(R.layout.activity_main);
    }

    public void onBtnStartGame(View view) {
        Intent intent = new Intent(this, PegglePangActivity.class);
        startActivity(intent);
    }
}