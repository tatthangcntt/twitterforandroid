/*
 * Copyright 2010 Facebook, Inc.
 * 
 * Code was changed by Efi MK. 
 * Visit my blog: http://couchpotatoapps.wordpress.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twitter.android;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.android.OAuthRequestTokenTask.TaskListener;



/**
 * A dialog box for twitter authentication.
 * @author Efi MK (Visit my <a href="http://couchpotatoapps.wordpress.com">blog</a>)
 *
 */
public class TwitterAuthDialog extends Dialog implements TaskListener {

    static final int TWITTER_BLUE = 0xFF6D84B4;
    static final float[] DIMENSIONS_DIFF_LANDSCAPE = {20, 60};
    static final float[] DIMENSIONS_DIFF_PORTRAIT = {40, 60};
    static final FrameLayout.LayoutParams FILL =
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                         ViewGroup.LayoutParams.FILL_PARENT);
    static final int MARGIN = 4;
    static final int PADDING = 2;

    /**
     * Update listener for various events.
     */
    private TwitterDialogListener mListener;
    /**
     * Display a spinner while waiting for the site to load.
     */
    private ProgressDialog mSpinner;
    /**
     * Display twitter's logon screen.
     */
    private WebView mWebView;
    private LinearLayout mContent;
    private TextView mTitle;

    /**
     * Used for logger.
     */
    final String TAG = getClass().getName();
    

    /**
     * Listen to this schema and act upon.
     */
	private static String	CALLBACK_SCHEME = "x-latify-oauth-twitter";
	/**
	 * A callback URL used to capture a successful authentication.
	 */
	public static String	CALLBACK_URL = CALLBACK_SCHEME + "://callback";
	
	
	/**
	 * A twitter agent.
	 */
	private Twitter mTwitter;
	/**
	 * 
	 */
	private RequestToken mRequestToken;
    
    /**
     * Create a new dialog box.
     * @param context - Context to use.
     * @param consumerKey - Required by OAuth. Register you app in twitter to get this value.
     * @param consumerSecret - Required by OAuth. Register you app in twitter to get this value.
     * @param listener - Listen to various events.
     */
    public TwitterAuthDialog(Context context, String consumerKey, String consumerSecret, TwitterDialogListener listener) {
        super(context);

        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(consumerKey, consumerSecret);
		
        
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpinner = new ProgressDialog(getContext());
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        mContent = new LinearLayout(getContext());
        mContent.setOrientation(LinearLayout.VERTICAL);
        setUpTitle();
        setUpWebView();
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final float scale =
            getContext().getResources().getDisplayMetrics().density;
        int orientation =
            getContext().getResources().getConfiguration().orientation;
        float[] dimensions =
            (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    ? DIMENSIONS_DIFF_LANDSCAPE : DIMENSIONS_DIFF_PORTRAIT;
        addContentView(mContent, new LinearLayout.LayoutParams(
                display.getWidth() - ((int) (dimensions[0] * scale + 0.5f)),
                display.getHeight() - ((int) (dimensions[1] * scale + 0.5f))));
        

		new OAuthRequestTokenTask(mTwitter, mWebView, this).execute();
		
        
    }

    private void setUpTitle() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable icon = getContext().getResources().getDrawable(
                R.drawable.twitter_small);
        mTitle = new TextView(getContext());
        mTitle.setText("Twitter");
        mTitle.setTextColor(Color.WHITE);
        mTitle.setTypeface(Typeface.DEFAULT_BOLD);
        mTitle.setBackgroundColor(TWITTER_BLUE);
        mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
        mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
        mTitle.setCompoundDrawablesWithIntrinsicBounds(
                icon, null, null, null);
        mContent.addView(mTitle);
    }

    private void setUpWebView() {
        mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setWebViewClient(new TwitterAuthDialog.TwitterWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setLayoutParams(FILL);
        mContent.addView(mWebView);
    }

    private class TwitterWebViewClient extends WebViewClient {
    	

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "Redirect URL: " + url);
            // It's our callback URL !
            Uri uri = Uri.parse(url);
            // Just make sure it's not denied URI.
            if (uri.getScheme().equals(CALLBACK_SCHEME) && !uri.toString().contains("denied")) {
            	// Dismiss the dialog
            	dismiss();
            	SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(getContext());
            	new RetrieveAccessTokenTask(mTwitter,mRequestToken,prefs, TwitterAuthDialog.this).execute(uri);
            	mListener.onComplete(uri);
            }
            // Anything else means the user cancelled.
            else {
            	mListener.onCancel();
            	dismiss();
            }
            return true;
        }

        
        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mListener.onError(
                    new TwitterDialogError(description, errorCode, failingUrl));
            TwitterAuthDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String title = mWebView.getTitle();
            if (title != null && title.length() > 0) {
                mTitle.setText(title);
            }
            mSpinner.dismiss();
        }

    }

	@Override
	public void onPostExecute(Exception e, Object result) {
		// If result is of type request token then save it for later use.
		if (result instanceof RequestToken) {
			mRequestToken = (RequestToken) result;
		}
		// In case exception is thrown then close the twitter dialogbox.
		if (e != null) {
			dismiss();
			mListener.onError(
                    new TwitterDialogError(e.getMessage()));
		}
	}
}
