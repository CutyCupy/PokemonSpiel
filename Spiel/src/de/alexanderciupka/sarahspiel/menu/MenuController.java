package de.alexanderciupka.sarahspiel.menu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.alexanderciupka.sarahspiel.map.GameController;
import de.alexanderciupka.sarahspiel.painting.PaintingController;

public class MenuController {

	private static MenuController instance;
	private PaintingController pController;
	private GameController gController;
	private Font menuFont;
	private JFileChooser fileChooser;
	private MainMenuFrame menuFrame;
	private PaintingNameFrame paintingFrame;
	public static final String SAVE_PATH = System.getProperty("user.home") + "/SarahsSpielSpeicherdateien/";

	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

	public static final List<Character> forbiddenChars = Arrays.asList('<', '>', '?' , '"', ':', '|', '\\', '/', '*', '.', ',', '[', ']', '(', ')', '!', '\'', '{', '}');

	private MenuController() {
	}

	public void start() {
		gController = GameController.getInstance();
		pController = PaintingController.getInstance();
		new File(SAVE_PATH).mkdir();
		fileChooser = new JFileChooser(SAVE_PATH);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Sarahs Spiel Speicherdateien",
	            "sss"));
		menuFont = importFont("/fonts/pokemon_solid.ttf");
		menuFrame = new MainMenuFrame();
	}

	public static MenuController getInstance() {
		if(instance == null) {
			instance = new MenuController();
			instance.start();
		}
		return instance;
	}

	public void startEditor() {
		if(paintingFrame == null)
			paintingFrame = new PaintingNameFrame();
		paintingFrame.setVisible(true);
	}

	public void openEditor(String pictureName, int width, int height) {
		pController.startNewImage(pictureName, width, height);
	}

	public Font getMenuFont() {
		return this.menuFont;
	}

	public Font importFont(String path) {
		InputStream is = getClass().getResourceAsStream(path);
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
			is.close();
		  	return f;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Rectangle getToCenter(int width, int height) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();
		return new Rectangle((screenWidth / 2) - (width / 2), (screenHeight / 2) - (height / 2), width, height);
	}

	public boolean checkName(String text, char typedChar) {
		if(text.length() <= 16) {
			if(forbiddenChars.contains(typedChar)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public void startNewGame() {
		gController.startNewGame();
	}

	public boolean loadGame() {
		return fileChooser.showDialog(null, "Laden") == 0 ? gController.loadGame(fileChooser.getSelectedFile().getPath()) : false;
	}

	public String saveGame() {
		fileChooser.showSaveDialog(null);
		if(fileChooser.getSelectedFile() != null) {
			return fileChooser.getSelectedFile().getName();
		}
		return null;
	}

	public int returnToMenu() {
		return JOptionPane.showOptionDialog(null, "Was möchtest du tun?", "Menü", JOptionPane.YES_NO_CANCEL_OPTION,  JOptionPane.QUESTION_MESSAGE, null,
				new Object[]{"Speichern", "Speichern und zum Hauptmenü", "Laden", "Abbrechen"}, "Speichern");
	}

	public void showMenu() {
		this.menuFrame.setVisible(true);
	}

	public MainMenuFrame getMenuFrame() {
		return menuFrame;
	}

	public PaintingNameFrame getPaintingFrame() {
		return paintingFrame;
	}



}
