package com.twitter.android;

import twitter4j.TwitterException;
import android.net.Uri;
import android.os.Bundle;

/**
 * Callback interface for dialog requests.
 *
 */
public interface TwitterDialogListener {
	
   

        /**
         * Called when a dialog completes.
         *
         * Executed by the thread that initiated the dialog.
         *
         * @param values
         *            The response URI.
         */
        public void onComplete(Uri values);

        /**
         * Called when twitter responds to a dialog with an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onTwitterkError(TwitterException e);

        /**
         * Called when a dialog has an error.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onError(TwitterDialogError e);

        /**
         * Called when a dialog is canceled by the user.
         *
         * Executed by the thread that initiated the dialog.
         *
         */
        public void onCancel();

    }

