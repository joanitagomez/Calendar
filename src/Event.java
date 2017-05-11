import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * 
 */
public class Event implements Serializable{

	private static final long serialVersionUID = 1L;
	String title;
	Calendar dateTime; // contains concatenated start time and date 
	Calendar endTime;// contains concatenated end time and date 

	public Event() {
	}


	public Event(String title, Calendar dt, Calendar endTime) {
		setTitle(title);
		setDateTime(dt);
		setEndTime(endTime);
	}
	
	
	/**
	 * Returns title of the event
	 */
	
	public String getTitle() {
		return title;
	}
	
	
	/**
	 * Sets title of the event
	 */
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * Returns date and start time of event
	 */
	
	public Calendar getDateTime() {
		return dateTime;
	}
	
	
	/**
	 * Sets date and start time of event
	 */
	
	public void setDateTime(Calendar dateTime) {
		this.dateTime = dateTime;
	}
	
	
	/**
	 * Returns date and end time of event
	 */
	
	public Calendar getEndTime() {
		return endTime;
	}
	
	
	/**
	 * Sets date and end time of event
	 */
	
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}
	
	
	/**
	 * Displays event information
	 */
	
	public void display() {

		DateFormat da = new SimpleDateFormat("EEE, MMM d,yyyy");
		DateFormat st = new SimpleDateFormat("HH:mm");
		DateFormat et = new SimpleDateFormat("HH:mm");
		System.out.println(da.format(getDateTime().getTime()) + " " +st.format(getDateTime().getTime()) + "-" + et.format(getEndTime().getTime()));
		System.out.println(getTitle());

	}
	
	
	/**
	 * Converts event information to string
	 */
	
	public String toString() {

		DateFormat st = new SimpleDateFormat("h:mm a");
		DateFormat et = new SimpleDateFormat("h:mm a");

		if(getEndTime() != null){
			return (getTitle() + ": " +st.format(getDateTime().getTime()) + "-" + et.format(getEndTime().getTime()));
		}
		else 
			return (getTitle()  + " " +st.format(getDateTime().getTime()));
	}

}
