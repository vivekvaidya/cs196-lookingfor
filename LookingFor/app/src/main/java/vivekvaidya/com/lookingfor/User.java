package vivekvaidya.com.lookingfor;

/**
 * Created by Administrator on 2017/10/26.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class User implements Serializable {
    /**Basic Data*/
    final private String UID;
    private String emailAddress;
    private String phoneNumber;
    private String userName;
    private String something;
    private Bitmap avatar;
    private boolean isLoggedIn = false;
    /**Constructors*/
//    public User(String UID, String phoneNumber){
//        this.UID = UID;
//        this.phoneNumber = phoneNumber;
//        this.isLoggedIn = true;
//        this.avatar = BitmapFactory.decodeResource(Resources.getSystem(),android.R.drawable.ic_menu_zoom);
//    }
    public User(String UID, String emailAddress){
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.isLoggedIn = true;
        this.avatar = BitmapFactory.decodeResource(Resources.getSystem(),android.R.drawable.ic_menu_zoom);

    }
    public User(String UID, String emailAddress, Bitmap avatarBitmap){
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.isLoggedIn = true;
        this.avatar = avatarBitmap;
    }
    /**Getter and Setters*/
    public String getUID(){
        return this.UID;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        if (this.isLoggedIn) {
            this.phoneNumber = phoneNumber;
        }
    }
    public String getEmailAddress(){
        return this.emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        if (this.isLoggedIn) {
            this.emailAddress = emailAddress;
        }
    }
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        if (this.isLoggedIn){
            this.userName = userName;
        }
    }
    public String getSomething(){
        return this.something;
    }
    public void setSomething(String something){
        if (this.isLoggedIn){
            this.something = something;
        }
    }
    public Bitmap getAvatar(){
        return this.avatar;
    }
    public void setAvatar(Bitmap avatar){
        if (this.isLoggedIn){
            this.avatar = avatar;
        }
    }
    public boolean getLoginStatus(){
        return this.isLoggedIn;
    }
    public void swapLogin(){
        this.isLoggedIn = !this.isLoggedIn;
    }

    /**Push User configuration to Database*/
    public void pushToFirebase(final OnCompleteListener<Void> onCompleteListener) {
        /**Put entries*/
        final HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("email", getEmailAddress());
        dataMap.put("phone", getPhoneNumber());
        dataMap.put("username", getUserName());
        String[] events = {"0","2"};
        List attendingEvents = new ArrayList<>(Arrays.asList(events));
        dataMap.put("attendingEvents", attendingEvents);
        String[] friendsList = {"sojasd","dsnfa98hsndfkj"};
        List friends = new ArrayList<>(Arrays.asList(friendsList));
        dataMap.put("friends", friends);
        String[] hosting = {};
        List hostingEvents = new ArrayList<>(Arrays.asList(hosting));
        dataMap.put("hostingEvents",hostingEvents);

        /**Push avatar to storage*/
        StorageReference avatarStorageReference = FirebaseStorage.getInstance().getReference().child("userAvatar/"+ getUID() + ".png");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG,100, outputStream);
        avatarStorageReference.putBytes(outputStream.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                dataMap.put("AvatarSet", true);
                DatabaseReference newEventReference = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(getUID());
                newEventReference.setValue(dataMap).addOnCompleteListener(onCompleteListener);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dataMap.put("AvatarSet", false);
                DatabaseReference newEventReference = FirebaseDatabase.getInstance().getReference().child("users")
                        .child(getUID());
                newEventReference.setValue(dataMap).addOnCompleteListener(onCompleteListener);
            }
        });

    }
}
