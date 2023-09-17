import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame {
	MainSerial ms;
	JFrame frame;
	MainPanel mp;
	Vector<Integer> data;
	JLabel[] la_vol;
	JLabel[] la_val;
	JLabel[] la_time;
	JLabel la_VOL, la_VAL, la_TIME, la_bpm, la_interval;
	double bpm,interval;
	public MainFrame(MainSerial ms) {
		this.ms = ms;
		bpm = 0;
		interval = 0;
		data = this.ms.data;
		frame = new JFrame("UART_Viewer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(0, 0, 1600, 800);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.addWindowListener(new WindowListener() {
			public void windowClosed(WindowEvent arg0) {
				System.exit(0);
			}
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		
		la_bpm = new JLabel("BPM : ");
		la_bpm.setBounds(1000, 50, 150, 50);

		la_interval = new JLabel("RR interval : ");
		la_interval.setBounds(1200, 50, 150, 50);
		
		la_VOL = new JLabel("[ V ]");
		la_VOL.setBounds(30, 50, 50, 50);

		la_VAL = new JLabel("[ R ]");
		la_VAL.setBounds(70, 50, 50, 50);
		
		la_TIME = new JLabel("[ S ]");
		la_TIME.setBounds(1550, 695, 50, 50);
		
		la_vol = new JLabel[34];
		for(int i=0;i<34;i++) {
			String str = ""+(i/10)+"."+(i%10);
			la_vol[i] = new JLabel(str);
			la_vol[i].setBounds(30, (int)(600*(1.0-((double)i/33.0)))+75, 20, 50);
		}

		la_val = new JLabel[6];
		for(int i=0;i<6;i++) {
			String str = ""+(i*50);
			la_val[i] = new JLabel(str);
			la_val[i].setBounds(70, 600-i*117+75, 50, 50);
		}

		la_time = new JLabel[15];
		for(int i=0;i<15;i++) {
			String str = ""+(i/10)+"."+(i%10);
			la_time[i] = new JLabel(str);
			la_time[i].setBounds((int)(1400*((double)i/14.0))+115, 695, 50, 50);
		}
		
		mp = new MainPanel();
		
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.white);
		frame.getContentPane().add(mp);
		for(int i=0;i<34;i++) {
			frame.getContentPane().add(la_vol[i]);
		}
		for(int i=0;i<6;i++) {
			frame.getContentPane().add(la_val[i]);
		}
		for(int i=0;i<15;i++) {
			frame.getContentPane().add(la_time[i]);
		}
		frame.getContentPane().add(la_VOL);
		frame.getContentPane().add(la_VAL);
		frame.getContentPane().add(la_TIME);
		frame.getContentPane().add(la_bpm);
		frame.getContentPane().add(la_interval);
	}
	
	public class MainPanel extends JPanel{
		
		public MainPanel() {
			this.setBounds(120, 100, 1400, 600);
		}
		
		protected void paintComponent(Graphics g) {
			data = ms.getVector();
			interval = ms.getInterval();
			la_bpm.setText("BPM : "+ms.getBPM());
			la_interval.setText("R-Interval : "+ms.getInterval());
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setColor(Color.black);
			g2d.fillRect(0, 0, 1400, 600);
			
			g2d.setColor(Color.white);
			for(int i=0;i<6;i++) {
				g2d.drawLine(0, 600-i*117, 1400, 600-i*117);
			}
			for(int i=0;i<15;i++) {
				g2d.drawLine((int)(1400*((double)i/14.0)), 0, (int)(1400*((double)i/14.0)), 600);
			}
			
			g2d.setColor(Color.green);
			if(data.size()>2) {
				if(data.size()<1400) {
					for(int i=1;i<data.size();i++) {
						g2d.drawLine(1399-data.size()+i, 600-(data.get(i-1)*600/255), 1400-data.size()+i, 600-(data.get(i)*600/255));
					}
				}else {
					for(int i=1;i<1400;i++) {
						g2d.drawLine(i-1, 600-(data.get(i-1)*600/255), i, 600-(data.get(i)*600/255));
					}
				}
			}
		}
	}
}
