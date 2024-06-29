package com.thesler.invdbmgr.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.thesler.invdbmgr.R;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private List<InventoryItem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public ItemRecyclerViewAdapter(Context context, List<InventoryItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextViews in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InventoryItem item = mData.get(position);
        holder.IDTextView.setText(Integer.toString(item.getId()));
        holder.NameTextView.setText(item.getName());
        holder.CountTextView.setText(Integer.toString(item.getCount()));

        //Make row red if items are out
        if(item.getCount() <= 0) {
            holder.IDTextView.setTextColor(Color.RED);
            holder.IDTextView.setTypeface(null,Typeface.BOLD);
            holder.NameTextView.setTextColor(Color.RED);
            holder.NameTextView.setTypeface(null,Typeface.BOLD);
            holder.CountTextView.setTextColor(Color.RED);
            holder.CountTextView.setTypeface(null,Typeface.BOLD);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView IDTextView;
        TextView NameTextView;
        TextView CountTextView;

        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            IDTextView = itemView.findViewById(R.id.textItemId);
            NameTextView = itemView.findViewById(R.id.textItemName);
            CountTextView = itemView.findViewById(R.id.textItemCount);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InvDBMgr invDB = new InvDBMgr(v.getContext());
                    invDB.deleteItem(NameTextView.getText().toString());
                    mData.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public InventoryItem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}