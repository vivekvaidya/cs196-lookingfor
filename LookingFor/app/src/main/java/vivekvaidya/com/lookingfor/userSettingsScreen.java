package vivekvaidya.com.lookingfor;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

public class userSettingsScreen extends NavigationActivity implements View.OnClickListener{
    /**Declare UI variables*/
    public EditText profileNameET;
    public EditText profilePhoneET;
    public EditText profileEmailET;
    public Button selectPhotoBT;
    public ImageView avatarView;
    public Button confirmButton;
    private static final int PICK_IMAGE_REQUEST = 33;
    private static final int CAM_CAPTURE_REQUEST = 41;
    private static final String USER_DATA_DOWNLOADED = "userDataDownloaded";
    private FirebaseUser curUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**Initialize Screen*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings_screen);
        /**Initialize Common UI*/
        profileNameET = (EditText) findViewById(R.id.profileNameET);
        profilePhoneET = (EditText) findViewById(R.id.profilePhoneET);
        profileEmailET = (EditText) findViewById(R.id.profileEmailET);
        selectPhotoBT = (Button) findViewById(R.id.selectPhotoBT);
        avatarView = (ImageView) findViewById(R.id.avatarPreviewIV);
        confirmButton = (Button) findViewById(R.id.profileConfirmBT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**Request Permission*/
        checkPermission();

        /**Set button Behavior*/
        confirmButton.setOnClickListener(this);
        selectPhotoBT.setOnClickListener(this);
        findViewById(R.id.useCamBT).setOnClickListener(this);
        curUser = FirebaseAuth.getInstance().getCurrentUser();

        if (curUser != null) {
            if (savedInstanceState == null || !savedInstanceState.getBoolean(USER_DATA_DOWNLOADED)) {
                loadWrittenData(curUser.getUid());
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(USER_DATA_DOWNLOADED,true);
    }

    private void loadWrittenData(final String UID) {
        FirebaseDatabase.getInstance().getReference().child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                if (currentUser != null){
                    profileEmailET.setText(currentUser.getEmailAddress());
                    profileNameET.setText(currentUser.getUserName());
                    profilePhoneET.setText(currentUser.getPhoneNumber());
                    avatarView.setImageBitmap(currentUser.getAvatarinBitmap());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UserProfile", "Can't find user.");
            }
        });
    }
    /**Check Permission for camera, and read from and write to external storage.*/
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 42);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 43);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 44);
        }
    }
    /**Button Clicked*/
    @Override
    public void onClick(View view) {
        int i = view.getId();
        switch (i) {
            /**Send Profile*/
            case R.id.profileConfirmBT:
                String uid;
                /**Try fetching uid of current user*/
                try {
                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                } catch (NullPointerException e) {
                    Toast.makeText(userSettingsScreen.this, "Error... UID not available", Toast.LENGTH_LONG).show();
                    return;
                }
                /**Prepare the user to be sent*/
                BitmapDrawable draw = (BitmapDrawable) avatarView.getDrawable();
                Bitmap userBitmap = draw.getBitmap();
                /**Dummy image*/
                if (userBitmap == null) {
                    userBitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.mipmap.ic_launcher);
                }

                User newUser = new User(uid,profileNameET.getText().toString(),profileEmailET.getText().toString(),profilePhoneET.getText().toString(),userBitmap);
                /**Send event*/
                newUser.pushToFirebase(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        onUserPushComplete(task);
                    }
                });
                break;

            /**Select Photo*/
            case R.id.selectPhotoBT:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                break;

            case R.id.useCamBT:
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (camIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(camIntent, CAM_CAPTURE_REQUEST);
                }
                break;
            default:
                break;
        }
    }
    /**Things to do when the push is complete*/
    public void onUserPushComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            Toast.makeText(userSettingsScreen.this, "User profile registered!", Toast.LENGTH_LONG).show();
            finish();

        } else {
            Toast.makeText(userSettingsScreen.this, "Error...", Toast.LENGTH_LONG).show();
        }
    }

    /**When done selecting avatar.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data!= null && data.getData() != null) {
                    Uri uri = data.getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        bitmap = Bitmap.createScaledBitmap(bitmap, User.AVATAR_SIDE_LENGTH, User.AVATAR_SIDE_LENGTH, false); //resize
                        // Log.d(TAG, String.valueOf(bitmap));
                        avatarView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(userSettingsScreen.this, "Something's wrong when getting photo from Google Drive", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(userSettingsScreen.this, "Something's wrong when selecting photo from Google Drive", Toast.LENGTH_LONG).show();
                }
                break;

            case CAM_CAPTURE_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");
                    bitmap = Bitmap.createScaledBitmap(bitmap, User.AVATAR_SIDE_LENGTH, User.AVATAR_SIDE_LENGTH, false); //resize
                    avatarView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(userSettingsScreen.this, "Something's wrong when taking picture", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
