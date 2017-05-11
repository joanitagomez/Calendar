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

	Calendar gcal;

	Event evnt;
	ArrayList<Event> events;
	ArrayList<Event> todaysEvents;
	ArrayList<ChangeListener> listeners;

	/**
	 * Constructs a Model object
	 */
	Model() {
		Calendar cal = Calendar.getInstance();
		gcal = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		events = new ArrayList<Event>();
		todaysEvents = new ArrayList<Event>();
		listeners = new ArrayList<ChangeListener>();
	}

	/**
	 * Returns the data in the model
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Event> getEvents() {
		return (ArrayList<Event>) events.clone();
	}

	/**
	 * Attach a listener to the Model
	 * 
	 * @param c
	 *            the listener
	 */
	public void attachListener(ChangeListener c) {
		listeners.add(c);
	}

	/**
	 * Change the current date in the model
	 * 
	 * @param yyyy
	 *            : year to be set
	 * @param mm
	 *            : month to be set
	 * @param dd
	 *            : date to be set
	 */
	public void update(int yyyy, int mm, int dd) {
		gcal.set(yyyy, mm, dd);
		setTodaysEvents();
		for (ChangeListener c : listeners)
			c.stateChanged(new ChangeEvent(this));
	}

	/**
	 * Writes list of events to file using serialization
	 * 
	 * @param filename
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
	 * Reads serialized file
	 * 
	 * @param filename
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
	 * Creates new event
	 * 
	 * @param title
	 * @param date
	 * @param sTime
	 * @param eTime
	 * @return Event - the new event that was created
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
			e.printStackTrace();
		}

		evnt = new Event(title, c, c1);
		return evnt;
	}

	
	/**
	 * Adds new event to arraylist of events
	 * 
	 * @param newEvent:
	 *            New event to be added
	 */
	
	public void addEvent(Event newEvent) {
		events.add(newEvent);
		sortByTime(events);
	}

	/**
	 * Deletes event from the list
	 * 
	 * @param ev
	 *            : Event to be deleted
	 */

	public void deleteEvent(Event ev) {
		events.remove(events.indexOf(ev));
	}

	/**
	 * Checks if there's a time conflict with existing events
	 * 
	 * @param newEvent
	 *            : New event that was created
	 */
	public boolean timeConflict(Event newEvent) {
		
		boolean conflict = false;
		for (Event e : todaysEvents) {
			if ((newEvent.dateTime.getTime().compareTo(e.endTime.getTime()) < 0)
					&& (e.dateTime.getTime().compareTo(newEvent.endTime.getTime()) < 0))
				conflict = true;

		}
		return conflict;
	}

	/**
	 * Returns events scheduled for the day
	 */
	public ArrayList<Event> getTodaysEvents() {
		return todaysEvents;
	}

	
	/**
	 * Sets list with events for the day
	 */
	
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
		sortByTime(todaysEvents);
	}

	/**
	 * Sorts events by date and time
	 * 
	 * @param ev
	 *            : list of events
	 */

	public void sortByTime(ArrayList<Event> ev) {
		
		Collections.sort(ev, new Comparator<Event>() {
			public int compare(Event e1, Event e2) {
				return e1.dateTime.getTime().compareTo(e2.dateTime.getTime());
			}
		});
	}

	/**
	 * Following month
	 */

	public void nextMonth() {
		gcal.add(Calendar.MONTH, 1);
	}

	/**
	 * Previous month
	 */
	public void prevMonth() {
		gcal.add(Calendar.MONTH, -1);
	}

	/**
	 * Previous day
	 */
	public void prevDay() {
		gcal.add(Calendar.DAY_OF_MONTH, -1);
	}

	/**
	 * Next day
	 */
	public void nextDay() {
		gcal.add(Calendar.DAY_OF_MONTH, 1);
	}

}
