package com.ogentertainment.urbanknights;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private CheckBox checkBox;
    private SharedPreferences mPrefs;

    String ip;
    String login_url;

    public static final String PREFS_NAME = "";
    public static final String TEXT = "text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ip connection paths
        ip = getString(R.string.ip);
        login_url = ip + "login.php";

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        checkBox = findViewById(R.id.checkbox);
        btnLogin = findViewById(R.id.btnLogin);

        mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        getPreferencesData();
    }

    private void getPreferencesData(){
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("pref_name")){
            String username = sp.getString("pref_name", "not found.");
            etUsername.setText(username);
        }
        if (sp.contains("pref_pass")){
            String password = sp.getString("pref_pass", "not found.");
            etPassword.setText(password);
        }
        if (sp.contains("pref_check")){
            Boolean wasChecked = sp.getBoolean("pref_check", false);
            if (wasChecked){
                checkBox.setChecked(true);
                //btnLogin.performClick(); 1.1.0 Removed
            }
        }
    }

    private void onLoginSuccess(JSONObject jsonObject){
        Log.d(TAG, "onLoginSuccess: called");
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        if (checkBox.isChecked()) {
            Boolean boolIsChecked = checkBox.isChecked();
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString("pref_name", etUsername.getText().toString());
            editor.putString("pref_pass", etPassword.getText().toString());
            editor.putBoolean("pref_check", boolIsChecked);
            editor.apply();
        } else {
            mPrefs.edit().clear().apply();
            startActivity(intent);
        }
        try {
            Log.d(TAG, "onLoginSuccess: try catch called");
            String id = jsonObject.getString("id");
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            String sector = jsonObject.getString("sector");
            String gold = jsonObject.getString("gold");
            String color = jsonObject.getString("color");
            String place1 = jsonObject.getString("place1");
            String place2 = jsonObject.getString("place2");
            String place3 = jsonObject.getString("place3");
            String lastRefresh = jsonObject.getString("lastRefresh");
            String sectorCount = jsonObject.getString("count");

            intent.putExtra("id", id);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            intent.putExtra("sector", sector);
            intent.putExtra("gold", gold);
            intent.putExtra("color", color);
            intent.putExtra("place1", place1);
            intent.putExtra("place2", place2);
            intent.putExtra("place3", place3);
            intent.putExtra("lastRefresh", lastRefresh);
            intent.putExtra("sectorCount", sectorCount);

        } catch (JSONException e) {
            Log.d(TAG, "onLoginSuccess: FAILED. error: " + e);
        }
        startActivity(intent);
    }

    private void onLoginFailed(String message) {
        Log.d(TAG, "onLoginFailed: called");
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void OnLogin(View view){
        Log.d(TAG, "OnLogin: called");
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (username.equals("") || password.equals("")){
            Toast.makeText(this, "Prašome nepalikti tuščių laukelių!", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d(TAG, "onResponse: " + response);
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String code = jsonObject.getString("code");
                        String message = jsonObject.getString("message");
                        if (code.equals("success")){
                            onLoginSuccess(jsonObject);
                        } else {
                            onLoginFailed(message);
                        }
                    } catch (JSONException e){
                        VolleyLog.d(TAG, "onResponse: response failed :( Error: " + e);
                    }
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };
            MySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
        }
    }
}