package com.twitter.android;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

/**
 * An asynchronous task that communicates with Twitter to 
 * retrieve a request token.
 * (OAuthGetRequestToken)
 * 
 * After receiving the request token from Twitter, 
 * pop a browser to the user to authorize the Request Token.
 * (OAuthAuthorizeToken)
 * 
 */
public class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

	/**
	 * When task is done call onPostExecute with exception details.
	 * @author Efi MK
	 *
	 */
	public interface TaskListener {
		void onPostExecute(Exception e);
	}
	/**
	 * Tag used for logging.
	 */
	final String TAG = getClass().getName();
	
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	/**
	 * Holds the callback URL.
	 */
	private final String mCallbackURL;

	private Exception mException = null;
	/**
	 * Holds the view to show the authorization.
	 */
	private WebView mWebView;

	/**
	 * When task is done call onPostExecute with exception details.
	 */
	private final TaskListener mListener;

	/**
	 * 
	 * We pass the OAuth consumer and provider.
	 * 
	 * @param 	provider - The OAuthProvider object
	 * @param 	consumer - The OAuthConsumer object
	 * @param callbackURL - Gets result back to this URL.
	 * @param view - Change the authentication URL for this web view.
	 * @param listener - When task is done call onPostExecute with exception details.
	 */
	public OAuthRequestTokenTask(OAuthConsumer consumer,
			OAuthProvider provider, String callbackURL, WebView view, 
			TaskListener listener) {
		this.consumer = consumer;
		this.provider = provider;
		mCallbackURL = callbackURL;
		mWebView = view;
		mListener = listener;
	}

	/**
	 * 
	 * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
	 * 
	 */
	@Override
	protected String doInBackground(Void... params) {
		String url = null;
		try {
			Log.i(TAG, "Retrieving request token from Google servers");
			url = provider.retrieveRequestToken(consumer, mCallbackURL);
			
		} catch (Exception e) {
			Log.e(TAG, "Error during OAUth retrieve request token", e);
			mException = e;
		}

		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i(TAG, "Popping a browser with the authorize URL : " + result);
		mWebView.loadUrl(result);
		// Update the listener.
		if (mListener != null) {
			mListener.onPostExecute(mException);
		}
	}
	
	

}