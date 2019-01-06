package de.alexanderciupka.pokemonspiel.routecreation.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.google.gson.JsonObject;

import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemonspiel.routecreation.RouteCreatorController;

public class EntitiesFrame extends JFrame {

	private GridLayout layout;

	private JPanel mainPanel;
	private JScrollPane pane;

	private Point clone;

	private HashMap<Integer, Boolean> pressedKeys;
	
	private JLabel[][] entities;

	public EntitiesFrame() {
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				RouteCreatorController.getInstance().save();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
			}
		});
		setBounds(0, 360, 1280, 720);

		this.mainPanel = new JPanel();
		this.layout = new GridLayout(1, 0, 0, 0);
		this.mainPanel.setLayout(this.layout);

		

		setResizable(false);

		pane = new JScrollPane(this.mainPanel);
		this.mainPanel.setOpaque(false);
		pane.setOpaque(false);
		
		pane.getHorizontalScrollBar().setUnitIncrement(GameFrame.GRID_SIZE);
		pane.getVerticalScrollBar().setUnitIncrement(GameFrame.GRID_SIZE);
		
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		

		getContentPane().add(pane);
		getContentPane().setComponentZOrder(pane, 0);

		pressedKeys = new HashMap<>();

		updateSize(100, 100);
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				pressedKeys.put(e.getKeyCode(), false);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				pressedKeys.put(e.getKeyCode(), true);
			}
		});
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					if(!getTitle().equals(RouteCreatorController.getInstance().getData().getAsJsonObject("route").get("name").getAsString())) {
						setTitle(RouteCreatorController.getInstance().getData().getAsJsonObject("route").get("name").getAsString());
					}
					Thread.yield();
				}
			}
		}).start();
	}

	public void updateBackground(int x, int y) {
		BufferedImage img = RouteCreatorController.getInstance().getMap();
		try {
			if(this.entities != null) {
				this.entities[x][y].setIcon(new ImageIcon(img.getSubimage(x * GameFrame.GRID_SIZE, y * GameFrame.GRID_SIZE,
						GameFrame.GRID_SIZE, GameFrame.GRID_SIZE)));
			}
		} catch (Exception e) {
			if (RouteCreatorController.getInstance().getEntity(x, y).entrySet().size() > 0) {
				e.printStackTrace();
			}
			return;
		}
	}

	public void updateSize(int width, int height) {
		if(width == this.layout.getColumns() && height == this.layout.getRows()) {
			return;
		}
		this.entities = new JLabel[width][height];
		this.mainPanel.removeAll();
		this.layout.setColumns(width);
		this.layout.setRows(height);
		for (int y = 0; y < this.layout.getRows(); y++) {
			for (int x = 0; x < this.layout.getColumns(); x++) {
				JLabel label = new JLabel();
				label.setOpaque(false);

				label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

				label.setPreferredSize(new Dimension(GameFrame.GRID_SIZE, GameFrame.GRID_SIZE));
				label.setMaximumSize(new Dimension(GameFrame.GRID_SIZE, GameFrame.GRID_SIZE));
				label.setMinimumSize(new Dimension(GameFrame.GRID_SIZE, GameFrame.GRID_SIZE));

				label.setName(x + "." + y);

				label.setToolTipText(label.getName());

				label.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(MouseEvent e) {
						switch (e.getButton()) {
						case MouseEvent.BUTTON1:
							if (clone != null) {
								if (new Boolean(true).equals(pressedKeys.get(KeyEvent.VK_CONTROL))) {
									Point p = new Point(
											Integer.parseInt(
													label.getName().substring(0, label.getName().indexOf("."))),
											Integer.parseInt(
													label.getName().substring(label.getName().indexOf(".") + 1)));
									for (int x = Math.min(p.x, clone.x); x <= Math.max(p.x, clone.x); x++) {
										for (int y = Math.min(p.y, clone.y); y <= Math.max(p.y, clone.y); y++) {
											RouteCreatorController.getInstance().setEntity(x, y,
													RouteCreatorController.getInstance().getEntity(clone.x, clone.y));
										}
									}
									((JLabel) mainPanel.getComponentAt(clone.x * GameFrame.GRID_SIZE,
											clone.y * GameFrame.GRID_SIZE))
													.setBorder(BorderFactory.createLineBorder(Color.BLACK));
									clone = null;
								} else {
									RouteCreatorController.getInstance().setEntity(
											Integer.parseInt(
													label.getName().substring(0, label.getName().indexOf("."))),
											Integer.parseInt(
													label.getName().substring(label.getName().indexOf(".") + 1)),
											RouteCreatorController.getInstance().getEntity(clone.x, clone.y));
								}
							} else {
								new EntityEditingFrame(
										Integer.parseInt(label.getName().substring(0, label.getName().indexOf("."))),
										Integer.parseInt(label.getName().substring(label.getName().indexOf(".") + 1,
												label.getName().length())));
							}
							break;
						case MouseEvent.BUTTON2:
							if (clone != null) {
								((JLabel) mainPanel.getComponentAt(clone.x * GameFrame.GRID_SIZE,
										clone.y * GameFrame.GRID_SIZE))
												.setBorder(BorderFactory.createLineBorder(Color.BLACK));
								clone = null;
							} else {
								clone = new Point(
										Integer.parseInt(label.getName().substring(0, label.getName().indexOf("."))),
										Integer.parseInt(label.getName().substring(label.getName().indexOf(".") + 1)));
								((JLabel) mainPanel.getComponentAt(clone.x * GameFrame.GRID_SIZE,
										clone.y * GameFrame.GRID_SIZE))
												.setBorder(BorderFactory.createLineBorder(Color.RED));
							}
							break;
						case MouseEvent.BUTTON3:
							if (clone == null) {
								RouteCreatorController.getInstance().setEntity(
										Integer.parseInt(label.getName().substring(0, label.getName().indexOf("."))),
										Integer.parseInt(label.getName().substring(label.getName().indexOf(".") + 1)),
										new JsonObject());
							}
							break;
						}
					}
				});
				this.mainPanel.add(label);
				this.entities[x][y] = label;
			}
		}
		for (int y = 0; y < this.layout.getRows(); y++) {
			for (int x = 0; x < this.layout.getColumns(); x++) {
				updateBackground(x, y);
			}
		}
		this.mainPanel.setSize(Math.min(this.getWidth(), GameFrame.GRID_SIZE * this.layout.getColumns()), 
				Math.min(this.getHeight(), GameFrame.GRID_SIZE * this.layout.getRows()));
		this.pane.setSize(Math.min(this.getWidth(), GameFrame.GRID_SIZE * this.layout.getColumns() + 40), 
				Math.min(this.getHeight(), GameFrame.GRID_SIZE * this.layout.getRows() + 40));
		repaint();
	}
}
