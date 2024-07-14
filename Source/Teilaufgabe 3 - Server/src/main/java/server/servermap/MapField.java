package server.servermap;

import java.util.Objects;

public class MapField {
	private ETerrainType tiletype;
	private ESpecial tileinfo;

	public MapField(ETerrainType terrain, ESpecial special) {
		this.tiletype = terrain;
		this.tileinfo = special;
	}

	/**
	 * Copy constructor
	 * 
	 * @param otherField other MapField that gets copied
	 */
	public MapField(MapField otherField) {
		this(otherField.getTiletype(), otherField.getTileinfo());
	}

	public ETerrainType getTiletype() {
		return this.tiletype;
	}

	public ESpecial getTileinfo() {
		return this.tileinfo;
	}

	public void setTileinfo(ESpecial tileinfo) {
		this.tileinfo = tileinfo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(tileinfo, tiletype);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapField other = (MapField) obj;
		return tileinfo == other.tileinfo && tiletype == other.tiletype;
	}

	@Override
	public String toString() {
		return "[" + tiletype + ", " + tileinfo + "]";
	}
}
