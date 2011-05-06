package com.twitter.android;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;


/**
 * An asynchronous task that communicates with Twitter to 
 * retrieve a request token.
 * <br>
 * After receiving the request token from Twitter, 
 * pop a browser to the user to authorize the Request Token.
 * @author Efi MK (Visit my <a href="http://couchpotatoapps.wordpress.com">blog</a>)
 *
 */
public class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

	/**
	 * When task is done call onPostExecute with exception details.
	 * @author Efi MK
	 *
	 */
	public interface TaskListener {
		/**
		 * Indicates that the task was completed.
		 * @param e - Any exception that was thrown during the task.
		 * @param result - Result retrieved by the task.
		 */
		void onPostExecute(Exception e, Object result);
	}
	/**
	 * Tag used for logging.
	 */
	final String TAG = getClass().getName();
	
	
	

	private Exception mException = null;
	/**
	 * Holds the view to show the authorization.
	 */
	private WebView mWebView;

	/**
	 * When task is done call onPostExecute with exception details.
	 */
	private final TaskListener mListener;


	private RequestToken mRequestToken;




	private final Twitter mTwitter;

	/**
	 * 
	 * A constructor
	 * 
	 * @param twitter - A twitter agent.
	 * @param view - Change the authentication URL for this web view.
	 * @param listener - When task is done call onPostExecute with exception details.
	 */
	public OAuthRequestTokenTask(Twitter twitter, WebView view, 
			TaskListener listener) {
		
		this.mTwitter = twitter;
		
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
			mRequestToken = mTwitter.getOAuthRequestToken(TwitterAuthDialog.CALLBACK_URL);
			url = mRequestToken.getAuthorizationURL();
			
		} catch (Exception e) {
			Log.e(TAG, "Error during OAUth retrieve request token", e);
			mException = e;
		}

		return url;
	}

	@Override
	protected void onPostExecute(String result) {
		// Make sure that we didn't have any error while processing.
		if (result != null) {
			Log.i(TAG, "Popping a browser with the authorize URL : " + result);
			mWebView.loadUrl(result);
		}
		else {
			Log.e(TAG, "Got an empty url!");
		}
		// Update the listener.
		if (mListener != null) {
			mListener.onPostExecute(mException, mRequestToken);
		}
	}
	
	

}