package cs1910.noiseout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Dashboard extends Activity  {
    Button b1;
    TextView detectNum, cancelNum, rateNum;
    RequestQueue requestQueue;

    //Variables to POST
    int ID = 10;
    int numCancels = 50;
    int numSnores = 62;
    String url;
    String StartTime = "2017-02-01 00:00:00";//new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    String EndTime = "2018-02-02 00:00:00";//new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        b1 = (Button)findViewById(R.id.Button1);
        detectNum = (TextView)findViewById(R.id.detectNum);
        cancelNum = (TextView)findViewById(R.id.cancelNum);
        rateNum = (TextView)findViewById(R.id.rateNum);
        requestQueue = Volley.newRequestQueue(this);

        b1.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      detectNum.setText("85");
                                      cancelNum.setText("80");
                                      rateNum.setText("95%");
                                      postSnores(v);
                                  }
                              }
        );
    }
    public void postSnores(View view){
        try {
            this.url = "http://noise-app.azurewebsites.net/snore-stats";

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("ID", ID);
            jsonBody.put("numCancels", numCancels);
            jsonBody.put("numSnores", numSnores);
            jsonBody.put("StartTime", (StartTime));
            jsonBody.put("EndTime", EndTime);

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
            requestQueue.add(arrReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Dashboard.this, Evaluation.class);

        startActivity(intent);
    }
}