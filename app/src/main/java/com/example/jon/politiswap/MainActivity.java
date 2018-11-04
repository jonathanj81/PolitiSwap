package com.example.jon.politiswap;

import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarLayout();
        setTabLayouts();

        RecyclerView recyclerView = findViewById(R.id.content_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new TestAdapter());
    }

    private void setToolbarLayout() {

        toolLayout = findViewById(R.id.toolbar_layout);
        toolLayout.setTitleEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_handshake);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                break;
            case R.id.action_settings:
                break;
            default:
                break;

        }
        return true;
    }

    private void setTabLayouts(){
        TabLayout topTab = findViewById(R.id.top_tab_layout);
        ((TextView)topTab.getTabAt(0).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(getResources().getString(R.string.tab_swaps));
        ((TextView)topTab.getTabAt(1).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(getResources().getString(R.string.tab_policies));
        ((TextView)topTab.getTabAt(2).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(getResources().getString(R.string.tab_activity));
        ((TextView)topTab.getTabAt(3).getCustomView().findViewById(R.id.tab_item_text_view))
                .setText(getResources().getString(R.string.tab_legislation));
    }
}
