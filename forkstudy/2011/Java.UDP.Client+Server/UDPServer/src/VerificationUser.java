import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class VerificationUser implements Runnable {
	/**
	 * 
	 * 用户验证操作类，主要负责验证客户输入的用户信息是否正确并给与反馈，从数据库中提取全部的用户信息，并将其与客户输入的信息比较
	 * 
	 */
	static ServerSocket ss; // 定义接收客户输入用户信息的服务端serversocket
	Socket s; // 定义客户端接收到的socket
	User user; // 定义一个User，为保存当前连入并且用户信息正确的User对象
	PrintWriter pw; // 定义一个向用户输出的printwriter
	byte[] userAddress; // 定义接收的用户地址
	static Thread t; // 验证用户的线程
	int userPort;
	public static Set<User> userSet = Collections
			.synchronizedSet(new HashSet<User>()); // 定义存放所有用户信息的userSet
	public static Map<User, InetAddress> map = Collections
			.synchronizedMap(new Hashtable<User, InetAddress>());// 存放已连接的用户和其InetAddress
	public static Set<User> onlineUser = Collections
			.synchronizedSet(new HashSet<User>()); // 在线用户
	public static Set<User> offlineUser = Collections
			.synchronizedSet(new HashSet<User>()); // 离线用户
	public static int threadNum = 0;

	public VerificationUser() {
		try {
			updateAllUser(); // 调用方法获取数据库中的所有用户信息
			getOfflineUser(); // 调用方法获取离线用户
			ss = new ServerSocket(1500); // 创建serversocket并设置端口为1500
			Thread t = new Thread(this); // 创建线程为当前对象
			t.start(); // 启动线程调用run方法
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void run() { // 实现Runable接口中的线程run方法

		while (true) {
			try {

				s = ss.accept(); // 等待客户socket连接并将s设为客户连接的socket
				user = verification(); // 当前用户信息的设定，若输入的用户信息不正确则user为null
				pw = new PrintWriter(s.getOutputStream()); // 将printwriter的对象pw设置为指向客户的socket输出
				@SuppressWarnings("resource")
				Scanner scan = new Scanner(new InputStreamReader(
						s.getInputStream(), "gbk")); // 定义一个从接收客户端socket输出信息的Scanner
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
							pw.println("true"); // 向客户端反馈登录信息正确，反馈字符串“true”
							pw.println(user.getNick_name());// 向客户端发送用户昵称
							pw.println(MyServer.inter); // 向客户端发送聊天服务端的端口号
							pw.flush(); // 刷新流
							userPort = scan.nextInt();
							user.setPort(userPort);
							onlineUser.add(user); // 向当前用户添加user
							pw.println("ok");
							pw.flush();
						} catch (Exception e) {
							s.close(); // 刷新该输出流
						}
					} else {
						pw.println("online"); // 向客户端反馈登录用户在线，反馈字符串“online”
						pw.flush(); // 刷新该输出流
					}
				} else {
					pw.println("false"); // 向客户端反馈登录信息错误，反馈字符串“false”
					pw.flush(); // 刷新该输出流
				}

			} catch (Exception e) { // 捕获异常
				e.printStackTrace();
			}
		}
	}

	public User verification() { // 验证用户信息是否正确的方法，正确返回该用户的对象，错误则返回null
		updateAllUser(); // 更新全部用户信息
		getOfflineUser(); // 调用方法获取离线用户
		User u = null; // 定义一个用来返回的user对象
		try {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(new InputStreamReader(
					s.getInputStream(), "utf-8")); // 定义一个从接收客户端socket输出信息的Scanner
			String username = scan.next(); // 定义一个用来读取用户id的字符串并读取信息
			String password = MD5.GetMD5Code(scan.next()); // 定义一个用来读取用户password的字符串并读取信息
			synchronized (userSet) { // 同步的情况下迭代输出
				Iterator<User> it = userSet.iterator();
				while (it.hasNext()) {
					User user = it.next();
					if (user.getUsername().equals(username)
							&& user.getPassword().equals(password)) { // 如果用户一致则设置u为该对象
						u = user;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u; // 返回User对象，若用户信息错误则为null
	}

	public static void updateAllUser() { // 获取数据库中全部用户信息的方法
		Connection con; // 创建连接数据库的connection
		Statement stm; // 创建连接数据库的statement
		ResultSet rs; // 创建获取用户信息的resultset
		try {
			Class.forName("com.mysql.jdbc.Driver"); // 安装数据库驱动
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/xeon",
					"root", "");
			stm = con.createStatement(); // 连接数据库
			con.setAutoCommit(true); // 设置数据库操作信息自动保存
			rs = stm.executeQuery("select * from tUser"); // 获取全部用户信息语句
			while (rs.next()) {
				int tUserID = rs.getInt("tUserID");
				String username = rs.getString("username"); // 获取username
				String password = rs.getString("password"); // 获取password
				String nick_name = rs.getString("nick_name"); // 获取name
				userSet.add(new User(tUserID, username, password, nick_name)); // 将用户user添加到userSet
			}
		} catch (Exception e) { // 捕获异常
			e.printStackTrace();
		}
	}

	public static void getOfflineUser() { // 更新离线用户列表

		synchronized (userSet) { // 同步的情况下迭代输出
			Iterator<User> itall = userSet.iterator();
			while (itall.hasNext()) {
				synchronized (onlineUser) { // 同步的情况下迭代输出
					User allU = itall.next();
					boolean bol = false; // 判定是否在线的bol判定值
					Iterator<User> iton = onlineUser.iterator();
					while (iton.hasNext()) {
						User onU = iton.next();
						if (onU.getUsername().equals(allU.getUsername())
								&& onU.getPassword().equals(allU.getPassword())) { // 判定当前用户在线则bol为true
							bol = true;
						}
					}
					if (!bol) { // 不在线则加入离线列表
						offlineUser.add(allU);
					}
				}
			}
		}
	}
}
