package com.congwiny.android11.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import androidx.core.content.PermissionChecker;

import java.lang.reflect.Method;

public class SimCardUtils {
    private static final String TAG = "SimCardUtils";

    public static String getSimIccId(Context context) {
        String iccId = "";
        int result = PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (result != PermissionChecker.PERMISSION_GRANTED) {
            return iccId;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager from = SubscriptionManager.from(context.getApplicationContext());
            if (from != null) {
                try {
                    Method method = SubscriptionManager.class.getMethod("getDefaultDataSubscriptionInfo");
                    SubscriptionInfo info = (SubscriptionInfo) method.invoke(from);
                    iccId = info.getIccId();
                    int id = info.getSubscriptionId();
                    Log.e(TAG, "iccId=" + iccId + ",SubscriptionId=" + id);
                } catch (Exception e) {
                    Log.e(TAG, "getSimIccId", e);
                }
            }
        }
        return iccId;
    }
}
