package com.yooiistudios.sequentialanimation.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.yooiistudios.sequentialanimation.ui.Animatable;
import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimator;
import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimator.TransitionProperty;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * BaseFragment
 */
public abstract class BaseFragment<T, S extends SerialAnimator> extends Fragment
        implements ViewProperty.AnimationListener,
        TransitionProperty.TransitionSupplier<T>,
        Animatable {
    private SerialAnimator mAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnimator = makeAnimator();
    }

    protected abstract S makeAnimator();

    public SerialAnimator getSequentialViewAnimator() {
        return mAnimator;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopAnimation();
    }

    @Override
    public void onAnimationEnd(ViewProperty property) {
        Log.i(getClass().getSimpleName(), "Animation end. Index : " + property.getViewIndex());
    }

    @NonNull
    @Override
    public abstract List<T> onSupplyTransitionList(View targetView);

    @Override
    public void stopAnimation() {
        getSequentialViewAnimator().cancelAllTransitions();
    }
}
