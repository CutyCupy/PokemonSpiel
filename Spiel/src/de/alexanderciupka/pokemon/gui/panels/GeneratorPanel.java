package de.alexanderciupka.pokemon.gui.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;

import de.alexanderciupka.hoverbutton.Main;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.entities.GeneratorEntity;
import de.alexanderciupka.pokemon.menu.SoundController;

public class GeneratorPanel extends JPanel {

	private static final long serialVersionUID = 7749339076796915860L;
	private JLabel good;
	private JLabel great;
	private JLabel bar;
	private JLabel zeiger;

	private JProgressBar progress;

	private boolean stop;

	private boolean interrupt;

	private boolean progressAccess = true;

	private int punishFrames;

	private static BufferedImage zeigerImage;

	private static final String LOW = "generator_low";
	private static final String MIDDLE = "generator_middle";
	private static final String HIGH = "generator_high";

	private String currentSound = "";

	public GeneratorPanel() {

		if(zeigerImage == null) {
			try {
				zeigerImage = ImageIO.read(new File(Main.class.getResource("/icons/Zeiger.png").getFile()));
			} catch (IOException e1) {
				e1.printStackTrace();
			};
		}

		setBounds(0, 0, 630, 630);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");

		getActionMap().put("space", new AbstractAction() {
			private static final long serialVersionUID = -4074916340116617809L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (bar.isVisible()) {
					stop = true;
					int zeigerX = zeiger.getX() + zeiger.getWidth() / 2;
					
					waitProgressAccess();
					if (hits(zeigerX, great)) {
						progress.setValue(Math.min(progress.getValue() + 250, progress.getMaximum()));
						SoundController.getInstance().playSound(SoundController.GREAT_SUCCESS);
					} else if (hits(zeigerX, good)) {
						SoundController.getInstance().playSound(SoundController.SUCCESS);
					} else {
						punishFrames = 100;
						progress.setValue(Math.max(progress.getValue() - 500, 0));
						SoundController.getInstance().playSound(SoundController.EXPLOSION);
					}
					progressAccess = true;
				}
			}
		});

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");

		getActionMap().put("escape", new AbstractAction() {
			private static final long serialVersionUID = 3138663353013351616L;

			@Override
			public void actionPerformed(ActionEvent e) {
				interrupt = true;
			}
		});

		setLayout(null);

		progress = new JProgressBar();
		progress.setBounds(100, 500, 430, 15);

		progress.setMinimum(0);
		progress.setMaximum(10000);

		progress.setVisible(true);


		zeiger = new JLabel();
		zeiger.setIcon(new ImageIcon(zeigerImage));
		zeiger.setVisible(false);
		zeiger.setSize(zeigerImage.getWidth(), zeigerImage.getHeight());

		great = new JLabel();
		great.setSize(10, 10);
		great.setVisible(false);
		great.setBackground(new Color(255, 102, 204));
		great.setOpaque(true);

		good = new JLabel();
		good.setVisible(false);
		good.setSize(40, 10);
		good.setBackground(Color.BLUE);
		good.setOpaque(true);

		bar = new JLabel();
		bar.setVisible(false);
		bar.setSize(175, 10);
		bar.setBackground(Color.BLACK);
		bar.setOpaque(true);

		add(zeiger);
		add(great);
		add(good);
		add(bar);
		add(progress);

		setComponentZOrder(zeiger, 4);
		setComponentZOrder(great, 0);
		setComponentZOrder(good, 0);
		setComponentZOrder(progress, 3);
		setComponentZOrder(bar, 2);

	}

	public void waitProgressAccess() {
		while(!progressAccess) {
			Thread.yield();
		}
		progressAccess = false;
	}

	public boolean hits(int zeigerX, JLabel zone) {
		return zeigerX >= zone.getX() && zeigerX <= zone.getX() + zone.getWidth();
	}

	public void start(GeneratorEntity generator) {

		interrupt = false;

		Random rng = new Random();

		progress.setValue((int) (generator.getPercentage() * 100));

		progress.setVisible(true);
		zeiger.setVisible(false);
		great.setVisible(false);
		good.setVisible(false);
		bar.setVisible(false);

		for(int i = 0; i < getComponentCount(); i++) {
			if(!(getComponent(i).equals(zeiger) || getComponent(i).equals(great) || getComponent(i).equals(good) ||
					getComponent(i).equals(bar) || getComponent(i).equals(progress))) {
				setComponentZOrder(getComponent(i), 5);
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(!generator.isDone() && !interrupt) {
					if(progress.getValue() < progress.getMaximum() / 3) {
						if(!currentSound.equals(LOW)) {
							currentSound = LOW;
							SoundController.getInstance().updateGenerator(currentSound);
						}
					} else if(progress.getValue() < (progress.getMaximum() * 2) / 3) {
						if(!currentSound.equals(MIDDLE)) {
							currentSound = MIDDLE;
							SoundController.getInstance().updateGenerator(currentSound);
						}
					} else {
						if(!currentSound.equals(HIGH)) {
							currentSound = HIGH;
							SoundController.getInstance().updateGenerator(currentSound);
						}
					}
					if(punishFrames > 0) {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						punishFrames = 0;
					} else {
						waitProgressAccess();
						progress.setValue(progress.getValue() + 2);
						progressAccess = true;
					}
					generator.setPercentage(progress.getValue() / 100.0);
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}
				SoundController.getInstance().updateGenerator(null);
			}
		}).start();

		while (!generator.isDone() && !interrupt) {
			if(rng.nextFloat() >= 0.01) {
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			} else {
				SoundController.getInstance().playSound(SoundController.ALERT);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			int x = getWidth() / 2 - bar.getWidth() / 2;
			int y = getHeight() / 2 - bar.getHeight() / 2;
			bar.setLocation(x, y);
			int min = bar.getWidth() / 5;
			great.setLocation(x + rng.nextInt(bar.getWidth() - min) + min, y);
			good.setLocation(great.getX() + great.getWidth(), y);

			int zeigerX = x + zeiger.getWidth() / 2;
			int zeigerY = y + bar.getHeight();
			zeiger.setLocation(zeigerX, zeigerY);

			bar.setVisible(true);
			good.setVisible(true);
			great.setVisible(true);
			zeiger.setVisible(true);
			progress.setVisible(true);

			stop = false;

			for (int j = 0; zeigerX < bar.getWidth() + x - (zeiger.getWidth() / 2); j++) {
				zeigerX = (int) Math.round(x + j / 4.0);
				zeigerX = Math.min(x + bar.getWidth() - (zeiger.getWidth() / 2),
						Math.max(zeigerX, x + zeiger.getWidth() / 2));
				zeiger.setLocation(zeigerX, zeigerY);
				if(zeigerX > good.getX() + good.getWidth() || generator.isDone()) {
					break;
				}
				if (stop) {
					break;
				}
				try {
					Thread.sleep(0, 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if(!stop && !generator.isDone()) {
				waitProgressAccess();
				progress.setValue(Math.max(progress.getValue() - 500, 0));
				progressAccess = true;
			} else if(generator.isDone()) {
				break;
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			bar.setVisible(false);
			good.setVisible(false);
			great.setVisible(false);
			zeiger.setVisible(false);
		}

		bar.setVisible(false);
		good.setVisible(false);
		great.setVisible(false);
		zeiger.setVisible(false);
		generator.setPercentage(progress.getValue() / 100.0);
		if(generator.isDone()) {
			SoundController.getInstance().playSound("generator_finished");
			GameController.getInstance().getRouteAnalyzer().updateHatches(generator);
		}
		GameController.getInstance().getGameFrame().setCurrentPanel(null);
	}


}
