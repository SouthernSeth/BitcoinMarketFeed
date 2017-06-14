package com.jordan.blockchain;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.json.JSONObject;

public class BlockchainInfo {

	public static final String APPLICATION = "Bitcoin Market Updates";

	public JFrame window;

	public JPanel panel;

	public JMenuBar menuBar;
	public JMenu file;
	public JMenu convert;
	public JMenuItem exit;
	public JMenuItem refresh;
	public JMenuItem BTCtoUSD;
	public JMenuItem USDtoBTC;

	public JLabel costOfCoin;
	public JLabel lastUpdated;
	public JLabel changeStatus;
	public JLabel logo;

	public JCheckBox alwaysOnTop;

	public double cost;
	public double lastCost;
	
	public int refreshRate;
	
	public Properties props;

	public CloseableHttpClient client;

	public BlockchainInfo() {
		loadProperties();
		
		HttpClientBuilder builder = HttpClients.custom();

		HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT, "http");
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		builder.setRoutePlanner(routePlanner);

		client = builder.build();

		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(400,275));

		BufferedImage newImg = null;

		try {
			BufferedImage io = ImageIO.read(getClass().getResourceAsStream("/logo.png"));
			newImg = resize(io, 300, 58);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		logo = new JLabel();
		logo.setIcon(new ImageIcon(newImg));
		logo.setHorizontalAlignment(SwingConstants.CENTER);

		Font f = new Font("Arial", Font.BOLD, 32);
		costOfCoin = new JLabel(getSymbol() + "-.--");
		costOfCoin.setHorizontalAlignment(SwingConstants.CENTER);
		costOfCoin.setFont(f);

		//Character.toString((char) 9650) == UP
		//Character.toString((char) 9660) == DOWN
		Font f2 = new Font("Arial", Font.BOLD, 16);
		changeStatus = new JLabel(Character.toString((char) 9632) + " $-.--");
		changeStatus.setForeground(Color.GRAY);
		changeStatus.setHorizontalAlignment(SwingConstants.CENTER);
		changeStatus.setFont(f2);

		Font f3 = new Font("Arial", Font.BOLD, 14);
		lastUpdated = new JLabel("Last updated @ not been updated yet");
		lastUpdated.setHorizontalAlignment(SwingConstants.CENTER);
		lastUpdated.setFont(f3);

		alwaysOnTop = new JCheckBox("Always on top");
		alwaysOnTop.setBackground(Color.WHITE);
		alwaysOnTop.setHorizontalAlignment(SwingConstants.CENTER);

		menuBar = new JMenuBar();
		file = new JMenu("File");
		convert = new JMenu("Converter");
		exit = new JMenuItem("Exit");
		refresh = new JMenuItem("Refresh");
		USDtoBTC = new JMenuItem("USD to BTC");
		BTCtoUSD = new JMenuItem("BTC to USD");

		convert.add(USDtoBTC);
		convert.add(BTCtoUSD);

		file.add(refresh);
		file.addSeparator();
		file.add(exit);

		menuBar.add(file);
		menuBar.add(convert);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 25, 0);
		panel.add(logo, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 0, 0);
		panel.add(costOfCoin, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 25, 0);
		panel.add(changeStatus, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(0, 0, 10, 0);
		panel.add(lastUpdated, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0.5;
		panel.add(alwaysOnTop, gbc);

		window = new JFrame(APPLICATION);
		window.setJMenuBar(menuBar);

		try {
			window.setIconImage(ImageIO.read(getClass().getResourceAsStream("/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setResizable(true);
		window.setVisible(true);

		alwaysOnTop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (alwaysOnTop.isSelected()) {
					window.setAlwaysOnTop(true);
				} else {
					window.setAlwaysOnTop(false);
				}
			}
		});

		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		USDtoBTC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(window, "Please enter the amount in USD that you want to convert to BTC", "Convert USD to BTC", JOptionPane.QUESTION_MESSAGE);
				double in = 0.0;

				try {
					in = Double.parseDouble(input);
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(window, "Your input was not a number!", "Convert USD to BTC", JOptionPane.ERROR_MESSAGE);
					return;
				}

				DecimalFormat format = new DecimalFormat("###,###,###,###.00");

				String output = convertUSDtoBTC(in);
				JOptionPane.showMessageDialog(window, "$" + format.format(in) + " USD converts to " + output + " BTC @ $" + format.format(cost) + " per Bitcoin", "Convert USD to BTC", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		BTCtoUSD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(window, "Please enter the amount in BTC that you want to convert to USD", "Convert BTC to USD", JOptionPane.QUESTION_MESSAGE);
				double in = 0.0;

				try {
					in = Double.parseDouble(input);
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(window, "Your input was not a number!", "Convert BTC to USD", JOptionPane.ERROR_MESSAGE);
					return;
				}

				DecimalFormat format = new DecimalFormat("###,###,###,###.00");

				String output = convertBTCtoUSD(in);
				JOptionPane.showMessageDialog(window, in + " BTC converts to $" + output + " USD @ $" + format.format(cost) + " per Bitcoin", "Convert BTC to USD", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		startUpdateTimer();
	}
	
	public void loadProperties() {
		File file = new File(System.getProperty("user.home") + "/.bitcoinMarket");
		props = new Properties();
		
		props.setProperty("lastValue", "0.00");
		props.setProperty("refreshRate", "5");
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				props.store(new FileOutputStream(file), null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				props.load(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		lastCost = Double.parseDouble(props.getProperty("lastValue").replaceAll(",", ""));
		refreshRate = Integer.parseInt(props.getProperty("refreshRate"));
	}
	
	public void saveProperties() {
		File file = new File(System.getProperty("user.home") + "/.bitcoinMarket");
		try {
			props.store(new FileOutputStream(file), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage resize(BufferedImage img, int w, int h) {
		BufferedImage dimg = new BufferedImage(w, h, img.getType());  
		Graphics2D g = dimg.createGraphics();  
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		g.drawImage(img, 0, 0, w, h, 0, 0, img.getWidth(), img.getHeight(), null);  
		g.dispose();  
		return dimg;  
	}

	public double get15mPrice() {
		try {
			HttpGet request = new HttpGet("https://blockchain.info/ticker");
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			JSONObject obj = new JSONObject(s);
			JSONObject obj2 = obj.getJSONObject("USD");

			double _15m = obj2.getDouble("15m");

			return _15m;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	public double getLatestPrice() {
		try {
			HttpGet request = new HttpGet("https://blockchain.info/ticker");
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			JSONObject obj = new JSONObject(s);
			JSONObject obj2 = obj.getJSONObject("USD");

			double last = obj2.getDouble("last");

			return last;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	public double getBuyPrice() {
		try {
			HttpGet request = new HttpGet("https://blockchain.info/ticker");
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			JSONObject obj = new JSONObject(s);
			JSONObject obj2 = obj.getJSONObject("USD");

			double buy = obj2.getDouble("buy");

			return buy;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	public double getSellPrice() {
		try {
			HttpGet request = new HttpGet("https://blockchain.info/ticker");
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			JSONObject obj = new JSONObject(s);
			JSONObject obj2 = obj.getJSONObject("USD");

			double sell = obj2.getDouble("sell");

			return sell;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0.00;
	}

	public String getSymbol() {
		try {
			HttpGet request = new HttpGet("https://blockchain.info/ticker");
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			JSONObject obj = new JSONObject(s);
			JSONObject obj2 = obj.getJSONObject("USD");

			String symbol = obj2.getString("symbol");

			return symbol;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String convertUSDtoBTC(double value) {
		try {
			cost = getLatestPrice();

			HttpGet request = new HttpGet("https://blockchain.info/tobtc?currency=USD&value=" + value);
			HttpResponse response = client.execute(request);

			InputStream is = response.getEntity().getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str = null;

			StringBuilder jsonBuilder = new StringBuilder();

			while ((str = br.readLine()) != null) {
				jsonBuilder.append(str + "\n");
			}

			br.close();

			String s = jsonBuilder.toString().trim();

			return s;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	public String convertBTCtoUSD(double value) {
		cost = getLatestPrice();
		double toUSD = value * cost;

		DecimalFormat format = new DecimalFormat("###,###,###,###.00");

		return format.format(toUSD);
	}

	public void startUpdateTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				refresh();
			}
		}, 0L, refreshRate * 60 * 1000);
	}

	public void refresh() {
		cost = getLatestPrice();
		double change = lastCost - cost;
		
		if (change == -cost) {
			change = 0.0;
		}

		DecimalFormat format = new DecimalFormat("###,###,###,##0.00");

		if (change < 0) {
			changeStatus.setForeground(Color.RED);
			changeStatus.setText(Character.toString((char) 9660) + " $" + format.format(change));
		} else if (change > 0) {
			changeStatus.setForeground(Color.GREEN);
			changeStatus.setText(Character.toString((char) 9650) + " $" + format.format(change));
		} else if (change == 0) {
			changeStatus.setForeground(Color.GRAY);
			changeStatus.setText(Character.toString((char) 9632) + " $-.--");
		}

		String n1 = format.format(cost);

		costOfCoin.setText(getSymbol() +  n1);
		lastUpdated.setText("Last updated @ " + new Date());
		
		props.setProperty("lastValue", n1);
		saveProperties();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new BlockchainInfo();
	}

}
