package gr.watchful.permsyncer.utils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class FileUtils {
	public static boolean writeFile(String string, File location) {
		return writeFile(string, location, false);
	}

	public static boolean writeFile(String string, File location, boolean forceASCII) {
		try{
			if (location.exists()) location.delete();
			location.createNewFile();

			if(!forceASCII) {
				FileWriter fstream = new FileWriter(location);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(string);
				out.close();
			} else {
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(location), "ASCII"));
				try {
					out.write(string);
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				} finally {
					out.close();
				}
			}
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
			return false;
		}
		return true;
	}

	public static String readFile(File location) {
		if(!location.exists()) return null;
		BufferedReader br = null;
		StringBuilder bldr = new StringBuilder();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(location));
			while ((sCurrentLine = br.readLine()) != null) {
				bldr.append(sCurrentLine+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return bldr.toString();
	}

	public static void downloadToFile(URL url, File file) throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(file);
		fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		fos.close();
	}

	public static String getJSON(Object object) {
		Gson gson = new Gson();
		return gson.toJson(object);
	}

	public static Object getObject(String JSON, Object object) {
		Gson gson = new Gson();
		Object tempObject;
		try {
			tempObject = gson.fromJson(JSON, object.getClass());
		} catch (JsonSyntaxException excp) {
			System.out.println("returning null");
			return null;
		}
		return tempObject;
	}

	public static void saveObject(Object object, File file) {
		writeFile(getJSON(object), file);
	}

	public static Object readObject(File file, Object object) {
		return getObject(readFile(file), object);
	}
}
