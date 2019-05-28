package com.politiswap.politiswap.NewVersion;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.politiswap.politiswap.R;
import com.politiswap.politiswap.databinding.ActivityPolicyBinding;

public class PolicyActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPolicyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_policy);

        AdRequest request = new AdRequest.Builder().build();
        mBinding.adView.loadAd(request);

        ImageButton navButton = mBinding.topOptionsIncludedLayout.navMenuButton;
        navButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_menu_button:
                ((DrawerLayout)findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                ((TextView)findViewById(R.id.nav_header_user_text)).setText("Default Name");
                break;
        }
    }
}
