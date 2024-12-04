package com.someone.familytree.TreeMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.someone.familytree.R;

import java.util.zip.Inflater;

public class TreeMenuActivity extends AppCompatActivity {

    public ItemAdapter itemAdapter;
    Fragment currentFragment;
    int selected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_menu);

        ImageButton overflow_menu = findViewById(R.id.overflow_menu);
        overflow_menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(TreeMenuActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_tree_menu, popupMenu.getMenu());
            updateOptionsMenu(popupMenu.getMenu());
            popupMenu.show();
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Offline");
            } else {
                tab.setText("Online");
            }
        }
        ).attach();

        viewPager.setUserInputEnabled(true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                if (position == 0) {
//                    switchFragment(offlineFragment); // Switch to offline fragment
//                } else if (position == 1) {
//                    switchFragment(onlineFragment);  // Switch to online fragment
//                }
            }
        });
    }

    private void updateOptionsMenu(Menu menu) {
    }

    private void switchFragment(Fragment fragment) {
        if(currentFragment != fragment) {
            currentFragment = fragment;
        }
    }
}