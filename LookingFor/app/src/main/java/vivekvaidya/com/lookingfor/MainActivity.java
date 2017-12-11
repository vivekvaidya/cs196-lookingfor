package vivekvaidya.com.lookingfor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth myAuth;
    private EditText password;
    private EditText username;
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
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        password =  findViewById(R.id.password);
        username =  findViewById(R.id.username);
        login =  findViewById(R.id.Login);
        register =  findViewById(R.id.register);
        loginPhone =  findViewById(R.id.phoneSignIn);
        verifyCode =  findViewById(R.id.verifyCode);


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
        Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");
        Pattern phonePattern = Pattern.compile("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");
        if (email == null || email.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (phonePattern.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Please use \"Sign in with phone\" button",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!emailPattern.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Please enter valid email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password == null || password.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

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
        Pattern emailPattern = Pattern.compile("^.+@.+\\..+$");
        Pattern phonePattern = Pattern.compile("\\d{10}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}");
        if (email == null || email.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (phonePattern.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Please use \"Sign in with phone\" button",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!emailPattern.matcher(email).matches()) {
            Toast.makeText(MainActivity.this, "Please enter valid email",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password == null || password.equals("")) {
            Toast.makeText(MainActivity.this, "Please enter password",
                    Toast.LENGTH_SHORT).show();
            return;
        }

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
            Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show();
        } else {
            verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    Toast.makeText(context, "You have signed in!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "Something is wrong with the database", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken token) {
                    phoneAuthID = verificationID;
                    resendToken = token;
                    Toast.makeText(context, "code sent!", Toast.LENGTH_SHORT).show();
                    Log.d("PhoneAuth", "code sent!");
                }
            };
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
                    return;
                } else {
                    if (task.getException() instanceof
                            FirebaseAuthInvalidCredentialsException) {
                        return;
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