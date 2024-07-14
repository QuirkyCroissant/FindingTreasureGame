package gaming_map;

import java.util.Objects;

public class Coordinate /* implements Comparable<Coordinate> */ {

	private int x;
	private int y;

	public Coordinate(int x, int y) {

		this.x = x;
		this.y = y;
	}

	public Coordinate() {
	}

	public Coordinate(Coordinate baseCastle) {
		this(baseCastle.getX(), baseCastle.getY());
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
