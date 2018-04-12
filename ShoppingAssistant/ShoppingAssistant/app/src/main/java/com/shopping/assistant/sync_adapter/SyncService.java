package com.shopping.assistant.sync_adapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {
    // Sync adapter instance'i için hafıza açar.
    private static SyncAdapter sSyncAdapter = null;
    // İş parçacağı güvencesi için nesne.
    private static final Object sSyncAdapterLock = new Object();
    /*
     * Sync adapter nesnesini tanımlayın.
     */
    @Override
    public void onCreate() {
        /*
         * Sync adapter'i singleton olarak oluşturun.
         * Sync adapter'i eşzamanlamanlanabilir yapın.
         * Paralel eşzamanlamayı engelleyin.
         */
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }
    /**
     * Sistemin sync adapteri çağırmasını sağlayan bir nesne dönün.
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * onPerformSync() metodunu çağırmak için harici işlemlere izin veren
         * nesneyi elde edin. Bu nesne, SyncAdapter yapılandırıcısı super() metodunu
         * çağırdığında temel sınıf kodunda oluşur.
         */
        return sSyncAdapter.getSyncAdapterBinder();
    }
}