import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends WindowAdapter{ //Windowʵ���˴��ڼ����ӿ�WindowListener
		  public void windowClosing(WindowEvent e) {  
		      System.exit(0); 
		  }
		  public void windowClosed(WindowEvent e){
			  System.exit(0);
		  }
	 }