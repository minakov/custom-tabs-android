package com.savefon.customtabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;

interface CustomTabsDelegate {
    void bindService(@NonNull Activity activity, @Nullable final CustomTabsCallback callback);

    void unbindService(@NonNull Activity activity);

    void open(@NonNull final Context context, @NonNull final Uri uri);
}
