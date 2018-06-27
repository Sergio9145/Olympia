package com.olympia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.olympia.Globals;
import com.olympia.R;
import com.olympia.TabFragment1;
import com.olympia.TabFragment2;
import com.olympia.TabFragment3;
import com.olympia.AdapterTabsPager;

public class WordsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        AdapterTabsPager adapterTabsPager = new AdapterTabsPager(getSupportFragmentManager());

        adapterTabsPager.addFragment(new TabFragment1(), getString(R.string.tab1_label));
        adapterTabsPager.addFragment(new TabFragment2(), getString(R.string.tab2_label));
        adapterTabsPager.addFragment(new TabFragment3(), getString(R.string.tab3_label));
        viewPager.setAdapter(adapterTabsPager);

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
                Intent intent2 = new Intent(this, AboutActivity.class);
                startActivityForResult(intent2, Globals.ABOUT_ACTIVITY);
                return true;

            case R.id.action_legal:
                Intent intent3 = new Intent(this, LegalActivity.class);
                startActivityForResult(intent3, Globals.lEGAL_ACTIVITY);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        // Suppressing navigating to login screen!
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Globals.SETTINGS_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    int result = data.getIntExtra(Globals.SETTINGS_EXTRA, 0);
                    Intent returnIntent = new Intent();
                    switch (result) {
                        case Globals.LOGOUT_REQUESTED:
                            returnIntent.putExtra(Globals.WORDS_LIST_EXTRA, Globals.LOGOUT_REQUESTED);
                            break;
                        case Globals.DELETE_ACCOUNT_REQUESTED:
                            returnIntent.putExtra(Globals.WORDS_LIST_EXTRA, Globals.DELETE_ACCOUNT_REQUESTED);
                        default:
                            break;
                    }
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
                break;
            case Globals.CAMERA_ACTIVITY:
                break;
        }
    }
}
