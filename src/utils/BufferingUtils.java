package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import game.BorderType;
import game.InventoryItem;
import game.Level;
import game.Planet;
import gameObservableViews.ObservableObjectFactory;
import observables.AbstractComplexObservable;

public class BufferingUtils {

	/**
	 * Saves the level sent in parameter to the Level's path returned by
	 * {@link Level#getPath()}. This level can be open again using
	 * {@link #openLevel(File)}.
	 * 
	 * @param level
	 *            The level to serialize.
	 */
	public static void saveLevel(Level level) {

		File file = new File(level.getPath());
		ObjectOutputStream stream = null;
		FileOutputStream fStream = null;

		try {
			// Open streams
			fStream = new FileOutputStream(file);
			stream = new ObjectOutputStream(fStream);

			// Writes properties
			stream.writeObject(level.getCreator());
			stream.writeObject(level.getName());
			stream.writeObject(level.getBorders());
			stream.writeDouble(level.getHeight());
			stream.writeDouble(level.getWidth());
			stream.writeObject(level.getScreenShot());
			stream.writeObject(level.getPlanet());

			final List<AbstractComplexObservable> fixed = level.getFixedObject();
			// Writes the size of the children
			stream.writeInt(fixed.size());

			// Then convert the children to Serializable objects, then serialize
			// them.
			for (AbstractComplexObservable object : fixed) {

				ObservableSerializableWrapper wrapper = new ObservableSerializableWrapper();
				wrapper.setObservable(object);
				stream.writeObject(wrapper);
			}
			// Writes the allowed inventory
			final List<InventoryItem> inventory = level.getInventory().getItems();

			stream.writeObject(inventory);

			stream.flush();
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read all the levels contained in the repository file specified in
	 * parameter and return them as a List.
	 * 
	 * The level are opened and initialized.
	 * 
	 * @param repository
	 *            The repository where the level are stored.
	 * @return The levels contained in the repository.
	 */
	public static List<Level> readAllLevels(File repository) {

		final List<File> personnalFiles = listFilesFromRepository(repository);
		final List<Level> personnalLevels = new ArrayList<>();

		for (File f : personnalFiles) {
			try {

				personnalLevels.add(openLevel(f));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return personnalLevels;
	}

	/**
	 * Opens the Level contained in the file sent in parameter. This method
	 * unserialize objects the way {@link #saveLevel(Level)} has saved them.
	 * 
	 * @param file
	 *            The file of the level
	 * @return The level contained in the file
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Level openLevel(File file) throws IOException, ClassNotFoundException {

		Level level = null;
		ObjectInputStream stream = null;
		FileInputStream fStream = null;
		ObservableObjectFactory factory = new ObservableObjectFactory();

		// Open the stream
		fStream = new FileInputStream(file);
		stream = new ObjectInputStream(fStream);

		// Reads default parameters and create the level back again.
		String creator = (String) stream.readObject();
		String name = (String) stream.readObject();
		String path = file.getPath();

		level = new Level(name, creator, path);

		level.setBorders((BorderType) stream.readObject());
		level.setHeight(stream.readDouble());
		level.setWidth(stream.readDouble());
		level.setScreenShot((byte[]) stream.readObject());
		level.setPlanet((Planet) stream.readObject());

		// Reads the fixed object that it was containing
		Integer size = stream.readInt();
		
		for (int i = 0; i < size; i++) {
			 

			ObservableSerializableWrapper wrapper = (ObservableSerializableWrapper) stream.readObject();
			
			AbstractComplexObservable object = factory.getWrapperInstance(wrapper.instance, wrapper.param1,
					wrapper.param2, wrapper.param3).observable;
			

			// Gives to the object its old values
			object.translate(wrapper.posX, wrapper.posY);
			object.rotate(wrapper.rotation);
			level.addFixedObject(object);

		}

		// Sets the inventory of the level.
		@SuppressWarnings("unchecked")
		List<InventoryItem> inventory = (List<InventoryItem>) stream.readObject();
		level.getInventory().addAllItem(inventory);

		stream.close();
		

		return level;
	}

	/**
	 * List the files contained in the repository sent in parameter.
	 * 
	 * If the file is not a repository, then only the file itself will be
	 * contained in the List.
	 * 
	 * Otherwise, the list will return all the file that this file contains, and
	 * will not add the file itself.
	 * 
	 * @param f
	 *            The repository file
	 * @return The files contained in this repository.
	 */
	public static List<File> listFilesFromRepository(File f) {

		List<File> fileList = new ArrayList<File>();

		// Check whether the file is a directory or not. If it is, all the
		// files
		// from this directory are watched.
		if (f.isDirectory()) {

			for (File file : f.listFiles()) {
				if (file.isDirectory()) {

					fileList.addAll(listFilesFromRepository(file));

				} else {

					fileList.add(file);

				}

			}

		} else {

			fileList.add(f);

		}

		return fileList;
	}
}
