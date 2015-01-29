package com.yooiistudios.sequentialanimation.ui;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yooiistudios.sequentialanimation.R;

/**
 * Created by Dongheyon Jeong in SequentialAnimationTest from Yooii Studios Co., LTD. on 15. 1. 29.
 *
 * SimpleAdapter
 */
public class SimpleAdapter extends RecyclerView.Adapter {
    private static final String TAG = SimpleAdapter.class.getSimpleName();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.i(TAG, "onCreateViewHolder : " + i);
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recycler_item, null);
        ViewCompat.setHasTransientState(itemView, true);
        return new SimpleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        Log.i(TAG, "onBindViewHolder : " + i);
        SimpleViewHolder simpleViewHolder = (SimpleViewHolder)viewHolder;

        simpleViewHolder.textView.setText("Position : " + (i < 10 ? 0 + String.valueOf(i) : i));

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
}
