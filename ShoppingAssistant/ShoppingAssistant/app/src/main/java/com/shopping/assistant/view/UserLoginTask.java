package com.shopping.assistant.view;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

import com.shopping.assistant.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<String, Void, Intent> implements ServerAuthentication {

    static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    static final String ACCOUNT = "ShoppingAssistant";
    private final String email;
    private final String password;
    private Context mContext;
    private Bundle data;
    private SharedPreferences preferences;
    Resources res;

    public UserLoginTask(Context context, String email, String password ) {
        mContext = context;
        this.email = email;
        this.password= password;
        res = mContext.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected Intent doInBackground(String... urls) {
        if (urls.length == 0)
            return null;

        data = new Bundle();
        String resultJSON = null;
        Intent res = new Intent();

        try {
            resultJSON = userSignIn( email, password, Constants.FULL_ACCESS, urls[0]);
            JSONObject auth = new JSONObject(resultJSON);
            String refreshToken = auth.getString("token");
            if( refreshToken.equals("null") ) {
                data.putBoolean("NoToken", true);
                res.putExtras(data);
                return res;
            }
            data.putString("User",auth.getString("user") );
            data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.FULL_ACCESS);
            data.putString("AuthTokenUstek", refreshToken);

        }catch (IOException e) {
            e.printStackTrace();
            data.putString(KEY_ERROR_MESSAGE, "IOException");
        }catch (JSONException e) {
            e.printStackTrace();
            data.putString(KEY_ERROR_MESSAGE, "InvalidJSON");
        }catch (Exception e) {
            data.putString(KEY_ERROR_MESSAGE, "NOService");
            e.printStackTrace();
        }

        res.putExtras(data);
        return res;
    }


    @Override
    public String userSignIn(String user, String pass, String authType, String requestUrl) throws Exception {

        String mUrl = preferences.getString(Constants.SERVER_URL, "NoServerURL");
        if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
            mUrl = "http://" + mUrl;
        }
        if (!mUrl.endsWith("/")) {
            mUrl += "/";
        }

        mUrl += requestUrl;

        StringWriter sw = new StringWriter();
        boolean ok = false;
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.put("u", user);
            obj.put("p", pass);
            out.write(obj.toString());
            out.flush();

            InputStreamReader in;
            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST)
                in = new InputStreamReader(conn.getErrorStream());
            else
                in = new InputStreamReader(conn.getInputStream());

            BufferedReader br = new BufferedReader(in);

            char[] buffer = new char[1024 * 4];
            int n;
            while (-1 != (n = br.read(buffer))) {
                sw.write(buffer, 0, n);
            }

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                ok = true;

            conn.disconnect();
        } catch (Exception e) {
            //This is for counting how many times error occurs
            e.printStackTrace();
            return "Error : " + e.getMessage();
        }

        if (ok)
            return sw.toString();
        else
            return "";
    }

}