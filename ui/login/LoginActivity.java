package com.benjaminsommer.dailygoals.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.benjaminsommer.dailygoals.R;
import com.benjaminsommer.dailygoals.objects.Resource;
import com.benjaminsommer.dailygoals.util.LoadProfileImage;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, SyncDialog.ISyncDialogToActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;


    private CardView boxLogin;
    private FloatingActionButton fabMail, fabGoogle, fabFacebook;
    private TextView tvRegister, tvAccName, tvAccMail, tvProviderHeader;
    private ImageView accPic, ivLinkMail, ivLinkFB, ivLinkGoogle;
    private ImageButton accSettings;
    private Switch switchMainProfile;
    private Button btnSignOut;
    private RelativeLayout boxAccount;

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog progressDialog;

    private int localGoalsCount, remoteGoalsCount;

    private final static String GOOGLE_WEB_CLIENT_ID = "9317065755-tvufd6339j32md7ciaom1rd3r3jie24s.apps.googleusercontent.com";

    //// [START FIREBASE]
    // Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;
    //// [END FIREBASE]

    private LoginViewModel viewModel;
    private int goalsLocal = 0, goalsRemote = 0;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        //// [START FIREBASE]
        // initiatlize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        //// [END FIREBASE]

        // set xml layout
        setContentView(R.layout.activity_login);

        // initialize Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.loginActivity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initialize facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        // initialize google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // view model setup
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        viewModel.getGoalsCount().observe(this, new Observer<Resource<List<Integer>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Integer>> listResource) {
                if (listResource.status.equals(com.benjaminsommer.dailygoals.objects.Status.SUCCESS)) {
                    startDataSync(listResource.data);
                }
            }
        });
        viewModel.getSyncState().observe(this, new Observer<Resource<Integer>>() {
            @Override
            public void onChanged(@Nullable Resource<Integer> integerResource) {
                if (integerResource.status.equals(com.benjaminsommer.dailygoals.objects.Status.SUCCESS))  {
                    finishDataSync(integerResource.data);
                }
            }
        });


//        LiveData<Resource<Integer>> resourceLocal = viewModel.getGoalsLocal();
//        resourceLocal.observe(this, new Observer<Resource<Integer>>() {
//            @Override
//            public void onChanged(@Nullable Resource<Integer> integerResource) {
//                if (integerResource.data != null) {
//                    goalsLocal = integerResource.data;
//                    Log.d(TAG, "Local: " + String.valueOf(integerResource.data));
//                }
//            }
//        });
//        LiveData<Integer> resourceRemote = viewModel.getGoalsRemote();
//        resourceRemote.observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(@Nullable Integer integer) {
//                goalsRemote = integer;
//                Log.d(TAG, "Remote: " + String.valueOf(integer));
//            }
//        });

        //// initiatile views
        boxLogin = (CardView) findViewById(R.id.loginActivity_box_loginSection);
        fabMail = (FloatingActionButton) findViewById(R.id.loginActivity_fab_email);
        fabGoogle = (FloatingActionButton) findViewById(R.id.loginActivity_fab_google);
        fabFacebook = (FloatingActionButton) findViewById(R.id.loginActivity_fab_facebook);
        ivLinkMail = (ImageView) findViewById(R.id.loginActivity_link_email);
        ivLinkFB = (ImageView) findViewById(R.id.loginActivity_link_facebook);
        ivLinkGoogle = (ImageView) findViewById(R.id.loginActivity_link_google);
        tvRegister = (TextView) findViewById(R.id.loginActivity_loginSection_register);
        boxAccount = (RelativeLayout) findViewById(R.id.loginActivity_acc_box);
        tvProviderHeader = (TextView) findViewById(R.id.loginActivity_loginSection_providerHeader);
        accPic = (ImageView) findViewById(R.id.loginActivity_acc_profilePic);
        tvAccName = (TextView) findViewById(R.id.loginActivity_acc_profileName);
        tvAccMail = (TextView) findViewById(R.id.loginActivity_acc_profileEmail);
        switchMainProfile = (Switch) findViewById(R.id.loginActivity_acc_switchMainProfile);
        accSettings = (ImageButton) findViewById(R.id.loginActivity_acc_settings);
        btnSignOut = (Button) findViewById(R.id.loginActivity_acc_signOut);

        // Google Sign In
        fabGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if current user is checked in to Google (bool 1)
                if (getAuthProviders(mAuth.getCurrentUser())[1]) {
                    updateUI(mAuth.getCurrentUser(), 2);
                } else {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }

            }
        });

        // Facebook Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                decideOnFacebookAuth(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        fabFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if current user is checked in to Google (bool 2)
                if (getAuthProviders(mAuth.getCurrentUser())[2]) {
                    updateUI(mAuth.getCurrentUser(), 3);
                } else {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
                }
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("AccessTokenTracker", "onCurrentAccessTokenChanged()");
                if (currentAccessToken == null) {
//                    facebookBox.setVisibility(View.GONE);
//                    user.setFacebookLoginStatus(false);
                }
            }
        };
        accessTokenTracker.startTracking();

        //// [START FIREBASE]
        // OnClickListener Email Register/Signin
        View.OnClickListener eMailOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean isEmailSignedIn = false;
