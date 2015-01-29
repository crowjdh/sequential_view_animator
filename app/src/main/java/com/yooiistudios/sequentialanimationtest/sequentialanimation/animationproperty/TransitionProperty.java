package com.yooiistudios.sequentialanimationtest.sequentialanimation.animationproperty;

import android.support.annotation.NonNull;

import com.yooiistudios.sequentialanimationtest.sequentialanimation.ViewProperty;

import java.util.List;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * AnimationProperty
 *  애니메이션의 속성을 정의
 */
public abstract class TransitionProperty<T> {
    public interface AnimationSupplier<T> {
        public @NonNull List<T> onSupplyTransitionList();
    }

    private AnimationSupplier<T> mAnimationSupplier;
    private long mInitialDelayInMillisec;
    private long mIntervalInMillisec;

    // TODO factory로 만들어야 하나?
    public TransitionProperty(AnimationSupplier<T> animationSupplier,
                              long initialDelayInMillisec, long intervalInMillisec) {
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

    protected AnimationSupplier<T> getAnimationSupplier() {
        return mAnimationSupplier;
    }

    public long getInitialDelayInMillisec() {
        return mInitialDelayInMillisec;
    }

    public long getIntervalInMillisec() {
        return mIntervalInMillisec;
    }

    public List<T> getTransitions() {
        return mAnimationSupplier.onSupplyTransitionList();
    }

    public abstract long getDelay(ViewProperty property);

    protected abstract long getDelayBetweenViews(ViewProperty property);

    protected abstract long getDelayBetweenAnimations(ViewProperty property);
}
