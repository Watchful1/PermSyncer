package gr.watchful.permsyncer;

import gr.watchful.permsyncer.datastructures.Config;
import gr.watchful.permsyncer.datastructures.DataStore;
import gr.watchful.permsyncer.datastructures.Mod;
import gr.watchful.permsyncer.utils.ExcelUtils;
import gr.watchful.permsyncer.utils.FTPUtils;
import gr.watchful.permsyncer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		final String permFileName = "Permissions.xlsx";
		final String configName = "config.json";
		final String permJsonName = "permissions.json";
		final String datastoreName = "data";

		// Load the config, creating it if it isn't there
		Config config = Config.load(configName);

		// Download the spreadsheet
		File permFile = new File(permFileName);
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

		// Check the md5 of the spreadsheet against the older one
		String md5 = FileUtils.getMD5(permFile);

		DataStore datastore = DataStore.load(datastoreName);

		if(!config.forceUpdate && md5.equals(datastore.lastMD5)) {
			System.out.println("MD5 same as last, not updating");
			return;
		} else {
			datastore.lastMD5 = md5;
			datastore.save();
		}

		// Parse the two relevant pages in the spreadsheet into arrays. This could likely be skipped and the data
		// parsed directly into the mod objects, but this is far simpler
		ArrayList<ArrayList<String>> infos;
		ArrayList<ArrayList<String>> mappings;
		try {
			infos = ExcelUtils.toArray(permFile, 1);
			mappings = ExcelUtils.toArray(permFile, 2);
		} catch (IOException e) {
			System.out.println("Could not read perm file");
			return;
		}
		infos.remove(0); // The first line is a header

		String imageBaseUrl = infos.get(15).get(14);
		String imageExtension = infos.get(15).get(15);
		HashMap<String, Mod> mods = new HashMap<>();
		int i=0;

		// Now parse the arrays into mod objects. Store in a hashmap so we can add the ID's easily later
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

		// now add the ID mappings to the mods
		for(ArrayList<String> row : mappings) {
			if(row.get(0) != null && row.get(1) != null && !row.get(0).equals("") && !row.get(1).equals("")) {
				if(mods.containsKey(row.get(1))) {
					mods.get(row.get(1)).modIDs.add(row.get(0));
				}
			}
		}

		// copy everything from the hashmap to an arraylist
		ArrayList<Mod> temp = new ArrayList<>();
		for(Map.Entry<String, Mod> entry : mods.entrySet()) {
			temp.add(entry.getValue());
		}

		// Switch to an array before saving to the json
		FileUtils.saveObject(temp.toArray(), new File(permJsonName));

		// Upload to the FTP server
		FTPUtils.upload(config.FTPUsername, config.FTBPassword, config.FTBServer, new File(permJsonName),
				"/static/permissions/permissions.json");
	}
}
