package cs1910.noiseout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class InProgressCancellation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_progress_cancellation);
    }

    public void transition(View view){
        Intent intent = new Intent(InProgressCancellation.this, Dashboard.class);

        startActivity(intent);
    }
}
