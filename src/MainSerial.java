import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JFrame;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class MainSerial {
	JFrame window;
	Thread thread_serial;
	Vector<Integer> data = new Vector<Integer>();
	Vector<Integer> data_peak = new Vector<Integer>();
	double bpm,interval;
	int print_cnt;
	public MainSerial(String portName) throws Exception {
		bpm = 0;
		interval = 0;
		print_cnt = 0;
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				thread_serial = new Thread(new SerialReader(in));
			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {
					if(data.size()>1405) {
						int gap = data.size()-1405;
						for(int i=0;i<gap;i++) {
							data.remove(0);	
						}
					}
					if(data_peak.size()>10000) {
						int gap = data_peak.size()-10000;
						for(int i=0;i<gap;i++) {
							data_peak.remove(0);	
						}
					}
					if(len>0) {
						for (int i=0;i<len;i++){
							int value = new Integer(buffer[i]);
							if(value<0) value = 256 + value;
							data.add(value);
							data_peak.add(value);
						}
						window.repaint();
					}
					if(print_cnt++>1000) {
						peak_detection(200,1);
						print_cnt=0;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public class MyData {
		int index;
		int value;
		public MyData(int ind, int val) {
			this.index = ind;
			this.value = val;
		}
		public int getValue() {
			return value;
		}
	}

	void peak_detection(int limit, int term) {
		int zero_cnt = 0;
		boolean working = false, save = false;
		Vector<MyData> temp = new Vector<MyData>();
		Vector<MyData> max_temp = new Vector<MyData>();
		temp.add(new MyData(-1,-1));
		for(int i=0;i<data_peak.size();i++) {
			int value = data_peak.get(i); 
			if(value<limit) {
				zero_cnt++;
				if(zero_cnt>term) {
					if(save) {
						working = false;
						save = false;
						temp.add(new MyData(i,-1));
					}
				}
			}else {
				if(zero_cnt>term) {
					working = true;
				}
				zero_cnt=0;
			}
			if(working) {
				save = true;
				temp.add(new MyData(i, value));
			}
		}
		temp.add(new MyData(temp.size(),-1));

		MyData max = new MyData(0,0);
		for(int i=0;i<temp.size();i++) {
			if(temp.get(i).value==-1) {
				if(max.index!=-1) {
					max_temp.add(max);
				}
				max = new MyData(0,0);
			}else {
				if(temp.get(i).value>max.value) {
					max = temp.get(i); 
				}
			}
		}
		max_temp.remove(max_temp.size()-1);

		double result = 0;
		if(max_temp.size()>1) {
			for(int i=1;i<max_temp.size();i++) {
				result+=max_temp.get(i).index-max_temp.get(i-1).index;
			}
			result/=max_temp.size()-1;
			interval = (double)result/1000*1000;	//result/[sampling-rate]*1000;
			bpm = 60000 /interval;
		}
	}

	public Vector<Integer> getVector(){
		return data;
	}

	public double getBPM(){
		return bpm;
	}

	public double getInterval(){
		return interval;
	}

	public void setWindow(JFrame frame) {
		window = frame;
	}
}