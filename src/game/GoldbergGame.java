package game;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import gameObservableViews.ObservableObjectFactory;
import gameObservableViews.ObservableWrapper;
import gameObservables.Coin;
import gameObservables.JointApplier;
import gameObservables.Observable;
import gameObservables.PotOfGold;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import observables.AbstractComplexObservable;
import observables.CollidingPair;
import observables.DynamicWorld;
import observables.DynamicWorld.Bounds;

/**
 * This class provides dataStructures that allows to run the Goldberg Game
 * properly.
 * 
 * This class represents the entire game utils.
 * 
 * The original game use to loads a single level at a time, and displays its
 * 
 * 
 */
public class GoldbergGame {

	/**
	 * The game's status. In the game's process, there are 3 available states
	 * 
	 * <p>
	 * BUILDING state means that the user is currently building the game. The
	 * inventory allowed should therefore be the BUILDING_INVENTORY. It is the
	 * mode used to build a world with the desired fixed objects. At this point,
	 * no collision detection is performed between surfaces, but they are
	 * performed between Coin and surfaces, to avoid wrong contacts.
	 * </p>
	 * 
	 * <p>
	 * PREPARING state represents the pre-running state, and it is where the
	 * user can place the Level's inventory object. The user will have the
	 * possibility to place every AbstractComplexObservable by himself in the
	 * world, and then switch to the running state, where no moving should be
	 * allowed, and the game simply animates, starting the winning thread. At
	 * this point, every collision detection are static, which means the
	 * handleStaticCollide method of those observables will be called when
	 * necessary.
	 * </p>
	 * 
	 * <p>
	 * RUNNING state is called when the DynamicWorld is wished to set to dynamic
	 * state. When the game is set to this mode, no object should normally be
	 * moved. The game simply run itself, and the dynamic collisions are called
	 * on objects.
	 * </p>
	 * 
	 * @author sunny, etienne
	 *
	 */
	public enum Status {
		BUILDING, PREPARING, RUNNING;
	}

	/**
	 * The final value of the friction coefficient for the world's bounds.
	 * Represents the optimal friction coefficient for the game.
	 */
	private static final double BORDER_FRICTION = 0.2;

	/**
	 * The dynamic world used to handle static collision on PREPARING state, or
	 * dynamicCollisions RUNNING state.
	 */
	private DynamicWorld world;

	/**
	 * The height property in meters associated to the world's properties.
	 */
	private DoubleProperty height;

	/**
	 * The width property in meters associated to the world's properties.
	 */
	private DoubleProperty width;

	/**
	 * The level of the game. All the items allowed to be used by the user are
	 * contained in the level's inventory.
	 */
	private Level level;

	/**
	 * All the components put together. The game has to handle the handle the
	 * collision of those objects together, but also with the fixed objects.
	 * However, fixed and components should have different listeners on their
	 * view, and are therefore stored separately.
	 */
	private List<ObservableWrapper> gameComponents;

	/**
	 * All the fixed objects that are contained in the Level's list
	 */
	private List<ObservableWrapper> fixedObjects;

	/**
	 * The position of all the gameComponents is stored in this list, and is
	 * updated when the method {@link #setStatus(Status)} is called with a
	 * RUNNING parameter. This list is used only when
	 */
	private List<Object[]> positionSave;

	private double orientationSave;

	/**
	 * The position saver for the object dragged in the view. Used when calling
	 * {@link #setDraggedWrapper(ObservableWrapper)}. The dragged position s
	 * saved in this vector.
	 */
	private Vector2 draggedPosSave;

	/**
	 * The actual dragged wrapper in the view.
	 */
	private ObservableWrapper dragged;

	/**
	 * The list of coin that is iterated to know if the game is over or not.
	 */
	private List<Coin> coinList;

	/**
	 * The pot of gold list that is iterated to know if the game is over
	 */
	private List<PotOfGold> potList;
	/**
	 * The factory for game collision handling. This factory sets the general
	 * view states for Static and Dynamic Collisions, but also handle sound of
	 * collisions.
	 */
	private GameCollideHandling handler;

	/**
	 * The current status of the game.
	 */
	private ObjectProperty<Status> status;

	/**
	 * The actual game's bounds
	 */
	private ObjectProperty<BorderType> bounds;

