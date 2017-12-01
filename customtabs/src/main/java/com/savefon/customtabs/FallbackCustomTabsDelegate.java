package com.savefon.customtabs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;

class FallbackCustomTabsDelegate implements CustomTabsDelegate {

    @Override
    public void bindService(@NonNull final Activity activity, @Nullable final CustomTabsCallback callback) {
    }

    @Override
    public void unbindService(@NonNull final Activity activity) {
    }

    @Override
    public void open(@NonNull final Context context, @NonNull final Uri uri) {
    }
}
