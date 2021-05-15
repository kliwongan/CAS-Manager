package com.casmanager.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.casmanager.SharedPref;
import com.casmanager.activity_recyclerview.Activity;
import com.casmanager.activity_recyclerview.MainAdapter;
import com.casmanager.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.casmanager.R;
import com.casmanager.activity.EditActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;


public class ActivityFragment extends Fragment {

    private RecyclerView recycler_view;
    private FloatingActionButton fab;
    private DatabaseHelper db;
    private SwipeRefreshLayout swipeRefresh;
    private MainAdapter adapter;
    private TextView status;
    private MainAdapter.ItemClickListener listener;
    SharedPref sharedPref;

    private static final int INTENT_EDIT = 200;
    private static final int INTENT_ADD = 100;

    private ArrayList<Activity> activity = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View baseView = inflater.inflate(R.layout.activity_fragment, container, false);

        db = new DatabaseHelper(getActivity());

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Activities");

        swipeRefresh = baseView.findViewById(R.id.swipe_refresh);
        recycler_view = baseView.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        status = baseView.findViewById(R.id.textView);

        fab = baseView.findViewById(R.id.add);
        fab.setOnClickListener(this::onClickActionBtn);

        listener = ((view, position) -> {
            int id = activity.get(position).getId();
            String title = activity.get(position).getTitle();
            String description = activity.get(position).getDescription();
            String date = activity.get(position).getDate();

            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("date", date);

            startActivityForResult(intent, INTENT_EDIT);
        });

        getData();

        swipeRefresh.setOnRefreshListener(
                () -> getData()
        );

        if (activity.size()==0) {
            hideLoading();
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.INVISIBLE);
        }

        return baseView;

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INTENT_ADD && resultCode == RESULT_OK) {
            getData(); //reload database
        } else if (requestCode == INTENT_EDIT && resultCode == RESULT_OK) {
            getData();
        }
    }

    public void getData() {
        activity.clear();

        Cursor data = db.getAllActivityData();
        showLoading();

        if (data.getCount() == 0) {
            return;
        } else {
            while(data.moveToNext()) {
                activity.add(new Activity(data.getInt(0),data.getString(1),
                        data.getString(2), data.getString(3)));
            }
        }

        onGetResult(activity);
        hideLoading();
    }

    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }

    public void onGetResult(ArrayList<Activity> activities) {
        adapter = new MainAdapter(getActivity(), activities, listener);
        recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onErrorLoading(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void onClickActionBtn(View view) {
        getActivity().finish();
        startActivityForResult(new Intent(getActivity(), EditActivity.class), INTENT_ADD);
    }
}

