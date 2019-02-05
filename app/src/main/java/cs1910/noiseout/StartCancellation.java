package cs1910.noiseout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartCancellation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_cancellation);
    }

    public void startInProgress(View view)
    {
        Intent intent = new Intent(StartCancellation.this, InProgressCancellation.class);

        startActivity(intent);
    }
}
