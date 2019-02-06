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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {


    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    String url;
    EditText etEmail, etPassword;
    String authEmail, authPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.email_login_field) ;
        etPassword = findViewById(R.id.password_login_field);
        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.

    }

    public void newUser(View view){
        Intent intent = new Intent(Login.this, Registration.class);
        startActivity(intent);
    }

    public void checkUser(View view) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
//        this.url = this.baseUrl + username + "/repos";
        this.url = "http://noise-app.azurewebsites.net/username?Email=";
        String userEmail = etEmail.getText().toString();
        this.url += userEmail;
        Log.v("testing", this.url);

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {
                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);

                                    authEmail = jsonObj.get("email").toString();
                                    authPassword = jsonObj.get("password").toString();

                                    String localEmail = etEmail.getText().toString();
                                    String localPassword = etPassword.getText().toString();

                                    Log.d("Testing", "ATTEMPTING TO VERIFY");
                                    if (localEmail.equals(authEmail) && localPassword.equals(authPassword)){
                                        Intent intent = new Intent(Login.this, Evaluation.class);
                                        startActivity(intent);
                                        Log.d("Testing", "VERIFIED");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            // The user didn't have any repos.
                            Log.e("Volley", "Could not get response");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Log.e("Volley", error.toString());
                    }
                }
        );
        arrReq.setRetryPolicy(new RetryPolicy() {
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

        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }
}