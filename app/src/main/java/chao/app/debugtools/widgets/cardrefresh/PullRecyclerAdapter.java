package chao.app.debugtools.widgets.cardrefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import chao.app.ami.Ami;
import chao.app.ami.UI;
import chao.app.ami.text.Poetry;
import chao.app.ami.text.TextManager;
import chao.app.debugtools.TestListFragment;

/**
 * @author qinchao
 * @since 2018/8/15
 */

public class PullRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int ITEM_TYPE_HEADER = 1;

    private static final int ITEM_TYPE_CONTENT = 2;

    private PullRecycleView mRecyclerView;

    private Context mContext;

    private Poetry poetry[];

    public PullRecyclerAdapter(PullRecycleView recyclerView) {
        mContext = recyclerView.getContext();
        mRecyclerView = recyclerView;
        TextManager.init();
        poetry = TextManager.getSPoetry(40);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new RecyclerView.ViewHolder(mRecyclerView.getHeaderView()) {};
        }
        return new RecyclerView.ViewHolder(LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false)) {
        };
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE_HEADER;
        }
        return ITEM_TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position == 0) {
//            mRecyclerView.resetCardMode();
//        }
        if (position > 0) {
            TextView textView = (TextView) holder.itemView;
            textView.setText(poetry[position - 1].getPoetryName());
        }
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return poetry.length + 1;
    }

    @Override
    public void onClick(View v) {
        Ami.log(v);
        UI.show(mContext, TestListFragment.class);
    }
}
