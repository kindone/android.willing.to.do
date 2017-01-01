package org.kindone.willingtodo.recyclerlist.context;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.kindone.willingtodo.R;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder;

/**
 * Created by kindone on 2016. 12. 25..
 */

public class ContextListItemViewHolder extends RecyclerListItemViewHolder {
    private final TextView mTitleView;
    private final ImageView mHandleView;
    private long mId;

    public ContextListItemViewHolder(View itemView) {
        super(itemView);
        mTitleView = (TextView) itemView.findViewById(R.id.item_title);
        mHandleView = (ImageView) itemView.findViewById(R.id.handle);
    }

    @Override
    protected View getHandleView() {
        return mHandleView;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    @Override
    public void setTitle(String title) {
        mTitleView.setText(title);
    }
}
