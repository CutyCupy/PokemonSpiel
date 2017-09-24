package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import de.alexanderciupka.pokemon.characters.NPC;
import de.alexanderciupka.pokemon.gui.overlay.CollapseOverlay;
import de.alexanderciupka.pokemon.gui.overlay.DarkOverlay;
import de.alexanderciupka.pokemon.gui.overlay.FogOverlay;
import de.alexanderciupka.pokemon.gui.overlay.Form;
import de.alexanderciupka.pokemon.gui.overlay.LogoOverlay;
import de.alexanderciupka.pokemon.gui.overlay.Overlay;
import de.alexanderciupka.pokemon.gui.overlay.RainOverlay;
import de.alexanderciupka.pokemon.gui.overlay.Raindrop;
import de.alexanderciupka.pokemon.gui.overlay.RouteOverlay;
import de.alexanderciupka.pokemon.gui.overlay.SnowOverlay;
import de.alexanderciupka.pokemon.gui.overlay.Snowflake;
import de.alexanderciupka.pokemon.gui.overlay.SpottedOverlay;
import de.alexanderciupka.pokemon.map.Camera;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.map.Route;

public class BackgroundLabel extends JLabel {

	private ArrayList<Overlay> overlays;
	private GameController gController;

	private boolean overlayAccess = true;

	private double oldX;
	private double oldY;

	private int waitFrames;

	public BackgroundLabel(int x, int y) {
		super();
		gController = GameController.getInstance();
		this.overlays = new ArrayList<Overlay>();
	}

	@Override
	public void paint(Graphics g) {
		long currentTime = System.currentTimeMillis();
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
//		System.out.print("clear: " + (System.currentTimeMillis() - currentTime) + " - ");
		currentTime = System.currentTimeMillis();

		Camera c = gController.getCurrentBackground().getCamera();

		double w = 9;
		double h = 9;

		double x = (c.getX() - (w / 2.0));
		double y = (c.getY() - (h / 2.0));

		double xOffset = 0;
		double yOffset = 0;

		if (x < 0) {
			xOffset = -x;
			x = 0;
		}
		if (y < 0) {
			yOffset = -y;
			y = 0;
		}

		if (x + w > gController.getCurrentBackground().getCurrentRoute().getWidth()) {
			w = (gController.getCurrentBackground().getCurrentRoute().getWidth() - x);
		}
		if (y + h > gController.getCurrentBackground().getCurrentRoute().getHeight()) {
			h = (gController.getCurrentBackground().getCurrentRoute().getHeight() - y);
		}

		g.drawImage(
				gController.getCurrentBackground().getCurrentRoute().getMap().getSubimage(
						(int) Math.round(x * GameFrame.GRID_SIZE), (int) Math.round(y * GameFrame.GRID_SIZE),
						(int) Math.round(w * GameFrame.GRID_SIZE), (int) Math.round(h * GameFrame.GRID_SIZE)),
				(int) Math.round(xOffset * GameFrame.GRID_SIZE), (int) Math.round(yOffset * GameFrame.GRID_SIZE), null);
		for (NPC npc : gController.getCurrentBackground().getCurrentRoute().getCharacters()) {
			int npcX = (int) Math.round((npc.getExactX() - x + xOffset) * GameFrame.GRID_SIZE);
			int npcY = (int) Math.round((npc.getExactY() - y + yOffset) * GameFrame.GRID_SIZE);
			if (npcX > -GameFrame.GRID_SIZE && npcX < GameFrame.GRID_SIZE * w
					|| npcX > -GameFrame.GRID_SIZE && npcY < GameFrame.GRID_SIZE * h) {
				g.drawImage(npc.getCharacterImage(), npcX, npcY, null);
			}
		}
//		g.drawImage(gController.getMainCharacter().getCharacterImage(),
//				(int) ((gController.getMainCharacter().getExactX() - x + xOffset) * GameFrame.GRID_SIZE),
//				(int) ((gController.getMainCharacter().getExactY() - y + yOffset) * GameFrame.GRID_SIZE), null);

//		System.out.print("drawing: " + (System.currentTimeMillis() - currentTime) + " - ");
		currentTime = System.currentTimeMillis();

		for (int i = 0; i < overlays.size(); i++) {
			if (overlays.get(i).isFinshed()) {
				overlays.get(i).onRemove();
				overlays.remove(i);
				i--;
			}
		}

//		System.out.print("overlaycheck: " + (System.currentTimeMillis() - currentTime));
		currentTime = System.currentTimeMillis();

		waitAccess();
		for (Overlay o : overlays) {
			if (o.created) {
				g.drawImage(o.getOverlay(), 0, 0, null);
			}
		}
		this.overlayAccess = true;

//		System.out.print("overlays: " + (System.currentTimeMillis() - currentTime) + " - ");
		currentTime = System.currentTimeMillis();

//		if (oldX != x + xOffset || oldY != y + yOffset) {
		if(waitFrames <= 0) {
			RainOverlay r = (RainOverlay) getOverlay(RainOverlay.class);
			SnowOverlay s = (SnowOverlay) getOverlay(SnowOverlay.class);
			double xo = (oldX - (x - xOffset)) * GameFrame.GRID_SIZE;
			double yo = (oldY - (y - yOffset)) * GameFrame.GRID_SIZE;
			if (r != null) {
				for (Raindrop rd : r.getRaindrops()) {
					rd.offset(xo, yo);
				}
			}
			if (s != null) {
				for (Snowflake sf : s.getSnowflakes()) {
					sf.offset(xo, yo);
				}
			}
		} else {
			waitFrames--;
		}

//		System.out.print("offset: " + (System.currentTimeMillis() - currentTime));

		oldX = x - xOffset;
		oldY = y - yOffset;

//		System.out.println();
	}

