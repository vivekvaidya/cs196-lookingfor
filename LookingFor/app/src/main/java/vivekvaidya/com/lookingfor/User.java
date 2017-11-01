package vivekvaidya.com.lookingfor;

/**
 * Created by Administrator on 2017/10/26.
 */

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {
    /**Basic Data*/
    final private String UID;
    private String emailAddress;
    private String phoneNumber;
    private String passWord;
    private String userName;
    private String avatarLink;
    private boolean isLoggedIn = false;
    /**Constructors*/
    public User(String UID, String phoneNumber){
        this.UID = UID;
        this.phoneNumber = phoneNumber;
        this.isLoggedIn = true;
    }
    public User(String UID, String emailAddress, String avatarAddress){
        this.UID = UID;
        this.emailAddress = emailAddress;
        this.isLoggedIn = true;
        this.avatarLink = avatarAddress;

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
    public String getPassWord(){
        return this.passWord;
    }
    public void setPassWord(String passWord){
        if (this.isLoggedIn){
            this.passWord = passWord;
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
    public String getAvatarLink(){
        return this.avatarLink;
    }
    public void setAvatarLink(String avatarLink){
        if (this.isLoggedIn){
            this.avatarLink = avatarLink;
        }
    }
    public boolean getLoginStatus(){
        return this.isLoggedIn;
    }
    public void swapLogin(){
        this.isLoggedIn = !this.isLoggedIn;
    }

}
