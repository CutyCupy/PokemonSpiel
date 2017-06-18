package de.alexanderciupka.sarahspiel.painting;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.alexanderciupka.sarahspiel.menu.MenuController;

public class Painting {

	private BufferedImage img;
	private String name;
	private PaintingController pController;
	
	public Painting(String name, int width, int height) {
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		pController = PaintingController.getInstance();
		this.name = name;
	}
	
	public boolean save() {
		try {
			makeTransparent();
			ImageIO.write(img, "png", new File("./res/paintings/" + name + ".png"));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean load() {
		try {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "Bilder", "jpg", "gif", "png");
	        chooser.setFileFilter(filter);
	        chooser.setCurrentDirectory(new File("./res/paintings/"));
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal == JFileChooser.APPROVE_OPTION) {
				img = scaleDown(ImageIO.read(chooser.getSelectedFile()));
				
//				if(img.getWidth() < pController.getScreenResolution().getWidth() || img.getHeight() < pController.getScreenResolution().getHeight()) {
					name = chooser.getSelectedFile().getName();
					int pos = name.lastIndexOf(".");
					if (pos > 0) {
					    name = name.substring(0, pos);
					}
					return true;
//				}				
//				return false;
	        }
	        return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private BufferedImage scaleDown(BufferedImage bi) {
		if(bi.getHeight() > MenuController.SCREEN_SIZE.height * 0.9 ||
				bi.getWidth() > MenuController.SCREEN_SIZE.width * 0.9) {
			double factor = Math.max(MenuController.SCREEN_SIZE.height * 0.9 / bi.getHeight(), MenuController.SCREEN_SIZE.width * 0.9 / bi.getWidth());
			return toBufferedImage(bi.getScaledInstance((int) (bi.getWidth() * factor), (int) (bi.getHeight() * factor), Image.SCALE_SMOOTH));
		}
		return bi;
	}
	
	public BufferedImage getImage() {
		return img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	private void makeTransparent() {
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				if(img.getRGB(x, y) == Color.WHITE.getRGB()) {
					img.setRGB(x, y, new Color(0,0,0,0).getRGB());
				}
			}
		}
	}
	
	public void flood(int x, int y, Color newColor) {
		boolean[][] visited = new boolean[img.getHeight()][img.getWidth()];
		Stack<Point> points = new Stack<Point>();
		points.push(new Point(x, y));
		backup();
		boolean colorize = true;
		Color oldColor = new Color(img.getRGB(x, y));
		while(!points.isEmpty()) {
			Point point = points.pop();
			if(point.x < 0 || point.y < 0 || point.x >= img.getWidth() || point.y >= img.getHeight()) {
				colorize = false;
			}
			if(visited[point.y][point.x]) {
				colorize = false;
			}
			int imgAlpha = (img.getRGB(point.x, point.y) & 0xff000000) >>> 24;
			int colorAlpha = (oldColor.getRGB() & 0xff000000) >>> 24;
			if((img.getRGB(point.x, point.y) - (imgAlpha << 24)) != (oldColor.getRGB() - (colorAlpha << 24))) {
				colorize = false;
			}
			visited[point.y][point.x] = true;
			if(colorize) {
				img.setRGB(point.x, point.y, newColor.getRGB());
				if(point.x < img.getWidth() - 1) {
					if(!visited[point.y][point.x + 1])
						points.push(new Point(point.x + 1, point.y));
				} 
				if(point.x > 0) {
					if(!visited[point.y][point.x - 1])
						points.push(new Point(point.x - 1, point.y));
				}
				if(point.y < img.getHeight() - 1) {
					if(!visited[point.y + 1][point.x])
						points.push(new Point(point.x, point.y + 1));
				} 
				if(point.y > 0) {
					if(!visited[point.y - 1][point.x])
						points.push(new Point(point.x, point.y - 1));
				}
			}
			colorize = true;
		}		
	}
	
	public void backup() {
		try {
			ImageIO.write(img, "png", new File("./res/paintings/backup.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage loadBackup() {
		try {
			img = ImageIO.read(new File(getClass().getResource("/paintings/backup.png").toURI().getPath()));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return img;
	}
	
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}
}
