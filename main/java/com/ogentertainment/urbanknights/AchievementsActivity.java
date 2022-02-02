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

public class AchievementsActivity extends AppCompatActivity {

    TextView txAchievementsUser1;
    TextView txAchievementsUser2;
    TextView txAchievementsUser3;
    TextView txAchievementsUser4;
    TextView txAchievementsUser5;
    TextView txAchievementsUser6;
    TextView txAchievementsUser7;
    TextView txAchievementsUser8;
    TextView txAchievementsUser9;
    TextView txAchievementsUser10;

    TextView txTaskai; //1.1.1 Taškus rodom tik kai užloadina info, kai not loaded - ,,Paruošiama informacija..."

    TextView txEil1Viet1;
    TextView txEil2Viet1;
    TextView txEil3Viet1;
    TextView txEil4Viet1;
    TextView txEil5Viet1;
    TextView txEil6Viet1;
    TextView txEil7Viet1;
    TextView txEil8Viet1;
    TextView txEil9Viet1;
    TextView txEil10Viet1;

    TextView txEil1Viet2;
    TextView txEil2Viet2;
    TextView txEil3Viet2;
    TextView txEil4Viet2;
    TextView txEil5Viet2;
    TextView txEil6Viet2;
    TextView txEil7Viet2;
    TextView txEil8Viet2;
    TextView txEil9Viet2;
    TextView txEil10Viet2;

    TextView txEil1Viet3;
    TextView txEil2Viet3;
    TextView txEil3Viet3;
    TextView txEil4Viet3;
    TextView txEil5Viet3;
    TextView txEil6Viet3;
    TextView txEil7Viet3;
    TextView txEil8Viet3;
    TextView txEil9Viet3;
    TextView txEil10Viet3;

    String ip;
    String achievementsURL;
    Button btnClose;

    ArrayList<TextView> users = new ArrayList<>();
    ArrayList<TextView> v1 = new ArrayList<>();
    ArrayList<TextView> v2 = new ArrayList<>();
    ArrayList<TextView> v3 = new ArrayList<>();
    String[] names = new String[]{};
    int[] pointsSums = new int[]{};

    private static final String TAG = "AchievementsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        ip = getString(R.string.ip);
        achievementsURL = ip + "getAchievements.php";

        txAchievementsUser1 = findViewById(R.id.txAchievementsUser1);
        txAchievementsUser2 = findViewById(R.id.txAchievementsUser2);
        txAchievementsUser3 = findViewById(R.id.txAchievementsUser3);
        txAchievementsUser4 = findViewById(R.id.txAchievementsUser4);
        txAchievementsUser5 = findViewById(R.id.txAchievementsUser5);
        txAchievementsUser6 = findViewById(R.id.txAchievementsUser6);
        txAchievementsUser7 = findViewById(R.id.txAchievementsUser7);
        txAchievementsUser8 = findViewById(R.id.txAchievementsUser8);
        txAchievementsUser9 = findViewById(R.id.txAchievementsUser9);
        txAchievementsUser10 = findViewById(R.id.txAchievementsUser10);

        txTaskai = findViewById(R.id.txVieta1); //1.1.1 Taškus rodom tik kai užloadina info, kai not loaded - ,,Paruošiama informacija..."

        txEil1Viet1 = findViewById(R.id.txEil1Viet1);
        txEil2Viet1 = findViewById(R.id.txEil2Viet1);
        txEil3Viet1 = findViewById(R.id.txEil3Viet1);
        txEil4Viet1 = findViewById(R.id.txEil4Viet1);
        txEil5Viet1 = findViewById(R.id.txEil5Viet1);
        txEil6Viet1 = findViewById(R.id.txEil6Viet1);
        txEil7Viet1 = findViewById(R.id.txEil7Viet1);
        txEil8Viet1 = findViewById(R.id.txEil8Viet1);
        txEil9Viet1 = findViewById(R.id.txEil9Viet1);
        txEil10Viet1 = findViewById(R.id.txEil10Viet1);

        txEil1Viet2 = findViewById(R.id.txEil1Viet2);
        txEil2Viet2 = findViewById(R.id.txEil2Viet2);
        txEil3Viet2 = findViewById(R.id.txEil3Viet2);
        txEil4Viet2 = findViewById(R.id.txEil4Viet2);
        txEil5Viet2 = findViewById(R.id.txEil5Viet2);
        txEil6Viet2 = findViewById(R.id.txEil6Viet2);
        txEil7Viet2 = findViewById(R.id.txEil7Viet2);
        txEil8Viet2 = findViewById(R.id.txEil8Viet2);
        txEil9Viet2 = findViewById(R.id.txEil9Viet2);
        txEil10Viet2 = findViewById(R.id.txEil10Viet2);

