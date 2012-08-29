package info.piwai.timeismoney;

import java.util.SortedSet;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.TextChange;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.time_is_money)
public class TimeIsMoneyActivity extends Activity {

	private static final String STORE_URL = "http://goo.gl/5NUr8";
	private static final String COST_PER_DAY = "costPerDay";
	private static final String LOST_MINUTES_PER_DAY = "lostMinutesPerDay";
	private static final String WORKING_DAYS = "workingDays";

	private TimeCosts timeCosts;

	@ViewById
	EditText costPerDayInput;

	@ViewById
	EditText lostMinutesPerDayInput;

	@ViewById
	SeekBar workingDaysInput;

	@ViewById
	TextView costPerDayLabel;

	@ViewById
	TextView workingDaysTitle;

	@ViewById
	TextView materialCostsTitle;

	@ViewById
	TextView materialCostsLabel;

	private BackgroundPreferenceHandler preferencesHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences preferences = getPreferences(MODE_PRIVATE);

		preferencesHandler = new BackgroundPreferenceHandler(preferences);

		SortedSet<Material> materials = Material.createFromResources(getResources());
		timeCosts = new TimeCosts(materials);

		initTimeCosts(preferences);
	}

	private void initTimeCosts(SharedPreferences preferences) {
		timeCosts.setCostPerDay(preferences.getInt(COST_PER_DAY, 450));
		timeCosts.setLostMinutesPerDay(preferences.getInt(LOST_MINUTES_PER_DAY, 15));
		timeCosts.setWorkingDays(preferences.getInt(WORKING_DAYS, 30));
	}

	@AfterViews
	void initViews() {
		workingDaysInput.setOnSeekBarChangeListener(new AbstractOnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				workingDaysInput(progress);
			}
		});

		costPerDayInput.setText(Integer.toString(timeCosts.getCostPerDay()));
		lostMinutesPerDayInput.setText(Integer.toString(timeCosts.getLostMinutesPerDay()));
		int progress = timeCosts.getWorkingDays() - 1;
		workingDaysInput.setProgress(progress);
		updateCosts();
	}

	@Click
	void shareButtonClicked() {

		StringBuilder message = new StringBuilder();

		message.append(String.format(getString(R.string.share_format), timeCosts.getWorkingDays(), timeCosts.getTotalCost()));

		SortedSet<Material> materials = timeCosts.getCostInMaterials();
		int lastItem = materials.size() - 1;
		int i = 0;
		for (Material material : materials) {
			if (i == 0) {
				message.append(getString(R.string.material_start_share));
			} else if (i < lastItem) {
				message.append(getString(R.string.material_separator));
			} else {
				message.append(getString(R.string.material_last_separator));
			}
			message.append(material.formatNameAndPrice(getString(R.string.material_format_share)));

			i++;
		}

		message.append(" ") //
				.append(STORE_URL);

		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
		startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_with)));
	}

	@TextChange
	void costPerDayInput(CharSequence text) {
		int costPerDay = parse(text);
		if (costPerDay != timeCosts.getCostPerDay()) {
			timeCosts.setCostPerDay(costPerDay);
			preferencesHandler.putIntAsync(COST_PER_DAY, costPerDay);
			updateCosts();
		}
	}

	@TextChange
	void lostMinutesPerDayInput(CharSequence text) {
		int lostMinutesPerDay = parse(text);
		if (lostMinutesPerDay != timeCosts.getLostMinutesPerDay()) {
			timeCosts.setLostMinutesPerDay(lostMinutesPerDay);
			preferencesHandler.putIntAsync(LOST_MINUTES_PER_DAY, lostMinutesPerDay);
			updateCosts();
		}
	}

	private int parse(CharSequence charSequence) {
		String string = charSequence.toString();
		if (string.equals("")) {
			return 0;
		}
		return Integer.parseInt(string);
	}

	/*
	 * TODO Use SeekBar dedicated annotation when available
	 */
	void workingDaysInput(int progress) {
		int workingDays = progress + 1;
		if (workingDays != timeCosts.getWorkingDays()) {
			timeCosts.setWorkingDays(workingDays);
			preferencesHandler.putIntAsync(WORKING_DAYS, workingDays);
			updateCosts();
		}
	}

	void updateCosts() {
		timeCosts.updateCosts();

		costPerDayLabel.setText(Html.fromHtml(String.format(getString(R.string.cost_per_minute_label_format), timeCosts.getCostPerMinute())));
		workingDaysTitle.setText(Html.fromHtml(String.format(getString(R.string.working_days_title_format), timeCosts.getWorkingDays())));
		materialCostsTitle.setText(Html.fromHtml(String.format(getString(R.string.total_cost_title_format), timeCosts.getTotalCost())));

		StringBuilder materialCosts = new StringBuilder();
		SortedSet<Material> materials = timeCosts.getCostInMaterials();
		int lastItem = materials.size() - 1;
		int i = 0;
		for (Material material : materials) {
			if (i == 0) {
				materialCosts.append(getString(R.string.material_start));
			} else if (i < lastItem) {
				materialCosts.append(getString(R.string.material_separator));
			} else {
				materialCosts.append(getString(R.string.material_last_separator));
			}
			materialCosts.append(material.formatNameAndPrice(getString(R.string.material_format)));

			i++;
		}

		materialCostsLabel.setText(Html.fromHtml(materialCosts.toString()));
	}
}
