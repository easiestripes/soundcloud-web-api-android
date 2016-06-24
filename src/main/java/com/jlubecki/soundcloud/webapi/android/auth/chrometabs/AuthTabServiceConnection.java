/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Jacob Lubecki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jlubecki.soundcloud.webapi.android.auth.chrometabs;

import android.content.ComponentName;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import com.jlubecki.soundcloud.webapi.android.auth.AuthenticationCallback;
import java.lang.ref.WeakReference;

/**
 * Created by Jacob on 6/22/16.
 */
public class AuthTabServiceConnection extends CustomTabsServiceConnection {

  private static final String AUTH_BASE_URL = "https://www.soundcloud.com/connect";

  private final WeakReference<AuthenticationCallback> authCallbackReference;
  private final WeakReference<AuthTabNavigationCallback> navCallbackReference;
  private CustomTabsClient tabsClient;
  private CustomTabsSession tabsSession;

  public AuthTabServiceConnection(@NonNull AuthenticationCallback callback) {
    this.authCallbackReference = new WeakReference<>(callback);
    this.navCallbackReference = new WeakReference<>(new AuthTabNavigationCallback());
  }

  public AuthTabServiceConnection(@NonNull AuthenticationCallback authenticationCallback,
      @Nullable AuthTabNavigationCallback navigationCallback) {
    this.authCallbackReference = new WeakReference<>(authenticationCallback);
    this.navCallbackReference = new WeakReference<>(navigationCallback);
  }

  @Override public void onCustomTabsServiceConnected(ComponentName componentName,
      CustomTabsClient customTabsClient) {

    tabsClient = customTabsClient;
    tabsClient.warmup(0);

    tabsSession = tabsClient.newSession(navCallbackReference.get());

    if (tabsSession != null) {
      tabsSession.mayLaunchUrl(Uri.parse(AUTH_BASE_URL), null, null);
    }

    AuthenticationCallback callback = authCallbackReference.get();
    if(callback != null) {
      callback.onReadyToAuthenticate();
    }
  }

  @Override public void onServiceDisconnected(ComponentName componentName) {
    tabsClient = null;
    tabsSession = null;

    AuthenticationCallback callback = authCallbackReference.get();
    if(callback != null) {
      callback.onAuthenticationEnded();
    }
  }

  public CustomTabsSession getSession() {
    return tabsSession;
  }
}
