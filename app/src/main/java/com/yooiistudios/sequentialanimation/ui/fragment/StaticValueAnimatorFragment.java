package com.yooiistudios.sequentialanimation.ui.fragment;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yooiistudios.sequentialanimation.R;
import com.yooiistudios.sequentialanimation.ui.AnimationFactory;
import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialValueAnimator;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * StaticLayoutFragment
 */
public class StaticValueAnimatorFragment extends ValueAnimatorFragment {
    private View mTarget0;
    private View mTarget1;
    private View mTarget2;
    private View mTarget3;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_static, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        if (rootView != null) {
            mTarget0 = rootView.findViewById(R.id.target0);
            mTarget1 = rootView.findViewById(R.id.target1);
            mTarget2 = rootView.findViewById(R.id.target2);
            mTarget3 = rootView.findViewById(R.id.target3);
        }
    }

    @NonNull
    @Override
    public List<ValueAnimator> onSupplyTransitionList(final View targetView) {
        ValueAnimator fadeOutAnimator = AnimationFactory.makeFadeOutAnimator();
//        ValueAnimator animator = valueAnimators.get(property.getTransitionIndex());
        fadeOutAnimator.setTarget(targetView);
        fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue("alpha");
                targetView.setAlpha(alpha);
            }
        });

        ValueAnimator fadeInAnimator = AnimationFactory.makeFadeInAnimator();
        fadeInAnimator.setTarget(targetView);
        fadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float)animation.getAnimatedValue("alpha");
                targetView.setAlpha(alpha);
            }
        });

        ValueAnimator fadeOutAnimator2 = AnimationFactory.makeFadeOutAnimator();
//        ValueAnimator animator = valueAnimators.get(property.getTransitionIndex());
        fadeOutAnimator.setTarget(targetView);
        fadeOutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float) animation.getAnimatedValue("alpha");
                targetView.setAlpha(alpha);
            }
        });

        ValueAnimator fadeInAnimator2 = AnimationFactory.makeFadeInAnimator();
        fadeInAnimator.setTarget(targetView);
        fadeInAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (Float)animation.getAnimatedValue("alpha");
                targetView.setAlpha(alpha);
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
        ViewProperty property0 =
                new ViewProperty.Builder()
                        .setView(mTarget0)
                        .setViewIndex(0)
                        .setAnimationListener(this)
                        .build();
        ViewProperty property1 =
                new ViewProperty.Builder()
                        .setView(mTarget1)
                        .setViewIndex(1)
                        .setAnimationListener(this)
                        .build();
        ViewProperty property2 =
                new ViewProperty.Builder()
                        .setView(mTarget2)
                        .setViewIndex(2)
                        .setAnimationListener(this)
                        .build();
        ViewProperty property3 =
                new ViewProperty.Builder()
                        .setView(mTarget3)
                        .setViewIndex(3)
                        .setAnimationListener(this)
                        .build();

//        ObjectAnimator.ofArgb().setTarget();
        SerialValueAnimator animator = (SerialValueAnimator)getSequentialViewAnimator();
        animator.putViewPropertyIfRoom(property0, 0);
        animator.putViewPropertyIfRoom(property1, 1);
        animator.putViewPropertyIfRoom(property2, 2);
        animator.putViewPropertyIfRoom(property3, 3);

        SerialValueAnimator.ValueAnimatorProperty transitionProperty
                = new SerialValueAnimator.ValueAnimatorProperty(this, 0, 1000);

        animator.setTransitionProperty(transitionProperty);

        animator.animate();
    }
}
