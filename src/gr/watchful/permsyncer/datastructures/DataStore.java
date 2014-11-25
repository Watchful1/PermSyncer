package gr.watchful.permsyncer.datastructures;

import gr.watchful.permsyncer.utils.FileUtils;

import java.io.File;

public class DataStore {
	public String lastMD5;

	transient public File saveLocation;

	public static DataStore load(String fileName) {
		File file = new File(fileName);
		DataStore dataStore = (DataStore) FileUtils.readObject(file, new DataStore());
		if(dataStore == null) dataStore = new DataStore();
		dataStore.saveLocation = file;
		if(dataStore.lastMD5 == null) dataStore.lastMD5 = "none";
		return dataStore;
	}

	public void save() {
		FileUtils.saveObject(this, saveLocation);
	}
}