//                if (mAuth.getCurrentUser() != null) {
//                    for (UserInfo userInfo : mAuth.getCurrentUser().getProviderData()) {
//                        if (userInfo.getProviderId().equals("password")) {
//                            isEmailSignedIn = true;
//                            break;
//                        }
//                    }
//                }

                if (v.getId() == fabMail.getId()) {
                    // check if current user is checked in to Google (bool 0)
                    if (getAuthProviders(mAuth.getCurrentUser())[0]) {
                        updateUI(mAuth.getCurrentUser(), 1);
                    } else {
                        LoginEmailDialog dialog = new LoginEmailDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt(LoginEmailDialog.SIGNIN_REGISTER_TYPE, 1);
                        dialog.setArguments(bundle);
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.add(dialog, LoginEmailDialog.TAG);
                        transaction.commit();
                    }
                }  else if (v.getId() == tvRegister.getId()) {
                    LoginEmailDialog dialog = new LoginEmailDialog();
                    Bundle bundle = new Bundle();
                    bundle.putInt(LoginEmailDialog.SIGNIN_REGISTER_TYPE, 0);
                    dialog.setArguments(bundle);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.add(dialog, LoginEmailDialog.TAG);
                    transaction.commit();
                }
            }
        };
        fabMail.setOnClickListener(eMailOnClickListener);
        tvRegister.setOnClickListener(eMailOnClickListener);

        // settings click and popup section
        accSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(LoginActivity.this, accSettings);
                popup.getMenuInflater().inflate(R.menu.menu_login_email_settings, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.menuLoginEmailSettings_changeAccount) {
                            // get account information
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = user.getDisplayName();
                            String email = user.getEmail();
                            for (UserInfo userInfo : user.getProviderData()) {
                                if (userInfo.getProviderId().equals("password")) {
                                    name = userInfo.getDisplayName();
                                    email = userInfo.getEmail();
                                }
                            }
                            LoginEmailDialog dialog = new LoginEmailDialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt(LoginEmailDialog.SIGNIN_REGISTER_TYPE, 3);
                            bundle.putString(LoginEmailDialog.FIREBASE_USER_NAME, name);
                            bundle.putString(LoginEmailDialog.FIREBASE_USER_MAIL, email);
                            dialog.setArguments(bundle);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.add(dialog, LoginEmailDialog.TAG);
                            transaction.commit();
                        } else if (item.getItemId() == R.id.menuLoginEmailSettings_changePassword) {
                            LoginEmailDialog dialog = new LoginEmailDialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt(LoginEmailDialog.SIGNIN_REGISTER_TYPE, 4);
                            dialog.setArguments(bundle);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.add(dialog, LoginEmailDialog.TAG);
                            transaction.commit();
                        } else if (item.getItemId() == R.id.menuLoginEmailSettings_deleteUser) {
                            deleteEmailUser();
                        } else if (item.getItemId() == R.id.menuLoginEmailSettings_unlinkAccount) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            for (UserInfo userInfo : user.getProviderData()) {
                                if (userInfo.getProviderId().equals("password")) {
                                    String emailProviderId = userInfo.getProviderId();
                                    unlinkAccounts(emailProviderId);
                                    break;
                                }
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });

        // LogOut Button Listener
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutAccount();
            }
        });


