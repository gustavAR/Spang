package tests;

import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import spang.mobile.MainActivity;

public class PreferencesGUITest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;

	public PreferencesGUITest() {
		super("com.test.editor",
				MainActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testPreferenceIsSaved() throws Exception {

		solo.sendKey(Solo.MENU);
		solo.clickOnText("Settings");
		Assert.assertTrue(solo.searchText("Sample rate")); //TODO To be continued...

		solo.clickOnText("Sample rate");
		solo.setSlidingDrawer(0, 5);
	/*	solo.enterText(2, "robotium");
		solo.clickOnButton("Save");
		solo.goBack();
		solo.clickOnText("Edit File Extensions");
		Assert.assertTrue(solo.searchText("application/robotium")); */

	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
