package com.ogentertainment.urbanknights;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SectorClickActivity extends AppCompatActivity {

    TextView txId;
    TextView txOwner;
    TextView txPlayers;
    TextView txLevel;
    TextView txProtection; //Value
    TextView txProt; //TExt
    TextView txWeight; //1.1.0 added
    TextView txDragon;//v1.2.0 dragon

    String id;
    Button btnClose;

    String owner;
    String level;
    String protection;
    String now;
    String weight; //1.1.0 added
    String dragon; //1.2.0 added
    ArrayList<String> players = new ArrayList<>();

    String ip;
    String getSectorInfoURL;
    String getSectorPlayersURL;

    private static final String TAG = "SectorClickActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sector_click);

        ip = getString(R.string.ip);
        getSectorInfoURL = ip + "getSectorInfo.php";
        getSectorPlayersURL = ip + "getSectorPlayers.php";

        txId = findViewById(R.id.sectorClickIdInput);
        txOwner = findViewById(R.id.sectorClickOwnerInput);
        txPlayers = findViewById(R.id.sectorClickPlayersInput);
        txLevel = findViewById(R.id.sectorClickLevelInput);
        txProtection = findViewById(R.id.sectorClickProtectionInput);
        txProt = findViewById(R.id.sectorClickProtection);
        txWeight = findViewById(R.id.sectorClickWeightInput); //1.1.0
        txDragon = findViewById(R.id.sectorClickDragonInput); //v1.2.0 dragon

        id = getIntent().getStringExtra("id");
        Log.d(TAG, "onCreate: " + id);
        txId.setText(id);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSectorInfo();
    }

    private void getSectorInfo(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getSectorInfoURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse getSectorInfo: " + response);
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jOb = jsonArray.getJSONObject(0);
                    owner = jOb.getString("ownerId");
                    level = jOb.getString("level");
                    protection = jOb.getString("captureTime");
                    now = jOb.getString("now");
                    weight = jOb.getString("weight"); //1.1.0 added
                    dragon = jOb.getString("dragon");

                    if (dragon.equals("1"))
                    {
                        txDragon.setText("Aktyvus");
                    }
                    else
                    {
                        txDragon.setText("-");
                    }

                    if(owner.equals("god")){
                        txOwner.setText("-");
                    } else {
                        txOwner.setText(owner);
                    }
                    txLevel.setText(level);
                    txWeight.setText(String.valueOf(Integer.valueOf(weight)*Integer.valueOf(level))); //1.1.0 added

                    getSectorPlayers();
                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse getSectorInfo: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse getSectorInfo: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(SectorClickActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    private void getSectorPlayers(){
        //Start of server request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getSectorPlayersURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse getSectorPlayers: " + response);

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i<jsonArray.length(); i++){
                        JSONObject jOb = jsonArray.getJSONObject(i);
                        String player = jOb.getString("username");
                        players.add(player);
                    }


                        Timestamp timestamp = Timestamp.valueOf(protection);
                        Timestamp timestampNow = Timestamp.valueOf(now);
                        if (timestampNow.before(timestamp)){
                            txProtection.setText(protection);
                        } else {
                            txProtection.setText("-");
                        }

                        if(players.size()>0){
                            String output = "";
                            for (int i = 0; i<players.size(); i++){
                                String player = players.get(i);
                                if (i==players.size()-1){ //Jei paskutinis playeris nedarom new line :)
                                    output = output + player;
                                } else {
                                    output = output + player + " \n";
                                }
                            }
                            txPlayers.setText(output);
                        } else {
                            txPlayers.setText("-");
                        }

                        if (players.size()>2){
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txProt.getLayoutParams();
                            params.addRule(RelativeLayout.BELOW, R.id.sectorClickPlayersInput);

                            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) txProt.getLayoutParams();
                            p.setMargins(0,0,0,0);
                            txProt.requestLayout();
                        }



                } catch (JSONException e){
                    VolleyLog.d(TAG, "onResponse getSectorPlayers: response failed :( Error: " + e);
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse getSectorPlayers: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(SectorClickActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
