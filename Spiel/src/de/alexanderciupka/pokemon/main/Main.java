package de.alexanderciupka.pokemon.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import de.alexanderciupka.pokemon.constants.Items;
import de.alexanderciupka.pokemon.gui.GameFrame;
import de.alexanderciupka.pokemon.map.GameController;
import de.alexanderciupka.pokemon.menu.MenuController;
import de.alexanderciupka.pokemon.menu.SoundController;
import de.alexanderciupka.pokemon.pokemon.PokemonInformation;

public class Main {

	public static final double FPS = 30;

	public static boolean SONG_PAUSE = false;
	public static boolean FORCE_REPAINT = false;

	public static final Random RNG = new Random();

	public static JOptionPane getOptionPane(JComponent parent) {
		JOptionPane pane = null;
		if (!(parent instanceof JOptionPane)) {
			pane = getOptionPane((JComponent) parent.getParent());
		} else {
			pane = (JOptionPane) parent;
		}
		return pane;
	}

	public static String arrayToString(Object[] array) {
		String result = "";
		for (int i = 0; i < array.length; i++) {
			if (i != 0) {
				result += "+";
			}
			result += array[i];
		}
		return result;
	}

	public static void main(String[] args) throws Exception {

		MenuController.getInstance();

		PokemonInformation info = GameController.getInstance().getInformation();

		JsonParser parser = new JsonParser();

		JsonArray items = parser
				.parse(new JsonReader(new BufferedReader(
						new InputStreamReader(Main.class.getResourceAsStream("/pokemon/items.json")))))
				.getAsJsonArray();

		Scanner scanner = new Scanner(System.in);

		for (int i = 0; i < items.size(); i++) {
			System.out.println(items.get(i).getAsJsonObject().get("name") + " - "
					+ items.get(i).getAsJsonObject().get("attributes") + " - " + items.get(i).getAsJsonObject().get("description").toString());
			if(items.get(i).getAsJsonObject().get("pocket").getAsString().equals(Items.POKEBALLS)) {
				JsonObject foo = new JsonObject();
				foo.addProperty("name", Items.ATTR_FIGHT);
				items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
				continue;
			}
			if(items.get(i).getAsJsonObject().get("pocket").getAsString().equals(Items.MACHINES)) {
				JsonObject foo = new JsonObject();
				foo.addProperty("name", Items.ATTR_USABLE_OVERWORLD);
				items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
				foo = new JsonObject();
				foo.addProperty("name", Items.ATTR_POKEMON);
				items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
				continue;
			}
			if(items.get(i).getAsJsonObject().get("pocket").getAsString().equals(Items.BERRIES)) {
				JsonObject foo = new JsonObject();
				foo.addProperty("name", Items.ATTR_USABLE_OVERWORLD);
				items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
				foo = new JsonObject();
				foo.addProperty("name", Items.ATTR_POKEMON);
				items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
			}
			for (String s : new String[] { Items.ATTR_CONSUMABLE, Items.ATTR_COUNTABLE, Items.ATTR_HOLDABLE,
					Items.ATTR_USABLE_IN_BATTLE, Items.ATTR_USABLE_OVERWORLD, Items.ATTR_POKEMON, Items.ATTR_FIGHT,
					Items.ATTR_MOVE, Items.ATTR_OVERWORLD }) {
				if(!items.get(i).getAsJsonObject().get("attributes").toString().contains("\"" + s + "\"")) {
					System.out.println(s + "?");
					if(!scanner.nextLine().isEmpty()) {
						JsonObject foo = new JsonObject();
						foo.addProperty("name", s);
						items.get(i).getAsJsonObject().get("attributes").getAsJsonArray().add(foo);
					}
				}
			}
		}

		FileWriter writer = new FileWriter(new File("C:/Users/alexa/Desktop/items.json"));

		writer.write("[\n");

		for (int i = 0; i < items.size(); i++) {
			writer.write("\t");
			for (char c : items.get(i).toString().toCharArray()) {
				writer.write(c);
				writer.flush();
			}
			writer.write(",\n");
		}

		writer.write("]");
		writer.close();

		Thread repainter = new Thread(new Runnable() {
			@Override
			public void run() {
				long startTime = 0;
				GameController gController = null;
				GameFrame frame = null;
				while ((gController = GameController.getInstance()) == null) {
					Thread.yield();
				}
				while ((frame = gController.getGameFrame()) == null) {
					Thread.yield();
				}
				while (true) {
					startTime = System.currentTimeMillis();
					if (!frame.getDialogue().isVisible()
							|| (gController.isFighting() && !frame.getFightPanel().getTextLabel().isVisible())
							|| FORCE_REPAINT) {
						frame.repaint();
						if (FORCE_REPAINT) {
							FORCE_REPAINT = false;
						}
					} else {
						if (frame.getDialogue().isVisible()) {
							frame.getDialogue().repaint();
						} else if (frame.getFightPanel().getTextLabel().isVisible()) {
							frame.getFightPanel().getTextLabel().repaint();
						}
					}
					try {
						Thread.sleep((long) Math.max(1000.0 / Main.FPS - (System.currentTimeMillis() - startTime), 0));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		repainter.setDaemon(true);
		repainter.setName("REPAINTER");
		repainter.start();

		Thread songChooser = new Thread(new Runnable() {
			@Override
			public void run() {
				SoundController sc = SoundController.getInstance();
				Random rng = new Random();
				File[] songs = new File(Main.class.getResource("/music/songs/").getFile()).listFiles();
				while (true) {
					if (!SONG_PAUSE && !sc.isSongRunning()) {
						File song = songs[rng.nextInt(songs.length)];
						sc.playSong(song);
					}
					Thread.yield();
				}
			}
		});
		songChooser.setDaemon(true);
		songChooser.setName("SONGCHOOSER");

		// songChooser.start();

		// JsonParser parser = new JsonParser();
		//
		// JsonArray result = parser.parse(new
		// FileReader("C:/users/alexa/Desktop/items/items.json")).getAsJsonArray();

		// File f = new File("C:/users/alexa/Desktop/items/Items.java");
		//
		// FileWriter writer = new FileWriter(f);
		//
		// for (int i = 0; i < result.size(); i++) {
		// System.out.println(result.get(i));
		// writer.write(" public static final int " +
		// result.get(i).getAsJsonObject().get("name").getAsString().replace("
		// ", "_")
		// .replace("-", "_").toUpperCase()
		// + " = " + result.get(i).getAsJsonObject().get("id").getAsInt() +
		// ";\n");
		// writer.flush();
		// }
		//
		// writer.close();

		// new File("C:/Users/alexa/Desktop/items/").mkdir();

		// for (int i = 0; i < result.size(); i++) {
		// try {
		// String httpsURL = "https://pokeapi.co/api/v2/item/"
		// + result.get(i).getAsJsonObject().get("id").getAsString() + "/";
		// URL myUrl = new URL(httpsURL);
		// HttpsURLConnection conn = (HttpsURLConnection)
		// myUrl.openConnection();
		// conn.setRequestProperty("User-Agent",
		// "Mozilla/5.0 (Windows NT 6.1;WOW64) AppleWebKit/537.11 (KHTML, like
		// Gecko) Chrome/23.0.1271.95Safari/537.11");
		// InputStream is = conn.getInputStream();
		// InputStreamReader isr = new InputStreamReader(is);
		// BufferedReader br = new BufferedReader(isr);
		//
		// String inputLine = br.readLine();
		//
		// JsonObject object = parser.parse(inputLine).getAsJsonObject();
		//
		// br.close();
		//
		// JsonArray attributes = new JsonArray();
		//
		// for (JsonElement e : object.get("attributes").getAsJsonArray()) {
		// JsonObject current = e.getAsJsonObject();
		//
		// current.remove("url");
		//
		// attributes.add(current);
		// }
		//
		// result.get(i).getAsJsonObject().add("attributes", attributes);
		//
		// myUrl = new
		// URL(object.get("category").getAsJsonObject().get("url").getAsString());
		// conn = (HttpsURLConnection) myUrl.openConnection();
		// conn.setRequestProperty("User-Agent",
		// "Mozilla/5.0 (Windows NT 6.1;WOW64) AppleWebKit/537.11 (KHTML, like
		// Gecko) Chrome/23.0.1271.95Safari/537.11");
		// is = conn.getInputStream();
		// isr = new InputStreamReader(is);
		// br = new BufferedReader(isr);
		//
		// JsonObject category = parser.parse(br.readLine()).getAsJsonObject();
		//
		// result.get(i).getAsJsonObject().addProperty("pocket",
		// category.get("pocket").getAsJsonObject().get("name").getAsString());
		//
		// // if (object.get("machines").getAsJsonArray().size() != 0) {
		// // String bar = "";
		// // for (JsonElement e : object.get("machines").getAsJsonArray())
		// // {
		// // if
		// //
		// (e.getAsJsonObject().get("version_group").getAsJsonObject().get("name").getAsString()
		// // .equals("omega-ruby-alpha-sapphire")) {
		// // String[] foo =
		// //
		// e.getAsJsonObject().get("machine").getAsJsonObject().get("url").getAsString()
		// // .replace("\\", "").split("/");
		// // bar = foo[foo.length - 1];
		// // }
		// // }
		// // System.err.println(bar);
		// // }
		// System.out.println(result.get(i));
		// } catch (FileNotFoundException fe) {
		// fe.printStackTrace();
		// continue;
		// } catch (Exception e) {
		// e.printStackTrace();
		// break;
		// }
		// }
		//
		// FileWriter writer = new
		// FileWriter("C:/Users/alexa/Desktop/items/items.json");
		//
		// writer.write("[\n");
		//
		// for (int i = 0; i < result.size(); i++) {
		// writer.write("\t");
		// for (char c : result.get(i).toString().toCharArray()) {
		// writer.write(c);
		// writer.flush();
		// }
		// writer.write(",\n");
		// }
		//
		// writer.write("]");
		// writer.close();
	}
}
