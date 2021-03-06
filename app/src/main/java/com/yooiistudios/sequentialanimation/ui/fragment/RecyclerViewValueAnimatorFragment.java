package com.yooiistudios.sequentialanimation.ui.fragment;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.yooiistudios.sequentialanimation.R;
import com.yooiistudios.sequentialanimation.ui.AnimationFactory;
import com.yooiistudios.sequentialanimation.ui.recyclerview.DividerItemDecoration;
import com.yooiistudios.sequentialanimation.ui.recyclerview.SimpleAdapter;
import com.yooiistudios.serialanimator.animator.SerialAnimator;
import com.yooiistudios.serialanimator.animator.SerialValueAnimator;
import com.yooiistudios.serialanimator.property.ViewProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * RecyclerViewFragment
 */
public class RecyclerViewValueAnimatorFragment extends ValueAnimatorFragment
        implements SimpleAdapter.OnBindViewHolderListener {
    private static final String TAG = RecyclerViewValueAnimatorFragment.class.getSimpleName();

    private RecyclerView mRecycler;
    private SimpleAdapter mAdapter;
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
            initAnimator();
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

    private void initAnimator() {
        SerialValueAnimator animator = (SerialValueAnimator)getSequentialViewAnimator();

        SerialValueAnimator.ValueAnimatorProperty transitionProperty
                = new SerialValueAnimator.ValueAnimatorProperty(this, 0, 500);
        animator.setTransitionProperty(transitionProperty);
    }

    private void initAdapter() {
        mAdapter = new SimpleAdapter();
        mAdapter.setOnBindViewHolderListener(this);
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

    @NonNull
    @Override
    public List<ValueAnimator> onSupplyTransitionList(final View targetView) {
        ValueAnimator fadeOutAnimator = AnimationFactory.makeIncreasingProgressAnimator();
        fadeOutAnimator.setTarget(targetView);
        fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (Integer) animation.getAnimatedValue("progress");
                ((ProgressBar)targetView.findViewById(R.id.progressBar)).setProgress(progress);
            }
        });

        ValueAnimator fadeInAnimator = AnimationFactory.makeDecreasingProgressAnimator();
        fadeInAnimator.setTarget(targetView);
        fadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (Integer) animation.getAnimatedValue("progress");
                ((ProgressBar)targetView.findViewById(R.id.progressBar)).setProgress(progress);
            }
        });

        ValueAnimator fadeOutAnimator2 = AnimationFactory.makeIncreasingProgressAnimator();
        fadeOutAnimator2.setTarget(targetView);
        fadeOutAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (Integer) animation.getAnimatedValue("progress");
                ((ProgressBar)targetView.findViewById(R.id.progressBar)).setProgress(progress);
            }
        });

        ValueAnimator fadeInAnimator2 = AnimationFactory.makeDecreasingProgressAnimator();
        fadeInAnimator2.setTarget(targetView);
        fadeInAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (Integer) animation.getAnimatedValue("progress");
                ((ProgressBar)targetView.findViewById(R.id.progressBar)).setProgress(progress);
            }
        });

        List<ValueAnimator> animators = new ArrayList<>();
        animators.add(fadeOutAnimator);
        animators.add(fadeInAnimator);
        animators.add(fadeOutAnimator2);
        animators.add(fadeInAnimator2);

        return animators;
    }

    @Override
    public void startAnimation() {
        getSequentialViewAnimator().animate();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        SerialAnimator animator = getSequentialViewAnimator();

        ViewProperty property =
                new ViewProperty.Builder()
                        .setView(viewHolder.itemView)
                        .setViewIndex(i)
                        .setAnimationListener(this)
                        .build();
        animator.putViewPropertyIfRoom(property, i);
    }
}
