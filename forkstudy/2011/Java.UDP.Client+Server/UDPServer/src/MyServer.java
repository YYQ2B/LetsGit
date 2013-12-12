import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

public class MyServer implements Runnable {// , ActionListener
	/**
	 * 
	 * 服务端的操作类，主要负责接收客户的socket和开启服务端的serversocket
	 * 
	 */
	static InetAddress serverInetAddress;
	public static DatagramSocket serverSocket; // 定义该服务器的socket
	DatagramPacket serverPacket; // 定义该服务器接收到的packet
	static int inter; // 定义该服务器的端口
	Thread t; // 定义服务器的线程
	boolean bol = true; // 判定的boolean值
	VerificationUser verUser; // 定义用户验证操作类

	@SuppressWarnings("static-access")
	public MyServer() { // 构造方法
		inter = new Random().nextInt(1000) + 2000; // 随机设定该服务端的端口号，范围2000~3000
		verUser = new VerificationUser(); // 实例化用户信息操作类，并默认先执行该类中的内容，若用户信息正确，则反馈给用户端口号连接该服务端
		try {
			/*
			 * 这段代码是获取服务段的InetAddress 需要改善
			 */
			InetAddress[] inetAdds = InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName());
			String regex = "(?:(?:[01]?\\d?\\d|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d?\\d|2[0-4]\\d|25[0-5])";
			serverInetAddress = Pattern.matches(regex,
					inetAdds[1].getHostAddress()) ? inetAdds[1] : inetAdds[0];
			serverSocket = new DatagramSocket(this.inter, serverInetAddress);
			Class.forName("com.mysql.jdbc.Driver"); // 加载驱动
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {
		System.out.println("服务器已经启动...");
		byte data[] = new byte[320]; // 定义信息最大长度为320字节
		serverPacket = new DatagramPacket(data, data.length); // 实例化服务端的DatagramSocket
		while (true) { // 循环接收消息
			if (serverSocket == null) {
				break;
			} else {
				try {
					serverSocket.receive(serverPacket); // 接收serverPacket
				} catch (IOException e) {
					e.printStackTrace();
				}

				String msg = null;
				try {
					msg = new String(serverPacket.getData(), 0, // 获取接收的信息
							serverPacket.getLength(), "utf-8"); // 接收的byte信息转换为utf-8的string
				} catch (Exception e) {
					e.printStackTrace();
				}
				InetAddress userInetAddress = serverPacket.getAddress(); // 获取信息的
				String nick_name = msg.split("\\s")[0];
				User user = getThisUser(nick_name); // 获取当前发消息的user
				if (msg.split("\\s")[2]
						.equals("78482a614b3ba74848448abae234e556")) { // 判断接收的消息是否为请求离线
					deleteUser(user); // 从在线用户列表中删除该用户
				} else if (msg.split("\\s")[2]
						.equals("51f861d8539990482dd6ab75d0fec7b9")) { // 判断接收的消息是否为请求连接
					sendUserOfflineMessage(user, userInetAddress); // 从在线用户列表中添加该用户
				} else {
					synchronized (VerificationUser.offlineUser) {
						sendMsg(msg); // 发送数据包
						saveOfflineMessage(msg); // 发送处理后的信息
					}
				}
			}
		}
	}

	public void sendMsg(String str) { // 发送数据包
		byte buffer[] = str.getBytes(); // 将准备发送的信息转换为byte
		Set<User> s = VerificationUser.map.keySet();
		synchronized (VerificationUser.map) { // 同步的情况下迭代输出
			Iterator<User> it = s.iterator();
			while (it.hasNext()) {
				User u = it.next();
				InetAddress add = VerificationUser.map.get(u); // 获取当前user与之对应的InetAddress
				try {
					DatagramPacket data_pack = new DatagramPacket(buffer,
							buffer.length, add, u.getPort());// 定义发送到指定InetAddress指定端口的DatagramPacket
					serverSocket.send(data_pack); // 从服务器serverSocket发送信息
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public User getThisUser(String nick_name) { // 获取发送用户的AnetAddress对应的User
		User u = null;
		while (true) {
			synchronized (VerificationUser.onlineUser) { // 同步的情况下迭代输出
				Iterator<User> it = VerificationUser.onlineUser.iterator();
				while (it.hasNext()) {
					User user = it.next();
					if (user.getNick_name().equals(nick_name)) { // 若InetAddress比较相同
						u = user;// 则user赋值
						break;
					}
				}
			}
			if (u != null) {
				break; // 如果找到用户则退出循环
			}
		}
		return u;
	}

	public void saveOfflineMessage(String message) {
		Connection con = null; // 定义连接数据库的Connection
		Statement stm = null; // 定义连接数据库的Statement
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/xeon", "root", ""); // 加载驱动
			stm = con.createStatement(); // 创建statement
		} catch (SQLException e) {
			e.printStackTrace();
		}
		StringBuffer info = new StringBuffer();
		synchronized (VerificationUser.offlineUser) {
			if (VerificationUser.offlineUser.size() > 0) { // 若有离线用户 则保存
				Iterator<User> it = VerificationUser.offlineUser.iterator(); // 遍历的iterator
				while (it.hasNext()) {
					User u = it.next(); // 将user赋值为所有用户中的下一个用户
					info.append(u.getUsername() + " ");
				}
				try {
					stm.execute("insert into message values('" + message
							+ "','" + info.toString() + "')");// 向该用户中添加未读信息
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					stm.close(); // 关闭流
					con.close(); // 关闭流
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void sendUserOfflineMessage(User user, InetAddress userInetAddress) { // 发送对应用户的离线消息
		Connection con = null; // 定义连接数据库的Connection
		Statement stm = null; // 定义连接数据库的Statement
		ResultSet rs = null; // 定义获取数据库信息的ResultSet
		TreeMap<String, String> use = new TreeMap<String, String>();
		Vector<String> msg = new Vector<String>();
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/xeon", "root", ""); // 加载驱动
			stm = con.createStatement(); // 创建statement
			rs = stm.executeQuery("select * from message"); // 查找信息
			while (rs.next()) { // 依次获取信息
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
			byte buffer[] = str.getBytes(); // 将信息转换为byte
			DatagramPacket data_pack = new DatagramPacket(buffer,
					buffer.length, userInetAddress, user.getPort()); // 定义发向该用户的DatagramPacket
			try {
				serverSocket.send(data_pack);
			} catch (IOException e) {
				e.printStackTrace();
			} // 从服务器serverSocket发送信息
			String info = use.get(str).replace(user.getUsername(), " ");
			if (info.split("\\s").length == 0) {
				try {
					stm.executeUpdate("delete from message where message ='"
							+ str + "'");
				} catch (SQLException e) {
					e.printStackTrace();
				} // 删除数据库中已读信息
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
			rs.close(); // 关闭流
			stm.close(); // 关闭流
			con.close(); // 关闭流
		} catch (Exception e) {
			e.printStackTrace();
		}
		VerificationUser.map.put(user, userInetAddress); // 向当前在线用户中添加当前user和与其对应的InetAddress
		VerificationUser.onlineUser.add(user); // 向当前用户添加user
		VerificationUser.offlineUser.remove(user);
		System.out.println("用户" + user.getUsername() + "登录成功!");
	}

	public void deleteUser(User user) { // 从在线列表中删除已断开的用户
		VerificationUser.map.remove(user);
		VerificationUser.onlineUser.remove(user);
		VerificationUser.offlineUser.add(user);
		System.out.println("用户" + user.getUsername() + "已断开");
	}

	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String args[]) {
		MyServer ms = new MyServer();
		Thread t = new Thread(ms); // 创建当前类的线程
		t.start(); // 线程启动
		Scanner in = new Scanner(System.in);
		while (!in.next().equals("close")) {
		}
		serverSocket.close();
		t.stop();
		System.out.println("服务器已经关闭...");
		System.exit(0);
	}

}
