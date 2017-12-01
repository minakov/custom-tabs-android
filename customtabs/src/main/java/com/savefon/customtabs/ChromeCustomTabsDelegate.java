package com.savefon.customtabs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

class ChromeCustomTabsDelegate implements CustomTabsDelegate {
    @NonNull
    private final String packageName;
    @Nullable
    private ServiceConnection connection;

    ChromeCustomTabsDelegate(@NonNull final String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void bindService(@NonNull final Activity activity, @Nullable final CustomTabsCallback callback) {
        if (connection != null) {
            return;
        }
        connection = ServiceConnection.bind(activity, packageName, callback);
    }

    @Override
    public void unbindService(@NonNull final Activity activity) {
        if (connection == null) {
            return;
        }
        connection.unbind(activity);
        connection = null;
    }

    @Override
    public void open(@NonNull final Context context, @NonNull final Uri uri) {
        if (connection != null) {
            final CustomTabsIntent intent = new CustomTabsIntent.Builder(connection.session).build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                final Uri value = Uri.parse(Intent.URI_ANDROID_APP_SCHEME + "//" + context.getPackageName());
                intent.intent.putExtra(Intent.EXTRA_REFERRER, value);
            }
            intent.intent.setPackage(packageName);
            intent.launchUrl(context, uri);
        }
    }

    private static class ServiceConnection extends CustomTabsServiceConnection {
        @Nullable
        private final CustomTabsCallback callback;
        @Nullable
        private CustomTabsSession session;

        private ServiceConnection(@Nullable final CustomTabsCallback callback) {
            this.callback = callback;
        }

        @Nullable
        private static ServiceConnection bind(@NonNull final Activity activity, @NonNull final String packageName, @Nullable final CustomTabsCallback callback) {
            final ServiceConnection connection = new ServiceConnection(callback);
            if (CustomTabsClient.bindCustomTabsService(activity, packageName, connection)) {
                return connection;
            }
            return null;
        }

        private void unbind(@NonNull final Activity activity) {
            if (session != null) {
                activity.unbindService(this);
                session = null;
            }
        }

        @Override
        public void onCustomTabsServiceConnected(@NonNull final ComponentName name, @NonNull final CustomTabsClient client) {
            client.warmup(0L);
            session = client.newSession(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (session != null) {
                session = null;
            }
        }

        @Override
        public void onBindingDied(ComponentName name) {
            if (session != null) {
                session = null;
            }
        }
    }
}
