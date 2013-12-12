import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class VerificationUser implements Runnable {
	/**
	 * 
	 * �û���֤�����࣬��Ҫ������֤�ͻ�������û���Ϣ�Ƿ���ȷ�����뷴���������ݿ�����ȡȫ�����û���Ϣ����������ͻ��������Ϣ�Ƚ�
	 * 
	 */
	static ServerSocket ss; // ������տͻ������û���Ϣ�ķ����serversocket
	Socket s; // ����ͻ��˽��յ���socket
	User user; // ����һ��User��Ϊ���浱ǰ���벢���û���Ϣ��ȷ��User����
	PrintWriter pw; // ����һ�����û������printwriter
	byte[] userAddress; // ������յ��û���ַ
	static Thread t; // ��֤�û����߳�
	int userPort;
	public static Set<User> userSet = Collections
			.synchronizedSet(new HashSet<User>()); // �����������û���Ϣ��userSet
	public static Map<User, InetAddress> map = Collections
			.synchronizedMap(new Hashtable<User, InetAddress>());// ��������ӵ��û�����InetAddress
	public static Set<User> onlineUser = Collections
			.synchronizedSet(new HashSet<User>()); // �����û�
	public static Set<User> offlineUser = Collections
			.synchronizedSet(new HashSet<User>()); // �����û�
	public static int threadNum = 0;

	public VerificationUser() {
		try {
			updateAllUser(); // ���÷�����ȡ���ݿ��е������û���Ϣ
			getOfflineUser(); // ���÷�����ȡ�����û�
			ss = new ServerSocket(1500); // ����serversocket�����ö˿�Ϊ1500
			Thread t = new Thread(this); // �����߳�Ϊ��ǰ����
			t.start(); // �����̵߳���run����
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void run() { // ʵ��Runable�ӿ��е��߳�run����

		while (true) {
			try {

				s = ss.accept(); // �ȴ��ͻ�socket���Ӳ���s��Ϊ�ͻ����ӵ�socket
				user = verification(); // ��ǰ�û���Ϣ���趨����������û���Ϣ����ȷ��userΪnull
				pw = new PrintWriter(s.getOutputStream()); // ��printwriter�Ķ���pw����Ϊָ��ͻ���socket���
				@SuppressWarnings("resource")
				Scanner scan = new Scanner(new InputStreamReader(
						s.getInputStream(), "gbk")); // ����һ���ӽ��տͻ���socket�����Ϣ��Scanner
				if (user != null) {
					boolean bol = true;
					synchronized (map) {
						Iterator<User> it = map.keySet().iterator();
						while (it.hasNext()) {
							User u = it.next();
							if (u.getUsername().equals(user.getUsername())
									&& u.getTUserID() == user.getTUserID()) {
								bol = false;
								break;
							}
						}
					}
					if (bol) {
						try {
							pw.println("true"); // ��ͻ��˷�����¼��Ϣ��ȷ�������ַ�����true��
							pw.println(user.getNick_name());// ��ͻ��˷����û��ǳ�
							pw.println(MyServer.inter); // ��ͻ��˷����������˵Ķ˿ں�
							pw.flush(); // ˢ����
							userPort = scan.nextInt();
							user.setPort(userPort);
							onlineUser.add(user); // ��ǰ�û����user
							pw.println("ok");
							pw.flush();
						} catch (Exception e) {
							s.close(); // ˢ�¸������
						}
					} else {
						pw.println("online"); // ��ͻ��˷�����¼�û����ߣ������ַ�����online��
						pw.flush(); // ˢ�¸������
					}
				} else {
					pw.println("false"); // ��ͻ��˷�����¼��Ϣ���󣬷����ַ�����false��
					pw.flush(); // ˢ�¸������
				}

			} catch (Exception e) { // �����쳣
				e.printStackTrace();
			}
		}
	}

	public User verification() { // ��֤�û���Ϣ�Ƿ���ȷ�ķ�������ȷ���ظ��û��Ķ��󣬴����򷵻�null
		updateAllUser(); // ����ȫ���û���Ϣ
		getOfflineUser(); // ���÷�����ȡ�����û�
		User u = null; // ����һ���������ص�user����
		try {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(new InputStreamReader(
					s.getInputStream(), "utf-8")); // ����һ���ӽ��տͻ���socket�����Ϣ��Scanner
			String username = scan.next(); // ����һ��������ȡ�û�id���ַ�������ȡ��Ϣ
			String password = MD5.GetMD5Code(scan.next()); // ����һ��������ȡ�û�password���ַ�������ȡ��Ϣ
			synchronized (userSet) { // ͬ��������µ������
				Iterator<User> it = userSet.iterator();
				while (it.hasNext()) {
					User user = it.next();
					if (user.getUsername().equals(username)
							&& user.getPassword().equals(password)) { // ����û�һ��������uΪ�ö���
						u = user;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u; // ����User�������û���Ϣ������Ϊnull
	}

	public static void updateAllUser() { // ��ȡ���ݿ���ȫ���û���Ϣ�ķ���
		Connection con; // �����������ݿ��connection
		Statement stm; // �����������ݿ��statement
		ResultSet rs; // ������ȡ�û���Ϣ��resultset
		try {
			Class.forName("com.mysql.jdbc.Driver"); // ��װ���ݿ�����
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/xeon",
					"root", "");
			stm = con.createStatement(); // �������ݿ�
			con.setAutoCommit(true); // �������ݿ������Ϣ�Զ�����
			rs = stm.executeQuery("select * from tUser"); // ��ȡȫ���û���Ϣ���
			while (rs.next()) {
				int tUserID = rs.getInt("tUserID");
				String username = rs.getString("username"); // ��ȡusername
				String password = rs.getString("password"); // ��ȡpassword
				String nick_name = rs.getString("nick_name"); // ��ȡname
				userSet.add(new User(tUserID, username, password, nick_name)); // ���û�user��ӵ�userSet
			}
		} catch (Exception e) { // �����쳣
			e.printStackTrace();
		}
	}

	public static void getOfflineUser() { // ���������û��б�

		synchronized (userSet) { // ͬ��������µ������
			Iterator<User> itall = userSet.iterator();
			while (itall.hasNext()) {
				synchronized (onlineUser) { // ͬ��������µ������
					User allU = itall.next();
					boolean bol = false; // �ж��Ƿ����ߵ�bol�ж�ֵ
					Iterator<User> iton = onlineUser.iterator();
					while (iton.hasNext()) {
						User onU = iton.next();
						if (onU.getUsername().equals(allU.getUsername())
								&& onU.getPassword().equals(allU.getPassword())) { // �ж���ǰ�û�������bolΪtrue
							bol = true;
						}
					}
					if (!bol) { // ����������������б�
						offlineUser.add(allU);
					}
				}
			}
		}
	}
}
