package gameObservables;

import java.net.URL;

/**
 * An enumeration of the available objects in the world.
 * 
 * @author sunny
 */
public enum Observable {
	// @formatter:off
	SPRING("Ressort", "/images/item_spring.png", "/interfaceViews/headers/headerSpring.fxml"),
	ROPE("Corde","/images/item_rope.png", "/interfaceViews/headers/headerRope.fxml"), 
	STICKWALL("Mur Collant","/images/item_stickWall.png", "/interfaceViews/headers/headerSurface.fxml"),
	SURFACE("Surface","/images/item_surface.png", "/interfaceViews/headers/headerSurface.fxml"), 
	BASCULE("Bascule", "/images/item_bascule.png","/interfaceViews/headers/headerSurface.fxml"), 
	COIN("Pièce","/images/item_coin.png",null), 
	POT_OF_GOLD("Pot d'or", "/images/item_pot.png", null),
	STICK_BASCULE("Bascule aimantée","/images/item_stickBascule.png","/interfaceViews/headers/headerSurface.fxml"),
	DOMINO("Domino","/images/item_domino.png",null);
	// @formatter:on

	
	/**
	 * name of instance of object path of picture
	 */
	private String name;
	private String itemPicture;
	private String headerPath;

	/**
	 * constructor of Observable set name and itemPicture of the new Observable
	 * 
	 * @param name
	 *            name of instance of object
	 * @param itemPicture
	 *            path of picture
	 */
	private Observable(String name, String itemPicture, String headerPath) {
		this.name = name;
		this.itemPicture = itemPicture;
		this.headerPath = headerPath;
	}

	/**
	 * return the path of itempicture of Observable
	 * 
	 * @return itempicture
	 */
	public String getItemPicture() {
		return getClass().getResource(itemPicture).toExternalForm();
	}

	/**
	 * @return The fxml url
	 */
	public URL getHeaderPath() {
		return this.headerPath == null ? null : getClass().getResource(headerPath);
	}

	/**
	 * return name of Observable
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

}
