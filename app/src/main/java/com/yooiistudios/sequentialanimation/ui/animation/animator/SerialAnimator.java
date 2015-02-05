package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
import com.yooiistudios.sequentialanimation.ui.animation.property.AbstractViewProperty;
import com.yooiistudios.sequentialanimation.ui.animation.property.ViewProperty;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * SerialAnimator
 *  순차적으로 뷰들을 animating 하는 클래스
 */
public abstract class SerialAnimator<T extends SerialAnimator.TransitionProperty,
        S extends SerialAnimator.TransitionListener> {
    protected interface TransitionListener { }

    private final SparseArray<ViewProperty> mViewProperties;
    private T mTransitionProperty;
    private TransitionHandler mTransitionHandler;
    private long mStartTimeInMilli;
//    private boolean mIsAnimating;

    protected SerialAnimator() {
        mViewProperties = new SparseArray<>();
        mTransitionHandler = new TransitionHandler(this);
    }

    public void animate() {
        if (isReadyForTransition()) {
            onAnimate();
            cancelAllTransitions();
            prepareForNewTransitionSequence();
            runSequentialTransition();
        }
    }

    protected abstract void onAnimate();

    public void cancelAllTransitions() {
        mTransitionHandler.removeCallbacksAndMessages(null);
    }

    public void cancelTransition(int index) {
        mTransitionHandler.removeMessages(index);
    }

    private void prepareForNewTransitionSequence() {
        mStartTimeInMilli = System.currentTimeMillis();
//        mIsAnimating = true;

        int viewCount = mViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // SparseArray 의 keyAt 메서드 특성상 아래와 같이 쿼리하면 key 의 ascending order 로 결과값이 나온다.
            int propertyIndex = mViewProperties.keyAt(i);
            ViewProperty viewProperty = mViewProperties.get(propertyIndex);
            viewProperty.resetTransitionInfo();
        }
    }

    private void runSequentialTransition() {
        int viewCount = mViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // SparseArray 의 keyAt 메서드 특성상 아래와 같이 쿼리하면 key 의 ascending order 로 결과값이 나온다.
            int propertyIndex = mViewProperties.keyAt(i);
            ViewProperty property = mViewProperties.get(propertyIndex);
            requestTransition(property);
        }
    }

    protected void requestTransitionWithDelayConsume(ViewProperty viewProperty, long consume) {
        long delay = getTransitionProperty().getDelay(viewProperty) - consume;
        Message message = Message.obtain();
        message.obj = viewProperty;
        // 핸들러에서 메시지를 제거하기 위해 what 값이 필요함.
        message.what = viewProperty.getViewIndex();

        mTransitionHandler.sendMessageDelayed(message, delay);
    }

    protected void requestTransition(ViewProperty viewProperty) {
        requestTransitionWithDelayConsume(viewProperty, 0);
    }

    protected void requestNextTransition(ViewProperty previousViewProperty) {
        if (!isLastTransition(previousViewProperty)) {
            AbstractViewProperty clonedViewProperty =
                    previousViewProperty.makeShallowCloneWithDeepTransitionInfoCloneWhenPossible();
            if (clonedViewProperty instanceof ViewProperty) {
                ViewProperty legitViewProperty = (ViewProperty)clonedViewProperty;
                legitViewProperty.changeToNextTransitionState();

                long consume = previousViewProperty.getTransitionInfo().currentPlayTime;
                requestTransitionWithDelayConsume(legitViewProperty, consume);
            }
        }
    }

    protected void transitAndRequestNext(ViewProperty property) {
        transit(property);
        requestNextTransition(property);
    }

    private void transit(ViewProperty property) {
        ViewTransientUtils.setState(property);
        S listener = makeTransitionListener(property);
        onTransit(property, listener);
    }

    protected abstract void onTransit(ViewProperty property, S transitionListener);

    public void putViewPropertyIfRoom(ViewProperty requestedViewProperty, int idx) {
        ViewProperty viewProperty = mViewProperties.get(idx);
        if (viewProperty == null) {
            mViewProperties.put(idx, requestedViewProperty);
        } else {
            // remove handler
            cancelTransition(idx);
            mViewProperties.get(idx).setView(requestedViewProperty.getView());
            mViewProperties.get(idx).setAnimationListener(requestedViewProperty.getAnimationListener());
        }
    }

    public void setTransitionProperty(T transitionProperty) {
        mTransitionProperty = transitionProperty;
    }

    protected boolean isLastTransition(ViewProperty property) {
        List transitions = getTransitionProperty().getTransitions(property.getView());
        return property.getTransitionInfo().index == transitions.size() - 1;
    }

    protected boolean isReadyForTransition() {
        return mViewProperties.size() > 0 && mTransitionProperty != null;
    }

    protected long getStartTimeInMilli() {
        return mStartTimeInMilli;
    }

    protected T getTransitionProperty() {
        return mTransitionProperty;
    }

    protected abstract S makeTransitionListener(ViewProperty property);

    protected SparseArray<ViewProperty> getViewProperties() {
        return mViewProperties;
    }

    private static class TransitionHandler extends Handler {
        private WeakReference<SerialAnimator> mAnimatorWeakReference;

        public TransitionHandler(SerialAnimator animator) {
            mAnimatorWeakReference = new WeakReference<>(animator);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            validateMessage(message);

            final ViewProperty property = getProperty(message);
            boolean animate = property.getView() != null;

            if (animate) {
                SerialAnimator animator = mAnimatorWeakReference.get();

                animator.transitAndRequestNext(property);
            }
        }

        private ViewProperty getProperty(Message message) {
            return (ViewProperty)message.obj;
        }

        private void validateMessage(Message message) {
            if (!(message.obj instanceof ViewProperty)) {
                throw new IllegalStateException("msg.obj MUST BE an instance of subclass of "
                        + ViewProperty.class.getSimpleName());
            }
        }
    }

    public static abstract class TransitionProperty<T> {
        public interface TransitionSupplier<T> {
            public @NonNull List<T> onSupplyTransitionList(View targetView);
        }

        private TransitionSupplier<T> mTransitionSupplier;
        private long mInitialDelayInMillisec;
        private long mIntervalInMillisec;

        // DOUBT factory 로 만들어야 하나?
        public TransitionProperty(TransitionSupplier<T> transitionSupplier,
                                  long initialDelayInMillisec, long intervalInMillisec) {
            throwIfParametersAreInvalid(initialDelayInMillisec, intervalInMillisec);

            mTransitionSupplier = transitionSupplier;
            mInitialDelayInMillisec = initialDelayInMillisec;
            mIntervalInMillisec = intervalInMillisec;
        }

        private void throwIfParametersAreInvalid(long initialDelayInMillisec,
                                                 long intervalInMillisec) {
            if (initialDelayInMillisec < 0 || intervalInMillisec < 0) {
                throw new IllegalArgumentException();
            }
        }

        protected TransitionSupplier<T> getTransitionSupplier() {
            return mTransitionSupplier;
        }

        public long getInitialDelayInMillisec() {
            return mInitialDelayInMillisec;
        }

        public long getIntervalInMillisec() {
            return mIntervalInMillisec;
        }

        public List<T> getTransitions(View targetView) {
            return mTransitionSupplier.onSupplyTransitionList(targetView);
        }

        protected List<T> getDummyTransitions() {
            return getTransitions(null);
        }

        protected final long getDelay(ViewProperty property) {
            long delay = getInitialDelayInMillisec() + getPreviousTransitionDuration(property);

            if (property.getTransitionInfo().index == 0) {
                delay += getDelayForInitialTransition(property);
            }

            return delay;
        }

        protected final long getDelayForInitialTransition(ViewProperty property) {
            return getIntervalInMillisec() * property.getViewIndex();
        }

        protected long getTotalTransitionDuration() {
            long totalDuration = 0;//getDelay(viewProperty);
            List<T> transitions = getDummyTransitions();
            for (int i = 0; i < transitions.size(); i++) {
                T transition = transitions.get(i);
                totalDuration += getDuration(transition);
            }

            return totalDuration;
        }

        /**
         * 해당 뷰의 트랜지션 시작 시간을 기준으로 현재 트랜지션이 언제 시작되야 하는지의 정보
         * eg, 2번째 뷰의 3번째 트랜지션의 경우 3번째 트랜지션 시작 전까지의 시간
         * @param property
         * @return
         */
        protected long getPreviousTransitionDuration(ViewProperty property) {
            long delayBeforeTransitions;
//            View targetView = property.getView();
            if (property.getTransitionInfo().index == 0) {
                delayBeforeTransitions = 0;
            } else {
                int previousTransitionIndex = property.getTransitionInfo().index - 1;
                T previousTransition = getTransitions(null).get(previousTransitionIndex);
                delayBeforeTransitions = getDuration(previousTransition);
            }

            return delayBeforeTransitions;
        }

        protected long getDelaySinceBase(ViewProperty property) {
            long delay = 0;
            List<T> transitions = getDummyTransitions();
            for (int i = 0; i < property.getTransitionInfo().index; i++) {
                T transition = transitions.get(i);
                delay += getDuration(transition);
            }

            return delay;
        }

        protected boolean inTimeToTransit(ViewProperty property, long timePast) {
//            long delay = getDelay(property);
            long startTime = getDelayForInitialTransition(property);
            long endTime = startTime + getTotalTransitionDuration();

            if (property.getViewIndex() == 0) {
                Log.i("inTimeToTransit", "view index : " + property.getViewIndex());
                Log.i("inTimeToTransit", "timePast : " + timePast);
                Log.i("inTimeToTransit", "startTime : " + startTime);
                Log.i("inTimeToTransit", "endTime : " + endTime);
            }

            return timePast > startTime && timePast < endTime;
        }

        protected boolean shouldTransitInFuture(ViewProperty property, long timePast) {
            long delayForInitialTransition = getDelayForInitialTransition(property);
            Log.i("shouldTransitInFuture", "delayForInitialTransition : " +
                    delayForInitialTransition);
            Log.i("shouldTransitInFuture", "timePast : " + timePast);
            return delayForInitialTransition > timePast;
        }

        protected int getTransitionIndexForProperty(ViewProperty property, long timePast) {
            List<T> transitions = getDummyTransitions();
            long transitionStartTime = getDelayForInitialTransition(property);
//            long playTime = property.getTransitionInfo().currentPlayTime;
            int transitionIndex = 0;
            for (int i = 0; i < transitions.size(); i++) {
                T transition = transitions.get(i);
                long transitionEndTime = transitionStartTime + getDuration(transition);

                if (timePast > transitionStartTime && timePast < transitionEndTime) {
                    transitionIndex = i;
                    break;
                } else {
                    transitionStartTime = transitionEndTime;
                }
            }

            return transitionIndex;
        }

        protected long getTotalDurationBefore(ViewProperty viewProperty) {
            List<T> transitionList = getTransitions(null);
            long delayBeforeTransition = 0;
            for (int i = 0; i < viewProperty.getTransitionInfo().index; i++) {
                T transition = transitionList.get(i);
                delayBeforeTransition += getDuration(transition);
            }

            return delayBeforeTransition;
        }

        protected abstract long getDuration(T transition);
    }
}
