package com.dreamogrammers.sunnat629.hellotwitter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

public class MainActivity extends AppCompatActivity {
    TwitterLoginButton loginButton;
    TwitterSession session;
    TwitterAuthToken authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        loginButton = findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                authToken = session.getAuthToken();


//                String userName = result.data.getUserName();
                long userID = result.data.getId();
//
//                // The Token and Secret is not fixed. Every single time, it changes but it is handled by your code,
//                String token = authToken.token;
//                String secret = authToken.secret;
//
//
//                ((TextView) findViewById(R.id.display)).setText(
//                        "User Name: " + userName +
//                                "\nUser ID: " + userID +
//                                "\nToken Key: " + token +
//                                "\nT.Secret: " + secret);

                // use AsyncTask to retrieve data with a Long parameter for userID
                loadTwitterAPI(userID);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("Failed", exception.toString());
            }
        });
    }

    private void loadTwitterAPI(long userID) {
        new MyTwitterApiClient(session).getCustomService().show(userID)
                .enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        ((TextView) findViewById(R.id.display)).setText(
                                "Name: "+result.data.name
                                        +"\nLocation: "+result.data.location
                                        +"\nFriends: "+result.data.friendsCount
                        );
                        Picasso.with(getBaseContext()).load(result.data.profileImageUrl).
                                resize(250,250)
                                .into((ImageView)findViewById(R.id.imageView));
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.e("Failed", exception.toString());
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}