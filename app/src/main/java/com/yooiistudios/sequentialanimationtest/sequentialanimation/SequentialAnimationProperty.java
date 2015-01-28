package com.yooiistudios.sequentialanimationtest.sequentialanimation;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.Animation;

import java.util.List;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * AnimationProperty
 *  애니메이션의 속성을 정의
 */
public class SequentialAnimationProperty {
    public interface AnimationSupplier {
        public @NonNull List<Animation> onSupplyAnimationList();
    }

    private static final String TAG = SequentialAnimationProperty.class.getSimpleName();

    private AnimationSupplier mAnimationSupplier;
    private long mInitialDelayInMillisec;
    private long mIntervalInMillisec;

    // TODO factory로 만들어야 하나?
    public SequentialAnimationProperty(@NonNull AnimationSupplier animationSupplier,
                                       long initialDelayInMillisec,
                                       long intervalInMillisec) {
        throwIfParametersAreInvalid(initialDelayInMillisec, intervalInMillisec);

        mAnimationSupplier = animationSupplier;
        mInitialDelayInMillisec = initialDelayInMillisec;
        mIntervalInMillisec = intervalInMillisec;
    }

    private void throwIfParametersAreInvalid(long initialDelayInMillisec,
                                             long intervalInMillisec) {
        if (initialDelayInMillisec < 0 || intervalInMillisec < 0) {
            throw new IllegalArgumentException();
        }
    }

    public List<Animation> getAnimations() {
        List<Animation> animations = mAnimationSupplier.onSupplyAnimationList();
        checkAnimationsIsValid(animations);

        return animations;
    }

    private void checkAnimationsIsValid(@NonNull List<Animation> animations) {
        if (animations.size() <= 0) {
            throw new IllegalStateException("MUST supply animation list via "
                    + AnimationSupplier.class.getSimpleName());
        }
    }

    public long getInitialDelayInMillisec() {
        return mInitialDelayInMillisec;
    }

    public long getIntervalInMillisec() {
        return mIntervalInMillisec;
    }

    public long getDelay(AnimateViewProperty property) {
        long delayBetweenAnimations = getDelayBetweenAnimations(property);
        long delay = getInitialDelayInMillisec() + delayBetweenAnimations;

        Log.i(TAG, "View Index\t\t: " + property.getViewIndex());
        if (property.getAnimationIndex() == 0) {
            long delayBetweenViews = getDelayBetweenViews(property);
            delay += delayBetweenViews;
            Log.i(TAG, "View Delay\t\t: " + delayBetweenViews);
        }

        Log.i(TAG, "Animation Index\t: " + property.getAnimationIndex());
        Log.i(TAG, "Animation Delay\t: " + delayBetweenAnimations);
        Log.i(TAG, "Delay\t\t\t: " + delay);

        return delay;
    }

    public long getDelayBetweenViews(AnimateViewProperty property) {
        return getIntervalInMillisec() * property.getViewIndex();
    }

    public long getDelayBetweenAnimations(AnimateViewProperty property) {
        long delayBetweenAnimations = 0;
        for (int i = 0; i < property.getAnimationIndex(); i++) {
            delayBetweenAnimations += getAnimations().get(i).getDuration()
                    * property.getAnimationIndex();
        }

        return delayBetweenAnimations;
    }
}
