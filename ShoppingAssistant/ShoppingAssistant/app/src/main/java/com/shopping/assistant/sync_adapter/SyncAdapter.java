package com.shopping.assistant.sync_adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.shopping.assistant.constants.Constants;

import java.io.StringWriter;


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private Context mContext;
    private String sURL;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.i("Sync","Sync DEBUGGING");
        this.mContext = context;
        sURL = PreferenceManager.getDefaultSharedPreferences(context).getString( Constants.SERVER_URL, "NoServerURL" );
        if (!sURL.startsWith("http://") && !sURL.startsWith("https://")) {
            sURL = "http://" + sURL;
        }
    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.mContext = context;
        sURL = PreferenceManager.getDefaultSharedPreferences(context).getString( Constants.SERVER_URL, "NoSServerURL" );
        if (!sURL.startsWith("http://") && !sURL.startsWith("https://")) {
            sURL = "http://" + sURL;
        }
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        /** Data transfer codes*/
        // TODO : Server a request atılıp gerekli bilgiler senkronize edilecek...
        StringWriter sw = new StringWriter();
        try {
            SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            //JSONArray jsonArray = dbHandler.getSession();


        } catch (Exception e) {
            e.printStackTrace();
            syncResult.stats.numParseExceptions++;
            Log.i("Sync", "3-Exception : " + e.getMessage() );
        }

    }
}
