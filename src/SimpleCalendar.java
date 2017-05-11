import javax.swing.JFrame;

public class SimpleCalendar {
	public static void main(String [] args){
		JFrame frame = new JFrame();
		frame.setSize(900, 900);
		frame.setTitle("Calendar");
		
		Model model = new Model();
		CalendarView view = new CalendarView(model);
		
		model.attachListener(view);
		
		frame.add(view);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
}
