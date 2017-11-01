package vivekvaidya.com.lookingfor;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;

public class userSettingsScreen extends AppCompatActivity implements View.OnClickListener{
    /**Declare UI variables*/
    public EditText profileNameET;
    public EditText profilePhoneET;
    public EditText profileEmailET;
    public Button selectPhotoBT;
    public ImageView avatarView;
    public Button confirmButton;
    private static int PICK_IMAGE_REQUEST = 33;

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

        /**Set button Behavior*/
        confirmButton.setOnClickListener(this);
        selectPhotoBT.setOnClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
                    userBitmap = BitmapFactory.decodeResource(Resources.getSystem(),android.R.drawable.ic_menu_zoom);
                }
                User newUser = new User(uid,
                        profileNameET.getText().toString(),
                        userBitmap);
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
        }
    }
    /**Things to do when the push is complete*/
    public void onUserPushComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            Toast.makeText(userSettingsScreen.this, "User profile registered!", Toast.LENGTH_LONG).show();
//                Intent data = new Intent();
//                data.putExtra(EVENT_DATA, newEvent);
//                setResult(Activity.RESULT_OK,data);
            finish();

        } else {
            Toast.makeText(userSettingsScreen.this, "Error...", Toast.LENGTH_LONG).show();
        }
    }

    /**When done selecting avatar.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                avatarView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(userSettingsScreen.this, "Something's wrong when getting photo", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(userSettingsScreen.this, "Something's wrong when selecting photo", Toast.LENGTH_LONG).show();
        }
    }

}
