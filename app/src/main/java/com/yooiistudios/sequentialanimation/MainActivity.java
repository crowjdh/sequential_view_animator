package com.yooiistudios.sequentialanimation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yooiistudios.sequentialanimation.ui.fragment.BaseFragment;
import com.yooiistudios.sequentialanimation.ui.fragment.RecyclerViewAnimationFragment;
import com.yooiistudios.sequentialanimation.ui.fragment.RecyclerViewValueAnimatorFragment;
import com.yooiistudios.sequentialanimation.ui.fragment.StaticAnimationFragment;
import com.yooiistudios.sequentialanimation.ui.fragment.StaticValueAnimatorFragment;


public class MainActivity extends ActionBarActivity
        implements View.OnClickListener {
    private static final String TAG_FRAGMENT_STATIC_ANIMATION = "TAG_FRAGMENT_STATIC_ANIMATION";
    private static final String TAG_FRAGMENT_RECYCLER_ANIMATION = "TAG_FRAGMENT_RECYCLER_ANIMATION";
    private static final String TAG_FRAGMENT_STATIC_VALUE_ANIMATOR = "TAG_FRAGMENT_STATIC_VALUE_ANIMATOR";
    private static final String TAG_FRAGMENT_RECYCLER_VALUE_ANIMATOR = "TAG_FRAGMENT_RECYCLER_VALUE_ANIMATOR";
    private static final String[] TAG_LIST = {
            TAG_FRAGMENT_STATIC_ANIMATION,
            TAG_FRAGMENT_RECYCLER_ANIMATION,
            TAG_FRAGMENT_STATIC_VALUE_ANIMATOR,
            TAG_FRAGMENT_RECYCLER_VALUE_ANIMATOR
    };
    private static final String DEFAULT_TAG_FRAGMENT = TAG_FRAGMENT_STATIC_VALUE_ANIMATOR;

    private static final String STATE_KEY_FRAGMENT = "STATE_KEY_FRAGMENT";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        findViewById(R.id.animate).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        initFragment(savedInstanceState);
    }

    private void initFragment(Bundle savedInstanceState) {
        String tag;
        if (savedInstanceState != null) {
            tag = savedInstanceState.getString(STATE_KEY_FRAGMENT, DEFAULT_TAG_FRAGMENT);
        } else {
            tag = DEFAULT_TAG_FRAGMENT;
        }
        showFragmentByTag(tag);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        String fragmentTag = getTagOfCurrentFragment();

        if (fragmentTag != null) {
            outState.putString(STATE_KEY_FRAGMENT, fragmentTag);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_anim_static:
                showAnimationStaticFragment();
                return true;
            case R.id.action_anim_recycler:
                showAnimationRecyclerFragment();
            case R.id.action_value_static:
                showValueAnimatorStaticFragment();
            case R.id.action_value_recycler:
                showValueAnimatorRecyclerFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getTagOfCurrentFragment() {
        for (String tag : TAG_LIST) {
            if (getSupportFragmentManager().findFragmentByTag(tag) != null) {
                return tag;
            }
        }
        return null;
    }

    private void showAnimationStaticFragment() {
        showFragmentByTag(TAG_FRAGMENT_STATIC_ANIMATION);
    }

    private void showAnimationRecyclerFragment() {
        showFragmentByTag(TAG_FRAGMENT_RECYCLER_ANIMATION);
    }

    private void showValueAnimatorStaticFragment() {
        showFragmentByTag(TAG_FRAGMENT_STATIC_VALUE_ANIMATOR);
    }

    private void showValueAnimatorRecyclerFragment() {
        showFragmentByTag(TAG_FRAGMENT_RECYCLER_VALUE_ANIMATOR);
    }

    private void removeFragments(FragmentTransaction transaction) {
        for (String tag : TAG_LIST) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                transaction.remove(fragment);
            }
        }
    }

    private void showFragmentByTag(String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            removeFragments(transaction);
            transaction.replace(R.id.fragment_container, makeFragmentByTag(tag), tag);
            transaction.commit();
        }
    }

    private Fragment makeFragmentByTag(String tag) {
        switch (tag) {
            case TAG_FRAGMENT_STATIC_ANIMATION:
                return new StaticAnimationFragment();
            case TAG_FRAGMENT_RECYCLER_ANIMATION:
                return new RecyclerViewAnimationFragment();
            case TAG_FRAGMENT_STATIC_VALUE_ANIMATOR:
                return new StaticValueAnimatorFragment();
            case TAG_FRAGMENT_RECYCLER_VALUE_ANIMATOR:
                return new RecyclerViewValueAnimatorFragment();
            default:
                return null;
        }
    }

    private BaseFragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getTagOfCurrentFragment());

        return (fragment instanceof BaseFragment) ? (BaseFragment)fragment : null;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.animate:
                getCurrentFragment().startAnimation();
                break;
            case R.id.cancel:
                getCurrentFragment().stopAnimation();
                break;
        }
    }
}
