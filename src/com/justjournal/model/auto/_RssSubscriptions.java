package com.justjournal.model.auto;

/** Class _RssSubscriptions was generated by Cayenne.
  * It is probably a good idea to avoid changing this class manually, 
  * since it may be overwritten next time code is regenerated. 
  * If you need to make any customizations, please use subclass. 
  */
public class _RssSubscriptions extends org.apache.cayenne.CayenneDataObject {

    public static final String ID_PROPERTY = "id";
    public static final String URI_PROPERTY = "uri";

    public static final String SUBID_PK_COLUMN = "subid";

    public void setId(Long id) {
        writeProperty("id", id);
    }
    public Long getId() {
        return (Long)readProperty("id");
    }
    
    
    public void setUri(String uri) {
        writeProperty("uri", uri);
    }
    public String getUri() {
        return (String)readProperty("uri");
    }
    
    
}