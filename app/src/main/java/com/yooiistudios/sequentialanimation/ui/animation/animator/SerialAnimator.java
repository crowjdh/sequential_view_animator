package com.yooiistudios.sequentialanimation.ui.animation.animator;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

import com.yooiistudios.sequentialanimation.ui.animation.ViewTransientUtils;
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

//    public void removePropertyWithIndex(int idx) {
//        mViewProperties.delete(idx);
//    }

    public void putAnimateViewPropertyAt(ViewProperty property, int idx) {
        mViewProperties.put(idx, property);
    }

    public void setTransitionProperty(T transitionProperty) {
        mTransitionProperty = transitionProperty;
    }

    public void animate() {
        if (isReadyForTransition()) {
            cancelAllTransitions();
            prepareForNewTransitionSequence();
            runSequentialTransition();
        }
    }

    public void cancelAllTransitions() {
        mTransitionHandler.removeCallbacksAndMessages(null);
    }

    private void runSequentialTransition() {
        int viewCount = mViewProperties.size();
        for (int i = 0; i < viewCount; i++) {
            // SparseArray 의 keyAt 메서드 특성상 아래와 같이 쿼리하면 key 의 ascending order 로 결과값이 나온다.
            int propertyIndex = mViewProperties.keyAt(i);
            ViewProperty property = mViewProperties.get(propertyIndex);
//            beforeRequestTransition(property);
            requestTransition(property);
        }
    }

    protected void requestTransition(ViewProperty property) {
        requestTransition(property, 0);
    }

    protected void requestTransition(ViewProperty property, long consume) {
        long delay = getTransitionProperty().getDelay(property) - consume;
        Message message = Message.obtain();
        message.obj = property;

        mTransitionHandler.sendMessageDelayed(message, delay);
    }

    protected void requestNextTransition(ViewProperty property) {
        if (!isLastTransition(property)) {
            long previousPlayTime = property.getTransitionInfo().getCurrentPlayTime();
            property.increaseTransitionIndexAndResetCurrentPlayTime();

            requestTransition(property, previousPlayTime);
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

//    private void beforeRequestTransition(ViewProperty property) {
//        ViewTransientUtils.setState(property);
//    }

    protected boolean isLastTransition(ViewProperty property) {
        List transitions = getTransitionProperty().getTransitions(property.getView());
        return property.getTransitionInfo().getIndex() == transitions.size() - 1;
    }

    protected boolean isReadyForTransition() {
        return mViewProperties.size() > 0 && mTransitionProperty != null;
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

//    protected void onAnimationEnd() {
//        mIsAnimating = false;
//    }

//    protected boolean isAnimating() {
//        return mIsAnimating;
//    }

//    protected void setAnimating(boolean isAnimating) {
//        mIsAnimating = isAnimating;
//    }

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
            long delayBetweenTransitions = getDelayOffset(property);
            long delay = getInitialDelayInMillisec() + delayBetweenTransitions;

            if (property.getTransitionInfo().getIndex() == 0) {
                delay += getBaseDelay(property);
            }

            return delay;
        }

        protected final long getBaseDelay(ViewProperty property) {
            return getIntervalInMillisec() * property.getViewIndex();
        }

        protected long getTotalTransitionDuration(ViewProperty viewProperty) {
            long totalDuration = getDelay(viewProperty);
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
        protected long getDelayOffset(ViewProperty property) {
            long delayBeforeTransitions;
            View targetView = property.getView();
            if (property.getTransitionInfo().getIndex() == 0) {
                delayBeforeTransitions = 0;
            } else {
                int previousTransitionIndex = property.getTransitionInfo().getIndex() - 1;
                T previousTransition = getTransitions(targetView).get(previousTransitionIndex);
                delayBeforeTransitions = getDuration(previousTransition);
            }

            return delayBeforeTransitions;
        }

        protected long getDelaySinceBase(ViewProperty property) {
            long delay = 0;
            List<T> transitions = getDummyTransitions();
            for (int i = 0; i < property.getTransitionInfo().getIndex(); i++) {
                T transition = transitions.get(i);
                delay += getDuration(transition);
            }

            return delay;
        }

        protected boolean inTimeToTransit(ViewProperty property, long initialStartTime) {
            long startTime = initialStartTime + getDelay(property);
            long endTime = startTime + getTotalTransitionDuration(property);
            return isTimeToTransit(startTime, endTime);
        }

        protected int getTransitionIndexForProperty(ViewProperty property) {
            List<T> transitions = getDummyTransitions();
            long transitionStartTime = getBaseDelay(property);
            long playTime = property.getTransitionInfo().getCurrentPlayTime();
            int transitionIndex = 0;
            for (int i = 0; i < transitions.size(); i++) {
                T transition = transitions.get(i);
                long transitionEndTime = transitionStartTime + getDuration(transition);

                if (playTime > transitionStartTime && playTime < transitionEndTime) {
                    transitionIndex = i;
                    break;
                } else {
                    transitionStartTime = transitionEndTime;
                }
            }

            return transitionIndex;
        }

        // TODO 날짜 비교 클래스 만들고 추상화
        private static boolean isTimeToTransit(long startTime, long endTime) {
            long currentTime = System.currentTimeMillis();
            return currentTime > startTime && currentTime < endTime;
        }

        protected abstract long getDuration(T transition);
    }
}