//        // click listener for password forgotten section
//        emailForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LoginEmailDialog dialog = new LoginEmailDialog();
//                Bundle bundle = new Bundle();
//                bundle.putInt(LoginEmailDialog.SIGNIN_REGISTER_TYPE, 2);
//                dialog.setArguments(bundle);
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction transaction = fm.beginTransaction();
//                transaction.add(dialog, LoginEmailDialog.TAG);
//                transaction.commit();
//            }
//        });

        //// [END FIREBASE]


    }

    //// [START OnActivityResult]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "GoogleSignIn:success");
                GoogleSignInAccount gAcct = result.getSignInAccount();
                decideOnGoogleAuth(gAcct);
            }
        }
    }
    //// [END OnActivityResult]

    @Override
    protected void onStart() {
        super.onStart();

        //// [START Firebase]
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        decideForShownAcc(currentUser);
        //// [END Firebase]

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//
//            Log.d(TAG, "Got cached Google sign-in");
//            GoogleSignInResult result = opr.get();
//            handleGoogleSignInResult(result);
//        }  else {
//
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleGoogleSignInResult(googleSignInResult);
//                }
//            });
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    //// [START FIREBASE FACEBOOK AUTH]
    // method to decide about google login (linking or simple sign in)
    private void decideOnFacebookAuth(final AccessToken token) {
        // if no firebase user is signed in, just sign in with facebook
        // if there is a firebase user already signed in, show dialog if linking accounts or not
        if (mAuth.getCurrentUser() == null) {
            firebaseAuthWithFacebook(token);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Verlinkung");
            builder.setMessage("Du bist bereits mit einem Account angemeldet. Möchtest Du die Accounts verlinken?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    linkAccounts(credential, 2); // link account with Facebook Sign In Credentials = 2
                }
            });
            builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    firebaseAuthWithFacebook(token);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
    //// [END FIREBASE FACEBOOK AUTH]

    //// [START FIREBASE GOOGLE AUTH]
    // authenticate in Firebase with Google credentials
    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d(TAG, "firebaseAuthWithFacebook:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithFacebookCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, 3);
                            startSyncProcess();
                        } else {
                            Log.w(TAG, "signInWithFacebookCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Facebook failed.", Toast.LENGTH_SHORT);
                            updateUI(mAuth.getCurrentUser(), 0);
                        }
                    }
                });
    }
    //// [END FIREBASE GOOGLE AUTH]

    //// [START FIREBASE GOOGLE AUTH]
    // method to decide about google login (linking or simple sign in)
    private void decideOnGoogleAuth(final GoogleSignInAccount googleAcct) {
        // if no firebase user is signed in, just sign in with google
        // if there is a firebase user already signed in, show dialog if linking accounts or not
        if (mAuth.getCurrentUser() == null) {
            firebaseAuthWithGoogle(googleAcct);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Verlinkung");
            builder.setMessage("Du bist bereits mit einem Account angemeldet. Möchtest Du die Accounts verlinken?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(googleAcct.getIdToken(), null);
                    linkAccounts(credential, 1); // link account with Google Sign In Credentials = 1
                }
            });
            builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    firebaseAuthWithGoogle(googleAcct);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
    //// [END FIREBASE GOOGLE AUTH]

    //// [START FIREBASE GOOGLE AUTH]
    // authenticate in Firebase with Google credentials
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithGoogleCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, 2);
                            startSyncProcess();
                        } else {
                            Log.w(TAG, "signInWithGoogleCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Google failed.", Toast.LENGTH_SHORT);
                            updateUI(mAuth.getCurrentUser(), 0);
                        }
                        hideProgressDialog();
                    }
                });
    }
    //// [END FIREBASE GOOGLE AUTH]

    //// [START FIREBASE AUTH]
    // link Google Account with current Firebase user
    private void linkAccounts(AuthCredential credential, final int type) {
        showProgressDialog();
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user, type + 1); // take credential type and bring it over to UI
                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Linking of accounts failed.", Toast.LENGTH_SHORT);
                            signOutAccount();
                        }
                        hideProgressDialog();
                    }
                });    }
    //// [END FIREBASE AUTH]

    //// [START FIREBASE AUTH]
    // link Google Account with current Firebase user
    private void unlinkAccounts(String providerId) {
        showProgressDialog();
        mAuth.getCurrentUser().unlink(providerId)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "unlink:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            decideForShownAcc(user);
                        } else {
                            Log.w(TAG, "unlink:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Unlinking of accounts failed.", Toast.LENGTH_SHORT);
                        }
                        hideProgressDialog();
                    }
                });    }
    //// [END FIREBASE AUTH]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //// [START FIREBASE]
    // receive feedback from login/signin dialog
    public void getEmailLoginSigninInfo(final int type, final String name, final String email, final String password) {
        if (mAuth.getCurrentUser() == null) {
            if (type == 1) {
                signInEmailAccount(email, password);
            } else if (type == 0){
                createEmailAccount(name, email, password);
            }
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("Verlinkung");
            builder.setMessage("Du bist bereits mit einem Account angemeldet. Möchtest Du die Accounts verlinken?");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    if (type == 1) {
                        linkAccounts(credential, 0);
                    } else if (type == 0){
                        boolean isSuccessful = createEmailAccount(name, email, password);
                        if (isSuccessful) {
                            linkAccounts(credential, 0);
                        }
                    }
                }
            });
            builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (type == 1) {
                        signInEmailAccount(email, password);
                    } else if (type == 0){
                        createEmailAccount(name, email, password);
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }


    }
    //// [END FIREBASE]


    //// [START FIREBASE]
    // Register new user with Email
    private boolean createEmailAccount(final String name, String email, String password) {
        Log.d(TAG, "createEmailAccount:" + email);

        showProgressDialog();

        // start create user with email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateEmailUI(user);
                            // update name
                            updateEmailName(user, name);
                        } else {
                            // if sign in fails, display a message to the user
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Autenticateion Email failed.", Toast.LENGTH_SHORT).show();
                            decideForShownAcc(mAuth.getCurrentUser());
                        }
                        hideProgressDialog();
                    }
                });
        return true;
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // Sign in new user with Email
    private void signInEmailAccount(String email, String password) {
        Log.d(TAG, "signInEmailAccount:" + email);

        showProgressDialog();

        // start create user with email
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInUserWithEmail:success");
                            updateUI(mAuth.getCurrentUser(), 1);
                            startSyncProcess();
                        } else {
                            // if sign in fails, display a message to the user
                            Log.w(TAG, "signInUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Autenticateion Email failed.", Toast.LENGTH_SHORT).show();
                            decideForShownAcc(mAuth.getCurrentUser());
                        }
                        hideProgressDialog();
                    }
                });
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // update email user name
    public void updateEmailName(FirebaseUser user, String name) {
        if (user == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseUser updatedUser = mAuth.getCurrentUser();
                    Log.d(TAG, "User display name updated");
                    updateUI(updatedUser, 1);
                }
            }
        });
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // update email user password
    public void updateEmailPassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User password updated");
                    Toast.makeText(LoginActivity.this, "Password updated", Toast.LENGTH_SHORT);
                }
            }
        });
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // send password reset
    public void sendEmailPasswordReset(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email with new password sent");
                    Toast.makeText(LoginActivity.this, "E-Mail mit neuem Passwort wurde versendet", Toast.LENGTH_SHORT);
                }
            }
        });
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // update email address of user (must be signed in)
    public void updateEmailAddress(String email) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "User email address updated");
                    FirebaseUser updatedUser = mAuth.getCurrentUser();
                    updateUI(updatedUser, 1);
                }
            });
        }
    }
    //// [END FIREBASE]

    //// [START FIREBASE]
    // delete email user
    public void deleteEmailUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Willst Du den Account wirklich löschen?");
        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User deleted");
                            Toast.makeText(LoginActivity.this, "Der Benutzer wurde erfolgreich gelöscht", Toast.LENGTH_SHORT);
                            decideForShownAcc(mAuth.getCurrentUser());
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //// [END FIREBASE]

    //// [START FIREBASE SIGNOUT]
    private void signOutAccount() {

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            Log.d(TAG, "FacebookSignOut:onSuccess");
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "GoogleSignOut:onSuccess");
                            }
                        }
                    }
            );
        }
        mAuth.signOut();
        updateUI(null, 0);
        Toast.makeText(LoginActivity.this, "User signed out", Toast.LENGTH_SHORT);

