package com.yooiistudios.sequentialanimationtest.sequentialanimation;

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
    private int mAnimationIndex;
    private boolean mIsAnimating;

    public ViewProperty() {
        mAnimationIndex = 0;
        mIsAnimating = true;
    }

    public View getView() {
        return mView;
    }

    public void setView(View view) {
        mView = view;
    }

    public AnimationListener getAnimationListener() {
        return mAnimationListener;
    }

    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }

    public int getViewIndex() {
        return mViewIndex;
    }

    public void setViewIndex(int viewIndex) {
        mViewIndex = viewIndex;
    }

    public int getTransitionIndex() {
        return mAnimationIndex;
    }

    public void setAnimationIndex(int animationIndex) {
        mAnimationIndex = animationIndex;
    }

    public void increaseTransitionIndex() {
        mAnimationIndex++;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

    public void setAnimating(boolean isAnimating) {
        mIsAnimating = isAnimating;
    }

    public static class Builder {
        private View mView;
        private AnimationListener mAnimationListener;
        private int mViewIndex;
        private int mAnimationIndex;
        private boolean mIsAnimating;

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

        public Builder setAnimationIndex(int animationIndex) {
            mAnimationIndex = animationIndex;
            return this;
        }

        public Builder setAnimating(boolean isAnimating) {
            mIsAnimating = isAnimating;
            return this;
        }

        public ViewProperty build() {
            ViewProperty property = new ViewProperty();
            property.setView(mView);
            property.setViewIndex(mViewIndex);
            property.setAnimating(mIsAnimating);
            property.setAnimationIndex(mAnimationIndex);
            property.setAnimationListener(mAnimationListener);
            return property;
        }
    }
}
