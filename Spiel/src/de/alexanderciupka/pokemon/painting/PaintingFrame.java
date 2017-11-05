package de.alexanderciupka.pokemon.painting;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import de.alexanderciupka.pokemon.menu.MenuController;

@SuppressWarnings("serial")
public class PaintingFrame extends JFrame {

	private Graphics g;

	private PaintingController pController;
	
	public PaintingFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new PaintingListener());
		setLayout(null);
		setResizable(false);
		setBounds(200, 200, 800, 800);
		addMouseListener();
		pController = PaintingController.getInstance();
		
	}

	public void start(String name) {
		g = pController.getCurrentPainting().getImage().getGraphics();
		setVisible(true);
		setTitle(name);
	}

	public void loadImage(BufferedImage img, String name) {
		g = pController.getCurrentPainting().getImage().getGraphics();
		setVisible(true);
		setTitle(name);
		setBounds(MenuController.getToCenter(img.getWidth(null), img.getHeight(null)));
		g.drawImage(img, 0, 0, null);
		repaint();
	}

	public void addMouseListener() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!pController.getFloodMode()) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						g.fillOval(e.getX() - (pController.getCircleWidth() / 2),
								e.getY() - (pController.getCircleWidth() / 2), pController.getCircleWidth(),
								pController.getCircleWidth());
					} else if (SwingUtilities.isRightMouseButton(e)) {
						g.setColor(new Color(pController.getCurrentPainting().getImage().getRGB(e.getX(), e.getY())));
						pController.updateColorLabel(g.getColor());
					}
				} else {
					pController.floodImage(e.getX(), e.getY(), g.getColor());
				}
				repaint();
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				updateCursor();	
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (!pController.getFloodMode()) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						g.fillOval(e.getX() - (pController.getCircleWidth() / 2),
								e.getY() - (pController.getCircleWidth() / 2), pController.getCircleWidth(),
								pController.getCircleWidth());
					}
					repaint();
				}
			}
		});
	}

	public void drawImg(BufferedImage img) {
		g.drawImage(img, 0, 0, null);
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(pController.getCurrentPainting().getImage(), 0, 0, null);
	}

	public Color getColor() {
		return g.getColor();
	}

	public void setColor(Color color) {
		g.setColor(color);
	}

	public void setColor(int red, int green, int blue) {
		g.setColor(new Color(red, green, blue));
	}

	public void setColor(int rgb) {
		g.setColor(new Color(rgb));
	}
	
	public void updateCursor() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension dim = kit.getBestCursorSize(pController.getCircleWidth(), pController.getCircleWidth());
		BufferedImage buffered = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = buffered.createGraphics();
		g.setColor(Color.BLUE);
		int location = ((dim.width - 1) / 2) - (pController.getCircleWidth() / 2);
		g.drawOval(location, location, pController.getCircleWidth(), pController.getCircleWidth());
		g.setColor(Color.RED);
		int centerX = (dim.width - 1) / 2;
		int centerY = (dim.height - 1) / 2;
		g.drawLine(centerX, location, centerX, location + pController.getCircleWidth());
		g.drawLine(location, centerY, location + pController.getCircleWidth(), centerY);
		g.dispose();
		Cursor cursor = kit.createCustomCursor(buffered, new Point(centerX, centerY), "myCursor");
		setCursor(cursor);
	}
}
