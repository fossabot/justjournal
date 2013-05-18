package com.justjournal.model.auto;

import org.apache.cayenne.CayenneDataObject;

/**
 * Class _MoodThemes was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _MoodThemes extends CayenneDataObject {

    public static final String DES_PROPERTY = "des";
    public static final String IS_PUBLIC_PROPERTY = "isPublic";
    public static final String NAME_PROPERTY = "name";
    public static final String OWNER_PROPERTY = "owner";

    public static final String ID_PK_COLUMN = "id";

    public void setDes(String des) {
        writeProperty(DES_PROPERTY, des);
    }
    public String getDes() {
        return (String)readProperty(DES_PROPERTY);
    }

    public void setIsPublic(String isPublic) {
        writeProperty(IS_PUBLIC_PROPERTY, isPublic);
    }
    public String getIsPublic() {
        return (String)readProperty(IS_PUBLIC_PROPERTY);
    }

    public void setName(String name) {
        writeProperty(NAME_PROPERTY, name);
    }
    public String getName() {
        return (String)readProperty(NAME_PROPERTY);
    }

    public void setOwner(Long owner) {
        writeProperty(OWNER_PROPERTY, owner);
    }
    public Long getOwner() {
        return (Long)readProperty(OWNER_PROPERTY);
    }

}