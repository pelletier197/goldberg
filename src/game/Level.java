package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import observables.AbstractComplexObservable;

public class Level implements Comparable<Level> {

	public static final String DEFAULT_NAME = "Anonyme";

	/**
	 * The name of the creator of this world.
	 */
	private String creator;

	/**
	 * The name of the level.
	 */
	private String name;

	/**
	 * The path to the level's file
	 */
	private String path;

	/**
	 * Objects that are contained in the world once it is created
	 */
	private List<AbstractComplexObservable> fixedObjects;

	/**
	 * The inventory a user is allowed to use in this level.
	 */
	private Inventory inventory;

	/**
	 * The type of border the world uses
	 */
	private BorderType borders;

	/**
	 * The height and the width of the world loaded by this level
	 */
	private double height, width;

	/**
	 * The level's planet
	 */
	private Planet planet;

	/**
	 * The level's overview
	 */
	private byte[] screenShot;

	/**
	 * 
	 * Constructs a level with the defined name, creator and path to file. The
	 * creator and name fields can be null or empty, and will be set to
	 * "anonyme" values.
	 * 
	 * However, the file must reference a correct file URL, or an exception will
	 * be thrown.
	 * 
	 * @param name
	 *            The name of the level. Can be null.
	 * @param creator
	 *            The creator of the level. Can be null.
	 * @param path
	 *            The path to the level's file. Cannot be null.
	 * 
	 * @throws IOException
	 *             If the path to the file is invalid
	 */
	public Level(String name, String creator, String path) throws IOException {

		File file = new File(path);
		setCreator(creator);
		setName(name);
		this.path = file.getPath();
		this.inventory = new Inventory();
		this.fixedObjects = new ArrayList<>();
	}

	/**
	 * Sets the level's screenShot from the byte array
	 */
	public void setScreenShot(byte[] picture) {
		this.screenShot = picture;
	}

	/**
	 * @return The level's screenShot
	 */
	public byte[] getScreenShot() {
		return screenShot;
	}

	/**
	 * Returns the path to the file of this level.
	 * 
	 * @return The path to the file of this level.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * 
	 * @return The name of the creator of the level
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * Sets the creator of the level to the value sent in parameter. If null or
	 * empty, the name is set to "Anonyme"
	 * 
	 * @param creator
	 */
	public void setCreator(String creator) {

		if (creator == null || creator.trim().isEmpty()) {
			creator = DEFAULT_NAME;
		}

		this.creator = creator;
	}

	/**
	 * 
	 * @return The name of the level
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the level to the value sent in parameter. If null or
	 * empty, the name is set to "Anonyme"
	 * 
	 * @param creator
	 */
	public void setName(String name) {
		if (name == null || name.trim().isEmpty()) {
			name = DEFAULT_NAME;
		}
		this.name = name;
	}

	/**
	 * Returns the inventory allowed to be used in this level. This inventory's
	 * items should only be modified during the creation of the level, and
	 * should never be changed after.
	 * 
	 * @return The inventory allowed to be used in this level.
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Removes the specified fixed object from the level.
	 * 
	 * @param object
	 *            The fixed object to be removed.
	 */
	public void removeFixedObject(AbstractComplexObservable object) {
		this.fixedObjects.remove(object);
	}

	/**
	 * Add the specified fixed object to the level.
	 * 
	 * @param object
	 *            The fixed object to be added
	 */
	public void addFixedObject(AbstractComplexObservable object) {
		this.fixedObjects.add(object);
	}

	/**
	 * Returns all the fixed object contained in this level.
	 * 
	 * @return The fixed objects from the level.
	 */
	public List<AbstractComplexObservable> getFixedObject() {
		return this.fixedObjects;
	}

	/**
	 * Sets the level's inventory to the one sent in parameter.
	 * 
	 * @param inventory
	 *            The new level's inventory.
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	/**
	 * Returns the fixed objects
	 * 
	 * @return
	 */
	public BorderType getBorders() {
		return borders;
	}

	/**
	 * Sets the borderType of the level.
	 * 
	 * @param borders
	 *            The new borders
	 */
	public void setBorders(BorderType borders) {
		this.borders = borders;
	}

	/**
	 * 
	 * @return The height of the level in meters.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * sets the height of the level in meters.
	 * 
	 * @param height
	 *            The new height
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * 
	 * @return The width of the world in meters
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of the world in meters.
	 * 
	 * @param width
	 *            The new width of the level
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Sets the level's planet.
	 * 
	 * @param planet
	 *            The new planet of the level
	 */
	public void setPlanet(Planet planet) {
		if (planet == null)
			throw new NullPointerException();
		this.planet = planet;

	}

	/**
	 * 
	 * @return The level's planet.
	 */
	public Planet getPlanet() {
		return planet;
	}

	/**
	 * Redefinition of the compareTo of 2 levels. 2 levels are comparable by
	 * their name, and are compared in alphabetic order, from
	 * {@link String #compareTo(String)}
	 */
	@Override
	public int compareTo(Level o) {

		return this.name.compareTo(o.name);

	}

}
