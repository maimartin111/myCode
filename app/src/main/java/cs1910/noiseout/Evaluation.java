package cs1910.noiseout;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Evaluation extends AppCompatActivity {

    //Variables to connect with front end elements
    ToggleButton start;
    ProgressBar bar;
    TextView counter;
    MyCountDownTimer timer;
    TextView instruction;

    TextView screenTitle;

    TextView output;
    RequestQueue queue;

    //Time is handled in milliseconds
    long countdownTime = 300000;  //5 min * 60 seconds * 1000
    long decrement = 1000;  //1 second * 1000


    long minutes = (countdownTime / 1000) / 60;
    long seconds = (countdownTime / 1000) % 60;


    //Use for recording audio
    MediaRecorder mediaRecorder;

    public static final int RequestPermissionCode = 1;

    //String to store file in phone storage
    String AudioSavePathInDevice = null;


    public void SkipScreen(View view)
    {
        //Switching to a new screen
        Intent intent = new Intent(Evaluation.this, StartCancellation.class);

        startActivity(intent);
    }


    public void MediaRecorderReady(){

        //New mediarecorder
        mediaRecorder=new MediaRecorder();

        //Set the source of the recording - VOICE_COMMUNICATION works much better than MIC
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);

        //Format and Encoder (followed from tutorial)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }



    //Copied from tutorial
    private void requestPermission() {
        ActivityCompat.requestPermissions(Evaluation.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }




    //Copied from tutorial
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(Evaluation.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Evaluation.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }



    //Copied from tutorial
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);


        //Connect variables to elements from activity_main.xml
        start = findViewById(R.id.evaluate_button);
        bar = findViewById(R.id.progress_bar);
        counter = findViewById(R.id.timer_text);
        instruction = findViewById(R.id.start_stop_text);
        screenTitle = findViewById(R.id.cancelling_text);

        output = findViewById(R.id.textView);
        output.setText("Display of REST API response");

        //Button starts out as not checked
        start.setChecked(false);

        //TextView element is set to display 5:00
        //The "seconds" part is padded with zeros to always be two digits
        counter.setText(String.valueOf(minutes) + ":" + String.format("%02d", seconds));

        bar.setMax((int) countdownTime / 1000);
        bar.setProgress(0);


        //Checks the state of the button
        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //The button starts out as not checked
                //When it is clicked - set the progress bar to 0
                //                   - create and start a new timer
                //                   - attempt to record audio
                if (isChecked) {


                    if(checkPermission()) {

                        //Create the full "filename" for saving the recording
                        AudioSavePathInDevice =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" +
                                        "AudioRecording.3gp";

                        //Call setup method for mediarecorder
                        MediaRecorderReady();



                        //Prepare mediarecorder
                        try {
                            mediaRecorder.prepare();

                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Start recording audio

                        mediaRecorder.start();

                        //On screen notification that disappears after a little while
                        //Can be removed, but useful for debugging
                        Toast.makeText(Evaluation.this, "Recording started",
                                Toast.LENGTH_LONG).show();

                        instruction.setText("Stop");
                        screenTitle.setText("Evaluating Background Noise\n");
                    } else {
                        requestPermission();
                    }



                    bar.setProgress(0);
                    timer = new MyCountDownTimer(countdownTime, decrement);
                    timer.start();

                    //When the user clicks the button again, everything is reset
                } else {

                    //Stop recording audio and release the resource since
                    //a new one will be created when starting over
                    mediaRecorder.stop();
                    mediaRecorder.release();

                    //Stop timer
                    timer.cancel();

                    //Reset the progress bar
                    bar.setProgress(0);

                    //Reset the TextView element to display 5:00
                    counter.setText(String.valueOf(minutes) + ":" + String.format("%02d", seconds));
                    instruction.setText("Start");
                    screenTitle.setText("Click to Begin Evaluating Background Noise");

                }
            }
        });

        //-----------------------------------------------------------------------------------------------

        queue = Volley.newRequestQueue(this);

        String url = "http://noise-app.azurewebsites.net/inverse";

        //String url = "https://androidtutorialpoint.com/api/volleyString";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        output.setText("Display of REST API response: " + response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        output.setText(error.toString());
                    }
                });
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });


        queue.add(stringRequest);

        //-----------------------------------------------------------------------------------------------
    }




    //Followed tutorial for countdown timer
    //not sure if using inheritance is necessary, but it might be useful for triggering the
    //next screen when the countdown is finished
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            counter.setText(String.valueOf((millisUntilFinished / 1000) / 60) + ":" + String.format("%02d", ((millisUntilFinished / 1000) % 60)));
            int progress = (int) (millisUntilFinished / 1000);
            bar.setProgress(progress);
        }


        @Override
        public void onFinish() {

            //Once the timer has fully completed its countdown,
            //stop the recording and release the resource
            mediaRecorder.stop();
            mediaRecorder.release();

            //The TextView element can be changed to something else or left at 0:00
            counter.setText("Evaluation Complete");
            bar.setProgress((int)(countdownTime / 1000));

            //Prevent user from pressing the button again
            start.setEnabled(false);

            //Switching to a new screen
            Intent intent = new Intent(Evaluation.this, StartCancellation.class);

            startActivity(intent);
        }
    }
}