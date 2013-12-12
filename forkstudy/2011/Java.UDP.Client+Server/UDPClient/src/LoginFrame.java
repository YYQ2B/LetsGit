import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {
	Socket login = null;
	private JPanel jContentPane = null;
	private JLabel lab1 = null;
	private JLabel lab2 = null;
	private JLabel lab3 = null;
	private JTextField userNameField = null;
	private JPasswordField passwordField = null;
	private JButton loginButton = null;
	private JButton cancelButton = null;

	private JTextField getUserNameField() {
		if (userNameField == null) {
			userNameField = new JTextField();
			userNameField.setSize(new Dimension(171, 33));
			userNameField.setFont(new Font("Dialog", Font.PLAIN, 18));
			userNameField.setLocation(new Point(140, 70));
		}
		return userNameField;
	}

	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setSize(new Dimension(173, 30));
			passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));
			passwordField.setLocation(new Point(140, 110));
		}
		return passwordField;
	}

	private JButton getLoginButton() {
		if (loginButton == null) {
			loginButton = new JButton();
			loginButton.setLocation(new Point(25, 180));
			loginButton.setText("登录");
			loginButton.setSize(new Dimension(75, 30));
		}
		return loginButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setLocation(new Point(270, 180));
			cancelButton.setText("取消");
			cancelButton.setSize(new Dimension(75, 30));
		}
		return cancelButton;
	}

	public LoginFrame() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		initialize();
		this.setResizable(false);
		this.setVisible(true);
		loginButton.addActionListener(new MyLoginListener());
		userNameField.addKeyListener(new MyLoginListener());
		passwordField.addKeyListener(new MyLoginListener());
	}

	private void initialize() {
		this.setSize(362, 267);
		this.setContentPane(getJContentPane());
		this.setTitle("用户登录");
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			lab3 = new JLabel();
			lab3.setText("密    码：");
			lab3.setSize(new Dimension(80, 30));
			lab3.setFont(new Font("Dialog", Font.BOLD, 18));
			lab3.setLocation(new Point(50, 110));
			lab2 = new JLabel();
			lab2.setText("用户名：");
			lab2.setSize(new Dimension(80, 30));
			lab2.setToolTipText("");
			lab2.setFont(new Font("Dialog", Font.BOLD, 18));
			lab2.setLocation(new Point(50, 70));
			lab1 = new JLabel();
			lab1.setBounds(new Rectangle(54, 12, 245, 43));
			lab1.setFont(new Font("Dialog", Font.BOLD, 24));
			lab1.setForeground(new Color(0, 0, 204));
			lab1.setText("用户登录");
			jContentPane = new JPanel();
			jContentPane.setBackground(Color.orange);
			jContentPane.setLayout(null);
			jContentPane.add(lab1, null);
			jContentPane.add(lab2, null);
			jContentPane.add(lab3, null);
			jContentPane.add(getUserNameField(), null);
			jContentPane.add(getPasswordField(), null);
			jContentPane.add(getLoginButton(), null);
			jContentPane.add(getCancelButton(), null);
			this.setLocationRelativeTo(null);
			try {
				this.setIconImage(ImageIO.read(new File(LoginFrame.class
						.getClassLoader().getResource("").getFile(),
						"image.jpg")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jContentPane;
	}

	void send() {
		Scanner scan = null;
		PrintWriter pw = null;
		String userName = userNameField.getText();
		char[] c = passwordField.getPassword();
		String password = String.valueOf(c);
		if (userName == null || userName.equals("")) {
			JOptionPane.showMessageDialog(null, "用户名不能为空");
			return;
		}
		if (password == null || password.equals("")) {
			JOptionPane.showMessageDialog(null, "密码名不能为空");
			return;
		}
		try {

			login = new Socket("192.168.1.106", 1500);
			scan = new Scanner(new InputStreamReader(login.getInputStream(),
					"gbk"));
			pw = new PrintWriter(login.getOutputStream());
			pw.println(userName);
			pw.println(password);
			pw.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			String str = scan.nextLine();

			if (str.equals("false")) {
				JOptionPane.showMessageDialog(null, "用户名或密码错误，请重新输入");
				userNameField.setText("");
				passwordField.setText("");
				return;
			} else if (str.equals("online")) {
				JOptionPane.showMessageDialog(null, "登录失败,该用户已在线");
				userNameField.setText("");
				passwordField.setText("");
				return;
			} else if (str.equals("true")) {
				String nick_name = scan.nextLine();
				int serverPort = scan.nextInt();
				InetAddress serverInetAddress = login.getInetAddress();
				InetAddress userInetAddress = login.getLocalAddress();
				int userPort = new Random().nextInt(1000) + 2000;
				pw.println(userPort);
				pw.flush();
				if (scan.next().equals("ok")) {
					LoginFrame.this.dispose();
					new Client(serverInetAddress, userInetAddress, serverPort,
							userPort, nick_name);
				}

			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	class MyLoginListener extends KeyAdapter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			send();
		}

		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				send();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LoginFrame();
	}

}
