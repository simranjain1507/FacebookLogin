package com.example.android.facebooklogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    TextView info;
    ImageView profilePictureView;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Context context;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

//        FacebookSdk.sdkInitialize(getApplicationContext());

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.android.facebooklogin",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


//        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        info = (TextView) findViewById(R.id.info);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        profilePictureView=(ImageView) findViewById(R.id.profile);
        //loginButton.setReadPermissions(Arrays.asList("user_status"));
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_location"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        final Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {

                            info.setText("Successful");
                            loginResult.getAccessToken().getPermissions();
                            info.setText(
                                    "User ID: " + loginResult.getAccessToken().getUserId()
                                            + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken()
                                            + "\n" + "Name: " + profile.getName()
                                            + "\n" + "First Name: " + profile.getFirstName()
                                            + "\n" + "Last Name: " + profile.getLastName()


                            );
                        }
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                             //   final JSONObject jsonObject = response.getJSONObject();
                                String id=loginResult.getAccessToken().getUserId();
                                String imageURL;
                                imageURL="http://graph.facebook.com/"+id+"/picture?type=large";

                                Log.e("Json new ", String.valueOf(object));
                                Log.e("Response new", String.valueOf(response));

                                if (response.getError()!= null){
                                    Toast.makeText(getApplicationContext(),"Error occured", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    try {
                                        String name = object.getString("name");
                                        Log.e("name facebook", name);
                                        String lastname =object.getString("last_name");
                                        String firstname=object.getString("first_name");
                                        String gender=object.getString("gender");
                                        String birthday=object.getString("birthday");
                                        String location=object.getString("location");

                                        info.setText("Name: " + name
                                        + "\n" + "First name: " + firstname
                                        + "\n" + "Last name: " + lastname
                                        + "\n" + "Birthday: " + birthday
                                        + "\n" + "Gender: " + gender
                                        + "\n" + "location: " + location);

                                        Glide.with(getApplicationContext()).load(imageURL).into(profilePictureView);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


//                                }
                                //name=jsonObject.getString("name");
//                                    String name=jsonObject.getString("name");

//
//                                    System.out.print(name);
//                                    System.out.print(lastname);
//                                    System.out.print(firstname);
//                                    System.out.print(gender);
//                                    System.out.print(birthday);
//                                    System.out.print(location);


                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name, first_name, last_name, birthday, gender, location, picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    //
                    @Override
                    public void onCancel() {
                        info.setText("Login attempt cancelled. ");

                    }

                    @Override
                    public void onError(FacebookException error) {
                        info.setText("Login attempt failed. ");

                    }
                });

            }
        });
    }
}
