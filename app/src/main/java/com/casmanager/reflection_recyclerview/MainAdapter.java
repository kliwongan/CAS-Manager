package com.casmanager.reflection_recyclerview;

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
    private ArrayList<Reflection> reflections;
    private ItemClickListener listener;

    public MainAdapter(Context c, ArrayList<Reflection> reflections, ItemClickListener listener) {
        this.c = c;
        this.reflections = reflections;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.reflection_item, parent, false);
        return new RecyclerViewAdapter(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        Reflection reflection = reflections.get(position);
        holder.title.setText(reflection.getTitle());
        holder.activity.setText("Activity: " + " " + reflection.getActivity());
        holder.date.setText(reflection.getDate());

        Animation animation = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return reflections.size();
    }

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    class RecyclerViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, activity, date;
        CardView item;
        ItemClickListener listener;

        public RecyclerViewAdapter(View itemView, ItemClickListener listener) {
            super(itemView);

            title = itemView.findViewById(R.id.ref_title);
            activity = itemView.findViewById(R.id.ref_activity);
            date = itemView.findViewById(R.id.ref_date);
            item = itemView.findViewById(R.id.ref_card);

            this.listener = listener;
            item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }

    }
}