//        if  (mAuth.getCurrentUser() != null) {
//
//            for (UserInfo userInfo : mAuth.getCurrentUser().getProviderData()) {
//
//                if (userInfo.getProviderId().equals("google.com")) {
//                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                            new ResultCallback<Status>() {
//                                @Override
//                                public void onResult(Status status) {
//                                    if (status.isSuccess()) {
//                                        Log.d(TAG, "GoogleSignOut:onSuccess");
//                                    }
//                                }
//                            }
//                    );
//                } else if (userInfo.getProviderId().equals("facebook.com")) {
//                    LoginManager.getInstance().logOut();
//                }
//            }
//            mAuth.signOut();
//            updateUI(null);
//            Toast.makeText(LoginActivity.this, "User signed out", Toast.LENGTH_SHORT);
//
//        }

    }
    //// [END FIREBASE SIGNOUT]

    // check if user is already logged in to facebook
    public boolean isLoggedInToFacebook() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //// [START FIREBASE]
    private void updateUI(FirebaseUser user, int providerType) {
        // providerType: 1 = mail, 2 = google, 3 = facebook

        if (user == null) {
            boxAccount.setVisibility(View.GONE);
            ivLinkMail.setVisibility(View.GONE);
            ivLinkGoogle.setVisibility(View.GONE);
            ivLinkFB.setVisibility(View.GONE);
            tvRegister.setVisibility(View.VISIBLE);
            updateFABMail(false);
            updateFABGoogle(false);
            updateFABFacebook(false);
        } else {
            boxAccount.setVisibility(View.VISIBLE);
            tvRegister.setVisibility(View.GONE);

            // define boolean to check if there is more than one provider
            // as soon as one provider is signed in, there is providerId firebase + another --> 2
            int sizeProviders = user.getProviderData().size();
            // show main profile switch or not
            if (sizeProviders >= 2) {
                switchMainProfile.setVisibility(View.VISIBLE);
            } else {
                switchMainProfile.setVisibility(View.GONE);
            }

            boolean[] loggedInProviders = getAuthProviders(user);
            updateFABMail(loggedInProviders[0]);
            updateFABGoogle(loggedInProviders[1]);
            updateFABFacebook(loggedInProviders[2]);

//            for (UserInfo ui: user.getProviderData()) {
//                if (ui.getProviderId().equals("password")) {
//                    fabMail.setForeground(getResources().getDrawable(R.drawable.ic_done_black_24px));
//                    fabMail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccentAlpha)));
//                    fabMail.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.materialGrey500)));
//                } else {
//                    fabMail.clearColorFilter();
//                    fabMail.setForeground(null);
//                    fabMail.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent
//                    )));
//                }
//            }

            if (providerType == 1) {
                for (UserInfo userInfo : user.getProviderData()) {
                    if (userInfo.getProviderId().equals("password")) {
                        tvAccName.setText(userInfo.getDisplayName());
                        if (userInfo.getPhotoUrl() != null) {
                            new LoadProfileImage(accPic).execute(userInfo.getPhotoUrl().toString());
                        } else {
                            accPic.setImageResource(R.drawable.icon_account_circle_24dp);
                        }

                    }
                }
                tvProviderHeader.setText("E-Mail");
                Drawable mail = getResources().getDrawable(R.drawable.icon_email_24dp);
                mail.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                tvProviderHeader.setCompoundDrawablesWithIntrinsicBounds(mail, null, null, null);
                tvAccMail.setText(user.getEmail());
                ivLinkMail.setVisibility(View.VISIBLE);
                ivLinkGoogle.setVisibility(View.GONE);
                ivLinkFB.setVisibility(View.GONE);
            } else if (providerType == 2) {
                for (UserInfo userInfo : user.getProviderData()) {
                    if (userInfo.getProviderId().equals("google.com")) {
                        tvAccName.setText(userInfo.getDisplayName());
                        if (userInfo.getPhotoUrl() != null) {
                            new LoadProfileImage(accPic).execute(userInfo.getPhotoUrl().toString());
                        } else {
                            accPic.setImageResource(R.drawable.icon_account_circle_24dp);
                        }

                    }
                }
                tvProviderHeader.setText("Google");
                Drawable mail = getResources().getDrawable(R.drawable.ic_google_plus_box);
                mail.setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                tvProviderHeader.setCompoundDrawablesWithIntrinsicBounds(mail, null, null, null);
                tvAccMail.setText(user.getEmail());
                ivLinkMail.setVisibility(View.GONE);
                ivLinkGoogle.setVisibility(View.VISIBLE);
                ivLinkFB.setVisibility(View.GONE);
            } else if (providerType == 3) {
                for (UserInfo userInfo : user.getProviderData()) {
                    if (userInfo.getProviderId().equals("facebook.com")) {
                        tvAccName.setText(userInfo.getDisplayName());
                        if (userInfo.getPhotoUrl() != null) {
                            new LoadProfileImage(accPic).execute(userInfo.getPhotoUrl().toString());
                        } else {
                            accPic.setImageResource(R.drawable.icon_account_circle_24dp);
                        }

                    }
                }
                tvProviderHeader.setText("Facebook");
                Drawable mail = getResources().getDrawable(R.drawable.ic_facebook_box);
                mail.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                tvProviderHeader.setCompoundDrawablesWithIntrinsicBounds(mail, null, null, null);
                tvAccMail.setText(user.getEmail());
                ivLinkMail.setVisibility(View.GONE);
                ivLinkGoogle.setVisibility(View.GONE);
                ivLinkFB.setVisibility(View.VISIBLE);
            }

        }

    }

    private void updateFABMail(boolean loggedIn) {
        if (loggedIn) {
            fabMail.setImageResource(R.drawable.ic_done_black_24px);
        } else {
            fabMail.setImageResource(R.drawable.icon_email);
        }
    }

    private void updateFABGoogle(boolean loggedIn) {
        if (loggedIn) {
            fabGoogle.setImageResource(R.drawable.ic_done_black_24px);
        } else {
            fabGoogle.setImageResource(R.drawable.icon_google);
        }
    }

    private void updateFABFacebook(boolean loggedIn) {
        if (loggedIn) {
            fabFacebook.setImageResource(R.drawable.ic_done_black_24px);
        } else {
            fabFacebook.setImageResource(R.drawable.ic_facebook_box);
        }
    }

    private void decideForShownAcc(FirebaseUser currentUser) {
        if (getAuthProviders(currentUser)[0]) {
            updateUI(currentUser, 1);
        } else if (getAuthProviders(currentUser)[1]) {
            updateUI(currentUser, 2);
        } else if (getAuthProviders(currentUser)[2]) {
            updateUI(currentUser, 3);
        } else {
            updateUI(currentUser, 0);
        }
    }

    //// [END FIREBASE]

    private boolean[] getAuthProviders(FirebaseUser user) {

        boolean isMail = false;
        boolean isFB = false;
        boolean isGoogle = false;

        if (user != null) {
            for (UserInfo userInfo : user.getProviderData()) {

                if (userInfo.getProviderId().equals("password")) {
                    isMail = true;
                } else if (userInfo.getProviderId().equals("facebook.com")) {
                    isFB = true;
                } else if (userInfo.getProviderId().equals("google.com")) {
                    isGoogle = true;
                }
            }
        }
            boolean[] array = {isMail, isGoogle, isFB};
            return array;
    }


