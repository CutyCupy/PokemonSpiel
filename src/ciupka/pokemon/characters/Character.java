package ciupka.pokemon.characters;

import ciupka.pokemon.interfaces.IMovable;

public abstract class Character implements IMovable {
	
	// Speed Constants
	public static final int SPEED_WALKING = 0;
	public static final int SPEED_RUNNING = 0;
	public static final int SPEED_BYKING = 0;

	// Location
	private Location location;

	// Naming
	private String name;
	// TODO: deciding how to save Sprite Data
	
	// Follower
	private Character follower;
	
	public Character(String name, Location loc) {
		this.name = name;
		this.location = loc;
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Character getFollower() {
		return this.follower;
	}
	
	public void setFollower(Character follower) {
		this.follower = follower;
	}
	
	
}
