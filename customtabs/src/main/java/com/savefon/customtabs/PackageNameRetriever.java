package com.savefon.customtabs;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class PackageNameRetriever {
    private static final String TAG = "PackageNameRetriever";

    private static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String BETA_PACKAGE = "com.chrome.beta";
    private static final String DEV_PACKAGE = "com.chrome.dev";
    private static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";

    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";

    private PackageNameRetriever() {
    }

    @Nullable
    static String retrieve(@NonNull final Context context) {
        final PackageManager pm = context.getPackageManager();
        final Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"));
        final List<String> packageNames = queryPackageNames(pm, activityIntent);
        if (packageNames.isEmpty()) {
            return null;
        }
        if (packageNames.size() == 1) {
            return packageNames.get(0);
        }
        final String defaultPackageName = retrieve(pm, activityIntent);
        if (!TextUtils.isEmpty(defaultPackageName)
                && !hasSpecializedHandlerIntents(context, activityIntent)
                && packageNames.contains(defaultPackageName)) {
            return defaultPackageName;
        }
        if (packageNames.contains(STABLE_PACKAGE)) {
            return STABLE_PACKAGE;
        }
        if (packageNames.contains(BETA_PACKAGE)) {
            return BETA_PACKAGE;
        }
        if (packageNames.contains(DEV_PACKAGE)) {
            return DEV_PACKAGE;
        }
        if (packageNames.contains(LOCAL_PACKAGE)) {
            return LOCAL_PACKAGE;
        }
        return null;
    }

    /**
     * Get all apps that can handle VIEW intents.
     */
    @NonNull
    private static List<String> queryPackageNames(@NonNull final PackageManager pm, @NonNull final Intent intent) {
        List<String> list = new ArrayList<>();
        for (final ResolveInfo info : pm.queryIntentActivities(intent, 0)) {
            final Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                list.add(info.activityInfo.packageName);
            }
        }
        return list;
    }

    /**
     * Get default intent handler.
     */
    @Nullable
    private static String retrieve(@NonNull final PackageManager pm, @NonNull final Intent intent) {
        final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
        if (resolveInfo != null) {
            return resolveInfo.activityInfo.packageName;
        }
        return null;
    }

    /**
     * Used to check whether there is a specialized handler for a given intent.
     */
    private static boolean hasSpecializedHandlerIntents(@NonNull final Context context, @NonNull final Intent intent) {
        try {
            final PackageManager pm = context.getPackageManager();
            final List<ResolveInfo> handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
            if (handlers == null || handlers.size() == 0) {
                return false;
            }
            for (final ResolveInfo resolveInfo : handlers) {
                final IntentFilter filter = resolveInfo.filter;
                if (filter == null) {
                    continue;
                }
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                    continue;
                }
                if (resolveInfo.activityInfo == null) {
                    continue;
                }
                return true;
            }
        } catch (final RuntimeException e) {
            Log.e(TAG, "Runtime exception while getting specialized handlers");
        }
        return false;
    }
}
