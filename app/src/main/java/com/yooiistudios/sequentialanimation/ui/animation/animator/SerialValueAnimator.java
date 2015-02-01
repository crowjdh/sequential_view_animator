package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yooiistudios.sequentialanimation.ui.animation.animator.SerialValueAnimator.ValueAnimatorProperty;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SerialValueAnimator
 *  android.view.animation.Animation 객체를 사용하는 애니메이터
 */
public class SerialValueAnimator extends SerialAnimator<ValueAnimatorProperty,
        SerialValueAnimator.ValueTransitionListener> {
    private static final String TAG = SerialValueAnimator.class.getSimpleName();
    private Map<ViewProperty, ValueAnimator> mValueAnimators;

    public SerialValueAnimator() {
        mValueAnimators = new HashMap<>();
    }

    @Override
    public void putAnimateViewPropertyAt(ViewProperty property, int idx) {
        super.putAnimateViewPropertyAt(property, idx);
//        if (isAnimating()) {
//            long transitionStartTime = getStartTimeInMilli() + getTransitionProperty().getDelay(property);
//            long transitionEndTime = transitionStartTime + getTransitionProperty().getTotalTransitionDuration();
//            long currentTime = System.currentTimeMillis();
////            Log.i(TAG, "transitionStartTime : " + transitionStartTime);
////            Log.i(TAG, "transitionEndTime : " + transitionEndTime);
////            Log.i(TAG, "currentTime : " + currentTime);
//
//            if (transitionStartTime < currentTime && transitionEndTime > currentTime) {
//                long timePast = currentTime - transitionStartTime;
//
//                List<ValueAnimator> valueAnimators = getTransitionProperty().getTransitions(property.getView());
//                ValueAnimator valueAnimator = valueAnimators.get(property.getTransitionIndex());
//                valueAnimator.setCurrentPlayTime(timePast);
//                valueAnimator.start();
//            }
//        }
    }

    @Override
    protected void transit(ViewProperty property, ValueTransitionListener transitionListener) {
        List<ValueAnimator> valueAnimators = getTransitionProperty().getTransitions(property.getView());
        if (isLastTransition(property)) {
            for (ValueAnimator animator : valueAnimators) {
                animator.addListener(transitionListener);
            }
        }
        ValueAnimator valueAnimator = valueAnimators.get(property.getTransitionIndex());
        valueAnimator.start();

        mValueAnimators.put(property, valueAnimator);
    }

    @Override
    protected boolean isReadyForTransition() {
        return super.isReadyForTransition() && !isAnimating();
    }

    @Override
    protected void beforeRequestTransition(ViewProperty property) {
        ViewTransientUtils.setState(property);
    }

    @Override
    public void cancelAllTransitions() {
        super.cancelAllTransitions();
        for (Map.Entry<ViewProperty, ValueAnimator> entry : mValueAnimators.entrySet()) {
            ViewProperty viewProperty = entry.getKey();
            ValueAnimator animator = entry.getValue();
            animator.cancel();

            List<ValueAnimator> valueAnimators = getTransitionProperty().getTransitions(viewProperty.getView());
            ValueAnimator valueAnimator = valueAnimators.get(0);
            valueAnimator.setCurrentPlayTime(0);
        }
    }

    @Override
    protected ValueTransitionListener makeTransitionListener(ViewProperty property) {
        ValueTransitionListener listener = new ValueTransitionListener(this, property);
        // FIXME 아래 라인 copy & paste 임. super 로 빼야 할듯
        listener.setIsLastTransition(isLastTransition(property));

        return listener;
    }

    protected static class ValueTransitionListener
            implements SerialAnimator.TransitionListener, ValueAnimator.AnimatorListener {
        private WeakReference<SerialAnimator> mAnimatorWeakReference;
        private ViewProperty mViewProperty;
        private boolean mIsLastTransition;

        public ValueTransitionListener(SerialAnimator animator, ViewProperty viewProperty) {
            mAnimatorWeakReference = new WeakReference<>(animator);
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

        private void notifyOnAnimationEnd() {
            ViewProperty.AnimationListener callback =
                    getViewProperty().getAnimationListener();

            ViewTransientUtils.clearState(getViewProperty());

            if (callback != null) {
                callback.onAnimationEnd(getViewProperty());
            }
        }


        @Override
        public void onAnimationEnd(Animator animation) {
            if (isLastTransition()) {
                notifyOnAnimationEnd();

                mAnimatorWeakReference.get().setAnimating(false);
            }
        }

        @Override public void onAnimationStart(Animator animation) { }
        @Override public void onAnimationCancel(Animator animation) { }
        @Override public void onAnimationRepeat(Animator animation) { }
    }

    public static class ValueAnimatorProperty extends SerialAnimator.TransitionProperty<ValueAnimator> {
        public ValueAnimatorProperty(@NonNull TransitionSupplier<ValueAnimator> transitionSupplier,
                                 long initialDelayInMillisec, long intervalInMillisec) {
            super(transitionSupplier, initialDelayInMillisec, intervalInMillisec);
        }

        @Override
        protected long getDelayBetweenTransitions(ViewProperty property) {
            long delayBeforeTransitions;
            View targetView = property.getView();
            if (property.getTransitionIndex() == 0) {
                delayBeforeTransitions = 0;
            } else {
                int previousTransitionIndex = property.getTransitionIndex() - 1;
                delayBeforeTransitions = getTransitions(targetView).get(previousTransitionIndex).getDuration();
            }

            return delayBeforeTransitions;
        }

        // TODO 추상화
        protected long getTotalTransitionDuration() {
            long delayBeforeTransitions = 0;
            List<ValueAnimator> transitions = getTransitions(null);
            for (int i = 0; i < transitions.size(); i++) {
                ValueAnimator animator = transitions.get(i);
                // FIXME i 말고 property.getTransitionIndex() 넣어야 하는데 생각해보자
                delayBeforeTransitions += animator.getDuration();
            }

            return delayBeforeTransitions;
        }
    }
}