//    // handle Sign In Result for Google
//    private void handleGoogleSignInResult(GoogleSignInResult result) {
//        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
//
//            // Signed in successfully, show authenticated UI.
//            GoogleSignInAccount googleAcc = result.getSignInAccount();
//
//            // fill in user information
//            user.setGoogleUser(googleAcc.getDisplayName(), googleAcc.getEmail(), googleAcc.getId());
//
//            Log.d("googleAcc.getPhotoUrl", String.valueOf(googleAcc.getPhotoUrl()));
//            if(googleAcc.getPhotoUrl() != null) {
//                new LoadProfileImage(googlePic).execute(googleAcc.getPhotoUrl().toString());
//            } else {
//                user.setGoogleProfilePicture(BitmapFactory.decodeResource(getResources(), R.drawable.icon_account_circle_24dp));
//            }
//
//            updateGoogleUI(true);
//
//            checkForFacebookGoogleSelection();
//
//        } else {
//            // Signed out, show unauthenticated UI.
//            updateGoogleUI(false);
//        }
//    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id =  item.getItemId();
//
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void startSyncProcess() {

        // start pushing process for local & remote goals count
        viewModel.startGoalsCount();

        // start progress dialog
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Ihre Daten werden zusammengestellt");
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();


    }

    private void startDataSync(List<Integer> list) {

        if (!(list.get(0) == list.get(1))) {

            // hide progress dialog
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // start sync dialog
            SyncDialog syncDialog = new SyncDialog();
            Bundle bundle = new Bundle();
            bundle.putInt(SyncDialog.COUNT_LOCAL, list.get(0));
            bundle.putInt(SyncDialog.COUNT_REMOTE, list.get(1));
            syncDialog.setArguments(bundle);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(syncDialog, SyncDialog.TAG);
            transaction.commit();

            Log.d(TAG, "GoalsCount - Local: " + list.get(0).toString() + ", Remote: " + list.get(1).toString());

        }

    }

    //// Start on click interface from SyncDialog to LoginActivity
    @Override
    public void syncSelected(int selection) {
        // input selection: -1 = not valid; 0 = select remote only; 1 = select local only; 2 = select merge
        if (selection == -1) {
            Toast.makeText(this, "Sync error occured", Toast.LENGTH_SHORT);
        } else {
            // start progress dialog
            if (progressDialog == null || !progressDialog.isShowing()) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Ihre Daten werden zusammengestellt");
                progressDialog.setIndeterminate(true);
            }
            progressDialog.show();

            viewModel.startSyncProcess(selection);

        }
    }

    @Override
    public void syncCanceled() {
        signOutAccount();
    }
    //// End on click interface

    private void finishDataSync(int selection) {

        // hide progress dialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Toast.makeText(this, "Synchronisation finished successfully", Toast.LENGTH_LONG);

    }
}
