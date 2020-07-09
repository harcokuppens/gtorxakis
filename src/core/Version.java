package core;

import java.lang.Comparable;

public class Version implements Comparable<Version>{

	public final int major, minor, patch;

	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	public Version(String majorString, String minorPatchString) {
		this(majorString + "." + minorPatchString);
	}

	public Version(String versionString) throws NumberFormatException {
		String[] ss = versionString.split("\\.");
		int[] values = new int[3];
		
		for(int i = 0; i < ss.length; i++) {
			values[i] = Integer.valueOf(ss[i]);
		}

		this.major = values[0];
		this.minor = values[1];
		this.patch = values[2];
	} 

	public boolean isOlderThan(Version v) {
		return this.compareTo(v) < 0; 
	}

	public boolean isNewerThan(Version v) {
		return this.compareTo(v) > 0;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Version)) {
			return false;
		}
		Version v = (Version) o;
		return v.major == this.major
			&& v.minor == this.minor
			&& v.patch == this.patch;
	}

	@Override
	public int compareTo(Version v) {
		if(this.major != v.major) {
			return this.major - v.major;
		} else if(this.minor != v.minor) {
			return this.minor - v.minor;
		} else {
			return this.patch - v.patch;
		}
	}

	@Override
	public String toString() {
		return major + "." + minor +(patch == 0? "":"." + patch);
	}

	@Override
	public int hashCode() {
		return 59 * toString().hashCode();
	}
}