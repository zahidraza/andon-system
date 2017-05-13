package in.andonsystem.v2.dto;

/**
 * Created by Md Zahid Raza on 25/06/2016.
 */
public class Problem implements Comparable<Problem> {

    private long issueId;
    private String dField1; //Dynamic field1: app1=line, app2=team
    private String dField2; //Dynamic field2: app1=department, app2=Buyer
    private String probName;
    private String critical; // app1 applicable
    private String raiseTime;
    private long downtime;
    private int flag;

    public Problem(){}

    public Problem(long issueId, String dField1, String dField2, String probName, long downtime) {
        this.issueId = issueId;
        this.dField1 = dField1;
        this.dField2 = dField2;
        this.probName = probName;
        this.downtime = downtime;
    }

    public Problem(long issueId, String dField1, String dField2, String probName, String raiseTime, long downtime, int flag) {
        this.issueId = issueId;
        this.dField1 = dField1;
        this.dField2 = dField2;
        this.probName = probName;
        this.raiseTime = raiseTime;
        this.downtime = downtime;
        this.flag = flag;
    }

    public Problem(int issueId, String dField1, String dField2, String probName, String critical, String raiseTime, int downtime, int flag) {
        this.issueId = issueId;
        this.dField1 = dField1;
        this.dField2 = dField2;
        this.probName = probName;
        this.critical = critical;
        this.raiseTime = raiseTime;
        this.downtime = downtime;
        this.flag = flag;
    }

    public long getDowntime() {
        return downtime;
    }

    public long getIssueId() {
        return issueId;
    }

    public String getdField1() {
        return dField1;
    }

    public String getdField2() {
        return dField2;
    }

    public String getProbName() {
        return probName;
    }

    public String getCritical() {
        return critical;
    }

    public String getRaiseTime() {
        return raiseTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setIssueId(long issueId) {
        this.issueId = issueId;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int compareTo(Problem another) {
        int result = this.flag - another.flag;
        if(result == 0){
            result = (int)(another.issueId - this.issueId);
        }
        return result;
    }
}
