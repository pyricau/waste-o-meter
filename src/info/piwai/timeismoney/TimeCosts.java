package info.piwai.timeismoney;

import java.util.SortedSet;
import java.util.TreeSet;

public class TimeCosts {

	private static final int MINUTES_PER_HOUR = 60;
	private static final int WORKING_HOURS_PER_DAY = 8;

	private final SortedSet<Material> materials;
	private final SortedSet<Material> costInMaterials = new TreeSet<Material>();

	private int costPerDay;
	private int lostMinutesPerDay;
	private int workingDays;
	private double costPerMinute;
	private double totalCost;

	public TimeCosts(SortedSet<Material> materials) {
		this.materials = materials;
	}

	public void updateCosts() {
		costPerMinute = costPerDay * 1. / WORKING_HOURS_PER_DAY / MINUTES_PER_HOUR;
		totalCost = costPerMinute * lostMinutesPerDay * workingDays;

		costInMaterials.clear();
		double rest = totalCost;
		for (Material material : materials) {
			int price = material.getPrice();
			if (rest > price) {
				rest -= price;
				costInMaterials.add(material);
			}
		}
	}

	public int getCostPerDay() {
		return costPerDay;
	}

	public void setCostPerDay(int costPerDay) {
		this.costPerDay = costPerDay;
	}

	public int getLostMinutesPerDay() {
		return lostMinutesPerDay;
	}

	public void setLostMinutesPerDay(int lostMinutesPerDay) {
		this.lostMinutesPerDay = lostMinutesPerDay;
	}

	public int getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}

	public double getCostPerMinute() {
		return costPerMinute;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public SortedSet<Material> getCostInMaterials() {
		return costInMaterials;
	}

}
