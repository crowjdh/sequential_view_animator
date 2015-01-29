package com.yooiistudios.sequentialanimationtest.ui.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialViewAnimator;
import com.yooiistudios.sequentialanimationtest.ui.AnimationFactory;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.AnimateViewProperty;
import com.yooiistudios.sequentialanimationtest.sequentialanimation.SequentialAnimationProperty;
import com.yooiistudios.sequentialanimationtest.ui.Animatable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * BaseFragment
 */
public abstract class BaseFragment extends Fragment
        implements AnimateViewProperty.AnimationListener,
        SequentialAnimationProperty.AnimationSupplier,
        Animatable {
    @Override
    public void onDetach() {
        super.onDetach();
        stopAnimation();
    }

    @Override
    public void onAnimationEnd(AnimateViewProperty property) {
        Log.i(getClass().getSimpleName(), "Animation end. Index : " + property.getViewIndex());
    }

    @NonNull
    @Override
    public List<Animation> onSupplyAnimationList() {
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
        SequentialViewAnimator.getInstance().cancelAllAnimations();
    }
}
