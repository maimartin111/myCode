package cs1910.noiseout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Registration extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //getSupportActionBar().hide();
        populateGenderSpinner();
    }

    public void populateGenderSpinner() {
        Spinner gender_spinner = findViewById(R.id.gender);
//        Spinner gender_spinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(adapter);
    }

    public void sendDemographics(View view){
        Intent intent = new Intent(Registration.this, Evaluation.class);

        startActivity(intent);
    }
}
