package info.piwai.timeismoney;

import java.util.SortedSet;
import java.util.TreeSet;

import android.content.res.Resources;

public class Material implements Comparable<Material> {

	public static SortedSet<Material> createFromResources(Resources resources) {
		SortedSet<Material> materials = new TreeSet<Material>();

		String[] materialNames = resources.getStringArray(R.array.materialNames);
		int[] materialPrices = resources.getIntArray(R.array.materialPrices);

		for (int i = 0; i < materialNames.length; i++) {
			materials.add(new Material(materialNames[i], materialPrices[i]));
		}

		return materials;
	}

	private final String name;
	private final int price;

	public Material(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String formatNameAndPrice(String format) {
		return String.format(format, name, price);
	}

	public int getPrice() {
		return price;
	}

	/**
	 * Sorted desc by price
	 */
	@Override
	public int compareTo(Material another) {
		return Integer.valueOf(another.price).compareTo(price);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + price;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Material other = (Material) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (price != other.price) {
			return false;
		}
		return true;
	}

}
