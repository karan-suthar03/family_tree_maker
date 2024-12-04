package com.someone.familytree.TreeMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    TreeMenuActivity treeMenuActivity;

    public ViewPagerAdapter(TreeMenuActivity fa) {
        super(fa);
        treeMenuActivity = fa;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new OnlineFragment(treeMenuActivity);
        }
        return new OfflineFragment(treeMenuActivity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
