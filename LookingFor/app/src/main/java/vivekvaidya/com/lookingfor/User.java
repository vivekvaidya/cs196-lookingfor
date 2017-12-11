package vivekvaidya.com.lookingfor;

/**
 * Created by Administrator on 2017/10/26.
 */

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
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

/**For Parcelable, you may want to find the Android Parcelable code generator from:
 * Preferences -> Plugins
 */
public class User implements Parcelable {

    /**Basic Data*/
    private String UID = "";
    @Exclude
    public final static int AVATAR_SIDE_LENGTH = 100;

    private String emailAddress = "";
    private String phoneNumber = "";
    private String userName = "";
    private List<String> attendingEvents = new ArrayList<>();
    private List<String> friends = new ArrayList<>();
    private List<String> hostingEvents = new ArrayList<>();
    private String avatar = "";

    public User() {

    }

    /**Constructors*/
    public User(String UID, String userName, String emailAddress, String phoneNumber, Bitmap avatarBitmap){
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.avatar = bitMapToString(avatarBitmap);
    }
    public User(String UID, String userName, String phoneNumber, String emailAddress, Bitmap avatarBitmap, List<String> friends, List<String> hostingEvents, List<String> attendingEvents) {
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.avatar = bitMapToString(avatarBitmap);
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.friends = friends;
        this.attendingEvents = attendingEvents;
        this.hostingEvents = hostingEvents;
    }
    /**Getter and Setters*/
    public String getUID(){
        return this.UID;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmailAddress(){
        return this.emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getUserName(){
        return this.userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    @Exclude
    public Bitmap getAvatarinBitmap() {
        return stringToBitMap(avatar);
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public void setAvatarWithBitmap(Bitmap avatar) {
        this.avatar = bitMapToString(avatar);
    }

    /**Bitmap to String*/
    public static String bitMapToString(Bitmap bitmap){
        ByteArrayOutputStream ByteStream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, ByteStream);
        byte [] b=ByteStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    /**String to Bitmap*/
    public static Bitmap stringToBitMap(String encodedString){
        try{
            byte[] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
    /**Push User configuration to Database*/
    public void pushToFirebase(final OnCompleteListener<Void> onCompleteListener) {

        DatabaseReference newEventReference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(getUID());
        newEventReference.setValue(this).addOnCompleteListener(onCompleteListener);


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.UID);
        dest.writeString(this.emailAddress);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.userName);
        dest.writeStringList(this.attendingEvents);
        dest.writeStringList(this.friends);
        dest.writeStringList(this.hostingEvents);
        dest.writeString(this.avatar);
    }

    protected User(Parcel in) {
        this.UID = in.readString();
        this.emailAddress = in.readString();
        this.phoneNumber = in.readString();
        this.userName = in.readString();
        this.attendingEvents = in.createStringArrayList();
        this.friends = in.createStringArrayList();
        this.hostingEvents = in.createStringArrayList();
        this.avatar = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
