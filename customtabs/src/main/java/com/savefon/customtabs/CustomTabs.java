package com.savefon.customtabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;

public class CustomTabs {
    @Nullable
    private static CustomTabsDelegate INSTANCE;

    private static CustomTabsDelegate initialize(@NonNull final Context context) {
        final String packageName = PackageNameRetriever.retrieve(context);
        if (packageName != null) {
            return new ChromeCustomTabsDelegate(packageName);
        } else {
            return new FallbackCustomTabsDelegate();
        }
    }

    @NonNull
    private static CustomTabsDelegate getDelegate(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = initialize(context);
        }
        return INSTANCE;
    }

    /**
     * Binds the Activity to the Custom Tabs Service.
     *
     * @param activity the activity to be bound to the service
     */
    public static void bindService(@NonNull final Activity activity, @Nullable final CustomTabsCallback callback) {
        getDelegate(activity).bindService(activity, callback);
    }

    /**
     * Unbinds the Activity from the Custom Tabs Service.
     *
     * @param activity the activity that is connected to the service
     */
    public static void unbindService(@NonNull final Activity activity) {
        getDelegate(activity).unbindService(activity);
    }

    /**
     * Opens the URL on a Custom Tab if possible.
     */
    public static void open(@NonNull final Context context, @NonNull final Uri uri) {
        getDelegate(context).open(context, uri);
    }
}
