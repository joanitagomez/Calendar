import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
 * @author Joanitha Christle Gomez 
 * CalendarView has the view for the Calendar
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
	TitledBorder border;
	JPanel datePanel;
	JPanel monthViewPanel;
	JPanel eventPanel;
	JPanel dayViewPanel;

	DefaultListModel<Event> evt;
	JList<Event> eList;
	JScrollPane eListScrollPane = null;
	final JPopupMenu menu;
	JMenuItem delete;

	CalendarView(final Model model) {

		File f = new File("event.ser");
		if (f.exists())
			model.readFile("event.ser");
		else
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

				if (model.timeConflict(newEvent)) {
					JOptionPane.showMessageDialog(null, "Time conflict. Try Again!");
				} else {
					model.addEvent(newEvent);
					model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				}
			}
		});

		JButton nextMonthButton = new JButton(">>");
		nextMonthButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextMonth();
				model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));				
			}
			
		});
		
		JButton nextDayButton = new JButton(">");
		nextDayButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				model.nextDay();
				model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
		});

		
		JButton prevMonthButton = new JButton("<<");
		prevMonthButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				model.prevMonth();
				model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			}
			
		});
		JButton prevDayButton = new JButton("<");
		prevDayButton.addActionListener(new ActionListener() {
			
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
				final JDialog dialog = pane.createDialog(null, "Save");
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

		navPanel.add(prevMonthButton);
		navPanel.add(prevDayButton);
		navPanel.add(nextDayButton);
		navPanel.add(nextMonthButton);

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
		dayViewPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		evsToday = model.getTodaysEvents();

		evt = new DefaultListModel<>();
		eList = new JList<>(evt);

		eList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		eList.setSelectedIndex(0);
		eList.setVisibleRowCount(-1);

		eListScrollPane = new JScrollPane(eList);
		Dimension d = eList.getPreferredSize();
		d.width = 300;
		eListScrollPane.setPreferredSize(d);
		
		menu = new JPopupMenu();
		
		delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(null,
						"Delete " + evt.elementAt(eList.getSelectedIndex()).title + "?");
				
				if (option == 0) {					
					model.deleteEvent(eList.getSelectedValue());
					model.update(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				}
			}
		});

		menu.add(delete);

		printEvents();

		setBorder(border);

		add(titlePanel, BorderLayout.NORTH);
		add(monthViewPanel, BorderLayout.CENTER);
		add(dayViewPanel, BorderLayout.EAST);
		
	}

	/**
	 * Repaints when model notifies of new changes
	 * 
	 * @param ChangeEvent
	 *            ce
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
		repaint();

	}

	/**
	 * Presents day view of the calendar that displays events scheduled
	 */
	public void printEvents() {

		evt.removeAllElements();
		
		for (int i = 0; i < evsToday.size(); i++) {
			evt.addElement(evsToday.get(i));
		}

		
		eList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = eList.locationToIndex(e.getPoint());
				if (index > -1 && eList.getCellBounds(index, index).contains(e.getPoint())) {
					eList.setSelectedIndex(index);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		dayViewPanel.add(eListScrollPane);
	}

	
	/**
	 * Displays calendar with event days highlighted.
	 */
	
	public void printCalendar() {
		
		datePanel.removeAll();

		GregorianCalendar temp = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
		int first = temp.get(Calendar.DAY_OF_WEEK) - 1;
		int last = c.getActualMaximum(Calendar.DAY_OF_MONTH);

		int m = 6;
		int n = 7;
		int date = 1;
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

				if (date == today.get(Calendar.DAY_OF_MONTH) && c.get(Calendar.MONTH) == today.get(Calendar.MONTH)
						&& c.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
					button.setForeground(Color.red);
					button.setOpaque(true);
				}

				if ((row == 0 && col < first) || (date > last)) {
					;
				} else if (date == c.get(Calendar.DAY_OF_MONTH)) {
					button.setBackground(Color.blue);
					button.setOpaque(true);
					button.setText(Integer.toString(date++));
					panelHolder[row][col].add(button);

				} else if (hasEvent(date)) {
					button.setBackground(Color.cyan);
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
	 * Determines if this day has an event scheduled
	 * 
	 * @param date
	 */
	private boolean hasEvent(int date) {
		Calendar cal;
		for (Event e : evs) {
			cal = e.getDateTime();
			if ((cal.get(Calendar.YEAR) == c.get(Calendar.YEAR)) && (cal.get(Calendar.MONTH) == c.get(Calendar.MONTH))
					&& cal.get(Calendar.DAY_OF_MONTH) == date)
				return true;
		}
		return false;
	}


	/**
	 * Input boxes through which user can enter an event
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
		dateField
				.setText((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));

		panel.add(dateField);
		panel.add(new JLabel("From:"));
		panel.add(fromField);
		panel.add(new JLabel("To:"));
		panel.add(toField);

		JOptionPane.showConfirmDialog(null, panel, "Create Event", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}

	private static final int PREF_W = 900;
	private static final int PREF_H = 400;

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}
}
