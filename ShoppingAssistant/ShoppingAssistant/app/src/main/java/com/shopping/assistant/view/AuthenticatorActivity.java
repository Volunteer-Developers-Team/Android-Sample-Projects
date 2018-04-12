package com.shopping.assistant.view;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.shopping.assistant.R;
import com.shopping.assistant.constants.Constants;

import static com.shopping.assistant.constants.Constants.ACCOUNT_TYPE;

public class AuthenticatorActivity extends AccountAuthenticatorActivity implements AsyncResponse, ActivityDone {

    private AccountManager mAccountManager;
    private Button mLoginButton;
    private EditText mPassword;
    private EditText mUserName;
    private Context mContext;

    // UI references.
    private View mLoginFormView;
    private ImageView mSplashView;
    private AnimationDrawable mUstekLogoAnim;
    private SharedPreferences prefs;
    static final String ACCOUNT = "ShoppingAssistant";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_layout);

        mContext = getApplicationContext();
        mAccountManager = AccountManager.get(mContext);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mSplashView = (ImageView) findViewById(R.id.ustek_logo);
        mLoginFormView = findViewById(R.id.email_login_form );
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Animation Drawable for logo transition ...
        mSplashView.setBackgroundResource(R.drawable.logo_anim);
        mUstekLogoAnim = (AnimationDrawable) mSplashView.getBackground();
        mUstekLogoAnim.start();

        // Provides smooth transition
        new CountDownTimer(Constants.SECOND, Constants.SECOND) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
                    final Account availableAccounts[] = mAccountManager.getAccountsByType(ACCOUNT_TYPE);

                    if (availableAccounts.length > 0 && prefs.contains("logOut")) {
                        if (!prefs.getBoolean("logOut", false)) {
                            apiRequest();
                        } else {
                            LogInProcess();
                        }
                    } else {
                        LogInProcess();
                    }
                } else {
                    showMessage( getResources().getString(R.string.permissions));
                    LogInProcess();
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            Constants.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }.start();

        // Check Auth.
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName.setError(null);
                mPassword.setError(null);

                if (TextUtils.isEmpty(mUserName.getText().toString().trim())) {
                    mUserName.setError(getString(R.string.error_field_required));
                    mUserName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(mPassword.getText().toString().trim())) {
                    mPassword.setError(getString(R.string.error_field_required));
                    mPassword.requestFocus();
                    return;
                }
                submit();
            }
        });
    }


    // To download QR code application - ZXing
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe){
                    anfe.printStackTrace();
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return downloadDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                //parseTheURL(contents);
            }

            if(resultCode == RESULT_CANCELED){
                showMessage( getResources().getString(R.string.qr_cancelled) );
                //mQRButton.setEnabled(true);
            }
        }
    }


    private void apiRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            final ApiRequest apiReq = new ApiRequest(this.getApplication());
            apiReq.delegate = this;
            apiReq.done = this;
            apiReq.execute("api/token");
        }
    }

    private void enableForm(){
        mSplashView.setVisibility(View.GONE);
        enableLogInForm();
        mPassword.setText("");
        mUserName.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void enableLogInForm(){
        mLoginFormView.setVisibility(View.VISIBLE);
    }

    private void  disableLogInForm(){
        mLoginFormView.setVisibility(View.GONE);
    }

    @Override
    public void LogInProcess() {
        enableForm();
    }

    private void showMessage(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 25);
        toast.show();
    }

    public void submit() {

        disableLogInForm();
        mSplashView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            final UserLoginTask userLoginTask = new UserLoginTask(getApplicationContext(), mUserName.getText().toString().trim(), mPassword.getText().toString().trim()){
                @Override
                protected void onPostExecute(Intent intent) {
                    if (intent.hasExtra("NoToken")) {
                        if (intent.getBooleanExtra("NoToken", false)) {
                            showMessage(res.getString(R.string.Expired));
                            enableForm();
                        }
                    } else if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        showMessage(res.getString(R.string.Invalid_Credentials));
                        enableForm();
                    } else {
                        final Account account = new Account(ACCOUNT, ACCOUNT_TYPE );
                        String refreshToken = intent.getExtras().getString("AuthTokenUstek");
                        mAccountManager.addAccountExplicitly(account, null , null);
                        mAccountManager.setAuthToken(account, Constants.FULL_ACCESS, refreshToken);
                        prefs.edit().putString(Constants.USERNAME, intent.getExtras().getString("User") ).apply();
                        prefs.edit().putBoolean("logOut", false).apply();
                        prefs.edit().putString(Constants.TOKEN, refreshToken ).apply();
                        Intent i = new Intent( getApplicationContext(), MainActivity.class);
                        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY );
                        startActivity(i);
                        finish();
                    }
                }
            };
            userLoginTask.execute("api/mobilelogin");
            }
        }, Constants.SECOND);
    }

    @Override
    public void ActivityFinish( String token ) {

        if( !token.equals("null") ) {
            final Account account = new Account(ACCOUNT, ACCOUNT_TYPE );
            mAccountManager.addAccountExplicitly(account, null , null);
            mAccountManager.setAuthToken(account, Constants.FULL_ACCESS, token);
            prefs.edit().putBoolean("logOut", false).apply();
            prefs.edit().putString(Constants.TOKEN, token ).apply();
        }
        finish();
    }
}
