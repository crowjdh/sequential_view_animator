package com.yooiistudios.sequentialanimation.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimation.ui.AnimationFactory;
import com.yooiistudios.serialanimator.animator.SerialAnimationAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 30.
 *
 * AnimationFragment
 */
public abstract class AnimationFragment extends BaseFragment<Animation, SerialAnimationAnimator> {

    @Override
    protected SerialAnimationAnimator makeAnimator() {
        return new SerialAnimationAnimator();
    }

    @NonNull
    @Override
    public List<Animation> onSupplyTransitionList(View targetView) {
        Context context = getActivity().getApplicationContext();
        Animation fadeOutAnim = AnimationFactory.makeBottomFadeOutAnimation(context);
        Animation fadeInAnim = AnimationFactory.makeBottomFadeInAnimation(context);
        Animation fadeOutAnim2 = AnimationFactory.makeBottomFadeOutAnimation(context);
        Animation fadeInAnim2 = AnimationFactory.makeBottomFadeInAnimation(context);

        ArrayList<Animation> animList = new ArrayList<>();
        animList.add(fadeOutAnim);
        animList.add(fadeInAnim);
        animList.add(fadeOutAnim2);
        animList.add(fadeInAnim2);

        return animList;
    }
}
