package gr.watchful.permsyncer;

import gr.watchful.permsyncer.datastructures.Config;
import gr.watchful.permsyncer.datastructures.Mod;
import gr.watchful.permsyncer.utils.ExcelUtils;
import gr.watchful.permsyncer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	private ArrayList<ArrayList<String>> infos;
	private ArrayList<ArrayList<String>> mappings;
	private HashMap<String, Mod> mods;
	private Config config;

	public Main() {
		File configFile = new File("config.json");
		if(configFile.exists()) {
			config = (Config) FileUtils.readObject(new File("config.json"), new Config());
		} else {
			config = new Config();
			config.init();
			FileUtils.saveObject(config, configFile);
		}
		if(config.init()) FileUtils.saveObject(config, configFile);

		File permFile = new File("Permissions.xlsx");
		if (!permFile.exists()) {
			try {
				permFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create Permissions.xlsx");
				return;
			}
		}

		try {
			FileUtils.downloadToFile(new URL(config.spreadsheetUrl), permFile);
		} catch (IOException e) {
			System.out.println("Could not download perm file");
			return;
		}
		String md5 = FileUtils.getMD5(permFile);
		if(md5.equals(config.lastMD5)) {
			System.out.println("MD5 same as last, not updating");
			return;
		} else {
			config.lastMD5 = md5;
			FileUtils.saveObject(config, configFile);
		}

		try {
			infos = ExcelUtils.toArray(permFile, 1);
			mappings = ExcelUtils.toArray(permFile, 2);
		} catch (IOException e) {
			System.out.println("Could not read perm file");
			return;
		}
		infos.remove(0);

		loadMods(infos.get(15).get(14), infos.get(15).get(15));

		ArrayList<Mod> temp = new ArrayList<>();
		for(Map.Entry<String, Mod> entry : mods.entrySet()) {
			temp.add(entry.getValue());
		}

		FileUtils.saveObject(temp.toArray(), new File("permissions.json"));
	}

	public void loadMods(String baseUrl, String extension) {
		String imageBaseUrl = baseUrl;
		String imageExtension = extension;
		mods = new HashMap<>();

		int i=0;
		for(ArrayList<String> row : infos) {
			i++;
			if(row.size() >= 6 && row.get(2) != null && !row.get(2).equals("")) {
				Mod info = new Mod();
				info.shortName = row.get(2);
				info.modName = row.get(0);//set name
				info.modAuthor = row.get(1);//set author
				info.modLink = row.get(5);//set url

				if(row.get(6).equals("")) {//set perm link
					info.licenseLink = info.modLink;
				} else {
					info.licenseLink = row.get(6);
				}
				if(row.get(7).equals("")) {//set private perm link
					info.privateLicenseLink = info.licenseLink;
				} else if(row.get(7).equals("PM")) {
					info.privateLicenseLink = imageBaseUrl+"PrivateMessage"+imageExtension;
				} else {
					info.privateLicenseLink = row.get(7);
				}

				switch(row.get(3)){//set the public policy
					case "Open":
						info.publicPolicy = Mod.OPEN;
						break;
					case "Notify":
						info.publicPolicy = Mod.NOTIFY;
						break;
					case "Request":
						info.publicPolicy = Mod.REQUEST;
						break;
					case "Closed":
						info.publicPolicy = Mod.CLOSED;
						break;
					case "FTB":
						info.publicPolicy = Mod.FTB;
						break;
					case "Unknown":
						info.publicPolicy = Mod.UNKNOWN;
						break;
					default:
						info.publicPolicy = Mod.UNKNOWN;
						break;
				}

				switch(row.get(4)){//set the private policy
					case "Open":
						info.privatePolicy = Mod.OPEN;
						break;
					case "Notify":
						info.privatePolicy = Mod.NOTIFY;
						break;
					case "Request":
						info.privatePolicy = Mod.REQUEST;
						break;
					case "Closed":
						info.privatePolicy = Mod.CLOSED;
						break;
					case "FTB":
						info.privatePolicy = Mod.FTB;
						break;
					case "Unknown":
						info.privatePolicy = Mod.UNKNOWN;
						break;
					default:
						info.privatePolicy = Mod.UNKNOWN;
						break;
				}

				info.licenseImage = imageBaseUrl+info.shortName+imageExtension;//set perm image link
				if(row.get(7).equals("")) {//set private perm image link
					info.privateLicenseImage = info.licenseImage;
				} else {
					info.privateLicenseImage = imageBaseUrl+info.shortName+"private"+imageExtension;
				}
				info.modIDs = new ArrayList<>();

				mods.put(info.shortName, info);
			}
		}
		for(ArrayList<String> row : mappings) {
			if(row.get(0) != null && row.get(1) != null && !row.get(0).equals("") && !row.get(1).equals("")) {
				if(mods.containsKey(row.get(1))) {
					mods.get(row.get(1)).modIDs.add(row.get(0));
				}
			}
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
