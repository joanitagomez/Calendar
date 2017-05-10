import java.awt.BorderLayout;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

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
	
	public static void main1(String [] args){
		
		Model model = new Model();
		CalendarView view = new CalendarView(model);
	
		JFrame f = new JFrame();
		f.setSize(200,	300);
		
		JButton button  = new JButton(foo());
		
		//button.setBorder(BorderFactory.createEmptyBorder());
		
	//	button.setContentAreaFilled(true);
		
		JTextArea a = new JTextArea();
		a.setText("hello world");
		f.add(button,BorderLayout.NORTH);
		f.add(a, BorderLayout.SOUTH);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static ImageIcon foo(){
		return new ImageIcon(SimpleCalendar.class.getResource("next.png"));

	}
}
