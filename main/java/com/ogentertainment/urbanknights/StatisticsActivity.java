package com.ogentertainment.urbanknights;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    
    TextView txStatsTer1;
    TextView txStatsTer2;
    TextView txStatsTer3;
    TextView txStatsTer4;
    TextView txStatsTer5;

    TextView txStatsHeader1;
    TextView txStatsHeader2;
    
    TextView txStatsTerValue1;
    TextView txStatsTerValue2;
    TextView txStatsTerValue3;
    TextView txStatsTerValue4;
    TextView txStatsTerValue5;

    TextView txStatsAuks1;
    TextView txStatsAuks2;
    TextView txStatsAuks3;
    TextView txStatsAuks4;
    TextView txStatsAuks5;

    TextView txStatsAuksValue1;
    TextView txStatsAuksValue2;
    TextView txStatsAuksValue3;
    TextView txStatsAuksValue4;
    TextView txStatsAuksValue5;

    ArrayList<String> usersTeritorijos = new ArrayList<>();
    ArrayList<String> valuesTeritorijos = new ArrayList<>();
    ArrayList<TextView> textViewsTeritorijos = new ArrayList<>();
    ArrayList<TextView> textViewsTeritorijosValue = new ArrayList<>();
    ArrayList<TextView> textViewsGold = new ArrayList<>();
    ArrayList<TextView> textViewsGoldValue = new ArrayList<>();
    String id = "1";
    Button btnClose;

    String ip;
    String getStatisticsDataURL;
    String getStatisticsDataGoldURL;
    private static final String TAG = "StatisticsActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        ip = getString(R.string.ip);
        getStatisticsDataURL = ip + "getStatisticsData.php";
        getStatisticsDataGoldURL = ip + "getStatisticsDataGold.php";

        txStatsTer1 = findViewById(R.id.txStatsTer1);
        txStatsTer2 = findViewById(R.id.txStatsTer2);
        txStatsTer3 = findViewById(R.id.txStatsTer3);
        txStatsTer4 = findViewById(R.id.txStatsTer4);
        txStatsTer5 = findViewById(R.id.txStatsTer5);

        txStatsHeader1 = findViewById(R.id.txStatsHeader1);
        txStatsHeader2 = findViewById(R.id.txStatsHeader2);

        txStatsTerValue1 = findViewById(R.id.txStatsTerValue1);
        txStatsTerValue2 = findViewById(R.id.txStatsTerValue2);
        txStatsTerValue3 = findViewById(R.id.txStatsTerValue3);
        txStatsTerValue4 = findViewById(R.id.txStatsTerValue4);
        txStatsTerValue5 = findViewById(R.id.txStatsTerValue5);

        txStatsAuks1 = findViewById(R.id.txStatsAuks1);
        txStatsAuks2 = findViewById(R.id.txStatsAuks2);
        txStatsAuks3 = findViewById(R.id.txStatsAuks3);
        txStatsAuks4 = findViewById(R.id.txStatsAuks4);
        txStatsAuks5 = findViewById(R.id.txStatsAuks5);

        txStatsAuksValue1 = findViewById(R.id.txStatsAuksValue1);
        txStatsAuksValue2 = findViewById(R.id.txStatsAuksValue2);
        txStatsAuksValue3 = findViewById(R.id.txStatsAuksValue3);
        txStatsAuksValue4 = findViewById(R.id.txStatsAuksValue4);
        txStatsAuksValue5 = findViewById(R.id.txStatsAuksValue5);

        textViewsTeritorijos.add(txStatsTer1);
        textViewsTeritorijos.add(txStatsTer2);
        textViewsTeritorijos.add(txStatsTer3);
        textViewsTeritorijos.add(txStatsTer4);
        textViewsTeritorijos.add(txStatsTer5);

        textViewsTeritorijosValue.add(txStatsTerValue1);
        textViewsTeritorijosValue.add(txStatsTerValue2);
        textViewsTeritorijosValue.add(txStatsTerValue3);
        textViewsTeritorijosValue.add(txStatsTerValue4);
        textViewsTeritorijosValue.add(txStatsTerValue5);

        textViewsGold.add(txStatsAuks1);
        textViewsGold.add(txStatsAuks2);
        textViewsGold.add(txStatsAuks3);
        textViewsGold.add(txStatsAuks4);
        textViewsGold.add(txStatsAuks5);

        textViewsGoldValue.add(txStatsAuksValue1);
        textViewsGoldValue.add(txStatsAuksValue2);
        textViewsGoldValue.add(txStatsAuksValue3);
        textViewsGoldValue.add(txStatsAuksValue4);
        textViewsGoldValue.add(txStatsAuksValue5);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getStatisticsDataTeritorijos();
        getStatisticsDataGold();
    }

    private void getStatisticsDataGold(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getStatisticsDataGoldURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse getStatisticsDataGold: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String user = jOb.getString("user");
                        String count = jOb.getString("count");
                        textViewsGold.get(i).setText(user);
                        textViewsGoldValue.get(i).setText(count);
                        textViewsGold.get(i).setVisibility(View.VISIBLE);
                        textViewsGoldValue.get(i).setVisibility(View.VISIBLE);
                        textViewsTeritorijos.get(i).setVisibility(View.VISIBLE);
                        textViewsTeritorijosValue.get(i).setVisibility(View.VISIBLE);

                        //1.2.0 Added Loading text
                        txStatsHeader2.setVisibility(View.VISIBLE);
                        txStatsHeader1.setText("Teritorijos");
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse getStatisticsDataGold: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse getStatisticsDataGold: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(StatisticsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }
    
    private void getStatisticsDataTeritorijos(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getStatisticsDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse getStatisticsData: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String user = jOb.getString("user");
                        String count = jOb.getString("count");
                        textViewsTeritorijos.get(i).setText(user);
                        textViewsTeritorijosValue.get(i).setText(count);
                    }
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse getStatisticsData: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse getStatisticsData: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(StatisticsActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }
}
