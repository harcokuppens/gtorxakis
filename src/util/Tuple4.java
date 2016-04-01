package util;

public class Tuple4 <K, L, M, N> {
	public final K k;
	public final L l;
	public final M m;
	public final N n;

	public Tuple4(K k, L l, M m, N n) {
		this.k = k;
		this.l = l;
		this.m = m;
		this.n = n;
	}

	public Object[] values() {
		return new Object[] {k, l, m, n};
	}

	public K getK() {
		return k;
	}

	public L getL() {
		return l;
	}

	public M getM() {
		return m;
	}

	public N getN() {
		return n;
	}

	@Override
	public int hashCode() {
		return 
			n.hashCode() + 31 * 
			 (m.hashCode() + 31 * 
			  (l.hashCode() + 31 *
			   k.hashCode()));
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Tuple4)) {
			return false;
		}
		Tuple4 t = (Tuple4) o;
		Object[] ourValues = values();
		Object[] theirValues = t.values();
		if(ourValues.length != theirValues.length) {
			return false;
		}
		for(int i = 0; i < ourValues.length; i++) {
			if(!ourValues[i].equals(theirValues[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + 
			k.toString() + ", " +
			l.toString() + ", " +
			m.toString() + ", " +
			n.toString() + ")";
	}
}