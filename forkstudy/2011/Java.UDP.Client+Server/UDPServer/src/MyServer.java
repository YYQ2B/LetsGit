import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

public class MyServer implements Runnable {// , ActionListener
	/**
	 * 
	 * ����˵Ĳ����࣬��Ҫ������տͻ���socket�Ϳ�������˵�serversocket
	 * 
	 */
	static InetAddress serverInetAddress;
	public static DatagramSocket serverSocket; // ����÷�������socket
	DatagramPacket serverPacket; // ����÷��������յ���packet
	static int inter; // ����÷������Ķ˿�
	Thread t; // ������������߳�
	boolean bol = true; // �ж���booleanֵ
	VerificationUser verUser; // �����û���֤������

	@SuppressWarnings("static-access")
	public MyServer() { // ���췽��
		inter = new Random().nextInt(1000) + 2000; // ����趨�÷���˵Ķ˿ںţ���Χ2000~3000
		verUser = new VerificationUser(); // ʵ�����û���Ϣ�����࣬��Ĭ����ִ�и����е����ݣ����û���Ϣ��ȷ���������û��˿ں����Ӹ÷����
		try {
			/*
			 * ��δ����ǻ�ȡ����ε�InetAddress ��Ҫ����
			 */
			InetAddress[] inetAdds = InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName());
			String regex = "(?:(?:[01]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d?\\d|2[0-4]\\d|25[0-5])";
			serverInetAddress = Pattern.matches(regex,
					inetAdds[1].getHostAddress()) ? inetAdds[1] : inetAdds[0];
			serverSocket = new DatagramSocket(this.inter, serverInetAddress);
			Class.forName("com.mysql.jdbc.Driver"); // ��������
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("�������Ѿ�����...");
		byte data[] = new byte[320]; // ������Ϣ��󳤶�Ϊ320�ֽ�
		serverPacket = new DatagramPacket(data, data.length); // ʵ��������˵�DatagramSocket
		while (true) { // ѭ��������Ϣ
			if (serverSocket == null) {
				break;
			} else {
				try {
					serverSocket.receive(serverPacket); // ����serverPacket
				} catch (IOException e) {
					e.printStackTrace();
				}

				String msg = null;
				try {
					msg = new String(serverPacket.getData(), 0, // ��ȡ���յ���Ϣ
							serverPacket.getLength(), "utf-8"); // ���յ�byte��Ϣת��Ϊutf-8��string
				} catch (Exception e) {
					e.printStackTrace();
				}
				InetAddress userInetAddress = serverPacket.getAddress(); // ��ȡ��Ϣ��
				String nick_name = msg.split("\\s")[0];
				User user = getThisUser(nick_name); // ��ȡ��ǰ����Ϣ��user
				if (msg.split("\\s")[2]
						.equals("78482a614b3ba74848448abae234e556")) { // �жϽ��յ���Ϣ�Ƿ�Ϊ��������
					deleteUser(user); // �������û��б���ɾ�����û�
				} else if (msg.split("\\s")[2]
						.equals("51f861d8539990482dd6ab75d0fec7b9")) { // �жϽ��յ���Ϣ�Ƿ�Ϊ��������
					sendUserOfflineMessage(user, userInetAddress); // �������û��б�����Ӹ��û�
				} else {
					synchronized (VerificationUser.offlineUser) {
						sendMsg(msg); // �������ݰ�
						saveOfflineMessage(msg); // ���ʹ�������Ϣ
					}
				}
			}
		}
	}

