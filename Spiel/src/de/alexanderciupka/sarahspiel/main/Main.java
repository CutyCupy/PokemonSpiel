package de.alexanderciupka.sarahspiel.main;

import de.alexanderciupka.sarahspiel.menu.MenuController;

public class Main {

	public static void main(String[] args) {
		 MenuController.getInstance();
//		readDescription();
	}

//	public static void readDescription() {
//		JsonParser parser = new JsonParser();
//		BufferedReader reader = null;
//		BufferedReader moveData = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/pokemon/moves.json")));
//		JsonArray allMoveData = null;
//		try {
//			allMoveData = parser.parse(moveData.readLine()).getAsJsonArray();
//		} catch (JsonSyntaxException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		int i = 1;
//		JsonArray newData = new JsonArray();
//		for (JsonElement element : allMoveData) {
//			try {
//				JsonObject currentJson = element.getAsJsonObject();
//				URLConnection connection = new URL("http://www.pokeapi.co/api/v2/move/" + i).openConnection();
//				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//				connection.connect();
//				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//				StringBuffer buffer = new StringBuffer();
//				int read;
//				char[] chars = new char[1024];
//				while ((read = reader.read(chars)) != -1)
//					buffer.append(chars, 0, read);
//				JsonObject currentMove = (JsonObject) parser.parse(buffer.toString());
//				for(JsonElement curDescription : currentMove.get("flavor_text_entries").getAsJsonArray()) {
//					JsonObject jo = curDescription.getAsJsonObject();
//					if(jo.get("language").getAsJsonObject().get("name").getAsString().equals("de")) {
//						currentJson.addProperty("desc", jo.get("flavor_text").getAsString());
//						break;
//					}
//				}
//				currentJson.addProperty("ailment", currentMove.get("meta").getAsJsonObject().get("ailment").getAsJsonObject().get("name").getAsString());
//				currentJson.addProperty("damage_class", currentMove.get("damage_class").getAsJsonObject().get("name").getAsString());
//				currentJson.addProperty("priority", currentMove.get("priority").getAsInt());
//				newData.add(currentJson);
//				System.out.println(currentJson);
//			} catch (JsonSyntaxException e) {
//				e.printStackTrace();
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			i++;
//		}
//		try {
//			FileWriter fw = new FileWriter(new File(Main.class.getResource("/pokemon/moves.json").getFile()));
//			for(char c : newData.toString().toCharArray()) {
//				fw.write(c);
//				fw.flush();
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

}
