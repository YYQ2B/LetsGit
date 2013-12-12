import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends WindowAdapter{ //Window实现了窗口监听接口WindowListener
		  public void windowClosing(WindowEvent e) {  
		      System.exit(0); 
		  }
		  public void windowClosed(WindowEvent e){
			  System.exit(0);
		  }
	 }