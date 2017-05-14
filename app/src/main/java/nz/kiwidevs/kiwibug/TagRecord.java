package nz.kiwidevs.kiwibug;

/**
 * Created by james on 7/05/2017.
 */

public class TagRecord {

    private int ID;
    private float lat;
    private float lng;
    private String TagTime;
    private String username;
    private String TagID;
    private String address;

    public TagRecord(){

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getLatitude() {
        return lat;
    }

    public void setLatitude(float lat) {
        this.lat = lat;
    }

    public float getLongitude() {
        return lng;
    }

    public void setLongitude(float lng) {
        this.lng = lng;
    }

    public String getTagTime() {
        return TagTime;
    }

    public void setTagTime(String tagTime) {
        TagTime = tagTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTagID() {
        return TagID;
    }

    public void setTagID(String tagID) {
        TagID = tagID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "TagRecord{" +
                "ID=" + ID +
                ", lat=" + lat +
                ", lng=" + lng +
                ", TagTime='" + TagTime + '\'' +
                ", username='" + username + '\'' +
                ", TagID='" + TagID + '\'' +
                '}';
    }
}
