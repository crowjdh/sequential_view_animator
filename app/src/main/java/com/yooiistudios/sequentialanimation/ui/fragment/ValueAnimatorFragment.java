package com.yooiistudios.sequentialanimation.ui.fragment;

import android.animation.ValueAnimator;

import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialValueAnimator;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 30.
 *
 * AnimationFragment
 */
public abstract class ValueAnimatorFragment extends BaseFragment<ValueAnimator, SerialValueAnimator> {

    @Override
    protected SerialValueAnimator makeAnimator() {
        return new SerialValueAnimator();
    }


}
