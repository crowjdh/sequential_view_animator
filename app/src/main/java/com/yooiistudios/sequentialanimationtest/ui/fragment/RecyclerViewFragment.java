package com.yooiistudios.sequentialanimationtest.ui.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yooiistudios.sequentialanimationtest.R;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty.AnimationProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animator.AnimationAnimator;
import com.yooiistudios.sequentialanimationtest.ui.SimpleAdapter;
import com.yooiistudios.sequentialanimationtest.ui.recyclerview.DividerItemDecoration;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * RecyclerViewFragment
 */
public class RecyclerViewFragment extends BaseFragment {
    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        if (rootView != null) {
            findViewsFromRootView(rootView);
            initRecyclerView();
        }
    }

    private void findViewsFromRootView(View rootView) {
        mRecycler = (RecyclerView)rootView.findViewById(R.id.recycler);
    }

    private void initRecyclerView() {
        initLayoutManager();
        initAdapter();
        initItemDecoration();

        configLayoutSpan();
    }

    private void initAdapter() {
        mAdapter = new SimpleAdapter();
        mRecycler.setAdapter(mAdapter);
    }

    private void initItemDecoration() {
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void initLayoutManager() {
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(mLayoutManager);
    }

    private void configLayoutSpan() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            setLayoutSpanOnPortrait();
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLayoutSpanOnLandscape();
        }
    }

    private void setLayoutSpanOnPortrait() {
        mLayoutManager.setSpanCount(1);
    }

    private void setLayoutSpanOnLandscape() {
        mLayoutManager.setSpanCount(2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        configLayoutSpan();
    }

    @Override
    public void startAnimation() {
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

        AnimationAnimator animator = (AnimationAnimator)getSequentialViewAnimator();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
            View itemView = mLayoutManager.findViewByPosition(i);
            ViewProperty property =
                    new ViewProperty.Builder()
                            .setView(itemView)
                            .setViewIndex(i)
                            .setAnimationListener(this)
                            .build();

            animator.putAnimateViewPropertyAt(property, i);
        }

        AnimationProperty transitionProperty = new AnimationProperty(this, 0, 1000);
        animator.setTransitionProperty(transitionProperty);

        animator.animate();
    }
}
