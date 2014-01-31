package com.droidev.tuto.slidemenu;

/**
 * Code Inspiré de
 * http://developer.android.com/training/implementing-navigation/nav-drawer.html
 * */
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {

	private DrawerLayout menuLayout;
	private ListView menuElementsList;
	private ActionBarDrawerToggle menuToggle;

	private CharSequence menuTitle = "Menu";
	private CharSequence activityTitle = "DroiDev : Slide Menu Tuto";

	ArrayList<String> TZIDs = new ArrayList<String>();
	public final String DATE_FORMAT = "yyyy-MM-dd' 'hh:mm' 'Z";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
		menuElementsList = (ListView) findViewById(R.id.menu_elements);

		// set a custom shadow that overlays the main content when the drawer opens
		menuLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Get TimeZone List
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				this, R.layout.menu_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		String[] TZ = TimeZone.getAvailableIDs();
		
		for (int i = 0; i < TZ.length; i++) {
			if (!(TZIDs.contains(TimeZone.getTimeZone(TZ[i]).getDisplayName()))) {
				TZIDs.add(TimeZone.getTimeZone(TZ[i]).getDisplayName());
			}
		}
		
		for (int i = 0; i < TZIDs.size(); i++) {
			adapter.add(TZIDs.get(i));
		}
		menuElementsList.setAdapter(adapter);
		menuElementsList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle menu
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		menuToggle = new ActionBarDrawerToggle(this, /* host Activity */
		menuLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(activityTitle);
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(menuTitle);
				invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};
		menuLayout.setDrawerListener(menuToggle);

		// If Application just started select Current TimeZone
		if (savedInstanceState == null) {
			Calendar calendar = Calendar.getInstance();
            String timezone = calendar.getTimeZone().getID();
			convert(timezone);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Hide menu element when menu is opened
		boolean drawerOpen = menuLayout.isDrawerOpen(menuElementsList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (menuToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_search:
			Toast.makeText(this, R.string.search, Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		convert(TZIDs.get(position));
		// update selected item and title, then close the drawer
		menuElementsList.setItemChecked(position, true);
		setTitle(TZIDs.get(position));
		menuLayout.closeDrawer(menuElementsList);
	}

	private void convert(String timezone) {
		TextView outputDateTime = (TextView) findViewById(R.id.time_zone_current_time);
		String out = ChangeTimeZone(timezone);
		outputDateTime.setText(out);

	};

	private Date getDateFromString(String inputDateTime) {
		DateFormat formater = new SimpleDateFormat(DATE_FORMAT);
		Date inputDate = null;
		try {
			inputDate = formater.parse(inputDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return inputDate;
	}

	private String ChangeTimeZone( String timezone) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String currentDateandTime = sdf.format(new Date());
		Date inputeDateTime = getDateFromString(currentDateandTime);
		
		DateFormat formater = new SimpleDateFormat(DATE_FORMAT);
		formater.setTimeZone(TimeZone.getTimeZone(timezone));
		String newDateTime = formater.format(inputeDateTime);
		return newDateTime;
	}

	@Override
	public void setTitle(CharSequence title) {
		activityTitle = title;
		getActionBar().setTitle(activityTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		menuToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		menuToggle.onConfigurationChanged(newConfig);
	}

}
