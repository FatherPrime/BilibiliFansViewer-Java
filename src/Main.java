import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.util.Timer;
import java.util.*;

public class Main {
	static int X = 0;
	static int Y = 0;

	public static void main(String[] args) {
		JFrame pre = new JFrame();
		pre.setSize(350, 235);
		pre.setVisible(true);
		pre.getContentPane().setBackground(new Color(200, 200, 255));
		pre.setLocationRelativeTo(null);
		pre.setLayout(null);
		pre.setResizable(false);
		pre.setTitle("小小粉丝数显示器");
		JLabel show1 = new JLabel("请输入你的UID");
		JLabel show2 = new JLabel("请输入刷新间隔(≥5s)");
		ImageIcon bgimg = new ImageIcon(Main.class.getResource("/res/main.png"));
		JLabel bg = new JLabel(bgimg);
		bg.setBounds(0, -10, 350, 235);
		JPanel imagePanel = (JPanel) pre.getContentPane();
		imagePanel.setOpaque(false);
		pre.getLayeredPane().add(bg, new Integer(Integer.MIN_VALUE));
		JTextField input = new JTextField();
		JTextField time = new JTextField();
		JButton ok = new JButton("确定");
		show1.setForeground(new Color(0, 0, 0));
		show1.setFont(new Font("黑体", 0, 20));
		show1.setBounds(20, 5, 380, 30);
		show2.setForeground(new Color(0, 0, 0));
		show2.setFont(new Font("黑体", 0, 20));
		show2.setBounds(20, 70, 380, 30);
		input.setFont(new Font("黑体", 0, 20));
		input.setBounds(20, 40, 180, 30);
		time.setFont(new Font("黑体", 0, 20));
		time.setBounds(20, 100, 180, 30);
		ok.setFont(new Font("黑体", 0, 30));
		ok.setBounds(40, 140, 130, 45);
		pre.add(show1);
		pre.add(show2);
		pre.add(time);
		pre.add(input);
		pre.add(ok);
		pre.repaint();
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					real(input.getText(), time.getText(), pre);
					pre.dispose();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		pre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void real(String UID, String Time, JFrame pre) throws InterruptedException {
		UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("黑体", 0, 20)));
		UIManager.put("OptionPane.messageFont", new FontUIResource(new Font("黑体", 0, 20)));
		JFrame body = new JFrame();
		body.setUndecorated(true);
		body.setSize(350, 350);
		body.setBackground(new Color(0, 0, 0, 0));
		body.setVisible(true);
		body.setAlwaysOnTop(true);
		body.setLocationRelativeTo(null);
		body.setLocation(1200, 300);
		body.setLayout(null);
		ImageIcon bgimg = new ImageIcon(Main.class.getResource("/res/bg.png"));
		JLabel bg = new JLabel(bgimg);
		bg.setBounds(0, 0, body.getHeight(), body.getWidth());
		JPanel imagePanel = (JPanel) body.getContentPane();
		imagePanel.setOpaque(false);
		body.getLayeredPane().add(bg, new Integer(Integer.MIN_VALUE));
		JLabel A = new JLabel("当前观测：" + name(UID));
		A.setForeground(new Color(20, 20, 20));
		A.setFont(new Font("黑体", 0, 17));
		A.setBounds(38, 246, 350, 30);
		JLabel B = new JLabel("加载中…");
		B.setForeground(new Color(50, 50, 255));
		B.setFont(new Font("思源黑体 CN Bold", 0, 55));
		B.setBounds(0, 265, 300, 80);
		B.setHorizontalAlignment(SwingConstants.RIGHT);
		JButton Close = new JButton("[×]");
		Close.setFont(new Font("微软雅黑", 1, 18));
		Close.setBounds(260, 245, 60, 30);
		Close.setContentAreaFilled(false);
		Close.setBorder(null);
		Close.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				body.dispose();
				pre.setVisible(true);
			}
		}));
		body.add(A);
		body.add(B);
		body.add(Close);
		body.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				X = e.getPoint().x;
				Y = e.getPoint().y;
			}
		});
		body.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				body.setLocation((e.getXOnScreen() - X), (e.getYOnScreen() - Y));
			}
		});
		body.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int a = 5;
		try {
			if (Integer.parseInt(Time) < 5) {
				JOptionPane.showMessageDialog(null, "输入时间间隔过短！", "错误", 2);
				return;
			}
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "输入时间间隔错误！", "错误", 2);
			e.printStackTrace();
			return;
		}
		a = Integer.parseInt(Time);
		System.out.println(a);// 时间调试输出
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				B.setText(fans(UID));
			}
		}, 1000, a * 1000);
	}

	public static String fans(String UID) {
		String data1;
		String[] Final = new String[5];
		try {
			URL url = new URL("https://api.bilibili.com/x/relation/stat?vmid=" + UID);
			try {
				InputStream is = url.openStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String data = br.readLine();
				if (data.equals("{\"code\":-400,\"message\":\"请求错误\",\"ttl\":1}")) {
					return "UP不存在";
				}
				data1 = data.replace("{\"code\":0,\"message\":\"0\",\"ttl\":1,\"data\":{\"mid\":", "");
				data1 = data1.replace(",\"following\":", "=");
				data1 = data1.replace(",\"whisper\":", "=");
				data1 = data1.replace(",\"black\":", "=");
				data1 = data1.replace(",\"follower\":", "=");
				data1 = data1.replace("}}", "");
				Final = data1.split("=");
				br.close();
				isr.close();
				is.close();
			} catch (IOException e) {
				{
					return "网络错误";
				}
			}
		} catch (MalformedURLException e) {
			{
				return "访问受限";
			}
		}
		return Final[4];
	}

	public static String name(String UID) {
		String[] namedata = new String[2];
		if (UID.length() == 0) {
			return "未输入UID";
		}
		try {
			URL url = new URL("https://space.bilibili.com/" + UID);
			try {
				InputStream is = url.openStream();
				InputStreamReader isr = new InputStreamReader(is, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				br.readLine();
				String data = br.readLine();
				namedata = data.split("的个人空间");
				namedata = namedata[0].split("<title>");
				br.close();
				isr.close();
				is.close();
				System.out.println(namedata[1]);// 昵称调试输出
			} catch (IOException e) {
				{
					return "（无法访问）";
				}
			}
		} catch (MalformedURLException e) {
			{
				return "（无法访问）";
			}
		}
		return namedata[1];
	}
}