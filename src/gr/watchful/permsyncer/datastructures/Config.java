package gr.watchful.permsyncer.datastructures;

import gr.watchful.permsyncer.utils.FileUtils;

import java.io.File;

public class Config {
	public String spreadsheetUrl;
	public String FTPUsername;
	public String FTBPassword;
	public String FTBServer;
	public boolean forceUpdate;

	transient public File saveLocation;

	public boolean init() {
		boolean changed = false;
		if(spreadsheetUrl == null) {
			changed = true;
			spreadsheetUrl = "urlhere";
		}
		if(FTPUsername == null) {
			changed = true;
			FTPUsername = "username";
		}
		if(FTBPassword == null) {
			changed = true;
			FTBPassword = "password";
		}
		if(FTBServer == null) {
			changed = true;
			FTBServer = "serveraddress";
		}
		return changed;
	}

	public static Config load(String fileName) {
		File file = new File(fileName);
		Config config = (Config) FileUtils.readObject(file, new Config());
		if(config == null) config = new Config();
		config.saveLocation = file;
		if(config.init()) config.save();
		return config;
	}

	public void save() {
		FileUtils.saveObject(this, saveLocation);
	}
}
