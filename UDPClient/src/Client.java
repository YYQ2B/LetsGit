import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
class Client extends JFrame implements Runnable {
	JTextArea mainArea;
	JTextArea sendArea;
	JButton sendBtn;
	static InetAddress serverInetAddress;
	static int userPort;
	static int serverPort;
	static String nick_name;
	static DatagramSocket mail_data = null;
	InetAddress userInetAddress;
	static Thread t;

	@SuppressWarnings("static-access")
	public Client(InetAddress serverInetAddress, InetAddress userInetAddress,
			int serverPort, int userPort, String nick_name) {
		super("Xeon聊天室      " + nick_name);
		this.nick_name = nick_name;
		this.userPort = userPort;
		this.serverInetAddress = serverInetAddress;
		this.userInetAddress = userInetAddress;
		this.serverPort = serverPort;
		Container contain = getContentPane();
		contain.setLayout(new BorderLayout(3, 3));
		mainArea = new JTextArea();
		mainArea.setEditable(false);
		JScrollPane mainAreaP = new JScrollPane(mainArea);// 为文本区加滚动条
		mainAreaP.setBorder(BorderFactory.createTitledBorder("聊天记录"));
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		sendArea = new JTextArea(3, 8);
		JScrollPane sendAreaP = new JScrollPane(sendArea);
		sendBtn = new JButton("发送");
		// 事件处理
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (sendArea.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "信息不能为空");
				} else {
					sendMsg(sendArea.getText());
					sendArea.setText("");
				}
			}
		});
		this.addWindowListener(new ClientAction(this));
		sendArea.addKeyListener(new ClientAction(this));
		JPanel tmpPanel = new JPanel();
		JLabel jl = new JLabel("CTRL+ENTER快捷发送");
		jl.setFont(new Font("宋体", 0, 9));
		tmpPanel.setLayout(new GridLayout(2, 1, 1, 1));
		tmpPanel.add(sendBtn);
		tmpPanel.add(jl);
		panel.add(tmpPanel, BorderLayout.EAST);
		panel.add(sendAreaP, BorderLayout.CENTER);
		contain.add(mainAreaP, BorderLayout.CENTER);
		contain.add(panel, BorderLayout.SOUTH);
		try {
			this.setIconImage(ImageIO.read(new File(LoginFrame.class
					.getClassLoader().getResource("").getFile(), "image.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setLocationRelativeTo(null);
		this.setSize(500, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		t = new Thread(this);
		t.start();
	}

	// 重写run()
	public void run() {// 接收数据包
		mail_data = null;
		DatagramPacket pack = null;
		byte data[] = new byte[320];
		pack = new DatagramPacket(data, data.length);
		try {
			mail_data = new DatagramSocket(userPort, userInetAddress);
			sendStart();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while (true) {
			if (mail_data == null&&!mail_data.isClosed())
				break;
			else {
				try {
					mail_data.receive(pack);
				} catch (IOException e) {
				}
				String msg = null;
				try {
					msg = new String(pack.getData(), 0, pack.getLength(), "gbk");
				} catch (Exception e) {
					e.printStackTrace();
				}
				mainArea.append(msg + "\n");
				mainArea.selectAll();
				mainArea.setCaretPosition(mainArea.getSelectedText().length());
				mainArea.requestFocus();
			}
		}
	}

	public void sendMsg(String s) {// 发送数据包
		if (s.equals("")) {
			JOptionPane.showMessageDialog(null, "发送的消息不能为空!");
		} else {
			s = nick_name + " : " + s;
			byte buffer[] = s.getBytes();
			try {
				DatagramPacket data_pack = new DatagramPacket(buffer,
						buffer.length, serverInetAddress, serverPort);
				mail_data.send(data_pack);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendStart() {// 发送数据包
		String str = nick_name + " : " + "51f861d8539990482dd6ab75d0fec7b9";
		byte buffer[] = str.getBytes();
		try {
			DatagramPacket data_pack = new DatagramPacket(buffer,
					buffer.length, serverInetAddress, serverPort);
			mail_data.send(data_pack);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendEnd() {// 发送数据包
		String str = nick_name + " : " + "78482a614b3ba74848448abae234e556";
		byte buffer[] = str.getBytes();
		try {
			DatagramPacket data_pack = new DatagramPacket(buffer,
					buffer.length, serverInetAddress, serverPort);
			mail_data.send(data_pack);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ClientAction extends WindowAdapter implements KeyListener {
		Client c;

		public ClientAction(Client c) {
			this.c = c;
		}

		@SuppressWarnings("deprecation")
		public void windowClosing(WindowEvent e) {
			Client.sendEnd();
			Client.mail_data.close();
			t.stop();
		}

		@SuppressWarnings("deprecation")
		public void windowClosed(WindowEvent e) {
			Client.sendEnd();
			Client.mail_data.close();
			t.stop();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
				if (sendArea.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "信息不能为空");
				} else {
					sendMsg(sendArea.getText());
					sendArea.setText("");
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
