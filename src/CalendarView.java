import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

enum MONTHS {
	January, February, March, April, May, June, July, August, September, October, November, December;
}

enum DAYS {
	Sun, Mon, Tue, Wed, Thu, Fri, Sat;
}

/**
 * @author jo
 *
 */
public class CalendarView extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;
	Model model;
	ArrayList<Event> evs;
	ArrayList<Event> evsToday;
	Calendar c;
	MONTHS[] arrayOfMonths = MONTHS.values();
	DAYS[] arrayOfDays = DAYS.values();
	JTextField titleField;
	JTextField dateField;
	JTextField fromField;
	JTextField toField;
	JTextArea area;
	TitledBorder border;
	JPanel datePanel;
	JPanel monthViewPanel;
	JPanel eventPanel;
	JPanel dayViewPanel;
	JButton eventButton;

	CalendarView(final Model model) {

		setBorder(border);

		System.out.println("Loading events..");
		
		File f = new File("event.ser");
		if (f.exists()) {
			model.readFile("event.ser");
		} else
			JOptionPane.showMessageDialog(null, "First run! File does not exist.");

		this.model = model;
		setLayout(new BorderLayout());
		c = model.gcal;
		evs = model.events;
		model.setTodaysEvents();

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BorderLayout());
		JButton createButton = new JButton("Create");
		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createEventDialog();
				Event newEvent = model.createNewEvent(titleField.getText(), dateField.getText(), fromField.getText(),
						toField.getText());
			
					if(model.timeConflict(newEvent)){
						JOptionPane.showMessageDialog(null,"Time conflict. Try Again!");
					}
					else{
						model.addEvent(newEvent);
						model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
						System.out.println("Event added.");
					}
				
			}
		});

		JButton nextButton = new JButton(">");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextDay();
				model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
		});
		
		JButton prevButton = new JButton("<");
		prevButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				model.prevDay();
				model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
		});

		JButton quitButton = new JButton("Quit");
		final int TIME_VISIBLE = 1000;
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				model.writeFile("event.ser");
				JOptionPane pane = new JOptionPane("Saving..", JOptionPane.INFORMATION_MESSAGE);
				final JDialog dialog = pane.createDialog(null, "Save & Close");
				dialog.setModal(false);
				dialog.setVisible(true);

				new Timer(TIME_VISIBLE, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);
						System.exit(0);
					}
				}).start();
			}
		});

		JPanel navPanel = new JPanel();
		navPanel.add(prevButton);
		navPanel.add(nextButton);

		titlePanel.add(createButton, BorderLayout.WEST);
		titlePanel.add(navPanel, BorderLayout.CENTER);
		titlePanel.add(quitButton, BorderLayout.EAST);

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridLayout(1, 0));
		JLabel b;
		for (int i = 0; i < 7; i++) {
			b = new JLabel(arrayOfDays[i] + "");
			b.setHorizontalAlignment(SwingConstants.CENTER);
			labelPanel.add(b);
		}

		datePanel = new JPanel();
		datePanel.setSize(30, 60);
		datePanel.setLayout(new GridLayout(0, 7));
		printCalendar();

		border = new TitledBorder(
				arrayOfDays[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + arrayOfMonths[c.get(Calendar.MONTH)] + " "
						+ c.get(Calendar.DAY_OF_MONTH) + ", " + c.get(Calendar.YEAR));
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		border.setTitleColor(Color.RED);
		border.setTitleFont(new Font("LucidaSans", Font.BOLD, 25));

		monthViewPanel = new JPanel();
		monthViewPanel.setLayout(new BorderLayout());
		monthViewPanel.add(labelPanel, BorderLayout.NORTH);
		monthViewPanel.add(datePanel, BorderLayout.CENTER);

		dayViewPanel = new JPanel(new GridLayout(0, 1));
		dayViewPanel.setSize(10, 10);
		dayViewPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		evsToday = model.getTodaysEvents();
		System.out.println(evsToday);
		printEvents();

		setBorder(border);
		add(titlePanel, BorderLayout.NORTH);
		add(monthViewPanel, BorderLayout.WEST);
		add(dayViewPanel, BorderLayout.EAST);

	}
	

	private void add(TitledBorder border2) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * stateChanged
	 * 
	 * 
	 */

	@Override
	public void stateChanged(ChangeEvent ce) {
		evs = model.getEvents();
		c = model.gcal;
		evsToday = model.getTodaysEvents();

		border.setTitle(arrayOfDays[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + arrayOfMonths[c.get(Calendar.MONTH)] + " "
				+ c.get(Calendar.DAY_OF_MONTH) + ", " + c.get(Calendar.YEAR));

		printCalendar();
		monthViewPanel.repaint();

		printEvents();
		dayViewPanel.repaint();
		repaint();
		// printDayView();
		printMonth(c);
	}
	
	/**
	 * printEvents
	 * 
	 * 
	 */


	private void printEvents() {

		dayViewPanel.removeAll();

		for (int i = 0; i < evsToday.size(); i++) {

			eventButton = new JButton();
			eventButton.setText(evsToday.get(i) + "");
			final JPopupMenu contextMenu = new JPopupMenu("Menu");

			final Event ev = evsToday.get(i);
			// System.out.println("ev: " + ev + "\ni: " + i);
			eventButton.setComponentPopupMenu(contextMenu);

			dayViewPanel.add(eventButton);

			JMenuItem delete = new JMenuItem("Delete");

			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int option = JOptionPane.showConfirmDialog(null, "Delete " + ev.title + "?");
					if(option == 0){
						model.deleteEvent(ev);
						model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
					}
					else
						return;
				}

			});
			contextMenu.add(delete);
		}
	}

	public void printCalendar() {
		datePanel.removeAll();

		// System.out.println("Date in model now: " + c.get(Calendar.YEAR) + "/"
		// + c.get(Calendar.MONTH) + "/" + c.get(Calendar.DATE));
		GregorianCalendar temp = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		int first = temp.get(Calendar.DAY_OF_WEEK) - 1;
		int last = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		int m = 6;
		int n = 7;
		int date = 1;
		int i = 0;
		JPanel[][] panelHolder = new JPanel[m][n];
		Calendar today = Calendar.getInstance();
		for (int row = 0; row < m; row++) {
			for (int col = 0; col < n; col++) {

				panelHolder[row][col] = new JPanel(new BorderLayout());
				JButton button = new JButton();
				final int currentDate = date;
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), currentDate);
					}
				});

				if ((row == 0 && col < first) || (date > last)) {
					;
				} else if (date == today.get(Calendar.DAY_OF_MONTH)
						&& c.get(Calendar.MONTH) == today.get(Calendar.MONTH)
						&& c.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
					button.setBackground(Color.red);
					button.setOpaque(true);
					button.setText(Integer.toString(date++));
					panelHolder[row][col].add(button);
				} else if (date == c.get(Calendar.DAY_OF_MONTH)) {
					button.setBackground(Color.black);
					button.setOpaque(true);
					button.setText(Integer.toString(date++));
					panelHolder[row][col].add(button);

				} else {
					button.setText(Integer.toString(date++));
					panelHolder[row][col].add(button);
				}

				datePanel.add(panelHolder[row][col]);
			}
		}

		datePanel.revalidate();
	}

	/**
	 * createEventDialog
	 * 
	 * 
	 */
	public void createEventDialog() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		titleField = new JTextField();
		dateField = new JTextField();
		fromField = new JTextField();
		toField = new JTextField();
		panel.add(new JLabel("Title:"));
		panel.add(titleField);
		panel.add(new JLabel("Date: "));
		dateField.setText((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
		panel.add(dateField);
		panel.add(new JLabel("From:"));
		panel.add(fromField);
		panel.add(new JLabel("To:"));
		panel.add(toField);
		int result = JOptionPane.showConfirmDialog(null, panel, "Create Event", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			System.out.println(titleField.getText() + " " + fromField.getText() + "-" + toField.getText());
		} else {
			System.out.println("Cancelled");
		}

	}

	public void printHighlightMonth(ArrayList<Integer> eventdates) {
		MONTHS[] arrayOfMonths = MONTHS.values();

		System.out.println(arrayOfMonths[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR));
		System.out.println("Su Mo Tu We Th Fr Sa");

		int first = c.get(Calendar.DAY_OF_WEEK) - 1;
		int last = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		String[][] grid = new String[6][7];
		int date = 1;
		int i = 0;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				if ((row == 0 && col < first) || (date > last)) {
					grid[row][col] = " ";
				} else if (i < eventdates.size() && date == eventdates.get(i)) {
					grid[row][col] = "[" + Integer.toString(date++) + "]";
					i++;
				} else
					grid[row][col] = Integer.toString(date++);

			}
		}

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {

				String s = String.format("%2s", grid[row][col]);
				System.out.print(s + " ");
			}
			System.out.print("\n");
		}
	}

	public void printMonth(Calendar cal) {
		MONTHS[] arrayOfMonths = MONTHS.values();

		System.out.println(arrayOfMonths[c.get(Calendar.MONTH)] + " " + cal.get(Calendar.YEAR));
		System.out.println("Su Mo Tu We Th Fr Sa");

		GregorianCalendar temp = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
		int first = temp.get(Calendar.DAY_OF_WEEK) - 1;
		int last = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		String[][] grid = new String[6][7];
		int date = 1;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {

				if ((row == 0 && col < first) || (date > last)) {
					grid[row][col] = " ";
				} else if (date == c.get(Calendar.DAY_OF_MONTH)) {
					grid[row][col] = "[" + Integer.toString(date++) + "]";
					// i++
				} else
					grid[row][col] = Integer.toString(date++);

			}
		}

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {

				String s = String.format("%2s", grid[row][col]);
				System.out.print(s + " ");
			}
			System.out.print("\n");
		}
	}

	private static final int PREF_W = 900;
	private static final int PREF_H = 400;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}
}
