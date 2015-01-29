package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimation.ui.AnimationListenerImpl;
import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.util.List;

import static com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimationAnimator.AnimationProperty;
import static com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimator.TransitionProperty;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SerialAnimationAnimator
 *  android.view.animation.Animation 객체를 사용하는 애니메이터
 */
public class SerialAnimationAnimator extends SerialAnimator<AnimationProperty> {

    @Override
    protected void transit(ViewProperty property) {
        List<Animation> animations = getTransitionProperty().getTransitions();
        Animation animation = animations.get(property.getTransitionIndex());

        startAnimation(property, animation);
    }

    @Override
    protected void beforeRequestTransition(ViewProperty property) {
        ViewTransientUtils.setState(property);
    }

    @Override
    public void cancelAllTransitions() {
        super.cancelAllTransitions();
        for (int i = 0; i < getViewProperties().size(); i++) {
            int propertyIndex = getViewProperties().keyAt(i);
            ViewProperty property = getViewProperties().get(propertyIndex);

            property.getView().clearAnimation();
        }
    }

    private void startAnimation(ViewProperty property, Animation animation) {
        animation.setAnimationListener(makeTransitionListener(property));
        View viewToAnimate = property.getView();
        viewToAnimate.startAnimation(animation);
    }


    private SerialTransitionListener makeTransitionListener(ViewProperty property) {
        SerialTransitionListener listener = new SerialTransitionListener(property);
        listener.setIsLastTransition(isLastTransition(property));

        return listener;
    }

    // TODO ValueAnimator 도 구현해야 하니 인터페이스로 묶어야 할듯..
    private static class SerialTransitionListener extends AnimationListenerImpl {
        private ViewProperty mViewProperty;
        private boolean mIsLastTransition;

        public SerialTransitionListener(ViewProperty viewProperty) {
            mViewProperty = viewProperty;
        }

        public ViewProperty getViewProperty() {
            return mViewProperty;
        }

        public boolean isLastTransition() {
            return mIsLastTransition;
        }

        public void setIsLastTransition(boolean isLastTransition) {
            mIsLastTransition = isLastTransition;
        }

        @Override
        public final void onAnimationEnd(Animation animation) {
            super.onAnimationEnd(animation);

            if (isLastTransition()) {
                notifyOnAnimationEnd();
            }
        }

        private void notifyOnAnimationEnd() {
            ViewProperty.AnimationListener callback =
                    getViewProperty().getAnimationListener();

            ViewTransientUtils.clearState(getViewProperty());

            if (callback != null) {
                callback.onAnimationEnd(getViewProperty());
            }
        }
    }

    public static class AnimationProperty extends SerialAnimator.TransitionProperty<Animation> {
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
}
