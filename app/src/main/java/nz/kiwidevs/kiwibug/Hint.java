package nz.kiwidevs.kiwibug;

/**
 * Created by james on 7/05/2017.
 */

public class Hint {

    private int ID;
    private String hint;
    private String UserSubmitted;
    private String TimeStamp;

    public Hint(){

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getUserSubmitted() {
        return UserSubmitted;
    }

    public void setUserSubmitted(String userSubmitted) {
        this.UserSubmitted = userSubmitted;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }
}
