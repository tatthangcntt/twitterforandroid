/**
 * 
 */
package com.twitter.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Various helper methods.
 * @author Efi MK (Visit my <a href="http://couchpotatoapps.wordpress.com">blog</a>)
 *
 */
public class TwitterUtils {

	/**
	 * Gets a twitter object which is initialized with a valid token.
	 * In case the user never authenticated itself against twitter then
	 * the return value will be null.
	 * @param ctx - A context object used to retrieve shared preferences.
	 * @return A twitter object in case user is already authenticated, null otherwise.
	 */
	public static Twitter getTwitterObject(Context ctx, String consumerKey, String consumerSecret) {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = null;
		// Load token info from shared preferences.
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String token = pref.getString(TwitterConstants.OAUTH_TOKEN, null);
		String tokenSecret = pref.getString(TwitterConstants.OAUTH_TOKEN_SECRET, null);
		
		// If any of them is null then we didn't authenticated it before.
		if (token != null && tokenSecret != null) {
			
			AccessToken accessToken = new AccessToken(token, tokenSecret);
			twitter = factory.getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			twitter.setOAuthAccessToken(accessToken);
		}

	    return twitter;
	}
}
