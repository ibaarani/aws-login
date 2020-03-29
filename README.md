#aws-login

# Building Android Applications with AWS Amplify

In this workshop we'll learn how to build cloud-enabled mobile applications with Andriod and [AWS Amplify]().

### Topics we'll be covering:

- [Authentication](#adding-authentication)


## Getting Started - Creating the Android Studio Project

To get started, we first need to create a new Andriod project.

If you already have a project set up, skip to the next step. Otherwise, in Android Studio:

- select `File > New > New Project`
- choose `Empty Activity`
- choose `Java` for application type and take note of where the project will be saved to (we'll need this information in the next step
- choose `Finish`

Now it's time to build and run the application.

## Installing the CLI & Initializing a new AWS Amplify Project

### Installing the CLI

Next, we'll install the AWS Amplify CLI:

```bash
npm install -g @aws-amplify/cli
```

Now we need to configure the CLI with our credentials:

```js
amplify configure
```

> If you'd like to see a video walkthrough of this configuration process, click [here](https://www.youtube.com/watch?v=fWbM5DLh25U).

Here we'll walk through the `amplify configure` setup. Once you've signed in to the AWS console, continue:

- Specify the AWS Region: **us-east-1**
- Specify the username of the new IAM user: **amplify-workshop-user**
  > In the AWS Console, click **Next: Permissions**, **Next: Tags**, **Next: Review**, & **Create User** to create the new IAM user. Then, return to the command line & press Enter.
- Enter the access key of the newly created user:  
  ? accessKeyId: **(<YOUR_ACCESS_KEY_ID>)**  
  ? secretAccessKey: **(<YOUR_SECRET_ACCESS_KEY>)**
- Profile Name: **amplify-workshop-user**

### Initializing A New Project

```bash
amplify init
```

- Enter a name for the project: **amplifyandroidapp**
- Enter a name for the environment: **master**
- Choose your default editor: **Visual Studio Code (or your default editor)**
- Please choose the type of app that you're building: **android**
- Where is your Res directory? **(app/src/main/res)**
- Do you want to use an AWS profile? **Y**
- Please choose the profile you want to use: **amplify-workshop-user**

```bash
amplify push
```

Now an `awsconfiguration.json` file has be created with your configuration and will be updated as features get added to your project by the Amplify CLI. The file is placed in the `./app/src/main/res/raw` directory of your Android Studio project and automatically used by the SDKs at runtime.

To view the status of the amplify project at any time, you can run the Amplify `status` command:

```sh
amplify status
```

## Adding Authentication

### Adding a Cognito User Pool

```sh
amplify add auth #accept default configuration
amplify push
```

### Setting Up the Dependencies

First add the following dependencies to your App `build.gradle`

```gradle
//For AWSMobileClient only:
implementation 'com.amazonaws:aws-android-sdk-mobile-client:2.13.+'

//For the drop-in UI also:
implementation 'com.amazonaws:aws-android-sdk-auth-userpools:2.13.+'
implementation 'com.amazonaws:aws-android-sdk-auth-ui:2.13.+'
```

You will also need to set the `minSdkVersion` to `23` for the drop-in UI.

```gradle
minSdkVersion 23
```

Make sure the following permissions are enabled in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### Updating the Main Activity

`AWSMobileClient` ships with a drop-in Activity for handling sign in and sign up functionality. Replace `MainActivity.java` with the following:

```java
package com.example.amplifyworkshopnyc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;

public class MainActivity extends AppCompatActivity {

    // tag for logs
    private static final String TAG = "MainActivity";

    // instance of mobile client
    private final AWSMobileClient CLIENT = AWSMobileClient.getInstance();

    // launches auth UI
    protected void showSignIn() {
        CLIENT.showSignIn(
                this,
                SignInUIOptions.builder()
                        .nextActivity(MainActivity.class)
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
}

```

Build and run the app and you should see the drop-in UI for authentication.
