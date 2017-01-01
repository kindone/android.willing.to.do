package org.kindone.willingtodo.recyclerlist.task;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.kindone.willingtodo.MainActivity;
import org.kindone.willingtodo.R;
import org.kindone.willingtodo.TaskEditActivity;
import org.kindone.willingtodo.recyclerlist.RecyclerListItemViewHolder;

/**
 * Simple example of a view holder that implements helper.ItemTouchHelperViewHolder and has a
 * "handle" view that initiates a drag event when touched.
 */
public class TaskListItemViewHolder extends RecyclerListItemViewHolder /*implements View.OnCreateContextMenuListener*/{

    private final TextView mTitleView;
    private final ImageView mHandleView;
    private long mId;

    public TaskListItemViewHolder(final View itemView) {
        super(itemView);
        mTitleView = (TextView) itemView.findViewById(R.id.item_title);
        mHandleView = (ImageView) itemView.findViewById(R.id.handle);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("TaskListItemVH", "clicked");
                Intent intent = new Intent(itemView.getContext(), TaskEditActivity.class);
                intent.putExtra(MainActivity.RESULT_TASK_ID, mId);
                intent.putExtra(MainActivity.RESULT_TASK_TITLE, mTitleView.getText());
                ((Activity)itemView.getContext()).startActivityForResult(intent, MainActivity.INTENT_EDIT_TASK);
            }
        });
//        itemView.setOnCreateContextMenuListener(this);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.setHeaderTitle(getTitle());
//        menu.add(Menu.NONE, R.id.action_pomodoro, 1, "Start Pomodoro Timer");
//        menu.add(Menu.NONE, R.id.action_move_task, 2, "Move Task to...");
//    }

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

    public String getTitle() { return mTitleView.getText().toString(); }
}