	/**
	 * An unmodifiable Inventory of all the objects allowed when a Level is
	 * being built.
	 * 
	 * This list contains COIN, SURFACE and POT_OF_GOLD Observables, all of them
	 * represented with an INFINITE quantity
	 */
	private Inventory BUILDING_INVENTORY;

	/**
	 * The game's factory, used to get fixed objects observables on
	 * {@link #setLevel(Level)}.
	 */
	private ObservableObjectFactory factory;

	/**
	 * The resetable service, whose job is simply to watch for Coin mass's that
	 * equals infinity, and also if they are all in collision with pot of gold.
	 */
	private Service<Void> endOfGameWatcher;

	/**
	 * Property that simply tells if the game has been win, or not by the user.
	 * The game is win when all the coin have reach at least one pot of gold.
	 */
	private BooleanProperty hasWon;

	/**
	 * Property that tells if the game is over or not. This property will be
	 * true only if {@link #hasWon} is set to true, or if all the coin have a
	 * null velocity.
	 */
	private BooleanProperty gameOver;

	/**
	 * Default constructor of a GoldbergGame.
	 * 
	 * This class provides dataStructures that allows to run the goldBerg game
	 * properly.
	 * 
	 * 
	 */
	public GoldbergGame() {

		// Instantiate the properties
		this.height = new SimpleDoubleProperty(50);
		this.width = new SimpleDoubleProperty(50);

		// The Default status is PREPARING
		this.status = new SimpleObjectProperty<GoldbergGame.Status>(Status.PREPARING);
		this.bounds = new SimpleObjectProperty<BorderType>(BorderType.NORMAL);

		// Instantiate the world
		this.world = new DynamicWorld(height.add(0), width.add(0));
		world.start();
		world.setDynamic(false);
		world.setAllBound(new Bounds[] {});
		world.setBorderFriction(BORDER_FRICTION);

		// The factory to generate the objects
		this.factory = new ObservableObjectFactory();

		// All the properties and lists for the game
		this.gameComponents = new ArrayList<>();
		this.positionSave = new ArrayList<>();
		this.fixedObjects = new ArrayList<>();
		this.coinList = new ArrayList<>();
		this.potList = new ArrayList<>();
		this.handler = new GameCollideHandling();
		this.hasWon = new SimpleBooleanProperty();
		this.gameOver = new SimpleBooleanProperty();

		init();
	}

