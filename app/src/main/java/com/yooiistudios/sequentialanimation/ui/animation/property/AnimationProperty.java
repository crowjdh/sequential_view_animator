package com.yooiistudios.sequentialanimation.ui.animation.property;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.Animation;

import java.util.List;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * ObjectAnimatorProperty
 */
public class AnimationProperty extends TransitionProperty<Animation> {
    private static final String TAG = AnimationProperty.class.getSimpleName();

    public AnimationProperty(@NonNull AnimationSupplier<Animation> animationSupplier,
                             long initialDelayInMillisec, long intervalInMillisec) {
        super(animationSupplier, initialDelayInMillisec, intervalInMillisec);
    }

    @Override
    public List<Animation> getTransitions() {
        return getAnimationSupplier().onSupplyTransitionList();
    }

    @Override
    public long getDelay(ViewProperty property) {
        long delayBetweenAnimations = getDelayBetweenAnimations(property);
        long delay = getInitialDelayInMillisec() + delayBetweenAnimations;

        Log.i(TAG, "View Index\t\t: " + property.getViewIndex());
        if (property.getTransitionIndex() == 0) {
            long delayBetweenViews = getDelayBetweenViews(property);
            delay += delayBetweenViews;
            Log.i(TAG, "View Delay\t\t: " + delayBetweenViews);
        }

        Log.i(TAG, "Animation Index\t: " + property.getTransitionIndex());
        Log.i(TAG, "Animation Delay\t: " + delayBetweenAnimations);
        Log.i(TAG, "Delay\t\t\t: " + delay);

        return delay;
    }

    @Override
    protected long getDelayBetweenViews(ViewProperty property) {
        return getIntervalInMillisec() * property.getViewIndex();
    }

    @Override
    protected long getDelayBetweenAnimations(ViewProperty property) {
        long delayBetweenAnimations = 0;
        for (int i = 0; i < property.getTransitionIndex(); i++) {
            delayBetweenAnimations += getTransitions().get(i).getDuration()
                    * property.getTransitionIndex();
        }

        return delayBetweenAnimations;
    }
}
