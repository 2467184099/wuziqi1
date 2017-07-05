package jiudianlianxian.wuziqi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private WuZiQiPanel wuZiQiPanel;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wuZiQiPanel = (WuZiQiPanel) findViewById(R.id.activity_main_wuziqi);
        button = (Button) findViewById(R.id.activity_main_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wuZiQiPanel.start();
            }
        });
    }


}
