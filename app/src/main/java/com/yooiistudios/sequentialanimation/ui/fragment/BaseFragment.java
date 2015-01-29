package com.yooiistudios.sequentialanimation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimationAnimator;
import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimator;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;
import com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimator.TransitionProperty;
import com.yooiistudios.sequentialanimation.ui.Animatable;
import com.yooiistudios.sequentialanimation.ui.AnimationFactory;

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
    private SerialAnimator mAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnimator = new SerialAnimationAnimator();
    }

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
