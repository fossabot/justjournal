package com.justjournal.model.auto;

/** Class _Location was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _Location extends org.apache.cayenne.CayenneDataObject {

    public static final String TITLE_PROPERTY = "title";

    public static final String ID_PK_COLUMN = "id";

    public void setTitle(String title) {
        writeProperty("title", title);
    }
    public String getTitle() {
        return (String)readProperty("title");
    }
    
    
}