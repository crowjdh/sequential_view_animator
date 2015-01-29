package com.yooiistudios.sequentialanimationtest.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty.TransitionProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animator.AnimationAnimator;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.animator.SequentialViewAnimator;
import com.yooiistudios.sequentialanimationtest.ui.Animatable;
import com.yooiistudios.sequentialanimationtest.ui.AnimationFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment
        implements ViewProperty.AnimationListener,
        TransitionProperty.AnimationSupplier<Animation>,
        Animatable {
    private SequentialViewAnimator mAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnimator = new AnimationAnimator();
    }

    public SequentialViewAnimator getSequentialViewAnimator() {
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
    public List<Animation> onSupplyTransitionList() {
        Context context = getActivity().getApplicationContext();
        Animation fadeOutAnim = AnimationFactory.makeBottomFadeOutAnimation(context);
        Animation fadeInAnim = AnimationFactory.makeBottomFadeInAnimation(context);

        ArrayList<Animation> animList = new ArrayList<>();
        animList.add(fadeOutAnim);
        animList.add(fadeInAnim);

        return animList;
    }

    @Override
    public void stopAnimation() {
        getSequentialViewAnimator().cancelAllTransitions();
    }
}
