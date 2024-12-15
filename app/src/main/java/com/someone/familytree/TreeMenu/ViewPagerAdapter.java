package com.someone.familytree.TreeMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
        return new PrivateFragment(treeMenuActivity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
