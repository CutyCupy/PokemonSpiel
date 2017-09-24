package de.alexanderciupka.pokemon.gui.overlay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.alexanderciupka.pokemon.gui.BackgroundLabel;
import de.alexanderciupka.pokemon.map.Route;

public class RouteOverlay extends Overlay {

	private Route route;

	private static final Font ROUTE_FONT = new Font(Font.MONOSPACED, Font.BOLD, 35);

	public RouteOverlay(Route route, BackgroundLabel parent, Dimension size) {
		super(parent, size);
		this.route = route;
	}


	@Override
	public void createOverlay() {
		this.overlay = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) overlay.getGraphics();
		g.setFont(ROUTE_FONT);
		Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(this.route.getName(), g);

		final int xOffset = 15, yOffset = 5;

		int rectX = (int) (this.size.width / 2 - stringBounds.getWidth() / 2 - xOffset);
		int rectY = 15;
		int rectHeight = (int) (stringBounds.getHeight() + 2 * yOffset);
		int rectWidth = (int) (stringBounds.getWidth() + 2 * xOffset);
		g.setColor(Color.WHITE);
		g.fillRect(rectX, rectY, rectWidth, rectHeight);

		g.setColor(Color.BLACK);

		 int x = (int) (rectX + (rectWidth - stringBounds.getWidth()) / 2);
		 int y = rectY + ((rectHeight - g.getFontMetrics().getHeight()) / 2) + g.getFontMetrics().getAscent();

		g.drawString(this.route.getName(), x, y);

		g.setStroke(new BasicStroke(5));
		g.drawRect(rectX, rectY, rectWidth, rectHeight);

		created = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finished = true;
//				parent.repaint();
			}
		}).start();
//		parent.repaint();
	}

	public Route getRoute() {
		return this.route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}
}
