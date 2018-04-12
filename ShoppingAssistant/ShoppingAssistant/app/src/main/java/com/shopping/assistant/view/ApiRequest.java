package com.shopping.assistant.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Toast;
import com.shopping.assistant.R;
import com.shopping.assistant.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;


class ApiRequest extends AsyncTask<String, Void, ApiRequest.Result> {

    public AsyncResponse delegate = null;
    ActivityDone done = null;

    private String mUrl = "";
    private String mToken = "";
    private Context mContext ;
    private Resources res;
    SharedPreferences preferences;

    ApiRequest( Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mUrl = preferences.getString(Constants.SERVER_URL, "NoServerURL");
        mToken = preferences.getString( Constants.TOKEN, "NoAuthToken");
        mContext = context;
        res = mContext.getResources();
        if (!mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
            mUrl = "http://" + mUrl;
        }
        if (!mUrl.endsWith("/")) {
            mUrl += "/";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Result doInBackground (String... urls) {
        if (urls.length == 0)
            return null;

        URL url;
        HttpURLConnection urlConnection = null;
        JSONObject jsonObject;
        try {
            url = new URL(mUrl + urls[0] );
            urlConnection = (HttpURLConnection) url.openConnection();
            if (!mToken.isEmpty() &&  !mToken.equals("NoAuthToken") ) {
                urlConnection.addRequestProperty("Content-Type", "application/json");
                urlConnection.addRequestProperty("Authorization", "Bearer " + mToken);
            }

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
                BufferedReader br = new BufferedReader(in);

                StringWriter sw = new StringWriter();
                char[] buffer = new char[1024 * 16];

                int n;
                while (-1 != (n = br.read(buffer))) {
                    sw.write(buffer, 0, n);
                }
                jsonObject = new JSONObject(sw.toString());
                return new Result(urlConnection.getResponseCode(), jsonObject);
            } else {
                return new Result(urlConnection.getResponseCode(), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToastMessage( res.getString(R.string.json_exception));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return new Result(HttpURLConnection.HTTP_UNAUTHORIZED, null);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(Constants.SC_SERVICE_UNAVAILABLE, null);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return new Result(HttpURLConnection.HTTP_BAD_REQUEST , null);
    }

    @Override
    protected void onPostExecute(Result result) {

        if (result.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Intent i = new Intent( mContext.getApplicationContext(), MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
            preferences.edit().putBoolean("logOut", false).apply();
            preferences.edit().putString(Constants.TOKEN, mToken ).apply();

            try {
                mContext.startActivity(i);
                done.ActivityFinish( mToken );
            } catch (Exception e) {
                showToastMessage("Exception : " + e.getMessage());
                e.printStackTrace();
            }

        } else if (result.getResponseCode() == Constants.SC_SERVICE_UNAVAILABLE) {
            parseJWT(mToken);
        } else if (result.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showToastMessage(res.getString(R.string.AuthorizationExpired));
            delegate.LogInProcess();
        }else {
            showToastMessage(res.getString(R.string.unexpected));
            delegate.LogInProcess();
        }
    }

    private void parseJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey( Constants.SECRET.getBytes() )
                    .parseClaimsJws(jwt).getBody();
    
        } catch (ExpiredJwtException e) {
            showToastMessage(res.getString(R.string.AuthorizationExpired));
            delegate.LogInProcess();
            e.printStackTrace();
            return;
        } catch (Exception e) {
            showToastMessage("Exception : " + e.getMessage());
            delegate.LogInProcess();
            e.printStackTrace();
            return;
        }

        showToastMessage( res.getString(R.string.expire_date ) + claims.getExpiration().toString());
        Intent i = new Intent( mContext.getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        preferences.edit().putBoolean("logOut", false).apply();
        mContext.startActivity(i);
        done.ActivityFinish("null");
    }

    class Result {
        private final int responseCode;
        private final JSONObject jsonObject;

        Result(int mResponseCode, JSONObject mJsonObject) {
            responseCode = mResponseCode;
            jsonObject = mJsonObject;
        }

        int getResponseCode() {
            return responseCode;
        }

        JSONObject getJsonObject() {
            return jsonObject;
        }
    }

    private void showToastMessage(String message){
        Toast toast = Toast.makeText( mContext.getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0, 25);
        toast.show();
    }

}