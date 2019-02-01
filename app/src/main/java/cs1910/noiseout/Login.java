package cs1910.noiseout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity  {
    Button b1,b2;
    EditText ed1,ed2;

    TextView tx1;
    int counter = 3;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().hide();

        //check Network connection.
        checkNetworkConnection();

        b1 = (Button)findViewById(R.id.stop_button);
        ed1 = (EditText)findViewById(R.id.email_login_field);
        ed2 = (EditText)findViewById(R.id.password_login_field);

        b2 = (Button)findViewById(R.id.sign_up_button);
        //tx1 = (TextView)findViewById(R.id.textView8);
        //tx1.setVisibility(View.GONE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed1.getText().toString().equals("admin") &&
                        ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Login.this, Evaluation.class);
                    startActivity(intent);


                }else{
                    Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();

                    tx1.setVisibility(View.VISIBLE);
                    tx1.setBackgroundColor(Color.RED);
                    counter--;
                    tx1.setText(Integer.toString(counter));

                    if (counter == 0) {
                        b1.setEnabled(false);
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

    }

    protected boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(), "Network Connected.",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "No Network Connection.",Toast.LENGTH_SHORT).show();
        }
        return isConnected;
    }

    //check the user
    protected boolean checkUser(View view){
        boolean result = false;
        this.url = "http://noise-app.azurewebsites.net/user?userID=";
        String userID = ed1.getText().toString();
        this.url += userID;
        Log.v("testing", this.url);

        return false;
    }


    //connect to the gttp
//    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//            // params comes from the execute() call: params[0] is the url.
//            try {
//                try {
//                    return HttpPost(urls[0]);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    return "Error!";
//                }
//            } catch (IOException e) {
//                return "Unable to retrieve web page. URL may be invalid.";
//            }
//        }
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(), result,Toast.LENGTH_SHORT).show();
//        }
//
//        private String HttpPost(String myUrl) throws IOException, JSONException {
//            String result = "";
//
//            URL url = new URL(myUrl);
//
//            // 1. create HttpURLConnection
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
//
//            // 2. build JSON object
//            JSONObject jsonObject = buidJsonObject();
//
//            // 3. add JSON content to POST request body
//            setPostRequestContent(conn, jsonObject);
//
//            // 4. make POST request to the given URL
//            conn.connect();
//
//            // 5. return response message
//            return conn.getResponseMessage()+"";
//
//        }
//
//        private JSONObject buidJsonObject() throws JSONException {
//
//            JSONObject jsonObject = new JSONObject();
////            jsonObject.accumulate("name", etName.getText().toString());
////            jsonObject.accumulate("country",  etCountry.getText().toString());
////            jsonObject.accumulate("twitter",  etTwitter.getText().toString());
//
//            return jsonObject;
//        }
//
//        private void setPostRequestContent(HttpURLConnection conn,
//                                           JSONObject jsonObject) throws IOException {
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(jsonObject.toString());
//            Log.i(Login.class.toString(), jsonObject.toString());
//            writer.flush();
//            writer.close();
//            os.close();
//        }
//    }
}