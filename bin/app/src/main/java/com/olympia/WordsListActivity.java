package com.olympia;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
public class WordsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_activity);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new TabFragment1(), getString(R.string.tab1_label));
        viewPagerAdapter.addFragment(new TabFragment2(), getString(R.string.tab2_label));
        viewPagerAdapter.addFragment(new TabFragment3(), getString(R.string.tab3_label));
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent1, Globals.SETTINGS_ACTIVITY);
                return true;

            case R.id.action_about:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent2, Globals.SETTINGS_ACTIVITY);
                return true;

            case R.id.action_legal:
                Intent intent3 = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent3, Globals.SETTINGS_ACTIVITY);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
