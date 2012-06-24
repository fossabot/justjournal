package com.justjournal.model.auto;

import java.util.List;

/** Class _User was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _User extends org.apache.cayenne.CayenneDataObject {

    public static final String LASTLOGIN_PROPERTY = "lastlogin";
    public static final String LASTNAME_PROPERTY = "lastname";
    public static final String MODIFIED_PROPERTY = "modified";
    public static final String NAME_PROPERTY = "name";
    public static final String PASSWORD_PROPERTY = "password";
    public static final String SINCE_PROPERTY = "since";
    public static final String TYPE_PROPERTY = "type";
    public static final String USERNAME_PROPERTY = "username";
    public static final String USER_TO_COMMENT_PROPERTY = "userToComment";
    public static final String USER_TO_ENTRY_PROPERTY = "userToEntry";
    public static final String USER_TO_FAVORITES_PROPERTY = "userToFavorites";
    public static final String USER_TO_USER_BIO_PROPERTY = "userToUserBio";
    public static final String USER_TO_USER_CONTACT_PROPERTY = "userToUserContact";
    public static final String USER_TO_USER_FILES_PROPERTY = "userToUserFiles";
    public static final String USER_TO_USER_IMAGES_PROPERTY = "userToUserImages";
    public static final String USER_TO_USER_LINK_PROPERTY = "userToUserLink";
    public static final String USER_TO_USER_LOCATION_PROPERTY = "userToUserLocation";
    public static final String USER_TO_USER_PIC_PROPERTY = "userToUserPic";
    public static final String USER_TO_USER_PREF_PROPERTY = "userToUserPref";
    public static final String USER_TO_USER_STYLE_PROPERTY = "userToUserStyle";

    public static final String ID_PK_COLUMN = "id";

    public void setLastlogin(java.util.Date lastlogin) {
        writeProperty("lastlogin", lastlogin);
    }
    public java.util.Date getLastlogin() {
        return (java.util.Date)readProperty("lastlogin");
    }
    
    
    public void setLastname(String lastname) {
        writeProperty("lastname", lastname);
    }
    public String getLastname() {
        return (String)readProperty("lastname");
    }
    
    
    public void setModified(java.util.Date modified) {
        writeProperty("modified", modified);
    }
    public java.util.Date getModified() {
        return (java.util.Date)readProperty("modified");
    }
    
    
    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }
    
    
    public void setPassword(String password) {
        writeProperty("password", password);
    }
    public String getPassword() {
        return (String)readProperty("password");
    }
    
    
    public void setSince(Short since) {
        writeProperty("since", since);
    }
    public Short getSince() {
        return (Short)readProperty("since");
    }
    
    
    public void setType(Byte type) {
        writeProperty("type", type);
    }
    public Byte getType() {
        return (Byte)readProperty("type");
    }
    
    
    public void setUsername(String username) {
        writeProperty("username", username);
    }
    public String getUsername() {
        return (String)readProperty("username");
    }
    
    
    public void addToUserToComment(com.justjournal.model.Comments obj) {
        addToManyTarget("userToComment", obj, true);
    }
    public void removeFromUserToComment(com.justjournal.model.Comments obj) {
        removeToManyTarget("userToComment", obj, true);
    }
    public List getUserToComment() {
        return (List)readProperty("userToComment");
    }
    
    
    public void addToUserToEntry(com.justjournal.model.Entry obj) {
        addToManyTarget("userToEntry", obj, true);
    }
    public void removeFromUserToEntry(com.justjournal.model.Entry obj) {
        removeToManyTarget("userToEntry", obj, true);
    }
    public List getUserToEntry() {
        return (List)readProperty("userToEntry");
    }
    
    
    public void addToUserToFavorites(com.justjournal.model.Favorites obj) {
        addToManyTarget("userToFavorites", obj, true);
    }
    public void removeFromUserToFavorites(com.justjournal.model.Favorites obj) {
        removeToManyTarget("userToFavorites", obj, true);
    }
    public List getUserToFavorites() {
        return (List)readProperty("userToFavorites");
    }
    
    
    public void setUserToUserBio(com.justjournal.model.UserBio userToUserBio) {
        setToOneTarget("userToUserBio", userToUserBio, true);
    }

    public com.justjournal.model.UserBio getUserToUserBio() {
        return (com.justjournal.model.UserBio)readProperty("userToUserBio");
    } 
    
    
    public void setUserToUserContact(com.justjournal.model.UserContact userToUserContact) {
        setToOneTarget("userToUserContact", userToUserContact, true);
    }

    public com.justjournal.model.UserContact getUserToUserContact() {
        return (com.justjournal.model.UserContact)readProperty("userToUserContact");
    } 
    
    
    public void addToUserToUserFiles(com.justjournal.model.UserFiles obj) {
        addToManyTarget("userToUserFiles", obj, true);
    }
    public void removeFromUserToUserFiles(com.justjournal.model.UserFiles obj) {
        removeToManyTarget("userToUserFiles", obj, true);
    }
    public List getUserToUserFiles() {
        return (List)readProperty("userToUserFiles");
    }
    
    
    public void addToUserToUserImages(com.justjournal.model.UserImages obj) {
        addToManyTarget("userToUserImages", obj, true);
    }
    public void removeFromUserToUserImages(com.justjournal.model.UserImages obj) {
        removeToManyTarget("userToUserImages", obj, true);
    }
    public List getUserToUserImages() {
        return (List)readProperty("userToUserImages");
    }
    
    
    public void setUserToUserLink(com.justjournal.model.UserLink userToUserLink) {
        setToOneTarget("userToUserLink", userToUserLink, true);
    }

    public com.justjournal.model.UserLink getUserToUserLink() {
        return (com.justjournal.model.UserLink)readProperty("userToUserLink");
    } 
    
    
    public void setUserToUserLocation(com.justjournal.model.UserLocation userToUserLocation) {
        setToOneTarget("userToUserLocation", userToUserLocation, true);
    }

    public com.justjournal.model.UserLocation getUserToUserLocation() {
        return (com.justjournal.model.UserLocation)readProperty("userToUserLocation");
    } 
    
    
    public void setUserToUserPic(com.justjournal.model.UserPic userToUserPic) {
        setToOneTarget("userToUserPic", userToUserPic, true);
    }

    public com.justjournal.model.UserPic getUserToUserPic() {
        return (com.justjournal.model.UserPic)readProperty("userToUserPic");
    } 
    
    
    public void setUserToUserPref(com.justjournal.model.UserPref userToUserPref) {
        setToOneTarget("userToUserPref", userToUserPref, true);
    }

    public com.justjournal.model.UserPref getUserToUserPref() {
        return (com.justjournal.model.UserPref)readProperty("userToUserPref");
    } 
    
    
    public void setUserToUserStyle(com.justjournal.model.UserStyle userToUserStyle) {
        setToOneTarget("userToUserStyle", userToUserStyle, true);
    }

    public com.justjournal.model.UserStyle getUserToUserStyle() {
        return (com.justjournal.model.UserStyle)readProperty("userToUserStyle");
    } 
    
    
}