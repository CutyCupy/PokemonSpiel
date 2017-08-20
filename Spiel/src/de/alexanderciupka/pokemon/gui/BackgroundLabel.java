package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;

public class BackgroundLabel extends JLabel {

	
	private ArrayList<Overlay> overlays;
	private GameController gController;
	
	private boolean overlayAccess = true;

	public BackgroundLabel(int x, int y) {
		super();
		gController = GameController.getInstance();
		this.overlays = new ArrayList<Overlay>();
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		double x = (gController.getMainCharacter().getExactX() - 4);
		double y = (gController.getMainCharacter().getExactY() - 4);
		double w = 9;
		double h = 9;

		double xOffset = 0;
		double yOffset = 0;

		if(x < 0) {
			xOffset = -x;
			x = 0;
		}
		if(y < 0) {
			yOffset = -y;
			y = 0;
		}

		if(x + w > gController.getCurrentBackground().getCurrentRoute().getWidth()) {
			w = (gController.getCurrentBackground().getCurrentRoute().getWidth() - x);
		}
		if(y + h > gController.getCurrentBackground().getCurrentRoute().getHeight()) {
			h = (gController.getCurrentBackground().getCurrentRoute().getHeight() - y);
		}
		
		g.drawImage(gController.getCurrentBackground().getCurrentRoute().getMap().getSubimage((int) (x * GameFrame.GRID_SIZE), (int) (y * GameFrame.GRID_SIZE), (int) (w * GameFrame.GRID_SIZE),
				(int) (h * GameFrame.GRID_SIZE)),
				(int) (xOffset * GameFrame.GRID_SIZE), (int) (yOffset * GameFrame.GRID_SIZE), null);
		g.drawImage(gController.getMainCharacter().getCharacterImage(), (int) ((gController.getMainCharacter().getExactX() - x + xOffset) * GameFrame.GRID_SIZE),
				(int) ((gController.getMainCharacter().getExactY() - y + yOffset) * GameFrame.GRID_SIZE), null);
		
		for(int i = 0; i < overlays.size(); i++) {
			if(overlays.get(i).isFinshed()) {
				overlays.remove(i);
				i--;
			}
		}
		
		waitAccess();
		for(Overlay o : overlays) {
			if(o.created) {
				g.drawImage(o.getOverlay(), 0, 0, null);
			}
		}
		this.overlayAccess = true;
	}
	
	private void waitAccess() {
		while(!this.overlayAccess) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.overlayAccess = false;
	}
	
	public void addOverlay(Overlay ov) {
		this.overlays.add(ov);
	}

	public void startEncounter() {
		Form f = Form.values()[new Random().nextInt(Form.values().length)];
		CollapseOverlay c = new CollapseOverlay(this, this.getSize(), f);
		final int DEFAULT_STEP_SIZE = 25;
		switch(f) {
		case CIRCLE:
			c.startAnimation((int) (DEFAULT_STEP_SIZE * ((Math.sqrt(Math.pow(this.getWidth(), 2) + Math.pow(this.getHeight(), 2))) / this.getWidth())), 0);
			break;
		case RECTANGLE:
			c.startAnimation(DEFAULT_STEP_SIZE, 0);
			break;
		}
		this.addOverlay(c);
		wait(c);
	}
	
	public void wait(Overlay o) {
		while(!o.isFinshed()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startFight(String logo) {
		if(logo != null && gController.getRouteAnalyzer().getLogoByName(logo) != null) {
			LogoOverlay l = new LogoOverlay(logo, this, this.getSize());
			l.createOverlay();
			this.addOverlay(l);
			wait(l);
		}
	}
	
	public void changeRoute(Route newRoute) {
		if(newRoute != null) {
			waitAccess();
			for(int i = 0; i < this.overlays.size(); i++) {
				if(this.overlays.get(i) instanceof RouteOverlay ||
						this.overlays.get(i) instanceof DarkOverlay) {
					this.overlays.remove(i);
					i--;
				}
			}
			if(newRoute.isDark()) {
				DarkOverlay d = new DarkOverlay(this, this.getSize(), GameFrame.GRID_SIZE * 3);
				d.createOverlay();
				this.addOverlay(d);
			}
			RouteOverlay r = new RouteOverlay(newRoute, this, this.getSize());
			r.createOverlay();
			this.addOverlay(r);
			this.overlayAccess = true;
		}
	}
}
