package com.twitter.android;

import twitter4j.Twitter;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.android.OAuthRequestTokenTask.TaskListener;

/**
 * Retrieve access token after a successful authentication/autherization.
 * @author Efi MK (Visit my <a href="http://couchpotatoapps.wordpress.com">blog</a>)
 *
 */
public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

	 /**
     * Used for logger.
     */
    final String TAG = getClass().getName();
	/**
	 * Save token received from twitter.
	 */
	private SharedPreferences prefs;
	/**
	 * A twitter agent.
	 */
	private final Twitter mTwitter;
	/**
	 * A request token used by the twitter agent.
	 */
	private final RequestToken mToken;
	/**
	 * Update listener after task is completed.
	 */
	private final TaskListener mListener;
	
	
	
	public RetrieveAccessTokenTask(Twitter twitter, RequestToken token, SharedPreferences prefs, TaskListener listener) {
		
		this.mTwitter = twitter;
		this.mToken = token;
		
		this.prefs=prefs;
		this.mListener = listener;
	}


	private Exception mException = null;
	
	/**
	 * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret 
	 * for future API calls.
	 */
	@Override
	protected Void doInBackground(Uri...params) {
		final Uri uri = params[0];
		final String oauth_verifier = uri.getQueryParameter(TwitterConstants.OAUTH_VERIFIER);

		try {
			AccessToken accessToken = mTwitter.getOAuthAccessToken(mToken, oauth_verifier);
			

			final Editor edit = prefs.edit();
			edit.putString(TwitterConstants.OAUTH_TOKEN, accessToken.getToken());
			edit.putString(TwitterConstants.OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
			edit.commit();

			Log.i(TAG, "OAuth - Access Token Retrieved");
			
		} catch (Exception e) {
			Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// Update the listener.
		if (mListener != null) {
			mListener.onPostExecute(mException, null);
		}
	}


	
}