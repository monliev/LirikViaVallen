package id.infotechnodev.lirikviavallen;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Default activity for preferences management
 * 
 * @author KM
 */
public class SettingsActivity extends PreferenceActivity {
	
	public static final String PREFERENCES_NAME = "id.infotechnodev.lirikviavallen_preferences";
	
	public static final String PREFERENCES_SHOW_CHORDS = "pref_show_chords";
	public static final String PREFERENCES_DISPLAY_MODE = "pref_display_mode";
	public static final String PREFERENCES_FONT_SIZE = "pref_font_size";
	
	@Override
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// use deprecated method to preserve gingerbread & froyo compatibility
		addPreferencesFromResource(R.xml.settings);
	}
}
