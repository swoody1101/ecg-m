
public class MainPrograme {

	
	public static void main(String[] args) {
		MainSerial ms;
		MainFrame mf;
		try {
			ms = new MainSerial("COM6");
			mf = new MainFrame(ms);
			ms.setWindow(mf.frame);
			ms.thread_serial.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
