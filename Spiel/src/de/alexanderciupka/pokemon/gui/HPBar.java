package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.JProgressBar;

import de.alexanderciupka.pokemon.main.Main;

public class HPBar extends JProgressBar implements Runnable {

	public static final Color FULL  = Color.GREEN;
	public static final Color HALF  = new Color(255, 255, 0);
	public static final Color EMPTY = Color.RED;

	private int nextValue = -1;
	private boolean finished = true;
	private boolean falling = false;;


	@Override
	public void setValue(int n) {
		setValue(n, true);
	}

	public void setValue(int n, boolean needScale) {
		super.setValue(needScale ? n * 1000 : n);
		setForeground(getHPColor());
	}

	public void updateValue(int n) {
		setFinished(false);
		nextValue = n * 1000;
		new Thread(this).start();
	}

	@Override
	public int getMaximum() {
		return super.getMaximum() / 1000;
	}

	@Override
	public void setMaximum(int n) {
		super.setMaximum(n * 1000);
	}

	public Color getHPColor() {
		double life = (this.getValue() / 1000) / ((double) this.getMaximum());
		if(life > 0.5) {
			return FULL;
		} else if(life > 0.2) {
			return HALF;
		} else if(life == 0) {
			return new Color(254, 0, 0);
		} else {
			return EMPTY;
		}
	}

	@Override
	public void run() {
		if(nextValue != -1) {
			int delta = this.getValue() - nextValue;
			this.falling = delta > 0;
			double change = delta / Main.FPS;
			for(double value = this.getValue(); this.falling ? value > nextValue :  value < nextValue; value -= change) {
				this.setValue((int) Math.round(value), false);
				try {
					Thread.sleep((long) (1000 / Main.FPS));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.setValue(nextValue, false);
		}
		this.falling = false;
		setFinished(true);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean isFalling() {
		return this.falling && !this.finished;
	}
}
