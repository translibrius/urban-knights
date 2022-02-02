package com.ogentertainment.urbanknights;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class UpgradeActivity extends AppCompatActivity {

    //Vars
    Integer level;
    Integer gold;
    Integer kaina;
    String playerId;
    String sectorId;

    Button btnUpgrade;
    Button btnClose;

    TextView txUpgradeCost;
    String ip;
    String upgradeURL;

    private static final String TAG = "UpgradeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upgrade);

        ip = getString(R.string.ip);
        upgradeURL = ip + "upgradeDefence.php";

        //Inits
        txUpgradeCost = findViewById(R.id.txUpgradeCost);
        btnUpgrade = findViewById(R.id.btnPopupUpgrade);
        btnClose = findViewById(R.id.btnClose);
        level = Integer.valueOf(getIntent().getStringExtra("level"));
        gold = Integer.valueOf(getIntent().getStringExtra("gold"));
        playerId = getIntent().getStringExtra("playerId");
        sectorId = getIntent().getStringExtra("sectorId");
        kaina = (int)(100 * Math.pow(2.0, level.doubleValue()-1)); //1.1.0 pakeista formule

        //Code
        txUpgradeCost.setText("Kaina: " + kaina.toString() + " aukso.");

        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kaina > gold){
                    Toast.makeText(UpgradeActivity.this, "Nepakanka aukso. Teritorijos sustiprinimo kaina: " + kaina + " aukso.", Toast.LENGTH_SHORT).show();
                } else {
                    upgrade();
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void upgrade(){
        //Start of server request
        btnUpgrade.setEnabled(false); //1.0.1 Upgrade fix
        StringRequest stringRequest = new StringRequest(Request.Method.POST, upgradeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse upgradeDefence: " + response);
                finish();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse upgradeDefence: " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Integer upgradedLvl = level+1; //1.0.1 actionLog (levelis i kuri upgradinam logams)
                Map<String,String> params = new HashMap<String, String>();
                params.put("playerId", playerId);
                params.put("sectorId", sectorId);
                params.put("cost", kaina.toString());
                params.put("lvl", upgradedLvl.toString()); //1.0.1 actionLog
                return params;
            }
        };
        MySingleton.getInstance(UpgradeActivity.this).addToRequestQueue(stringRequest);
        //End of server request
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
