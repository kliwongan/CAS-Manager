package com.casmanager.logger_recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.casmanager.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.RecyclerViewAdapter> {

    private Context c;
    private ArrayList<Log> logs;
    private ItemClickListener listener;

    public MainAdapter(Context c, ArrayList<Log> logs, ItemClickListener listener) {
        this.c = c;
        this.logs = logs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.logger_item, parent, false);
        return new RecyclerViewAdapter(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        Log log = logs.get(position);
        holder.activity.setText("Activity: " + " " + log.getActivity());
        holder.date.setText(log.getDate());
        holder.length.setText(log.getLength() + " " + log.getType().toLowerCase());

        Animation animation = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView activity, date, length;
        CardView item;
        ItemClickListener listener;

        public RecyclerViewAdapter(View itemView, ItemClickListener listener) {
            super(itemView);

            activity = itemView.findViewById(R.id.log_activity);
            date = itemView.findViewById(R.id.log_date);
            length = itemView.findViewById(R.id.log_len);
            item = itemView.findViewById(R.id.log_card);

            this.listener = listener;
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }

    }
}

