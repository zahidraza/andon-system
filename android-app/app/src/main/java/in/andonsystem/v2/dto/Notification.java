package in.andonsystem.v2.dto;

/**
 * Created by razamd on 4/8/2017.
 */

public class Notification implements Comparable<Notification>{

    private long issueId;
    private String message;
    private long time;
    private int state;  // 0 - raised state, 1 - acknowledged state, 2 - fixed state

    public Notification(long issueId, String message, long time, int state) {
        this.issueId = issueId;
        this.message = message;
        this.time = time;
        this.state = state;
    }

    public long getIssueId() {
        return issueId;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public int compareTo(Notification another) {
        int result = (int)(this.time - another.time);
        return result;
    }
}
