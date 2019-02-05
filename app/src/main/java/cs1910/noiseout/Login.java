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
    /*
    Button b1, b2;
    EditText ed1, ed2;
    RequestQueue requestQueue;
    TextView tx1;
    int counter = 3;
    String url;
    String authName;
    String authPw;

    //String url;
    EditText etName, etEmail, etPassword, etHeight, etWeight, etDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();

        etEmail = findViewById(R.id.email_field);
        etPassword = findViewById(R.id.password_login_field);
        etName = findViewById(R.id.email_login_field);
        etHeight = findViewById(R.id.height_field);
        etWeight = findViewById(R.id.weight_field);
        etDateOfBirth = findViewById(R.id.date_of_birth_field);

        //check Network connection.
        checkNetworkConnection();

        requestQueue = Volley.newRequestQueue(this);


        b1 = (Button) findViewById(R.id.stop_button);
        ed1 = (EditText) findViewById(R.id.email_login_field);
        ed2 = (EditText) findViewById(R.id.password_login_field);

        b2 = (Button) findViewById(R.id.sign_up_button);
        tx1 = (TextView) findViewById(R.id.new_noiseout);
        //tx1.setVisibility(View.GONE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkEmail = ed1.getText().toString();
                String checkPassword = ed2.getText().toString();
                getUserInfo(v);
                //checkUser(v);
                if (checkEmail.equals(authName) &&
                        checkPassword.equals(authPw)) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Login.this, Evaluation.class);
                    startActivity(intent);


                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getApplicationContext(), "Network Connected.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "No Network Connection.", Toast.LENGTH_SHORT).show();
        }
        return isConnected;
    }

    //check the user


    protected void checkUser(View view) {
        this.url = "http://noise-app.azurewebsites.net/username?Email=";
        String Email = ed1.getText().toString();
        this.url += Email;
        Log.v("testing", this.url);
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
//                                    String firstName = jsonObj.get("first_name").toString();
//                                    String lastName = jsonObj.get("last_name").toString();
//                                    String dateOfBirth = jsonObj.get("date_of_birth").toString();
//                                    String height = jsonObj.get("height_inches").toString();
//                                    String weight = jsonObj.get("weight_pounds").toString();
//                                    String gender = jsonObj.get("gender").toString();
                                    String email = jsonObj.get("email").toString();
                                    String password = jsonObj.get("password").toString();

                                    authName = email;
                                    authPw = password;
//                                    etName.setText(firstName + " " + lastName);
//                                    etDateOfBirth.setText(dateOfBirth);
//                                    etWeight.setText(weight);
//                                    etHeight.setText(height);
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


    public void getUserInfo(View view) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
//        this.url = this.baseUrl + username + "/repos";
        this.url = "http://noise-app.azurewebsites.net/user?userID=";
        String userID = etName.getText().toString();
        this.url += userID;
        //this.url = "https://jsonplaceholder.typicode.com/todos/1";
        Log.v("testing", this.url);


        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("testing", "ON RESPONSE");
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {
                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    Log.d("testing", "Got repsonse");
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String firstName = jsonObj.get("first_name").toString();
                                    String lastName = jsonObj.get("last_name").toString();
                                    String dateOfBirth = jsonObj.get("date_of_birth").toString();
                                    String height = jsonObj.get("height_inches").toString();
                                    String weight = jsonObj.get("weight_pounds").toString();
                                    String gender = jsonObj.get("gender").toString();
                                    String email = jsonObj.get("email").toString();
                                    String password = jsonObj.get("password").toString();

                                    //etEmail.setText(email);
                                    //etPassword.setText(password);
                                    etName.setText(firstName + " " + lastName);
                                    //etDateOfBirth.setText(dateOfBirth);
                                    //etWeight.setText(weight);
                                    //etHeight.setText(height);
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
    }
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
*/