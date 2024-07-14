package gaming_map;

import java.util.Objects;

public class Field {
	private ETerrain tiletype;
	private ESpecial tileinfo;

	public Field(ETerrain terrain, ESpecial special) {
		this.tiletype = terrain;
		this.tileinfo = special;
	}

	public ETerrain getTiletype() {
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
		Field other = (Field) obj;
		return tileinfo == other.tileinfo && tiletype == other.tiletype;
	}

	@Override
	public String toString() {
		return "[" + tiletype + ", " + tileinfo + "]";
	}

}
