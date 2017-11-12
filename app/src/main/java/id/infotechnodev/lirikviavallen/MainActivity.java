package id.infotechnodev.lirikviavallen;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Map;

import id.infotechnodev.lirikviavallen.db.DatabaseHelper;
import id.infotechnodev.lirikviavallen.db.Song;

/**
 * Main application activity representing the list of available songs
 * 
 * @author KM
 */
public class MainActivity extends ListActivity {

	private DatabaseHelper db;
	private ArrayAdapter<String> adapter;

	private Map<String, Integer> songs;

	private AdView mAdView;
	private InterstitialAd interstitial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Handler handler = new Handler();
		setContentView(R.layout.main);

		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		interstitial=new InterstitialAd(this);
		interstitial.setAdUnitId("ca-app-pub-5543468129947515/3797975218"); // viavallen
		//interstitial.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // test AdUnit
		interstitial.loadAd(adRequest);
		//displayMyAd();

		// delegate database operations to a background thread
		new Thread() {
			public void run() {
				if (db == null) {
					db = new DatabaseHelper(MainActivity.this);
				}
				songs = db.getAllSongs();
				// after retrieving data from db, post list update
				// for execution in UI thread
				handler.post(new Runnable() {
					public void run() {
						String[] items = songs.keySet().toArray(new String[0]);
						// using ArrayAdapter instead of CursorAdapter allows
						// for filtering list without requerying the db
						adapter = new ArrayAdapter<String>(MainActivity.this,
								android.R.layout.simple_list_item_1, items);
						setListAdapter(adapter);
					}
				});
			}
		}.start();

		setSearchListener();

	}

	public void displayMyAd() {
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		String title = ((TextView) v).getText().toString();
		Integer songId = songs.get(title);
		displayMyAd();
		if (songId == null) {
			// should never happen, but just to be on the safe side...
			Toast.makeText(this, getString(R.string.error_title_not_found, title),
					Toast.LENGTH_SHORT).show();
			return;
		}
		Song song = db.getSong(songId);
		Intent intent = new Intent(this, SongActivity.class);
		intent.putExtra(Song.DATA_NAME, song);
		this.startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * register search textfield for list filtering
	 */
	private void setSearchListener() {
		EditText search = (EditText) this.findViewById(R.id.search);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (adapter != null) {
					adapter.getFilter().filter(s);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// do nothing
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// do nothing
			}

		});
	}

	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
	}

	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		new android.app.AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Selesai & Keluar")
				.setMessage("Keluar dari aplikasi?")
				.setPositiveButton("YA", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				})
				.setNegativeButton("TIDAK", null)
				.show();
	}

}
