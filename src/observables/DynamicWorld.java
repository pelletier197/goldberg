package observables;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.collision.BoundsListener;
import org.dyn4j.collision.Collidable;
import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Settings;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.contact.ContactListener;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.dynamics.contact.PersistedContactPoint;
import org.dyn4j.dynamics.contact.SolvedContactPoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Vector2;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DynamicWorld implements ContactListener, BoundsListener {
	/**
	 * The available bounds for the world.
	 * 
	 * @author sunny
	 *
	 */
	public enum Bounds {
		TOP, RIGHT, BOTTOM, LEFT;
	}

	/**
	 * The width of the bounds. This value must be big, so the objects will
	 * never cross them wherever they are in the world.
	 */
	public static final double BOUNDS_WIDTH = 6.24323;

	/**
	 * The bound that are currently displayed.
	 */
	private Bounds[] actualBounds;

	/**
	 * The updater for the world and the views.
	 */
	private AnimationTimer updater;

	/**
	 * The collision environment
	 */
	private World world;

	/**
	 * This boolean is true when collision are enabled.
	 */
	private boolean isDynamic;

	/**
	 * Tells when the world is running. The world is running if it's being
	 * updated in any static or dynamic way.
	 */
	private boolean isRunning;

	/**
	 * The list of complex objects contained in this world
	 */
	private ObservableList<AbstractComplexObservable> complexList;

	/**
	 * The set of CollidingPairs this object contains. EveryTime a collision is
	 * handled by the world, the 2 objects are added to this set, then removed
	 * at the end of the process.
	 */
	private TreeSet<CollidingPair> colliders;

	/**
	 * The world's constraints, to avoid node escaping. Those will usually have
	 * small width and big height, so they can't be crossed. Their views will
	 * usually be set to invisible, but this can be changed by calling
	 * #setBoundsVisible
	 */
	private Surface top, right, bottom, left;

	/**
	 * {@link #boundsReflexion} is a value that tells if the bounds are
	 * reflective or not. A bounds reflection set to true means that the object
	 * going out of the bounds will be "teleported" at the opposite bound.
	 * 
	 * For example, an object that would completely disappear behind the right
	 * edge of the window would be replaced at the left edge of the window.
	 */
	private BooleanProperty boundsCrossedTeleportation;

	/**
	 * The height and the width of the world.
	 */
	private DoubleBinding height, width;

	/**
	 * The property of the gravity
	 */
	private DoubleProperty gravity;

	/**
	 * The update ratio speed. A value of 1 represent a regular movement, which
	 * is a normal-time speed. If 0<value<1, the update will be slower than a
	 * real time speed.
	 * 
	 * If value > 1, then the speed update is faster than real time speed.
	 * 
	 * 1 is the default value
	 */
	private double updateRatio = 1;

	/**
	 * The friction coefficient of the borders of the world.
	 */
	private double fricCoef = 0;

	/**
	 * Creates a default DynamicWorld. The height and the width are obligated
	 * parameters to ensure application running.By default, the
	 * {@link #boundsCrossedTeleportation} and {@link #isBounded} parameters are
	 * false. The {@link #world} contains no bodies. The {@link #gravity} is set
	 * to Earth gravity.
	 * 
	 * Note : the bounds should not be the graphical view necessarily. Those are
	 * the bounds given to the world. As a start value. Those should not be
	 * modified while the application is running.
	 * 
	 * @param height
	 *            The heightProperty of the world in meters represented as a
	 *            DoubleBinding
	 * @param width
	 *            The widthProperty of the world in meters represented as a
	 *            DoubleBinding
	 * 
	 */
	public DynamicWorld(DoubleBinding height, DoubleBinding width) {
		this(height, width, World.EARTH_GRAVITY.y);
	}

	/**
	 * Creates a default DynamicWorld. The height and the width are obligated
	 * parameters to ensure application running. By default, the
	 * {@link #boundsCrossedTeleportation} and {@link #isBounded} parameters are
	 * false. The {@link #world} contains no bodies.
	 * 
	 * @param height
	 *            The heightProperty of the world in meters represented as a
	 *            DoubleBinding
	 * @param width
	 *            The widthProperty of the world in meters represented as a
	 *            DoubleBinding
	 * @param gravity
	 *            The gravity of the world in m/s². WARNING : a positive gravity
	 *            would tend to make the objects go up. A gravity should always
	 *            be a negative number.
	 */
	public DynamicWorld(DoubleBinding height, DoubleBinding width, double gravity) {
		super();

		this.height = height;
		this.width = width;

		// The gravity
		this.gravity = new SimpleDoubleProperty(gravity);

		// Instantiate the list of objects
		this.complexList = FXCollections.observableArrayList();
		this.colliders = new TreeSet<>();

		// By default, the world is not dynamic
		this.isDynamic = false;

		// default values
		this.actualBounds = new Bounds[4];
		this.boundsCrossedTeleportation = new SimpleBooleanProperty(false);

		height.addListener((value, old, newv) -> {
			computeBounds();
			generatePhysicalBounds();
		});
		width.addListener((value, old, newv) -> {
			computeBounds();
			generatePhysicalBounds();
		});

		initializeWorld();
	}

	/**
	 * Compute the value of the bounds depending on the value of height and with
	 * property. Those bounds are added to the world as a listener value. An
	 * object going out of the bounds will be informed via the
	 * {@link #outside(Collidable)} method from the boundsListener interface.
	 * Those bounds should be computed everyTime that the height and width are
	 * changed.
	 */
	private void computeBounds() {

		// Typically, the bounds are only used if the object is bound crossed
		// teleportation
		if (isBoundsCrossedTeleported()) {

			AxisAlignedBounds newBounds = null;

			// Take the positive value of the bounds. Can happen to be negative
			// in
			// some applications.
			double height = Math.abs(this.height.get());
			double width = Math.abs(this.width.get());

			try {
				newBounds = new AxisAlignedBounds(width, height);

				// The bounds are usually centered on the 0,0 axis. We translate
				// it
				// to make it only visible form positive axis.
				newBounds.translate(width / 2, height / 2);

			} catch (Exception e) {

			}

			world.setBounds(newBounds);

			// If object that were not in the bounds are now in the bounds, it
			// is
			// because the new bounds are bigger than the old ones. We therefore
			// set
			// them as active object.
			for (AbstractComplexObservable object : complexList) {
				object.setMobile(true);
			}
		} else {
			world.setBounds(null);
		}
	}

	/**
	 * Initialize the world. The {@link #updater} is instantiated. The
	 * {@link #updater} thread's job is to ensure that the {@link #world}
	 * constantly evolves with the time that it receives in parameter. It will
	 * also ensures that the object from {@link #objectList} are updated by
	 * calling updateView() method from all the objects. During that update, the
	 * objects will be replaced if needed, depending on the values of
	 * {@link #isBounded} and {@link #boundsCrossedTeleportation}.
	 * 
	 * The world is also created, with the gravity and the ContactListener
	 * (this). Creates the updater of the world. It iterates over every body in
	 * the world and update it's properties by calling the update() method from
	 * the AbstractObservableObject interface.
	 */
	private void initializeWorld() {
		// Sets the world gravity and add the current object as a listener of
		// contacts
		this.world = new World();
		world.setGravity(new Vector2(0, getGravity()));
		world.addListener(this);
		world.getSettings().setAutoSleepingEnabled(false);

		/*
		 * Creates the updater of the world. It iterates over every body in the
		 * world and update it's properties by calling the update method from
		 **/
		this.updater = new AnimationTimer() {

			private final LongProperty lastUpdate = new SimpleLongProperty(0);
			private long ellapsed = 0;
			boolean iterationDynamism = false;

			@Override
			public void handle(long now) {

				iterationDynamism = isDynamic;

				// retrieve time ellapsed
				ellapsed = now - lastUpdate.get();
				lastUpdate.set(now);
				world.update(ellapsed);

				// Update the objects
				final ListIterator<AbstractComplexObservable> it = complexList.listIterator();
				AbstractComplexObservable obj = null;

				while (it.hasNext()) {
					obj = it.next();
					obj.update();

					if (!iterationDynamism) {
						obj.setMobile(false);
					}
				}

				if (!iterationDynamism) {
					// Has collisions are not handled in a static world,we have
					// to verify manually that they have stopped
					Iterator<CollidingPair> sit = colliders.iterator();
					CollidingPair pair = null;
					while (sit.hasNext()) {
						pair = sit.next();
						if (!pair.stillCollides()) {
							pair.obj1.handleStaticCollideEnds(null, pair.obj2, null, world);
							pair.obj2.handleStaticCollideEnds(null, pair.obj1, null, world);

							sit.remove();
						}
					}
				}

			}

		};

	}

	/**
	 * Adds the complex object to the world. This object will instantly be part
	 * of the updated world.
	 * 
	 * The object is initialized via the {@link #AbstractComplexObservable}
	 * interface.
	 * 
	 * @param object
	 *            The AbstractComplexObject added to the world.
	 */
	public synchronized void addComplexObject(AbstractComplexObservable object) {
		object.initializeObject(world);
		complexList.add(object);
	}

	/**
	 * Add all the complex objects sent in parameter. Those object will be added
	 * to the world instantly.
	 * 
	 * The object are initialized via the {@link #AbstractComplexObservable}
	 * interface.
	 * 
	 * @param objects
	 *            The objects added to the world.
	 */
	public synchronized void addAllComplexObjects(AbstractComplexObservable... objects) {

		final int size = objects.length;

		for (int i = 0; i < size; i++) {
			addComplexObject(objects[i]);
		}
	}

	/**
	 * Returns the value of the world bounded value. Check all the bounds from
	 * {@link #actualBounds} and returns true if one of the bounds is active.
	 * 
	 * @return True if the world is bounded, false otherwise.
	 */
	public final boolean isBounded() {
		return actualBounds[0] != null || actualBounds[1] != null || actualBounds[2] != null || actualBounds[3] != null;
	}

	/**
	 * Add the specified bound to the world. If the bound already exists, no
	 * change is performed.
	 * 
	 * @param bound
	 */
	public final void addBounds(Bounds bound) {

		final int size = actualBounds.length;

		// Check if the bound is not already contained.
		for (int i = 0; i < size; i++) {
			if (actualBounds[i] == bound) {
				// If yes, no action is performed.
				return;
			}
		}

		// add the bound on a null position
		for (int i = 0; i < size; i++) {
			if (actualBounds[i] == null) {
				actualBounds[i] = bound;
				break;
			}
		}
		generatePhysicalBounds();
	}

	/**
	 * Set the actual bounds of the world for the one in parameters.
	 * 
	 * @param bounds
	 *            The new bounds of the world. If null, all bounds are removed.
	 */
	public final void setAllBound(Bounds... bounds) {
		if (bounds == null) {
			setAllBound(new Bounds[] {});
		}

		final int size = actualBounds.length;

		// Iterates over the actual bounds
		for (int i = 0; i < size; i++) {
			// The old value is net to null
			actualBounds[i] = null;
			// If i fits in bounds size, then actualBounds[i] is set to
			// bounds[i].
			if (bounds != null && i < bounds.length) {
				actualBounds[i] = bounds[i];
			}
		}
		generatePhysicalBounds();
	}

	/**
	 * This method will generate physical bounds depending on the
	 * {@link #actualBounds} object. The bounds will first be cleared, so all
	 * old physical bounds will be removed from the {@link #world}.
	 * 
	 * Therefore, if all the objects of {@link #actualBounds} are set to null,
	 * then there will be no new physical bounds.
	 * 
	 * Secondly, if the boolean property of {@link #boundsVisible} is true, the
	 * bounds will be visible with the actual skin of the Surface object.
	 * 
	 * This method should also be called every time that the {@link #width} and
	 * {@link #height} properties are changed.
	 */

	private void generatePhysicalBounds() {

		removeComplexObject(top);
		removeComplexObject(right);
		removeComplexObject(bottom);
		removeComplexObject(left);

		for (int i = 0; i < actualBounds.length; i++) {
			generateOneBound(actualBounds[i]);
		}
	}

	/**
	 * Removes the complex object specified in parameter from the world's update
	 * list.
	 * 
	 * It is also removed from the physic engine.
	 * 
	 * @param object
	 *            The object to be removed.
	 */
	public void removeComplexObject(AbstractComplexObservable object) {
		if (object != null) {
			for (Body b : object.getBodies()) {
				world.removeBody(b);
			}
			for (Joint j : object.getJoints()) {
				world.removeJoint(j);
			}
		}
	}

	/**
	 * This method will generate one physical bound depending on the entry
	 * Bounds(TOP, BOTTOM, RIGHT, LEFT).
	 * 
	 * Therefore, if all the objects of {@link #actualBounds} are set to null,
	 * then there will be no new physical bounds.
	 * 
	 * Secondly, if the boolean property of {@link #boundsVisible} is true, the
	 * bounds will be visible with the actual skin of the Surface object.
	 * 
	 * This method should also be called every time that the {@link #width} and
	 * {@link #height} properties are changed.
	 */

	private void generateOneBound(Bounds type) {

		if (type != null) {
			Double screenWidth = Math.abs(width.get());
			Double screenHeight = Math.abs(height.get());
			Double boundsWidth = BOUNDS_WIDTH;

			if (!screenWidth.isNaN() && !screenHeight.isNaN() && screenWidth > 0 && screenHeight > 0) {

				if (type == Bounds.TOP) {
					top = new Surface(screenWidth, boundsWidth);
					top.translate(screenWidth / 2, screenHeight + BOUNDS_WIDTH / 2);
					addBounds(Bounds.TOP);
					addComplexObject(top);

					// Sets predefined parameters
					top.getBodies().get(0).getFixture(0).setFriction(fricCoef);

				} else if (type == Bounds.BOTTOM) {

					bottom = new Surface(screenWidth, boundsWidth);
					bottom.translate(screenWidth / 2, -BOUNDS_WIDTH / 2);
					addBounds(Bounds.BOTTOM);
					addComplexObject(bottom);

					// Sets predefined parameters
					bottom.getBodies().get(0).getFixture(0).setFriction(fricCoef);

				} else if (type == Bounds.RIGHT) {

					right = new Surface(boundsWidth, screenHeight);
					right.translate(screenWidth + BOUNDS_WIDTH / 2, screenHeight / 2);
					addBounds(Bounds.RIGHT);
					addComplexObject(right);

					// Sets predefined parameters
					right.getBodies().get(0).getFixture(0).setFriction(fricCoef);

				} else if (type == Bounds.LEFT) {

					left = new Surface(boundsWidth, screenHeight);
					left.translate(-BOUNDS_WIDTH / 2, screenHeight / 2);
					addBounds(Bounds.LEFT);
					addComplexObject(left);

					// Sets predefined parameters
					left.getBodies().get(0).getFixture(0).setFriction(fricCoef);

				}
				boundsCrossedTeleportation.set(false);
			}
		}
	}

	/**
	 * Clear the world's bounds. Physical bounds are removed from the world, and
	 * no limits will be applied to the objects.
	 */
	public final void clearBounds() {
		final int size = actualBounds.length;

		for (int i = 0; i < size; i++) {
			actualBounds[i] = null;
		}
		generatePhysicalBounds();
	}

	/**
	 * The boolean property that tells if the bounds are crossedTeleportation.
	 * CrossedTeleportation bounds means that an object that goes completely out
	 * of the world's bounds is moved to the opposite bound.
	 * 
	 * @return {@link #boundsCrossedTeleportationProperty()}
	 */
	public final BooleanProperty boundsCrossedTeleportationProperty() {
		return this.boundsCrossedTeleportation;
	}

	/**
	 * The boolean property that tells if the bounds are crossedTeleportation.
	 * CrossedTeleportation bounds means that an object that goes completely out
	 * of the world's bounds is moved to the opposite bound.
	 * 
	 * @return the value of {@link #boundsCrossedTeleportationProperty()}
	 */
	public final boolean isBoundsCrossedTeleported() {
		return this.boundsCrossedTeleportationProperty().get();
	}

	/**
	 * Sets the value of the {@link #boundsCrossedTeleportationProperty()} to
	 * the parameter.
	 * 
	 * The physical bounds are cleared if this value is true, and are set to
	 * invisible.
	 * 
	 * @param boundsReflection
	 *            The bound crossed teleportation property
	 */
	public final void setBoundsCrossedTeleportation(final boolean boundsReflection) {

		this.boundsCrossedTeleportationProperty().set(boundsReflection);

		// If the bounds are now crossedTeleportation, the bounds are cleared.
		if (boundsReflection) {
			clearBounds();
			computeBounds();
		}
	}

	/**
	 * The world's gravity property.
	 * 
	 * @return {@link #gravity}
	 */
	public final DoubleProperty gravityProperty() {
		return this.gravity;
	}

	/**
	 * 
	 * @return returns the value of {@link #gravity}
	 */
	public final double getGravity() {
		return this.gravityProperty().get();
	}

	/**
	 * Returns an array of the bounds contained in the world.
	 * 
	 * @return An array of the bounds contained in the world.
	 */
	public Bounds[] getBounds() {
		return actualBounds.clone();
	}

	/**
	 * Sets the value of {@link #gravity}
	 * 
	 * @param gravity
	 *            The new gravity in m/s². A negative gravity make objects go
	 *            down.
	 */
	public final void setGravity(final double gravity) {
		this.gravityProperty().set(gravity);

		// sets the world from the dynamic value if objects were asleep, they
		// will be woken.
		setDynamic(isDynamic());
	}

	/**
	 * Starts the world to update.
	 */
	public void start() {
		updater.start();
		isRunning = true;

	}

	/**
	 * Stops the world update
	 */
	public void pause() {
		updater.stop();
		isRunning = false;
	}

	/**
	 * Returns true or false depending on the fact that the world is currently
	 * being updated via {@link #updater} or false if the update thread is
	 * stopped.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * Sets the world as dynamic or not. A dynamic world updates the object in
	 * order to make it dynamic. The collision are real time speed.
	 * 
	 * If the value of {@link #isDynamic()} is false, then the world simply
	 * updates to check for collision, but seems like not moving.
	 * 
	 * @param isDynamic
	 *            True if the world is dynamic, false otherwise.
	 */
	public synchronized void setDynamic(boolean isDynamic) {

		boolean old = isDynamic;
		this.isDynamic = isDynamic;

		if (!isDynamic) {
			world.setGravity(new Vector2(0, 0));
			// Breaks object movement

		} else {
			world.setGravity(new Vector2(0, getGravity()));
		}

		for (AbstractComplexObservable obj : complexList) {
			// if changed to a dynamic world
			if (this.isDynamic && this.isDynamic != old) {
				obj.handleStaticCollideEnds(null, null, null, world);

				// If changed to a static world
			} else if (!this.isDynamic && this.isDynamic != old) {
				obj.handleDynamicCollideEnds(null, null, null, world);

			}

			obj.setMobile(isDynamic);

		}

		// We clear collisions
		colliders.clear();

	}

	/**
	 * Method used to find which observable is contained in this body. This
	 * method is used to find the two object that collided. One of those object
	 * can be null and the method will return an array containing a null value
	 * at the position of the null body.
	 * 
	 * The array returned must be considered this way
	 * [[AbstractComplexObservable1 : from body1],[AbstractComplexObservable2 :
	 * from body2]].
	 * 
	 * @param body1
	 *            The first body
	 * @param body2
	 *            The second body
	 * 
	 * @throws NullPointerException
	 *             if the 2 bodies are null
	 */
	private AbstractComplexObservable[] whichObservable(Body body1, Body body2) {
		if (body1 == null && body2 == null) {
			throw new NullPointerException("The 2 bodies cannot be null");
		}
		AbstractComplexObservable object1 = null;
		AbstractComplexObservable object2 = null;

		if (body1 != null) {
			object1 = (AbstractComplexObservable) body1.getUserData();
		}
		if (body2 != null) {
			object2 = (AbstractComplexObservable) body2.getUserData();
		}

		return new AbstractComplexObservable[] { object1, object2 };

	}

	/**
	 * Returns the AbstractComplexObservable associated to this body.
	 * 
	 * @param body
	 *            The body
	 * @return The AbstractComplexObservable associated to this body.
	 */
	private AbstractComplexObservable whichObservable(Body body) {

		return whichObservable(body, null)[0];

	}

	/**
	 * Override method from ContactListener interface. Allows to the world to
	 * listen world's collision and notify the AbstractObservable that collided,
	 * by iterating over the {@link #complexList}'s elements.
	 * 
	 * if {@link #isDynamic()} value is set to true, then the world will notify
	 * the AbstractComplexObject via the
	 * handleDynamicCollide(AbstractComplexObject,World) method, or via the
	 * handleStaticCollide(AbstractComplexObject,World) if the
	 * {@link #isDynamic()} value is false.
	 */
	@Override
	public boolean begin(ContactPoint point) {

		final Body body1 = point.getBody1();
		final Body body2 = point.getBody2();

		// Find the objects that collided
		AbstractComplexObservable obj1 = null;
		AbstractComplexObservable obj2 = null;

		if (body1 != null && body2 != null) {

			final AbstractComplexObservable[] objects = whichObservable(body1, body2);

			obj1 = objects[0];
			obj2 = objects[1];

			if (obj1 != null && obj2 != null) {

				// Handle collision on both objects
				if (isDynamic()) {

					obj1.handleDynamicCollide(point.getPoint(), obj2, body1, world);
					obj2.handleDynamicCollide(point.getPoint(), obj1, body2, world);

				} else {

					obj1.handleStaticCollide(point.getPoint(), obj2, body1, world);
					obj2.handleStaticCollide(point.getPoint(), obj1, body2, world);

				}
				colliders.add(new CollidingPair(obj1, obj2));
			}
		}

		// if the object collided with itself, the collision must be handled.
		return isDynamic() || (obj1 != null && obj2 != null && obj1 == obj2);
	}

	/**
	 * Tell if the object sent in parameter is still colliding in the world.
	 * 
	 * @returns true if the object still collides, false otherwise. Not that the
	 *          object is considered in collision even if it collides with
	 *          itself.
	 */
	public boolean stillCollide(AbstractComplexObservable object) {

		for (CollidingPair pair : colliders) {
			// If the object is in one of the pairs, true is returned
			if (pair.contains(object)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Override method from ContactListener interface. Allows to the world to
	 * listen world's collision and notify the AbstractObservable that collided,
	 * by iterating over the {@link #complexList}'s elements.
	 * 
	 * if {@link #isDynamic()} value is set to true, then the world will notify
	 * the AbstractComplexObject via the
	 * {@link AbstractComplexObservable#handleDynamicCollideEnds(AbstractComplexObservable, Body, World)}
	 * method, or via the
	 * {@link AbstractComplexObservable #handleStaticCollideEnds(AbstractComplexObservable, Body, World)}
	 * if the {@link #isDynamic()} value is false.
	 */
	@Override
	public void end(ContactPoint point) {

		final Body body1 = point.getBody1();
		final Body body2 = point.getBody2();

		if (!isDynamic) {

			if (body1 != null && body2 != null) {

				// Find the objects that collided
				AbstractComplexObservable obj1 = null;
				AbstractComplexObservable obj2 = null;

				final AbstractComplexObservable[] objects = whichObservable(body1, body2);

				obj1 = objects[0];
				obj2 = objects[1];

				if (obj1 != null && obj2 != null) {

					// Handle collision on both objects
					if (isDynamic()) {

						obj1.handleDynamicCollideEnds(point.getPoint(), obj2, body1, world);
						obj2.handleDynamicCollideEnds(point.getPoint(), obj1, body2, world);

					} else {

						obj1.handleStaticCollideEnds(point.getPoint(), obj2, body1, world);
						obj2.handleStaticCollideEnds(point.getPoint(), obj1, body2, world);

					}
					// We remove the pair from colliders set
					colliders.remove(new CollidingPair(obj1, obj2));
				}
			}
		}

	}

	/**
	 * Never used method.
	 */
	@Override
	public boolean persist(PersistedContactPoint point) {

		return true;
	}

	/**
	 * Never used method.
	 */
	@Override
	public void postSolve(SolvedContactPoint point) {

	}

	/**
	 * Never used method.
	 */
	@Override
	public boolean preSolve(ContactPoint point) {
		// always set to true, let the collision begin, then refuse it if
		// necessary
		return true;
	}

	/**
	 * Never used method.
	 */
	@Override
	public void sensed(ContactPoint point) {

	}

	/**
	 * Returns the value of {@link #isDynamic} field.
	 * 
	 * A false value means that the world is not updated (looks like no
	 * movement), but still collides.
	 * 
	 * A true value is set for a moving and colliding world.
	 * 
	 * @return
	 */
	public boolean isDynamic() {

		return isDynamic;

	}

	/**
	 * Called by the engine every time an object is outside the world's bounds.
	 * The method from AbstractComplexObservable isOutside(bounds) is called to
	 * ensure that the object is fully contained outside the bounds.
	 * 
	 * <p>
	 * As this method is only called when the world is crossedTeleprotable, the
	 * object going out of the bounds with a velocity going in the same
	 * direction of the bounds will be teleported to the opposite bounds when
	 * this method will be called.
	 * 
	 * <p>
	 * Also, the object will remain mobile after going out of the bounds, in
	 * opposition to what is supposed to happen normally.
	 */
	@Override
	public <E extends Collidable<T>, T extends Fixture> void outside(E collidable) {

		final AxisAlignedBounds bounds = (AxisAlignedBounds) world.getBounds();
		final AbstractComplexObservable object = whichObservable((Body) collidable);
		Vector2 objectPos = object.getTranslate();

		// The object should remain mobile
		object.setMobile(true);

		// If not already considered outside
		if (isBoundsCrossedTeleported() && isDynamic) {

			// Test if the object is fully outside the bounds
			if (object.isOutside(bounds)) {

				if (object.isCrossedTeleportable()) {

					final double depthX = objectPos.x - bounds.getWidth();
					final double depthY = objectPos.y - bounds.getHeight();
					final Vector2 velocity = object.getBodies().get(0).getLinearVelocity();

					// React in order to reposition the object depending on the
					// given coordinates of the object.
					if (objectPos.x > bounds.getWidth() && velocity.x > 0) {
						// is out from right
						object.translate(-depthX, objectPos.y);
					} else if (objectPos.x < 0 && velocity.x < 0) {
						// is out from left
						object.translate(bounds.getWidth() - objectPos.x, objectPos.y);
					}

					if (objectPos.y > bounds.getHeight() && velocity.y > 0) {
						// Object is out from top
						object.translate(objectPos.x, -depthY);
					} else if (objectPos.y < 0 && velocity.y < 0) {
						// Object is out from bottom
						object.translate(objectPos.x, bounds.getHeight() - objectPos.y);
					}
				}

			}
		}
	}

	/**
	 * Returns an unmodifiable list of the objects contained in the world.
	 * 
	 * @return An unmodifiable list of the objects contained in the world.
	 */
	public ObservableList<AbstractComplexObservable> getObservables() {
		return FXCollections.unmodifiableObservableList(complexList);
	}

	/**
	 * Returns the update ratio of the dynamic world
	 * <p>
	 * {@code for(int i = 0 : i<=3:i++{ {@link #isDynamic()} }}
	 * 
	 * @deprecated The changing of this value may occur in update problems.
	 * 
	 * @return The value of {@link #updateRatio}
	 */
	@Deprecated
	public double getUpdateRatio() {
		return updateRatio;
	}

	/**
	 * This method allows to give the world an update ratio. The value of
	 * {@link #getUpdateRatio()} is modified in order to give the impression
	 * that the world is updated slower or faster than the real-time speed.
	 * 
	 * <p>
	 * A value of 1 to ratio represents a world updated in real-time
	 * <p>
	 * A value less than 1 is a world updated in slow speed
	 * <p>
	 * A value over 1 is a world updated in high-speed
	 * 
	 * @param ratio
	 *            The new value of the ratio. Must be higher than 1
	 */
	public void setUpdateRatio(double ratio) {
		if (ratio <= 0)
			ratio = 0.00000001;

		this.updateRatio = ratio;
		// Modify the number of update made per second
		world.getSettings().setStepFrequency(Settings.DEFAULT_STEP_FREQUENCY * ratio);

		// As the gravity will be applied ratio times more often, we modify the
		// gravity to give the illusion that the object simply goes in slow
		// speed.
		world.setGravity(new Vector2(0, getGravity() / ratio));
	}

	/**
	 * Sets the friction coefficient for the borders of the world. This
	 * parameter should be between 0 and 1, where 1 represents infinite
	 * friction, and 0 null friction.
	 * 
	 */
	public void setBorderFriction(double fric) {
		this.fricCoef = fric;
		generatePhysicalBounds();
	}

	/**
	 * @return The value of {@link #fricCoef}.
	 */
	public double getBorderFriction() {
		return fricCoef;
	}

	/**
	 * 
	 * @return An unmodifiable set of the collisions that are currently
	 *         occurring and that the engine tries to handle. Note that
	 *         modification of the state of those objects might create certain
	 *         problem in update.
	 */
	public Set<CollidingPair> getColliders() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableSet(colliders);
	}

	/**
	 * Removes all the object of the dynamic world. No collision will be handled
	 * after this method is called. However, the bounds will remain in position,
	 * to make sure that the objects added will still collide with them.
	 */
	public void clearObjects() {

		this.complexList.clear();
		this.colliders.clear();
		this.world.removeAllBodiesAndJoints();

		// put bound back again
		this.computeBounds();

	}

}
