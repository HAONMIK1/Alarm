package alarm.model;
public class AlertDTO
{
    private String location;
    private int grade;
    private String time;
    private String alert_check;
	public String getAlert_check() {
		return alert_check;
	}
	public void setAlert_check(String alert_check) {
		this.alert_check = alert_check;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getGrade() {
		return grade;
	}
	public void setGrade(int grade) {
		this.grade = grade;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
    
}
