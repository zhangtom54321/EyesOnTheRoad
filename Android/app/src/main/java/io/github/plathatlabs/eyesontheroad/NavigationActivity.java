package io.github.plathatlabs.eyesontheroad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class NavigationActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener,
                                                                        HistoryFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case io.github.plathatlabs.eyesontheroad.R.id.navigation_history:
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(io.github.plathatlabs.eyesontheroad.R.id.fragment_container, new HistoryFragment()).commit();
                    return true;
                case io.github.plathatlabs.eyesontheroad.R.id.navigation_settings:
                    FragmentManager manager2 = getSupportFragmentManager();
                    manager2.beginTransaction().replace(io.github.plathatlabs.eyesontheroad.R.id.fragment_container, new SettingsFragment()).commit();
                    return true;
                case io.github.plathatlabs.eyesontheroad.R.id.navigation_app:
                    Intent myIntent = new Intent(NavigationActivity.this, MainActivity.class);
                    NavigationActivity.this.startActivity(myIntent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(io.github.plathatlabs.eyesontheroad.R.layout.activity_navigation);

        mTextMessage = (TextView) findViewById(io.github.plathatlabs.eyesontheroad.R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(io.github.plathatlabs.eyesontheroad.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
