import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Event implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String title;
	Calendar dateTime;
	Calendar endTime;

	public Event() {
	}

	public Event(String title, Calendar dt) {
		setTitle(title);
		setDateTime(dt);
	}

	public Event(String title, Calendar dt, Calendar endTime) {
		setTitle(title);
		setDateTime(dt);
		setEndTime(endTime);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Calendar getDateTime() {
		return dateTime;
	}

	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}

	public Calendar getEndTime() {
		return endTime;
	}

	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}

	public void display() {

		DateFormat da = new SimpleDateFormat("EEE, MMM d,yyyy");
		DateFormat st = new SimpleDateFormat("HH:mm");
		DateFormat et = new SimpleDateFormat("HH:mm");
		System.out.println(da.format(getDateTime().getTime()) + " " +st.format(getDateTime().getTime()) + "-" + et.format(getEndTime().getTime()));
		System.out.println(getTitle());

	}
	
	
	public String toString() {

		DateFormat da = new SimpleDateFormat("EEE, MMM d,yyyy");
		DateFormat st = new SimpleDateFormat("hh:mm a");
		DateFormat et = new SimpleDateFormat("hh:mm a");

		if(getEndTime() != null){
			return (da.format(getDateTime().getTime()) + " " +st.format(getDateTime().getTime()) + "-" + et.format(getEndTime().getTime())
		+ "\n" +getTitle());
		}
		else 
			return (da.format(getDateTime().getTime()) + " " +st.format(getDateTime().getTime()) + "\n" +getTitle());
	}

	public static void main(String[] args) {
		SimpleCalendar.class.getResource("next.png");
	}

}
