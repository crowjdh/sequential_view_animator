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
import com.yooiistudios.sequentialanimationtest.ui.SimpleAdapter;
import com.yooiistudios.sequentialanimationtest.ui.recyclerview.DividerItemDecoration;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * RecyclerViewFragment
 */
public class RecyclerViewFragment extends BaseFragment {
    private RecyclerView mRecycler;
    private GridLayoutManager mGridLayoutManager;

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
        configLayoutSpan();
        setAdapter();

        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void setAdapter() {
        mRecycler.setAdapter(new SimpleAdapter());
    }

    private void initLayoutManager() {
        mGridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(mGridLayoutManager);
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
        mGridLayoutManager.setSpanCount(1);
    }

    private void setLayoutSpanOnLandscape() {
        mGridLayoutManager.setSpanCount(2);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        configLayoutSpan();
    }

    @Override
    protected void animate() {
//        AnimateViewProperty property0 =
//                new AnimateViewProperty.Builder()
//                        .setView(mTarget0)
//                        .setViewIndex(0)
//                        .setAnimationListener(this)
//                        .build();
//        AnimateViewProperty property1 =
//                new AnimateViewProperty.Builder()
//                        .setView(mTarget1)
//                        .setViewIndex(1)
//                        .setAnimationListener(this)
//                        .build();
//        AnimateViewProperty property2 =
//                new AnimateViewProperty.Builder()
//                        .setView(mTarget2)
//                        .setViewIndex(2)
//                        .setAnimationListener(this)
//                        .build();
//        AnimateViewProperty property3 =
//                new AnimateViewProperty.Builder()
//                        .setView(mTarget3)
//                        .setViewIndex(3)
//                        .setAnimationListener(this)
//                        .build();
//
//        SequentialViewAnimator animatorInstance = SequentialViewAnimator.getInstance();
//        animatorInstance.putAnimateViewPropertyAt(property0, 0);
//        animatorInstance.putAnimateViewPropertyAt(property1, 1);
//        animatorInstance.putAnimateViewPropertyAt(property2, 2);
//        animatorInstance.putAnimateViewPropertyAt(property3, 3);
//
//        SequentialAnimationProperty sequentialAnimationProperty =
//                new SequentialAnimationProperty(this, 0, 1000);
//
//        animatorInstance.setSequentialAnimationProperty(sequentialAnimationProperty);
//
//        animatorInstance.animate();
    }

}
