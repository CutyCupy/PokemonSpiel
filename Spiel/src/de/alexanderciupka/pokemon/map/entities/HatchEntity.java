package de.alexanderciupka.pokemon.map.entities;

import java.awt.Image;
import java.awt.Point;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;

import de.alexanderciupka.pokemon.characters.Character;
import de.alexanderciupka.pokemon.map.Route;

public class HatchEntity extends Entity {

	private HashSet<SimpleEntry<String, Point>> generators;
	private int minimum;
	private String id;

	private Image close;
	private Image open;

	public HatchEntity(Route parent, String id, String terrainName) {
		super(parent, true, "", 0, terrainName);
		this.id = id;
		generators = new HashSet<>();
		close = gController.getRouteAnalyzer().getSpriteByName("close_hatch");
		open = gController.getRouteAnalyzer().getSpriteByName("open_hatch");
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMinimum() {
		return this.minimum;
	}

	public void setGenerators(SimpleEntry<String, Point>... g) {
		this.generators.clear();
		for (SimpleEntry<String, Point> current : g) {
			this.generators.add(current);
		}
	}

	public void addGenerator(String route, Point p) {
		this.generators.add(new SimpleEntry<String, Point>(route, p));
	}

	public HashSet<SimpleEntry<String, Point>> getGenerators() {
		return this.generators;
	}

	public boolean containsGenerator(GeneratorEntity generator) {
		for (SimpleEntry<String, Point> g : generators) {
			if (g.getKey().equals(generator.getRoute().getId()) && g.getValue().x == generator.getX()
					&& g.getValue().y == generator.getY()) {
				return true;
			}
		}
		return false;
	}

	public boolean isDone() {
		if (minimum == 0 || minimum > generators.size()) {
			minimum = generators.size();
		}
		int done = 0;
		for (SimpleEntry<String, Point> g : generators) {
			Route r = gController.getRouteAnalyzer().getRouteById(g.getKey());
			if (r != null) {
				Entity e = r.getEntity(g.getValue().x, g.getValue().y);
				if (e instanceof GeneratorEntity && ((GeneratorEntity) e).isDone()) {
					done++;
				}
			}
		}
		return done >= this.minimum;
	}

	@Override
	public boolean startWarp(Character c) {
		if (isDone()) {
			return super.startWarp(c);
		}
		return false;
	}

	@Override
	public Image getSprite() {
		return isDone() ? open : close;
	}

	public String getId() {
		return this.id;
	}

}