	private void waitAccess() {
		while (!this.overlayAccess) {
			System.out.println("waiting");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.overlayAccess = false;
	}

	public void addOverlay(Overlay ov) {
		waitAccess();
		this.overlays.add(ov);
		this.overlayAccess = true;
	}

	public void spotted(de.alexanderciupka.pokemon.characters.Character spotter) {
		SpottedOverlay s = new SpottedOverlay(this, spotter);
		this.addOverlay(s);
		s.createOverlay();
		wait(s);
	}

	public void startEncounter() {
		Form f = Form.values()[new Random().nextInt(Form.values().length)];
		CollapseOverlay c = new CollapseOverlay(this, this.getSize(), f);
		final int DEFAULT_STEP_SIZE = 25;
		switch (f) {
		case CIRCLE:
			c.startAnimation((int) (DEFAULT_STEP_SIZE
					* ((Math.sqrt(Math.pow(this.getWidth(), 2) + Math.pow(this.getHeight(), 2))) / this.getWidth())),
					0);
			break;
		case RECTANGLE:
			c.startAnimation(DEFAULT_STEP_SIZE, 0);
			break;
		}
		this.addOverlay(c);
		wait(c);
	}

	public void wait(Overlay o) {
		while (!o.isFinshed()) {
			Thread.yield();
		}
	}

	public void startFight(String logo) {
		if (logo != null && gController.getRouteAnalyzer().getLogoByName(logo) != null) {
			LogoOverlay l = new LogoOverlay(logo, this, this.getSize());
			l.createOverlay();
			this.addOverlay(l);
			wait(l);
		}
	}

	public void changeRoute(Route newRoute) {
		if (newRoute != null) {
			waitAccess();
			for (int i = 0; i < this.overlays.size(); i++) {
				if (this.overlays.get(i) instanceof RouteOverlay || this.overlays.get(i) instanceof DarkOverlay
						|| this.overlays.get(i) instanceof RainOverlay || this.overlays.get(i) instanceof SnowOverlay) {
					overlays.get(i).onRemove();
					this.overlays.remove(i);
					i--;
				}
			}
			this.overlayAccess = true;
			if (newRoute.isDark()) {
				DarkOverlay d = new DarkOverlay(this, this.getSize(), GameFrame.GRID_SIZE * 3);
				d.createOverlay();
				this.addOverlay(d);
			}
			if (newRoute.getRain() != null) {
				RainOverlay rain = new RainOverlay(this, this.getSize(), newRoute.getRain());
				rain.startAnimation();
				this.addOverlay(rain);
			}
			if (newRoute.getSnow() != null) {
				SnowOverlay snow = new SnowOverlay(this, this.getSize(), newRoute.getSnow());
				snow.startAnimation();
				this.addOverlay(snow);
			}
			if(newRoute.getFog() != null) {
				FogOverlay fog = new FogOverlay(this, this.getSize(), newRoute.getFog());
				fog.createOverlay();
				this.addOverlay(fog);
			}
			RouteOverlay r = new RouteOverlay(newRoute, this, this.getSize());
			r.createOverlay();
			this.addOverlay(r);
			waitFrames = 5;
//			oldX = gController.getCurrentBackground().getCamera().getX();
//			oldY = gController.getCurrentBackground().getCamera().getY();
//			repaint();
		}
	}

	public Overlay getOverlay(Class<? extends Overlay> overlay) {
		waitAccess();
		for (int i = 0; i < this.overlays.size(); i++) {
			if (overlay.isInstance(this.overlays.get(i))) {
				overlayAccess = true;
				return overlay.cast(this.overlays.get(i));
			}
		}
		overlayAccess = true;
		return null;
	}
}
