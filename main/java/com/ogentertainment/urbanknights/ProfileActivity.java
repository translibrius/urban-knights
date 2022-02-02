package com.ogentertainment.urbanknights;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity { //1.2.0 all activity done

    String ip;
    String getProfileDataURL;
    String id = "";
    String username = "";
    TextView txCaptCount;
    TextView txChangeCount;
    TextView txUpgrdCount;
    TextView txUsername;
    TextView txDragonCount;
    TextView txUniqueCount; //Kiek pacapturino niekieno sektoriu

    TextView tx1row1;
    TextView tx1row2;
    TextView tx1row3;
    TextView tx1row4;
    TextView tx1row5;
    Button btnClose;
    private static final String TAG = "ProfileActivity";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getStringExtra("id");
        username = getIntent().getStringExtra("username");
        txCaptCount = findViewById(R.id.txCaptCount);
        txChangeCount = findViewById(R.id.txChangeCount);
        txUpgrdCount = findViewById(R.id.txUpgrdCount);
        txUsername = findViewById(R.id.textViewUsername);
        txDragonCount = findViewById(R.id.txDragonCount);
        txUniqueCount = findViewById(R.id.txUniqueCount);
        btnClose = findViewById(R.id.btnClose);

        tx1row1 = findViewById(R.id.tx1row1);
        tx1row2 = findViewById(R.id.tx1row2);
        tx1row3 = findViewById(R.id.tx1row3);
        tx1row4 = findViewById(R.id.tx1row4);
        tx1row5 = findViewById(R.id.tx1row5);

        txUsername.setText(username);

        ip = getString(R.string.ip);
        getProfileDataURL = ip + "getProfileData.php";

        getProfileData();
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getProfileData()
    {
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getProfileDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse getProfileData: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String captCount = jOb.getString("captCount");
                        String changeCount = jOb.getString("changeCount");
                        String upgrCount = jOb.getString("upgrCount");
                        String dragonCount = jOb.getString("dragonCount");
                        String uniqueCount = jOb.getString("uniqueCount");
                        Log.d(TAG, "fsfasfasfsa: " + dragonCount);

                        tx1row1.setText("Užimta teritorijų:");
                        tx1row2.setVisibility(View.VISIBLE);
                        tx1row3.setVisibility(View.VISIBLE);
                        tx1row4.setVisibility(View.VISIBLE);
                        tx1row5.setVisibility(View.VISIBLE);

                        txCaptCount.setText(captCount);
                        txChangeCount.setText(changeCount);
                        txUpgrdCount.setText(upgrCount);
                        txDragonCount.setText(dragonCount);
                        txUniqueCount.setText(uniqueCount);
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse getProfileData: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse getProfileData: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(ProfileActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }
}
