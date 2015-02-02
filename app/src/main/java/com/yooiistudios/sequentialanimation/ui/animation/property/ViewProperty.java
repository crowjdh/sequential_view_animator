package com.yooiistudios.sequentialanimation.ui.animation.property;

import android.view.View;

/**
 * Created by Dongheyon Jeong in News-Android-L from Yooii Studios Co., LTD. on 15. 1. 27.
 *
 * ViewProperty
 *  애니메이션될 뷰의 속성
 */
public class ViewProperty {
    public interface AnimationListener {
        public void onAnimationEnd(ViewProperty property);
    }
    private View mView;
    private AnimationListener mAnimationListener;
    private int mViewIndex;
    private TransitionInfo mTransitionInfo;

    public ViewProperty() {
        mTransitionInfo = TransitionInfo.makeDefault();
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    // TODO rename to transition
    public AnimationListener getAnimationListener() {
        return mAnimationListener;
    }

    // TODO rename to transition
    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public TransitionInfo getTransitionInfo() {
        return mTransitionInfo;
    }

    public void setTransitionInfo(TransitionInfo transitionInfo) {
        mTransitionInfo = transitionInfo;
    }

    public int getViewIndex() {
        return mViewIndex;
    }

    public void setViewIndex(int viewIndex) {
        mViewIndex = viewIndex;
    }

    public void increaseTransitionIndexAndResetCurrentPlayTime() {
        mTransitionInfo.increaseTransitionIndex();
        mTransitionInfo.setCurrentPlayTime(0L);
    }

    public void resetTransitionInfo() {
        mTransitionInfo.reset();
    }

    public static class Builder {
        private View mView;
        private AnimationListener mAnimationListener;
        private int mViewIndex;
        private TransitionInfo mTransitionInfo = TransitionInfo.makeDefault();

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setAnimationListener(AnimationListener animationListener) {
            mAnimationListener = animationListener;
            return this;
        }

        public Builder setViewIndex(int viewIndex) {
            mViewIndex = viewIndex;
            return this;
        }

        public ViewProperty build() {
            ViewProperty property = new ViewProperty();
            property.setView(mView);
            property.setViewIndex(mViewIndex);
            property.setTransitionInfo(mTransitionInfo);
            property.setAnimationListener(mAnimationListener);
            return property;
        }
    }

    public static class TransitionInfo {
        private static final int DEFAULT_INDEX = 0;
        private static final long DEFAULT_CURRENT_PLAY_TIME = 0L;
        private int mIndex;
        private long mCurrentPlayTime;

        public TransitionInfo(int index, long currentPlayTime) {
            mIndex = index;
            mCurrentPlayTime = currentPlayTime;
        }

        public int getIndex() {
            return mIndex;
        }

        public void setIndex(int index) {
            mIndex = index;
        }

        public long getCurrentPlayTime() {
            return mCurrentPlayTime;
        }

        public void setCurrentPlayTime(long currentPlayTime) {
            mCurrentPlayTime = currentPlayTime;
        }

        public void increaseTransitionIndex() {
            mIndex++;
        }

        public void reset() {
            setIndex(DEFAULT_INDEX);
            setCurrentPlayTime(DEFAULT_CURRENT_PLAY_TIME);
        }

        public static TransitionInfo makeDefault() {
            return new TransitionInfo(DEFAULT_INDEX, DEFAULT_CURRENT_PLAY_TIME);
        }
    }
}
