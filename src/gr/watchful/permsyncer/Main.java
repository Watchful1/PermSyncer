package gr.watchful.permsyncer;

import gr.watchful.permsyncer.utils.ExcelUtils;
import gr.watchful.permsyncer.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main {
	public static final String permUrl = "https://onedrive.live.com/download?resid=96628E67B4C51B81!161&ithint=" +
			"file%2c.xlsx&app=Excel&authkey=!APQ4QtFrBqa1HwM";

	public Main() {
		System.out.println("PING");
	}

	public boolean updateListings() {
		File permFile = new File("Permissions.xlsx");
		if (!permFile.exists()) {
			try {
				permFile.createNewFile();
			} catch (IOException e) {
				System.out.println("Could not create Permissions.xlsx");
				return false;
			}
		}

		try {
			FileUtils.downloadToFile(new URL(permUrl), permFile);
		} catch (IOException e) {
			System.out.println("Could not download perm file");
			return false;
		}

		ArrayList<ArrayList<String>> infos;
		ArrayList<ArrayList<String>> mappings;
		try {
			infos = ExcelUtils.toArray(permFile, 1);
			mappings = ExcelUtils.toArray(permFile, 2);
		} catch (IOException e) {
			System.out.println("Could not read perm file");
			return false;
		}
		infos.remove(0);//remove the first row, it contains column titles
		//nameRegistry.loadMappings(infos, mappings, infos.get(15).get(14), infos.get(15).get(15));
		return true;
	}

	public static void main(String[] args) {
		new Main();
	}
}
