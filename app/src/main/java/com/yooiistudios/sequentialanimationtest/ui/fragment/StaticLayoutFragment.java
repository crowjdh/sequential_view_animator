package com.yooiistudios.sequentialanimationtest.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yooiistudios.sequentialanimationtest.R;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty.AnimationProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animator.AnimationAnimator;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * StaticLayoutFragment
 */
public class StaticLayoutFragment extends BaseFragment {
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

        AnimationAnimator animator = (AnimationAnimator)getSequentialViewAnimator();
        animator.putAnimateViewPropertyAt(property0, 0);
        animator.putAnimateViewPropertyAt(property1, 1);
        animator.putAnimateViewPropertyAt(property2, 2);
        animator.putAnimateViewPropertyAt(property3, 3);

        AnimationProperty transitionProperty = new AnimationProperty(this, 0, 1000);

        animator.setTransitionProperty(transitionProperty);

        animator.animate();
    }
}
