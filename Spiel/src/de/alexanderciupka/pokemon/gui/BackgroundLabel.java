package de.alexanderciupka.pokemon.gui;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

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
import de.alexanderciupka.pokemon.menu.SoundController;

public class BackgroundLabel extends JLabel {

	private ArrayList<Overlay> overlays;
	private GameController gController;

	private boolean overlayAccess = true;

	private double oldX;
	private double oldY;

	private int waitFrames;

	public BackgroundLabel(int x, int y) {
		super();
		this.gController = GameController.getInstance();
		this.overlays = new ArrayList<Overlay>();
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		Camera c = this.gController.getCurrentBackground().getCamera();

		double w = GameFrame.FRAME_SIZE / GameFrame.GRID_SIZE;
		double h = GameFrame.FRAME_SIZE / GameFrame.GRID_SIZE;

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

		if (x + w > c.getRoute().getWidth()) {
			w = (c.getRoute().getWidth() - x);
		}
		if (y + h > c.getRoute().getHeight()) {
			h = (c.getRoute().getHeight() - y);
		}

		// g.drawImage(
		// c.getRoute().getMap().getSubimage(
		// (int) Math.round(x * GameFrame.GRID_SIZE), (int) Math.round(y *
		// GameFrame.GRID_SIZE),
		// (int) Math.round(w * GameFrame.GRID_SIZE), (int) Math.round(h *
		// GameFrame.GRID_SIZE)),
		// (int) Math.round(xOffset * GameFrame.GRID_SIZE), (int) Math.round(yOffset *
		// GameFrame.GRID_SIZE), null);
		//
		g.drawImage(c.getRoute().generateMap((int) x, (int) y, (int) w + 1, (int) h + 1),
				(int) Math.round(xOffset * GameFrame.GRID_SIZE), (int) Math.round(yOffset * GameFrame.GRID_SIZE), null);

		for (int i = 0; i < this.overlays.size(); i++) {
			if (this.overlays.get(i).isFinshed()) {
				this.overlays.get(i).onRemove();
				this.overlays.remove(i);
				i--;
			}
		}

		this.waitAccess();
		for (Overlay o : this.overlays) {
			if (o.created) {
				g.drawImage(o.getOverlay(), 0, 0, null);
			}
		}
		this.overlayAccess = true;

		if (this.waitFrames <= 0) {
			RainOverlay r = (RainOverlay) this.getOverlay(RainOverlay.class);
			SnowOverlay s = (SnowOverlay) this.getOverlay(SnowOverlay.class);
			double xo = (this.oldX - (x - xOffset)) * GameFrame.GRID_SIZE;
			double yo = (this.oldY - (y - yOffset)) * GameFrame.GRID_SIZE;
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
			this.waitFrames--;
		}

		this.oldX = x - xOffset;
		this.oldY = y - yOffset;
	}

	private void waitAccess() {
		while (!this.overlayAccess) {
			Thread.yield();
		}
		this.overlayAccess = false;
	}

	public void addOverlay(Overlay ov) {
		this.waitAccess();
		this.overlays.add(ov);
		this.overlayAccess = true;
	}

	public void spotted(de.alexanderciupka.pokemon.characters.types.Character spotter) {
		SoundController.getInstance().playSound(SoundController.ALERT);
		SpottedOverlay s = new SpottedOverlay(this, spotter);
		this.addOverlay(s);
		s.createOverlay();
		this.wait(s);
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
		this.wait(c);
	}

	public void wait(Overlay o) {
		while (!o.isFinshed()) {
			Thread.yield();
		}
	}

	public void startFight(String logo) {
		if (logo != null && this.gController.getRouteAnalyzer().getLogoByName(logo) != null) {
			LogoOverlay l = new LogoOverlay(logo, this, this.getSize());
			l.createOverlay();
			this.addOverlay(l);
			this.wait(l);
		} else {
			this.startEncounter();
		}
	}

	public void changeRoute(Route newRoute) {
		if (newRoute != null) {
			this.waitAccess();
			for (int i = 0; i < this.overlays.size(); i++) {
				if (this.overlays.get(i) instanceof RouteOverlay || this.overlays.get(i) instanceof DarkOverlay
						|| this.overlays.get(i) instanceof RainOverlay || this.overlays.get(i) instanceof SnowOverlay) {
					this.overlays.get(i).onRemove();
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
			if (newRoute.getFog() != null) {
				FogOverlay fog = new FogOverlay(this, this.getSize(), newRoute.getFog());
				fog.createOverlay();
				this.addOverlay(fog);
			}
			RouteOverlay r = new RouteOverlay(newRoute, this, this.getSize());
			r.createOverlay();
			this.addOverlay(r);
			this.waitFrames = 5;
		}
	}

	public Overlay getOverlay(Class<? extends Overlay> overlay) {
		this.waitAccess();
		for (int i = 0; i < this.overlays.size(); i++) {
			if (overlay.isInstance(this.overlays.get(i))) {
				this.overlayAccess = true;
				return overlay.cast(this.overlays.get(i));
			}
		}
		this.overlayAccess = true;
		return null;
	}
}
