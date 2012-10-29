package com.github.astah.tips;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.sun.javafx.application.PlatformImpl;

@SuppressWarnings("serial")
public class TipsPanel extends JPanel {
	private WebView browser;
	private JFXPanel jfxPanel;
	private JButton prevButton;
	private JButton nextButton;
	private JComboBox<String> tipsList;
	private WebEngine webEngine;
	
	private TipsLoader tipsLoader;
	private List<String[]> tipsLabelAndUrls;
	private int current = 0;

	public TipsPanel() {
		tipsLoader = new TipsLoader();
		initComponents();
		
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				tipsLabelAndUrls = tipsLoader.getTipsLabelAndUrls();
				if (!tipsLabelAndUrls.isEmpty()) {
					for (String[] tipsLabelAndUrl : tipsLabelAndUrls) {
						tipsList.addItem(tipsLabelAndUrl[0]);			
					}
					
					String url = tipsLabelAndUrls.get(0)[1];
					String[] titleAndContents = tipsLoader.getTitleAndContents(url);
					loadContents(url, titleAndContents[0], titleAndContents[1]);
				}
				return null;
			}
		}.execute();
	}
	
	public void loadContents(final String url, final String title, final String content) {
		if (current != tipsList.getSelectedIndex()) {
			tipsList.setSelectedIndex(current);
		}
		
		PlatformImpl.startup(new Runnable() {
			public void run() {
				String html = "<body style='margin: 12px; font-size: 13px;'>";
				html += "<h1 style='font-size: 1.4em; border-bottom: solid 2px rgb(41, 31, 29);'><a style='text-decoration: none;' href='" + url + "'>" + title + "</a></h1>";
				html += content;
				html += "<cite style='float: right; margin-top: 12px;'>引用元: <a href='" + url + "'>" + url +  "</a></cite><div style='clear: both;'></div>";
				html += "</body>";
				webEngine.loadContent(html);
			}
		});
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JFrame frame = new JFrame();

				frame.getContentPane().add(new TipsPanel());

//				frame.pack();
				frame.setSize(640, 480);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	private void initComponents() {
		jfxPanel = new JFXPanel();
		createScene();

		setLayout(new BorderLayout());
		
		JPanel headerPanel = new JPanel();
		tipsList = new JComboBox<String>();
		tipsList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				current = tipsList.getSelectedIndex();
				final String url = tipsLabelAndUrls.get(current)[1];
				
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						String[] titleAndContents = tipsLoader.getTitleAndContents(url);
						loadContents(url, titleAndContents[0], titleAndContents[1]);
						return null;
					}
				}.execute();
			}
		});
		headerPanel.add(tipsList);
		
		add(headerPanel, BorderLayout.NORTH);
		add(jfxPanel, BorderLayout.CENTER);

		prevButton = new JButton("前のTips");
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (current <= 0) {
					current = tipsLabelAndUrls.size() - 1;
				} else {
					current--;
				}
				
				final String url = tipsLabelAndUrls.get(current)[1];
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						String[] titleAndContents = tipsLoader.getTitleAndContents(url);
						loadContents(url, titleAndContents[0], titleAndContents[1]);
						return null;
					}
				}.execute();
			}
		});

		nextButton = new JButton("次のTips");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tipsLabelAndUrls.size() <= current) {
					current = 0;
				} else {
					current++;
				}
				
				final String url = tipsLabelAndUrls.get(current)[1];
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						String[] titleAndContents = tipsLoader.getTitleAndContents(url);
						loadContents(url, titleAndContents[0], titleAndContents[1]);
						return null;
					}
				}.execute();
			}
		});

		JPanel controls = new JPanel();
		controls.add(prevButton);
		controls.add(nextButton);
		add(controls, BorderLayout.SOUTH);
	}

	private void createScene() {
		PlatformImpl.startup(new Runnable() {
			public void run() {
				browser = new WebView();
				webEngine = browser.getEngine();
//				webEngine.load("http://www.google.co.jp");
//				webEngine.loadContent("<h1>HOGE</h1>");

				jfxPanel.setScene(new Scene(browser));
			}
		});
	}
}
