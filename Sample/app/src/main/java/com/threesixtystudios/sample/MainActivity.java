package com.threesixtystudios.sample;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // tag for logs
    private static final String TAG = "MainActivity";

    // instance of mobile client
    private final AWSMobileClient CLIENT = AWSMobileClient.getInstance();

    // launches auth UI


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CLIENT.initialize(getApplicationContext(), new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()) {
                    case SIGNED_IN:
                        Log.d(TAG, "user signed in: " + userStateDetails.getUserState());
                        break;
                    case SIGNED_OUT:
                        showSignIn();
                        break;
                    default:
                        CLIENT.signOut();
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("INIT", "Initialization error.", e);
            }
        });
    }

    protected void showSignIn() {
        CLIENT.showSignIn(
                this,
                SignInUIOptions.builder()
                        .nextActivity(main2.class)
                        .build(),
                new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails result) {
                        Log.d(TAG, "onResult: " + result.getUserState());
                        switch (result.getUserState()) {
                            case SIGNED_IN:
                                Log.i("INIT", "logged in!");
                                break;
                            case SIGNED_OUT:
                                Log.i(TAG, "onResult: User did not choose to sign-in");
                                break;
                            default:
                                AWSMobileClient.getInstance().signOut();
                                break;
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "onError: ", e);
                    }
                }
        );
    }
}