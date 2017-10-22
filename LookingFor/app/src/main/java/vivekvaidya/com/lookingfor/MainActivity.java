package vivekvaidya.com.lookingfor;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import static android.R.attr.phoneNumber;
import static vivekvaidya.com.lookingfor.R.id.fab;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth myAuth;
    private EditText password;
    private EditText username;
    private EditText contents;
    private Button login;
    private Button register;
    private Button signOut;
    private Button upload;
    private Button download;
    private Button loginPhone;
    private Button verifyCode;
    private String phoneAuthID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        password = (EditText) findViewById(R.id.password);
        username = (EditText) findViewById(R.id.username);
        contents = (EditText) findViewById(R.id.contents);
        login = (Button) findViewById(R.id.Login);
        register = (Button) findViewById(R.id.register);
        signOut = (Button) findViewById(R.id.signOut);
        upload = (Button) findViewById(R.id.uploadText);
        download = (Button) findViewById(R.id.downloadText);
        loginPhone = (Button) findViewById(R.id.phoneSignIn);
        verifyCode = (Button) findViewById(R.id.verifyCode);

        /**FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });**/
        verifyCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String code = password.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneAuthID, code);
                SignInWithPhoneAuthCredential(credential);
            }
        });
        loginPhone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                phoneAuth();
            }
        });

        login.setOnClickListener(this);
        register.setOnClickListener(this);
        signOut.setOnClickListener(this);
        upload.setOnClickListener(this);
        download.setOnClickListener(this);
        myAuth = FirebaseAuth.getInstance();

    }
    public void phoneAuth(){
        String phoneNumber = username.getText().toString();
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                contents.setText("you have signed in!");
                SignInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    contents.setText("invalid phone number");
                } else if (e instanceof FirebaseTooManyRequestsException){
                    contents.setText("aaa");
                }
            }
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken token){
                phoneAuthID = verificationID;
                resendToken = token;
                contents.setText("code sent!");
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                10,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks
        );
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) .
        FirebaseUser currentUser = myAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            contents.setText(getString(R.string.emailFirebase,
                    user.getEmail(), user.isEmailVerified(), user.getUid()));
            login.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
            signOut.setVisibility(View.VISIBLE);

        } else {
            contents.setText(R.string.signedOut);
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
            signOut.setVisibility(View.GONE);
        }
    }

    private void createAccount(String email, String password) {
        myAuth.createUserWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = myAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void signIn(String email, String password) {
        myAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = myAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    updateUI(null);
                }
            }
        });
    }

    private void signOut() {
        myAuth.signOut();
        Toast.makeText(MainActivity.this, "signed out",Toast.LENGTH_SHORT).show();
        updateUI(null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void phoneAuth(View view){
        String phoneNumber = username.getText().toString();
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                contents.setText("you have signed in!");
                SignInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException){
                    contents.setText("invalid phone number");
                } else if (e instanceof FirebaseTooManyRequestsException){
                    contents.setText("aaa");
                }
            }
            public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken token){
                phoneAuthID = verificationID;
                resendToken = token;
                contents.setText("code sent!");
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                10,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks
        );
    }
    private void SignInWithPhoneAuthCredential(PhoneAuthCredential credential){
        myAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    contents.setText("logged in!");
                    FirebaseUser user = task.getResult().getUser();
                } else {
                    if (task.getException() instanceof
                            FirebaseAuthInvalidCredentialsException) {
                        contents.setText("wrong!");
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register) {
            createAccount(username.getText().toString(), password.getText().toString());
            Toast.makeText(MainActivity.this, "You Pressed a button.",
                    Toast.LENGTH_SHORT).show();
        } else if (i == R.id.Login) {
            signIn(username.getText().toString(), password.getText().toString());
        }
        else if (i == R.id.signOut) {
            signOut();
        } else if (i == R.id.uploadText) {
  //TODO:          upload(contents.getText().toString());
        } else if (i == R.id.downloadText) {
   //TODO:         download(username.getText().toString());
        }
    }
}
