package com.yooiistudios.sequentialanimation.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yooiistudios.sequentialanimation.R;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SimpleAdapter
 */
public class SimpleAdapter extends RecyclerView.Adapter {
    public interface OnBindViewHolderListener {
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i);
    }

    private static final String TAG = SimpleAdapter.class.getSimpleName();

    private OnBindViewHolderListener mOnBindViewHolderListener;

    public void setOnBindViewHolderListener(OnBindViewHolderListener onBindViewHolderListener) {
        mOnBindViewHolderListener = onBindViewHolderListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        Log.i(TAG, "onCreateViewHolder : " + i);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recycler_item_progress, null);
//        ViewTransientUtils.setState(itemView);
//        ViewCompat.setHasTransientState(itemView, true);
//        return new SimpleViewHolder(itemView);
        return new ProgressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
//        Log.i(TAG, "onBindViewHolder : " + i);
//        SimpleViewHolder simpleViewHolder = (SimpleViewHolder)viewHolder;
//        simpleViewHolder.textView.setText("Position : " + (i < 10 ? 0 + String.valueOf(i) : i));

        ProgressViewHolder progressViewHolder = (ProgressViewHolder)viewHolder;
        progressViewHolder.textView.setText("Position : " + (i < 10 ? 0 + String.valueOf(i) : i));
        progressViewHolder.progressBar.setProgress(0);
        progressViewHolder.index = i;
        if (mOnBindViewHolderListener != null) {
            mOnBindViewHolderListener.onBindViewHolder(viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    private class SimpleViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView;
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public TextView textView;
        public int index;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.progressBar);
            textView = (TextView)itemView.findViewById(R.id.textView);
        }
    }
}