	public void sendMsg(String str) { // �������ݰ�
		byte buffer[] = str.getBytes(); // ��׼�����͵���Ϣת��Ϊbyte
		Set<User> s = VerificationUser.map.keySet();
		synchronized (VerificationUser.map) { // ͬ��������µ������
			Iterator<User> it = s.iterator();
			while (it.hasNext()) {
				User u = it.next();
				InetAddress add = VerificationUser.map.get(u); // ��ȡ��ǰuser��֮��Ӧ��InetAddress
				try {
					DatagramPacket data_pack = new DatagramPacket(buffer,
							buffer.length, add, u.getPort());// ���巢�͵�ָ��InetAddressָ���˿ڵ�DatagramPacket
					serverSocket.send(data_pack); // �ӷ�����serverSocket������Ϣ
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public User getThisUser(String nick_name) { // ��ȡ�����û���AnetAddress��Ӧ��User
		User u = null;
		while (true) {
			synchronized (VerificationUser.onlineUser) { // ͬ��������µ������
				Iterator<User> it = VerificationUser.onlineUser.iterator();
				while (it.hasNext()) {
					User user = it.next();
					if (user.getNick_name().equals(nick_name)) { // ��InetAddress�Ƚ���ͬ
						u = user;// ��user��ֵ
						break;
					}
				}
			}
			if (u != null) {
				break; // ����ҵ��û����˳�ѭ��
			}
		}
		return u;
	}

	public void saveOfflineMessage(String message) {
		Connection con = null; // �����������ݿ��Connection
		Statement stm = null; // �����������ݿ��Statement
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/xeon", "root", ""); // ��������
			stm = con.createStatement(); // ����statement
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuffer info = new StringBuffer();
		synchronized (VerificationUser.offlineUser) {
			if (VerificationUser.offlineUser.size() > 0) { // ���������û� �򱣴�
				Iterator<User> it = VerificationUser.offlineUser.iterator(); // ������iterator
				while (it.hasNext()) {
					User u = it.next(); // ��user��ֵΪ�����û��е���һ���û�
					info.append(u.getUsername() + " ");
				}
				try {
					stm.execute("insert into message values('" + message
							+ "','" + info.toString() + "')");// ����û������δ����Ϣ
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					stm.close(); // �ر���
					con.close(); // �ر���
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void sendUserOfflineMessage(User user, InetAddress userInetAddress) { // ���Ͷ�Ӧ�û���������Ϣ
		Connection con = null; // �����������ݿ��Connection
		Statement stm = null; // �����������ݿ��Statement
		ResultSet rs = null; // �����ȡ���ݿ���Ϣ��ResultSet
		TreeMap<String, String> use = new TreeMap<String, String>();
		Vector<String> msg = new Vector<String>();
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/xeon", "root", ""); // ��������
			stm = con.createStatement(); // ����statement
			rs = stm.executeQuery("select * from message"); // ������Ϣ
			while (rs.next()) { // ���λ�ȡ��Ϣ
				String message = rs.getString("message");
				String info = rs.getString("info");
				String[] s = info.split("\\s");
				inner: for (String str : s) {
					if (user.getUsername().equals(str)) {
						msg.add(message);
						use.put(message, info);
						break inner;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (String str : msg) {
			byte buffer[] = str.getBytes(); // ����Ϣת��Ϊbyte
			DatagramPacket data_pack = new DatagramPacket(buffer,
					buffer.length, userInetAddress, user.getPort()); // ���巢����û���DatagramPacket
			try {
				serverSocket.send(data_pack);
			} catch (IOException e) {
				e.printStackTrace();
			} // �ӷ�����serverSocket������Ϣ
			String info = use.get(str).replace(user.getUsername(), " ");
			if (info.split("\\s").length == 0) {
				try {
					stm.executeUpdate("delete from message where message ='"
							+ str + "'");
				} catch (SQLException e) {
					e.printStackTrace();
				} // ɾ�����ݿ����Ѷ���Ϣ
			} else {
				try {
					stm.executeUpdate("update message set info = '" + info
							+ "' where message = '" + str + "'");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			rs.close(); // �ر���
			stm.close(); // �ر���
			con.close(); // �ر���
		} catch (Exception e) {
			e.printStackTrace();
		}
		VerificationUser.map.put(user, userInetAddress); // ��ǰ�����û�����ӵ�ǰuser�������Ӧ��InetAddress
		VerificationUser.onlineUser.add(user); // ��ǰ�û����user
		VerificationUser.offlineUser.remove(user);
		System.out.println("�û�" + user.getUsername() + "��¼�ɹ�!");
	}

	public void deleteUser(User user) { // �������б���ɾ���ѶϿ����û�
		VerificationUser.map.remove(user);
		VerificationUser.onlineUser.remove(user);
		VerificationUser.offlineUser.add(user);
		System.out.println("�û�" + user.getUsername() + "�ѶϿ�");
	}

	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String args[]) {
		MyServer ms = new MyServer();
		Thread t = new Thread(ms); // ������ǰ����߳�
		t.start(); // �߳�����
		Scanner in = new Scanner(System.in);
		while (!in.next().equals("close")) {
		}
		serverSocket.close();
		t.stop();
		System.out.println("�������Ѿ��ر�...");
		System.exit(0);
	}

}
