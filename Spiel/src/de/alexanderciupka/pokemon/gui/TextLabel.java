package de.alexanderciupka.pokemon.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class TextLabel extends JLabel implements Runnable {

	private boolean isActive;
	private ArrayList<String> text;
	private ArrayList<Color> color;
	private long delay;
	public static final long SLOW = 30;
	public static final long FAST = 10;
	private boolean autoMove;
	private After after;

	public static final String NEW_LINE = " <n> ";

	private boolean waiting;
	private JPanel parent;

	public TextLabel() {
		super();
		new Thread(this).start();
		this.after = After.NOTHING;
		this.isActive = false;
		this.text = new ArrayList<String>();
		this.color = new ArrayList<>();
		this.setVisible(true);
		this.setFont(new Font(Font.MONOSPACED, Font.BOLD, 35));
		this.setBorder(
				BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 5), new EmptyBorder(2, 10, 2, 10)));
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);

		this.delay = SLOW;
	}

	public void setActive() {
		try {
			if (!this.text.isEmpty()) {
				this.isActive = true;
			} else {
				this.isActive = false;
				this.setVisible(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			if (this.isActive && !this.waiting) {
				this.setVisible(true);
				this.setForeground(this.color.get(0));
				this.setText("<html>");
				for (char c : this.text.get(0).toCharArray()) {
					this.setText(this.getText() + c);
					try {
						Thread.sleep(this.delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				this.removeFirst();
				if (this.autoMove) {
					try {
						Thread.sleep(1000);
						this.setActive();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else if (this.waiting) {
				this.setVisible(false);
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean setDelay(long delay) {
		if (delay >= 0) {
			this.delay = delay;
			return true;
		}
		return false;
	}

	private void removeFirst() {
		this.text.remove(0);
		this.color.remove(0);
		this.isActive = false;
	}

	public void addText(String text) {
		this.addText(text, Color.BLACK);
	}

	public void addText(String text, Color c) {

		String[] lines = text.split(NEW_LINE);
		for (String line : lines) {
			String[] words = line.split(" ");
			String currentRow = "";
			String secondRow = "";
			boolean firstRow = true;
			for (String word : words) {
				if (firstRow) {
					if (currentRow.length() + word.length() <= 24) {
						currentRow += word + " ";
					} else {
						currentRow += "<br>";
						secondRow = word + " ";
						firstRow = false;
					}
				} else {
					if (secondRow.length() + word.length() <= 24) {
						secondRow += word + " ";
					} else {
						this.text.add(currentRow + secondRow);
						this.color.add(c);
						currentRow = word + " ";
						secondRow = "";
						firstRow = true;
					}
				}
			}
			this.text.add(currentRow + secondRow);
			this.color.add(c);
		}
	}

	public boolean isActive() {
		return this.isActive;
	}

	public boolean isEmpty() {
		return this.text.isEmpty() && !this.isVisible();
	}

	public void setAutoMove(boolean autoMove) {
		this.autoMove = autoMove;
	}

	public void setAfter(After after) {
		this.after = after;
	}

	public After getAfter() {
		return this.after;
	}

	public void setWaiting(boolean wait) {
		this.waiting = wait;
	}

	public boolean isWaiting() {
		return this.waiting;
	}

	public void setParent(JPanel parent) {
		if (this.parent != null) {
			this.parent.remove(this);
		}
		this.parent = parent;
		this.parent.add(this);
	}

	public void waitText() {
		this.setWaiting(false);
		while (!this.isEmpty()) {
			Thread.yield();
			this.repaint();
		}
	}
}