	/**
	 * Used to get the allowed building inventory. Every time this method is
	 * called, an empty inventory in created, to ensure the values stay the
	 * same, whatever the number of times the
	 * {@link Inventory #pickItem(Observable)} is called over the inventory.
	 * 
	 * @return An unmodifiable inventory with infinite quantities of desired
	 *         objects
	 */
	public Inventory getBuildingInventory() {
		// Instantiate the unmodifiable building inventory with all its objects
		BUILDING_INVENTORY = new Inventory();
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.COIN, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.SURFACE, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.POT_OF_GOLD, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.STICKWALL, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.BASCULE, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.STICK_BASCULE, Quantity.INFINITE));
		BUILDING_INVENTORY.addItem(new InventoryItem(Observable.DOMINO, Quantity.INFINITE));

		BUILDING_INVENTORY.makeUnmodifiable();

		return BUILDING_INVENTORY;
	}

	/**
	 * Variables to help the thread handling verification.
	 */
	private boolean hasGameWon;
	private long startTime;
	private boolean isCoinMoving;

	private void init() {
		endOfGameWatcher = new Service<Void>() {

			@Override
			protected Task<Void> createTask() {

				return new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						long actual = 0;
						while (!Thread.interrupted()) {

							if ((actual = ((System.currentTimeMillis() - startTime) / 1000)) >= 2) {

								// Tells if the player has won
								hasGameWon = true;

								// Tells if at least 1 coin is moving
								isCoinMoving = false;

								for (Coin coin : coinList) {
									/*
									 * Block checking for player won
									 */
									// for this coin, tells if it won.
									boolean hasCoinReach = false;

									// we go over every pot of gold. If the coin
									// collides with any of them, the player has
									// won for this coin
									for (PotOfGold pot : potList) {
										if (pot.getTranslate().equals(coin.getTranslate())) {
											hasCoinReach = true;
											break;
										}
									}
									// If at least one coin did not collide, the
									// player did not win
									if (!hasCoinReach) {
										hasGameWon = false;
									}

									isCoinMoving = true;

									/*
									 * Block checking for player lost. We only
									 * check the lost of the player if he has
									 * not win yet for this coin.
									 */
									if (!hasCoinReach) {
										// If the coin is not dynamic, meaning
										// it
										// has an
										// infinite mass, the player lost. It
										// would
										// have stick on a stickwall
										if (!coin.getBodies().get(0).isDynamic()) {
											Platform.runLater(() -> gameOver.set(true));
											cancel();
											// In this case, the velocity of the
											// coin is too
										}

									}
								}
								if (hasGameWon) {
									// If the value changed, we tell the game it
									// is
									// over, because every coin collided with a
									// pot
									// of gold
									Platform.runLater(() -> {

										GoldbergGame.this.hasWon.set(hasGameWon);
										GoldbergGame.this.gameOver.set(true);
									});
									this.cancel();
								} else if (!isCoinMoving && actual >= 8) {
									GoldbergGame.this.hasWon.set(false);
									// If all coins are not moving over the 10
									// seconds.
									Platform.runLater(() -> GoldbergGame.this.gameOver.set(true));
									this.cancel();
								}
							}
							Thread.sleep(1000);
						}
						return null;

					}
				};
			}
		};

	}

	/**
	 * Used to get the actual level's inventory
	 * 
	 * @return The actual level's inventory.
	 */
	public Inventory getAllowedInventory() {
		return this.level == null ? null : level.getInventory();
	}

	/**
	 * Sets the current level to the one set in parameter. Once the level is
	 * changed, the old wrappers are cleared from the game, and the new ones are
	 * placed. The new wrappers can be accessed via {@link #getWrappers()}.
	 * 
	 * This method is going to perform wrappers generation for all the new fixed
	 * objects. Therefore, the execution time is proportional to the quantity of
	 * objects contained in the fixedObject list.
	 * 
	 * @param level
	 *            The level to be set.
	 */
	public void setLevel(Level level) {
		if (getStatus() != Status.RUNNING) {

			// clears the list of wrapper
			clear();

			// Sets the level properties to the game's
			this.level = level;
			this.bounds.set(level.getBorders());

			switch (bounds.get()) {

			case NORMAL:
				world.setAllBound(Bounds.values());
				break;
			case TELEPORTABLE:
				world.setBoundsCrossedTeleportation(true);
				break;
			}

			// Creates the wrappers associated to the level
			final List<AbstractComplexObservable> levelFixed = level.getFixedObject();

			for (AbstractComplexObservable observable : levelFixed) {
				ObservableWrapper wrapper = factory.getWrapperInstance(observable);

				fixedObjects.add(wrapper);
				handler.applyEvents(wrapper);
				world.addAllComplexObjects(wrapper.observable);

				/*
				 * Add the object to a predefined list if necessary
				 */
				if (wrapper.instance == Observable.POT_OF_GOLD) {
					potList.add((PotOfGold) wrapper.observable);
				} else if (wrapper.instance == Observable.COIN) {
					coinList.add((Coin) wrapper.observable);
				}

			}

			this.width.set(level.getWidth());
			this.height.set(level.getHeight());
			this.world.setGravity(-level.getPlanet().getGravity());

		} else {
			throw new IllegalStateException("Cannot charge a new level in RUNNING status");
		}

	}

	/**
	 * Tells if the world can build and accept the current level has an
	 * acceptable level for the game. If the level doesn't have what it requires
	 * to work, it will be impossible to start the game.
	 * 
	 * To work, the game must, at least, contain 1 pot of gold and 1 coin,
	 * however the game will never end.
	 * 
	 * Therefore, the game also require a level to build.
	 */
	public boolean canBuild() {
		if (level == null) {
			return false;
		}
		if (potList.isEmpty() || coinList.isEmpty()) {
			return false;
		}
		return !stillHaveCollisions();
	}

	/**
	 * Iterates over every collision pair contained in the world to know if
	 * there is still some collisions in the world. If any collision type is
	 * detected in the world, except for items colliding with themselves, true
	 * is returned.
	 * 
	 * @return True if the world still have some sort of collision in it.
	 */
	public boolean stillHaveCollisions() {

		final Set<CollidingPair> collisions = world.getColliders();

		for (CollidingPair pair : collisions) {
			if (pair.obj1 != pair.obj2 && pair.stillCollides()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return True if the game's level contains Pots Of Gold, false otherwise
	 */
	public boolean hasPots() {
		return !potList.isEmpty();
	}

	/**
	 * 
	 * @return True if the game's level contains Coins, false otherwise
	 */
	public boolean hasCoins() {
		return !coinList.isEmpty();
	}

	/**
	 * Returns the wrappers of the {@link #gameComponents} objects. The fixed
	 * objects of the level are not contained in the data structure returned by
	 * this method.
	 * 
	 * @return The game components of the game.
	 */
	public List<ObservableWrapper> getWrappers() {
		return gameComponents;
	}

	/**
	 * Returns the fixed object wrappers of the game, containing the view, the
	 * controller, the instance and the observable.
	 * 
	 * This method should be called after {@link #setLevel(Level)} is called, to
	 * update the view's content according to the new level.
	 * 
	 * This data structure doesn't contain the game objects that are not fixed.
	 * The {@link #getWrappers()} method should be called instead.
	 */
	public List<ObservableWrapper> getFixeedWrappers() {
		return fixedObjects;
	}

	/**
	 * Returns the height property of the game. This height is set in meters.
	 * 
	 * @return The height of the world as a property.
	 */
	public final ReadOnlyDoubleProperty heightProperty() {
		return this.height;
	}

	/**
	 * 
	 * @return The height of the world in meter
	 */
	public final double getHeight() {
		return this.heightProperty().get();
	}

	/**
	 * 
	 * @return The width of the world as a property in meters.
	 */
	public final ReadOnlyDoubleProperty widthProperty() {
		return this.width;
	}

	/**
	 * 
	 * @return The width of the world in meter
	 */
	public final double getWidth() {
		return this.widthProperty().get();
	}

	/**
	 * Sets the update ratio of the Dynamic world
	 * 
	 * @param ratio
	 *            The new ratio.
	 * @deprecated Because the update ratio modification might occur in strange
	 *             behavior.
	 */
	@Deprecated
	public void setSpeedUpdateRatio(double ratio) {
		if (ratio >= 0.25 && ratio <= 2) {
			world.setUpdateRatio(ratio);
		}
	}

	/**
	 * Adds the specified object as a game object. Therefore, collisions will be
	 * handled on this object as a gameComponent, not as a fixed Object.
	 * 
	 * This method is only allowed to be called when the game is in the
	 * PREPARING state.
	 * 
	 * If the game is in BUILDING state, call
	 * {@link #addFixedObject(ObservableWrapper)} instead of this method.
	 * 
	 * @param wrapper
	 *            The wrapper to be added as a game component.
	 */
	public void addGameObject(ObservableWrapper wrapper) {
		/*
		 * Add the object to a predefined list if necessary
		 */
		if (wrapper.instance == Observable.POT_OF_GOLD) {
			throw new IllegalArgumentException("Pot of gold should be added as fixed objects");
		} else if (wrapper.instance == Observable.COIN) {
			throw new IllegalArgumentException("Coins should be added as fixed objects");
		}

		if (status.get() == Status.PREPARING) {
			world.addAllComplexObjects(wrapper.observable);
			handler.applyEvents(wrapper);
			this.gameComponents.add(wrapper);

		} else {
			throw new IllegalStateException("The game should be in the PREPARING state");
		}
	}

	/**
	 * Returns the current status of the game.
	 * 
	 * <p>
	 * BUILDING state means that the user is currently building the game. The
	 * inventory allowed should therefore be the BUILDING_INVENTORY. It is the
	 * mode used to build a world with the desired fixed objects. At this point,
	 * no collision detection is performed between surfaces, but they are
	 * performed between Coin and surfaces, to avoid wrong contacts.
	 * </p>
	 * 
	 * <p>
	 * PREPARING state represents the pre-running state, and it is where the
	 * user can place the Level's inventory object. The user will have the
	 * possibility to place every AbstractComplexObservable by himself in the
	 * world, and then switch to the running state, where no moving should be
	 * allowed, and the game simply animates, starting the winning thread. At
	 * this point, every collision detection are static, which means the
	 * handleStaticCollide method of those observables will be called when
	 * necessary.
	 * </p>
	 * 
	 * <p>
	 * RUNNING state is called when the DynamicWorld is wished to set to dynamic
	 * state. When the game is set to this mode, no object should normally be
	 * moved. The game simply run itself, and the dynamic collisions are called
	 * on objects.
	 * </p>
	 * 
	 * @return The current status of the game.
	 */
	public Status getStatus() {
		return status.get();
	}

	/**
	 * 
	 * @return The status of the game as a read only property
	 * 
	 * @see #getStatus()
	 */
	public ReadOnlyObjectProperty<Status> statusProperty() {
		return status;
	}

	/**
	 * Returns the actual bounds of the level. Normal bounds are simply walls,
	 * as specified in {@link DynamicWorld #setAllBound(Bounds...)}
	 * 
	 * For Crossed teleportable bounds, the objects will be teleported from one
	 * bound to the other. See {@link DynamicWorld #isBoundsCrossedTeleported()}
	 * for more detail.
	 * 
	 * @see {@link DynamicWorld}
	 * 
	 * @return The actual bounds contained in the level. The collision detection
	 *         with bounds will depend on this parameter
	 */
	public BorderType getBounds() {
		return bounds.get();
	}

	/**
	 * Returns the bounds of the world as a Read only property.
	 * 
	 * @see #getBounds()
	 * 
	 * @return The bounds of the world as a property
	 */
	public ReadOnlyObjectProperty<BorderType> boundsProperty() {
		return bounds;
	}

	/**
	 * The boolean property telling either the game is finished or not.
	 * 
	 * @return A property that tells if the game is over or not.
	 */
	public ReadOnlyBooleanProperty isOverProperty() {
		return gameOver;
	}

	/**
	 * Used to know if the player has won the game. Normally, this property will
	 * not be to true until {@link #isOverProperty()} is set to true.
	 * 
	 * @return The winning value of the game as a property.
	 */
	public boolean hasWon() {
		return hasWon.get();
	}

	/**
	 * Resets all the gameComponents position to the position they were before
	 * the {@link #setStatus(Status)} gets called with a RUNNING parameter.
	 * 
	 * This method can only be called if the game is in the RUNNING state, and
	 * will be set to the PREPARING state after it has been called.
	 */
	public void reset() {
		if (status.get() == Status.RUNNING) {

			setStatus(Status.PREPARING);

			while(world.isDynamic()){
				
			}

			// Shows the 2 iterators. We iterate both at the same time
			ListIterator<ObservableWrapper> wrappers = fixedObjects.listIterator();
			ListIterator<Object[]> positions = positionSave.listIterator();

			ObservableWrapper object = null;
			Object[] position = null;

			// First iterate the game components, and remove useless joints
			// for
			// reseting.
			for (int i = 0; i < gameComponents.size(); i++) {
				object = gameComponents.get(i);
				position = positions.next();

				// If the object applied a joint to any sort of object, the
				// joint is removed. This will free any object movement.
				if (object.observable instanceof JointApplier) {
					((JointApplier) object.observable).removeAppliedJoint();
				}

				// Then reset position and orientation
				object.observable.translate(((Vector2) position[0]).x, ((Vector2) position[0]).y);
				object.observable.rotate((Double) position[1]);
			}

			// The two lists have the same size
			while (wrappers.hasNext()) {
				object = wrappers.next();
				position = positions.next();

				// If the object applied a joint to any sort of object, the
				// joint is removed
				if (object.observable instanceof JointApplier) {
					((JointApplier) object.observable).removeAppliedJoint();
				}

				// The coin is set to a normal mass if changed, and will
				// translate freely again
				if (object.instance == Observable.COIN) {
					object.observable.getBodies().get(0).setMass(MassType.NORMAL);
					object.observable.getBodies().get(0).setMass(Coin.COIN_MASS);
				}

				// Then reset position and orientation
				object.observable.translate(((Vector2) position[0]).x, ((Vector2) position[0]).y);
				object.observable.rotate((Double) position[1]);

			}

		} else {
			throw new IllegalStateException("Cannot reset the game. Status must be RUNNING");
		}
	}

	/**
	 * Stops the game update of the game. The status of the game is not changed,
	 * and will remain the same if {@link #start()} is called. However, every
	 * update thread is stopped. This method should be called to pause the game,
	 * or when the game's view is exited.
	 */
	public void stop() {
		world.pause();
		this.endOfGameWatcher.cancel();
	}

	/**
	 * Starts the game back on. If the game was in the RUNNING state, the
	 * {@link #endOfGameWatcher} thread is started again. This should be called
	 * to resume the game after it has been pause via {@link #stop()}.
	 */
	public void start() {
		world.start();
		if (status.get() == Status.RUNNING) {
			this.endOfGameWatcher.restart();
		}
	}

	/**
	 * Sets the game status.
	 * 
	 * 
	 * 
	 * <p>
	 * The state BUILDING represents a state where objects must be added via
	 * {@link #addFixedObject(ObservableWrapper)}. This is where the fixed
	 * objects are added to the level and the world as at the same time, to
	 * allow collision detection. If collision is detected, the object will be
	 * notified directly via an event given in {@link #handler}. When the object
	 * will be dragged, it is important to call
	 * {@link #setDraggedWrapper(ObservableWrapper)} and {@link #dropWrapper()}
	 * when this object will be dropped, so collision detection and replacing
	 * will be handled.
	 * </p>
	 * 
	 * <p>
	 * Same as the BUILDING state, PREPARING state is used to handle static
	 * collisions between objects. Call the same method when a wrapper is
	 * dragged and dropped. The only difference is when objects are added. The
	 * objects added will not be contained in the level, and must be added via
	 * {@link #addGameObject(ObservableWrapper)}, so only collision will be
	 * handled when the game will switch to RUNNING state.
	 * 
	 * </p>
	 * 
	 * <p>
	 * For the RUNNING state, this method will throw
	 * {@link IllegalStateException} if the value returned by
	 * {@link #canBuild()} is false. This state will start collision detection
	 * in a dynamic way. Therefore, gravity will apply. After this method is
	 * called, no object should be dragged or dropped and moved.
	 * </p>
	 * 
	 * @param state
	 */
	public void setStatus(Status state) {
		if (state != getStatus()) {

			switch (state) {
			case BUILDING:
				this.gameOver.set(false);
				this.hasWon.set(false);
				gameComponents.clear();
				world.setDynamic(false);
				this.endOfGameWatcher.cancel();
				break;
			case PREPARING:
				this.gameOver.set(false);
				this.hasWon.set(false);
				world.setDynamic(false);
				this.endOfGameWatcher.cancel();

				break;
			case RUNNING:
				if (canBuild()) {
					this.gameOver.set(false);
					this.hasWon.set(false);
					savePositions();
					this.startTime = System.currentTimeMillis();
					run();
				} else {
					throw new IllegalStateException("Cannot build the world.");
				}
				break;

			}
			this.status.set(state);

		}
	}

	/**
	 * Runs the world in a dynamic way and starts {@link #endOfGameWatcher}
	 * thread.
	 */
	private void run() {
		world.setDynamic(true);
		endOfGameWatcher.restart();
	}

	/**
	 * Saves every objects positions. They are saved as arrays of objects, with
	 * the first parameter set as a {@link Vector2} which is the position, and
	 * the second is a Double representing the orientation. They are saved in
	 * {@link #positionSave}. The first object to be saved are the game
	 * components from {@link #gameComponents}, and then the objects from
	 * {@link #fixedObjects}. They should be replaced the same when replaced in
	 * {@link #reset()}.
	 */
	private void savePositions() {
		// removes old positions
		positionSave.clear();

		// save the game components objects and orientation
		for (ObservableWrapper wrapper : gameComponents) {
			// Save the translate
			positionSave
					.add(new Object[] { wrapper.observable.getTranslate(), (Double) wrapper.observable.getRotate() });
		}
		// save the fixed objects
		for (ObservableWrapper wrapper : fixedObjects) {
			// Save the translate
			positionSave
					.add(new Object[] { wrapper.observable.getTranslate(), (Double) wrapper.observable.getRotate() });
		}

	}

	/**
	 * Adds the fixed object sent in parameter. This method is only allowed to
	 * be called in the BUILDING state.
	 * 
	 * @see #getStatus() for more info about status.
	 * 
	 * @param wrapper
	 *            The wrapper that has to be added.
	 */
	public void addFixedObject(ObservableWrapper wrapper) {
		if (status.get() == Status.BUILDING) {

			fixedObjects.add(wrapper);
			handler.applyEvents(wrapper);
			world.addAllComplexObjects(wrapper.observable);

			/*
			 * Add the object to a predefined list if necessary
			 */
			if (wrapper.instance == Observable.POT_OF_GOLD) {
				potList.add((PotOfGold) wrapper.observable);
			} else if (wrapper.instance == Observable.COIN) {
				coinList.add((Coin) wrapper.observable);
			}

		} else {
			throw new IllegalStateException("The game should be in the BUILDING state");
		}
	}

	/**
	 * Removes the fixed object from everywhere. This method is only allowed to
	 * be called in the BUILDING state.
	 * 
	 * @see #getStatus() for more info about status.
	 * 
	 * @param wrapper
	 *            The wrapper that must be removed.
	 */
	public void removeFixedObject(ObservableWrapper wrapper) {
		if (status.get() == Status.BUILDING) {
			world.removeComplexObject(wrapper.observable);
			level.removeFixedObject(wrapper.observable);
			if (wrapper.instance == Observable.COIN) {
				coinList.remove(wrapper.observable);
			} else if (wrapper.instance == Observable.POT_OF_GOLD) {
				potList.remove(wrapper.observable);
			}
		} else {
			throw new IllegalStateException("The game should be in the BUILDING state");
		}
	}

	/**
	 * Sets the currently dragged wrapper. Closely related to
	 * {@link #dropWrapper()}. This set of method will be used to save the
	 * object's position when the drag is started. If this object still collides
	 * when {@link #dropWrapper()}, it will be replaced at its original position
	 * which was saved when this method was called.
	 * 
	 * @param wrapper
	 *            The dragged wrapper on which drag gesture started.
	 */
	public void setDraggedWrapper(ObservableWrapper wrapper) {
		if (getStatus() != Status.RUNNING) {
			this.draggedPosSave = wrapper.observable.getTranslate();
			this.orientationSave = wrapper.observable.getRotate();
			this.dragged = wrapper;
		} else {
			throw new IllegalStateException("The game should be in the BUILDING or PREPARING state");
		}
	}

	/**
	 * Drops the wrapper that has been set using
	 * {@link #setDraggedWrapper(ObservableWrapper)}. If the wrapper set at this
	 * moment still collides, it will be automatically replaced to its position
	 * saved before.
	 */
	public void dropWrapper() {
		if (getStatus() != Status.RUNNING) {
			if (stillCollides(dragged)) {
				// Move the object to the old position
				dragged.observable.translate(draggedPosSave.x, draggedPosSave.y);
				dragged.observable.rotate(orientationSave);
			}
		} else {
			throw new IllegalStateException("The game should be in the BUILDING or PREPARING state");
		}
	}

	/**
	 * Iterates over the observable wrappers to know if the object still
	 * collides with any other object.
	 * 
	 * @param wrapper
	 *            The wrapper on which verification must be handled.
	 * 
	 * @return True if a collision is occurring, false otherwise.
	 */
	private boolean stillCollides(ObservableWrapper wrapper) {

		final Set<CollidingPair> colliders = world.getColliders();

		synchronized (colliders) {

			for (CollidingPair pair : colliders) {
				if ((pair.obj1 == wrapper.observable || pair.obj2 == wrapper.observable) && pair.obj1 != pair.obj2) {
					return true;
				}
			}

		}

		return false;
	}

	/**
	 * Returns true if collisions are being detected in a dynamic way. The game
	 * will be in the RUNNING state if this method returns true.
	 * 
	 * @return True if the game is running, false otherwise
	 */
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return world.isRunning();
	}

	/**
	 * Clears the current level, all the game wrappers and the fixed object.
	 * After this method is called, no objects will be contained in any world of
	 * the engine.
	 */
	public void clear() {
		this.world.clearObjects();
		this.fixedObjects.clear();
		this.gameComponents.clear();
		this.potList.clear();
		this.coinList.clear();
	}

	/**
	 * Removes the game object sent in parameter.This method will throw an
	 * {@link IllegalStateException} if the game is not in the PREPARING state.
	 * 
	 * @see #setStatus(Status) for more info about status.
	 * 
	 * @param clickedWrapper
	 */
	public void removeGameObject(ObservableWrapper clickedWrapper) {
		if (getStatus() == Status.PREPARING) {
			this.gameComponents.remove(clickedWrapper);
			this.world.removeComplexObject(clickedWrapper.observable);
		} else {
			throw new IllegalStateException("The game should be in the PREPARING state");
		}

	}
}
