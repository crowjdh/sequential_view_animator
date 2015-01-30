package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;

import com.yooiistudios.sequentialanimation.ui.AnimationListenerImpl;
import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.util.List;

import static com.yooiistudios.sequentialanimation.ui.animation.animator.SerialAnimationAnimator.AnimationProperty;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SerialAnimationAnimator
 *  android.view.animation.Animation 객체를 사용하는 애니메이터
 */
public class SerialAnimationAnimator extends SerialAnimator<AnimationProperty,
        SerialAnimationAnimator.AnimationTransitionListener> {

    @Override
    protected void transit(ViewProperty property, AnimationTransitionListener transitionListener) {
        List<Animation> animations = getTransitionProperty().getTransitions(property.getView());
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
//        animation.setAnimationListener(makeTransitionListener(property));
        View viewToAnimate = property.getView();
        viewToAnimate.startAnimation(animation);
    }

    @Override
    protected AnimationTransitionListener makeTransitionListener(ViewProperty property) {
        AnimationTransitionListener listener = new AnimationTransitionListener(property);
        // FIXME 아래 라인 copy & paste 임. super 로 빼야 할듯
        listener.setIsLastTransition(isLastTransition(property));

        return listener;
    }

    protected static class AnimationTransitionListener extends AnimationListenerImpl
            implements SerialAnimator.TransitionListener {
        private ViewProperty mViewProperty;
        private boolean mIsLastTransition;

        public AnimationTransitionListener(ViewProperty viewProperty) {
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
        public AnimationProperty(@NonNull TransitionSupplier<Animation> transitionSupplier,
                                 long initialDelayInMillisec, long intervalInMillisec) {
            super(transitionSupplier, initialDelayInMillisec, intervalInMillisec);
        }

        @Override
        protected long getDelayBeforeTransitions(ViewProperty property) {
            long delayBeforeTransitions = 0;
            View targetView = property.getView();
            for (int i = 0; i < property.getTransitionIndex(); i++) {
                delayBeforeTransitions += getTransitions(targetView).get(i).getDuration()
                        * property.getTransitionIndex();
            }

            return delayBeforeTransitions;
        }
    }
}