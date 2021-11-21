package game;

/**
 * Enumaration of the game's planet. Planets are defined by their images
 * accessed by {@link #getPicture()}, which returns a random image from the
 * available ones. They also have name that can be accessed via
 * {@link #getName()}, and a specific gravity, that can be accessed from
 * {@link #getGravity()}.
 * 
 * @author 1434866
 *
 */
public enum Planet {
//@formatter:off
	SPACE("Espace", 0.0, true, "/images/space.jpg"), 
	MOON("Lune", 1.6, true, "/images/moon.jpg"), 
	MARS("Mars", 3.8,true, "/images/mars.jpg"), 
	EARTH("Terre", 9.8,true, "/images/earth1.jpg","/images/earth2.jpg"), 
	JUPITER("Jupiter", 24.8,true,"/images/jupiter.jpg"), 
	SUN("Soleil", 273.9,true, "/images/sun.jpg"),
	MUFFIN("Planète Muffin", -6.66,false,"/images/muffin.jpg");
//@formatter:on

	private double gravity;
	private String[] pictures;
	private String name;
	private boolean allowed;

	private Planet(String name, double gravity, boolean allowedInBuilder, String... pictures) {

		this.gravity = gravity;
		this.pictures = pictures;
		this.name = name;
		this.allowed = allowedInBuilder;
	}

	/**
	 * 
	 * @return The planet's gravity
	 */
	public double getGravity() {
		return gravity;
	}

	/**
	 * 
	 * @return The planet's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return A random picture between the available ones.
	 */
	public String getPicture() {
		return getClass().getResource(pictures[(int) (Math.random() * pictures.length)]).toExternalForm();
	}

	/**
	 * The name of the planet represented to a String.
	 */
	@Override
	public String toString() {
		return name;

	}

	/**
	 * 
	 * @return True if the planet is allowed to be used in the builder, false
	 *         otherwise.
	 */
	public boolean isAllowedInBuilder() {
		return allowed;
	}

}
