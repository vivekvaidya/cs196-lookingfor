package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import java.io.Serializable;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;
import java.util.HashMap;
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
        loginPhone = (Button) findViewById(R.id.phoneSignIn);
        verifyCode = (Button) findViewById(R.id.verifyCode);



        verifyCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String code = password.getText().toString();
                if (phoneAuthID == null || phoneAuthID.equals("")) {
                    Toast.makeText(MainActivity.this, "Code not sent.", Toast.LENGTH_LONG).show();
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneAuthID, code);
                    SignInWithPhoneAuthCredential(credential);
                }
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
        myAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) .
        FirebaseUser currentUser = myAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this.getApplicationContext(),welcomScreen.class);
            startActivity(intent);
        }
    }

    private void createAccount(final String email, final String password) {
        final Context context = this.getApplicationContext();
        myAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(context, userSettingsScreen.class);
                            intent.putExtra("nameString",myAuth.getUid());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, final String password) {
        final Context context = this.getApplicationContext();
        myAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = myAuth.getCurrentUser();
                    Intent intent = new Intent(context,welcomScreen.class);
                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void phoneAuth() {

        final Context context = this.getApplicationContext();
        final String phoneNumber = username.getText().toString();

        if (phoneNumber.equals("")) {
            Toast.makeText(context, "phoneAuth() called with no phone number", Toast.LENGTH_SHORT).show();
        } else {
            verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Toast.makeText(context, "you have signed in!", Toast.LENGTH_SHORT).show();
                    SignInWithPhoneAuthCredential(credential);
                    Intent intent = new Intent(context, welcomScreen.class);
                    intent.putExtra("nameString", myAuth.getUid());
                    startActivity(intent);
                }


                @Override
                public void onVerificationFailed(FirebaseException e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "invalid phone number", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Toast.makeText(context, "aaa", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("PhoneAuth", "auth failed");
                    }
                }

                public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken token) {
                    phoneAuthID = verificationID;
                    resendToken = token;
                    Toast.makeText(context, "code sent!", Toast.LENGTH_SHORT).show();
                    Log.d("PhoneAuth", "code sent!");
                }
            };
            Log.d("PhoneAuth", phoneNumber);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, verificationCallbacks);
        }
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
    private void SignInWithPhoneAuthCredential(PhoneAuthCredential credential){
        myAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    contents.setText("logged in!");
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
        } else if (i == R.id.Login) {
            signIn(username.getText().toString(), password.getText().toString());
        }
    }

}
