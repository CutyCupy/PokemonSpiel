package de.alexanderciupka.pokemon.gui;

import java.awt.Color;

import javax.swing.JProgressBar;

public class HPBar extends JProgressBar implements Runnable {

	private static final Color FULL  = Color.GREEN;
	private static final Color HALF  = new Color(255, 255, 0);
	private static final Color EMPTY = Color.RED;

	private int nextValue = -1;
	private static final long time = 1;
	private static final double FPS = 60;

	private boolean finished = true;


	@Override
	public void setValue(int n) {
		super.setValue(n);
		setForeground(getHPColor());
	}

	public void updateValue(int n) {
		setFinished(false);
		nextValue = n;
		new Thread(this).start();
	}

	private Color getHPColor() {
		double life = (this.getValue()) / ((double) this.getMaximum());
		if(life > 0.5) {
			return FULL;
		} else if(life > 0.15) {
			return HALF;
		} else {
			return EMPTY;
		}
	}

	@Override
	public void run() {
		if(nextValue != -1) {
			int delta = this.getValue() - nextValue;
			double change = delta / (FPS * time);
			for(double value = this.getValue(); delta > 0 ? value > nextValue : value < nextValue; value -= change) {
				this.setValue((int) Math.round(value));
				try {
					Thread.sleep((long) (1000 / FPS));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.setValue(nextValue);
		} //else if(nextValue >= this.getValue()) {
//			this.setValue(nextValue);
//		}
		setFinished(true);
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
}
