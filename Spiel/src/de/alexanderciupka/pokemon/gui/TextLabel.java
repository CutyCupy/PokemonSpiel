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
	private long delay;
	public static final long SLOW = 30;
	public static final long FAST = 10;
	private boolean autoMove;
	private After after;

	private boolean waiting;
	private JPanel parent;

	public TextLabel() {
		super();
		new Thread(this).start();
		after = After.NOTHING;
		isActive = false;
		text = new ArrayList<String>();
		//24 chars max length row
		this.setVisible(true);
		this.setFont(new Font(Font.MONOSPACED, Font.BOLD, 35));
		this.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Color.BLACK, 5), new EmptyBorder(2, 10, 2, 10)));
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.TOP);

		delay = SLOW;
	}

	public void setActive() {
		try {
			if(!text.isEmpty()) {
				isActive = true;
			} else {
				isActive = false;
				this.setVisible(false);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(true) {
			if(isActive && !waiting) {
				this.setVisible(true);
				this.setText("<html>");
				for(char c : text.get(0).toCharArray()) {
					this.setText(this.getText() + c);
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				removeFirst();
				if(autoMove) {
					try {
						Thread.sleep(1000);
						setActive();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else if(waiting) {
				setVisible(false);
			}
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean setDelay(long delay) {
		if(delay >= 0) {
			this.delay = delay;
			return true;
		}
		return false;
	}

	private void removeFirst() {
		text.remove(0);
		isActive = false;
	}

	public void addText(String text) {
		String[] words = text.split(" ");
		String currentRow = "";
		String secondRow = "";
		boolean firstRow = true;
		for(int i = 0; i < words.length; i++) {
			if(firstRow) {
				if(currentRow.length() + words[i].length() <= 24) {
					currentRow += words[i] + " ";
				} else {
					currentRow += "<br>";
					secondRow = words[i] + " ";
					firstRow = false;
				}
			} else {
				if(secondRow.length() + words[i].length() <= 24) {
					secondRow += words[i] + " ";
				} else {
					this.text.add(currentRow + secondRow);
					currentRow = words[i] + " ";
					secondRow = "";
					firstRow = true;
				}
			}
		}
		this.text.add(currentRow + secondRow);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public boolean isEmpty() {
		return text.isEmpty() && !isVisible();
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
		if(this.parent != null) {
			this.parent.remove(this);
		}
		this.parent = parent;
		this.parent.add(this);
	}

	public void waitText() {
		setWaiting(false);
		while (!isEmpty()) {
			Thread.yield();
			repaint();
		}
	}
}