        txEil1Viet3 = findViewById(R.id.txEil1Viet3);
        txEil2Viet3 = findViewById(R.id.txEil2Viet3);
        txEil3Viet3 = findViewById(R.id.txEil3Viet3);
        txEil4Viet3 = findViewById(R.id.txEil4Viet3);
        txEil5Viet3 = findViewById(R.id.txEil5Viet3);
        txEil6Viet3 = findViewById(R.id.txEil6Viet3);
        txEil7Viet3 = findViewById(R.id.txEil7Viet3);
        txEil8Viet3 = findViewById(R.id.txEil8Viet3);
        txEil9Viet3 = findViewById(R.id.txEil9Viet3);
        txEil10Viet3 = findViewById(R.id.txEil10Viet3);

        users.add(txAchievementsUser1);
        users.add(txAchievementsUser2);
        users.add(txAchievementsUser3);
        users.add(txAchievementsUser4);
        users.add(txAchievementsUser5);
        users.add(txAchievementsUser6);
        users.add(txAchievementsUser7);
        users.add(txAchievementsUser8);
        users.add(txAchievementsUser9);
        users.add(txAchievementsUser10);

        v1.add(txEil1Viet1);
        v1.add(txEil2Viet1);
        v1.add(txEil3Viet1);
        v1.add(txEil4Viet1);
        v1.add(txEil5Viet1);
        v1.add(txEil6Viet1);
        v1.add(txEil7Viet1);
        v1.add(txEil8Viet1);
        v1.add(txEil9Viet1);
        v1.add(txEil10Viet1);

        v2.add(txEil1Viet2);
        v2.add(txEil2Viet2);
        v2.add(txEil3Viet2);
        v2.add(txEil4Viet2);
        v2.add(txEil5Viet2);
        v2.add(txEil6Viet2);
        v2.add(txEil7Viet2);
        v2.add(txEil8Viet2);
        v2.add(txEil9Viet2);
        v2.add(txEil10Viet2);

        v3.add(txEil1Viet3);
        v3.add(txEil2Viet3);
        v3.add(txEil3Viet3);
        v3.add(txEil4Viet3);
        v3.add(txEil5Viet3);
        v3.add(txEil6Viet3);
        v3.add(txEil7Viet3);
        v3.add(txEil8Viet3);
        v3.add(txEil9Viet3);
        v3.add(txEil10Viet3);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAchievementData();
    }

    void bubbleSort(int n)
    {
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (pointsSums[j] < pointsSums[j+1])
                {
                    // swap arr[j+1] and arr[i]
                    int temp = pointsSums[j];
                    String tempName = names[j];
                    pointsSums[j] = pointsSums[j+1];
                    names[j] = names[j+1];
                    pointsSums[j+1] = temp;
                    names[j+1] = tempName;
                }
                fillTextViews(n);
    }

    private void fillTextViews(int n)
    {
        for (int i = 0; i < n; i++)
        {
            Log.d(TAG, "fillTextViews: " + names[i] + " " + pointsSums[i]);
            users.get(i).setText(i+1 + ". " + names[i]);
            v1.get(i).setText(String.valueOf(pointsSums[i]));
            //v2.get(i).setText(p2);
            //v3.get(i).setText(p3);
        }
        txTaskai.setText("Taškai"); //1.1.1 Taškus rodom tik kai užloadina info, kai not loaded - ,,Paruošiama informacija..."
    }

    private void getAchievementData(){
            //Start of server request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, achievementsURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d(TAG, "onResponse getAchievementData: " + response);
                        JSONArray jsonArray = new JSONArray(response);
                        names = new String[jsonArray.length()];
                        pointsSums = new int[jsonArray.length()];
                        for (int i = 0; i<jsonArray.length(); i++){
                            JSONObject jOb = jsonArray.getJSONObject(i);
                            String user = jOb.getString("username");
                            String p1 = jOb.getString("place1");
                            String p2 = jOb.getString("place2");
                            String p3 = jOb.getString("place3");
                            try{
                                names[i] = user;
                                pointsSums[i] = (Integer.valueOf(p1) * 3 + Integer.valueOf(p2) * 2 + Integer.valueOf(p3));
                            } catch (Exception e){
                                Log.d(TAG, "CRASHAS! in getAchievementsData()");
                            }
                        }
                        Log.d(TAG, "Nam: " + names[4]);
                        bubbleSort(jsonArray.length()); //Sorts names by points
                    } catch (JSONException e){
                        VolleyLog.d(TAG, "onResponse getAchievementData: response failed :( Error: " + e);
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse getAchievementData: " + error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    return params;
                }
            };
            MySingleton.getInstance(AchievementsActivity.this).addToRequestQueue(stringRequest);
            //End of server request
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
