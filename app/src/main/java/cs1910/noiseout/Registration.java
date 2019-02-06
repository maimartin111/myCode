package cs1910.noiseout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class Registration extends AppCompatActivity {

    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.
    String url;
    EditText etFirstName, etLastName, etEmail, etPassword, etHeight, etWeight, etDateOfBirth;
    Spinner gender_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        populateGenderSpinner();

        etEmail = findViewById(R.id.email_field) ;
        etPassword = findViewById(R.id.password_field);
        etFirstName = findViewById(R.id.first_name_field);
        etLastName = findViewById(R.id.last_name_field);
        etHeight = findViewById(R.id.height_field);
        etWeight = findViewById(R.id.weight_field);
        etDateOfBirth = findViewById(R.id.date_of_birth_field);

        // This setups up a new request queue which we will need to make HTTP requests.
        requestQueue = Volley.newRequestQueue(this);

    }

    public void populateGenderSpinner() {
        gender_spinner = findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_spinner.setAdapter(adapter);
    }

    public void createUser(View view){
        try {
            //RequestQueue requestQueue = Volley.newRequestQueue(this);
            this.url = "http://noise-app.azurewebsites.net/insert";

            // create new json object
            JSONObject jsonBody = new JSONObject();

            // attach values to json body
            jsonBody.put("userID", UUID.randomUUID().toString());
            jsonBody.put("email", etEmail.getText().toString());
            jsonBody.put("password", etPassword.getText().toString());
            jsonBody.put("firstName", etFirstName.getText().toString());
            jsonBody.put("lastName", etLastName.getText().toString());
            jsonBody.put("dateOfBirth", etDateOfBirth.getText().toString());
            jsonBody.put("height", etHeight.getText().toString());
            jsonBody.put("weight", etWeight.getText().toString());
            jsonBody.put("gender", gender_spinner.getSelectedItem().toString());

            // cast json body to string
            final String mRequestBody = jsonBody.toString();

            StringRequest arrReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            // Allow extra time for request to execute
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Switch to next screen
        Intent intent = new Intent(Registration.this, Evaluation.class);
        startActivity(intent);
    }

    public void getUserInfo(View view) {
        // set base url
        this.url = "http://noise-app.azurewebsites.net/user?userID=";

        // get and append userID to base url
        String userID = etFirstName.getText().toString();
        this.url += userID;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check if user exists in table
                        if (response.length() > 0) {
                            // the user exists, so we loop through all possible users
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // get values from json body
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String firstName = jsonObj.get("first_name").toString();
                                    String lastName = jsonObj.get("last_name").toString();
                                    String dateOfBirth = jsonObj.get("date_of_birth").toString();
                                    String height = jsonObj.get("height_inches").toString();
                                    String weight = jsonObj.get("weight_pounds").toString();
                                    String gender = jsonObj.get("gender").toString();
                                    String email = jsonObj.get("email").toString();
                                    String password = jsonObj.get("password").toString();

                                    // set values in edit texts
                                    etEmail.setText(email);
                                    etPassword.setText(password);
                                    etFirstName.setText(firstName);
                                    etLastName.setText(lastName);
                                    etDateOfBirth.setText(dateOfBirth);
                                    etWeight.setText(weight);
                                    etHeight.setText(height);

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            // The user does not exist in table
                            Log.e("Volley", "Could not get response");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // there was an http error
                        Log.e("Volley", error.toString());
                    }
                }
        );

        // Allow for extra time for the request to execute fully
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