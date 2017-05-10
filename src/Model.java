import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Model {
	int year;
	int month;
	int day;
	Calendar gcal;

	Event evnt;
	ArrayList<Event> events;
	ArrayList<Event> todaysEvents;
	ArrayList<ChangeListener> listeners;

	Model() {
		Calendar cal = Calendar.getInstance();
		gcal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		events = new ArrayList<Event>();
		todaysEvents = new ArrayList<Event>();
		listeners = new ArrayList<ChangeListener>();
	}

	/**
	 * getData returns the data
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Event> getEvents() {
		return (ArrayList<Event>) events.clone();
	}

	public void attachListener(ChangeListener c) {
		listeners.add(c);
	}

	public void update(int yyyy, int mm, int dd) {
		gcal.set(yyyy, mm, dd);
		setTodaysEvents();
		for (ChangeListener c : listeners)
			c.stateChanged(new ChangeEvent(this));
	}

	/**
	 * This is the writeFile method that writes list of events to file using
	 * serialization
	 * 
	 * @param filename
	 * @return nothing
	 */

	public void writeFile(String filename) {
		// Writing ..
		try {
			FileOutputStream fs = new FileOutputStream(filename);
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(events);
			os.close();
		} catch (IOException e) {
			System.out.println("Error: " + e);
		}
	}

	/**
	 * This is the readFile method that reads serialized file
	 * 
	 * @param filename
	 * @return nothing
	 */

	@SuppressWarnings("unchecked")
	public void readFile(String filename) {
		// Reading ..
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
			events = ((ArrayList<Event>) in.readObject());
			in.close();
		} catch (Exception e) {
			System.out.print("Error: " + e);
			System.exit(1);
		}
	}

	/**
	 * This is the createEvent method that creates events and adds to list of
	 * events
	 * 
	 * @param title
	 * @param date
	 * @param sTime
	 * @param eTime
	 * @return boolean - to check if user entered date and time in the given
	 *         format
	 */

	public Event createNewEvent(String title, String date, String sTime, String eTime) {
		String dateTime = date + " " + sTime;
		String endTime = date + " " + eTime;
		SimpleDateFormat dformatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat eformatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date d1;
		Date d2;
		Calendar c;
		Calendar c1;
		c = Calendar.getInstance();
		try {
			d1 = dformatter.parse(dateTime);
			c.setTime(d1);
		} catch (ParseException e) {
			;
		}

		c1 = Calendar.getInstance();
		try {
			d2 = eformatter.parse(endTime);
			c1.setTime(d2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		evnt = new Event(title, c, c1);
		return evnt;
	}

	
	public void addEvent(Event ev){
		events.add(ev);
	}
	/**
	 * deleteEvent method
	 * 
	 * @param
	 */

	public void deleteEvent(Event ev) {
		events.remove(events.indexOf(ev));
	}

	/**
	 * timeConflict method
	 * 
	 * @param
	 */
	public boolean timeConflict(Event newEvent) {
		boolean conflict = false;
		for (Event e : todaysEvents) {
			if ((newEvent.dateTime.getTime().compareTo(e.endTime.getTime()) < 0) && (e.dateTime.getTime().compareTo(newEvent.endTime.getTime()) < 0)){
				conflict = true;
			}
			
			if(newEvent.dateTime.getTime().compareTo(e.dateTime.getTime()) < 0){
				System.out.println(newEvent.dateTime.getTime() + " is before endtime of " + e.dateTime.getTime());

		}
//			if((newEvent.endTime.getTime().compareTo(e.dateTime.getTime()) > 0)){
//				System.out.println(newEvent.endTime.getTime() + " new event endTime is after starttime of existing" + e.dateTime.getTime());
//			}
		
			}
		return conflict;
	}

	public ArrayList<Event> getTodaysEvents() {
		return todaysEvents;
	}

	public void setTodaysEvents() {
		Calendar currEvent;
		todaysEvents = new ArrayList<Event>();
		// find event that has same date as given date
		for (int i = 0; i < events.size(); i++) {
			currEvent = events.get(i).getDateTime();
			if ((currEvent.get(Calendar.YEAR) == gcal.get(Calendar.YEAR))
					&& (currEvent.get(Calendar.MONTH) == gcal.get(Calendar.MONTH))
					&& (currEvent.get(Calendar.DAY_OF_MONTH) == gcal.get(Calendar.DAY_OF_MONTH)))
				todaysEvents.add(events.get(i));
		}
	}

	/**
	 * This is the printMonthView method that finds events scheduled for this
	 * month and calls printHighlightMethod to highlight and print them.
	 * 
	 * @param mycal
	 * @return nothing
	 */

	public void printMonthView() {
		sortByTime(events);
		Calendar currEvent;

		for (int i = 0; i < events.size(); i++) {
			currEvent = events.get(i).getDateTime();
			if ((currEvent.get(Calendar.YEAR) == gcal.get(Calendar.YEAR))
					&& (currEvent.get(Calendar.MONTH) == gcal.get(Calendar.MONTH))) {
				// evdates.add(events.get(i).getDateTime().get(Calendar.DAY_OF_MONTH));
			}
		}
		// printHighlightMonth();
	}

	/**
	 * This is the deleteAll method that deletes all the events
	 * 
	 * @param nothing.
	 * @return nothing.
	 */

	public void deleteAll() {
		events.clear();
		if (events.isEmpty())
			System.out.println("Successfully deleted all events.");
		else
			System.out.println("DeleteAll failed.");
	}

	/**
	 * This is the sortByTime method that sorts events by date and time
	 * 
	 * @param ev
	 *            : list of events
	 * @return nothing.
	 */

	public void sortByTime(ArrayList<Event> ev) {
		Collections.sort(ev, new Comparator<Event>() {
			public int compare(Event e1, Event e2) {
				return e1.dateTime.getTime().compareTo(e2.dateTime.getTime());
			}
		});
	}

	public void displayEvents() {
		if (events.isEmpty())
			System.out.println("Eventlist empty.\n");
		else {
			sortByTime(events);
			for (int i = 0; i < events.size(); i++)
				System.out.println(events.get(i).toString());
		}
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void nextMonth() {
		gcal.add(Calendar.MONTH, 1);
	}

	public void prevMonth() {
		gcal.add(Calendar.MONTH, -1);
	}

	public void prevDay() {
		gcal.add(Calendar.DAY_OF_MONTH, -1);
	}

	public void nextDay() {
		gcal.add(Calendar.DAY_OF_MONTH, 1);
	}

}
