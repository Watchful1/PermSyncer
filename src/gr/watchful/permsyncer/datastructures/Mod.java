package gr.watchful.permsyncer.datastructures;

public class Mod {
	public static final int OPEN = 0;
	public static final int NOTIFY = 1;
	public static final int REQUEST = 2;
	public static final int CLOSED = 3;
	public static final int FTB = 4;
	public static final int UNKNOWN = 5;

	public String shortName;

	public String modName;
	public String modVersion;
	public String modAuthor;
	public String modLink;

	public String licenseLink;
	public String licenseImage;
	public String privateLicenseLink;
	public String privateLicenseImage;

	public String customLink;
	public boolean isPublicPerm;

	public int publicPolicy;
	public int privatePolicy;

	public Mod(String shortName) {
		this.shortName = shortName;
		modName = "Unknown";
		modAuthor = "Unknown";
		modLink = "None";
		modVersion = "Unknown";
		publicPolicy = UNKNOWN;
		privatePolicy = UNKNOWN;
		licenseLink = "";
		licenseImage = "";
		privateLicenseLink = "";
		privateLicenseImage = "";
		customLink = "";
		isPublicPerm = false;
	}
}
