package com.casmanager.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.casmanager.database.DatabaseHelper;
import com.casmanager.reflection_recyclerview.MainAdapter;
import com.casmanager.reflection_recyclerview.Reflection;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.casmanager.R;
import com.casmanager.activity.EditReflection;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.app.Activity.RESULT_OK;

public class ReflectionFragment extends Fragment {

    private RecyclerView recycler_view;
    private FloatingActionButton fab;
    private DatabaseHelper db;
    private SwipeRefreshLayout swipeRefresh;
    private MainAdapter adapter;
    private TextView status;
    private MainAdapter.ItemClickListener listener;

    private static final int INTENT_EDIT = 200;
    private static final int INTENT_ADD = 100;
    private static final String NO_ACTIVITIES = "You don't have any activities to reflect on, create one!";

    private ArrayList<Reflection> reflections = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View baseView = inflater.inflate(R.layout.reflection_fragment, container, false);

        db = new DatabaseHelper(getActivity());

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null) ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Reflections");

        swipeRefresh = baseView.findViewById(R.id.ref_swipe_refresh);
        recycler_view = baseView.findViewById(R.id.reflection_rv);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        status = baseView.findViewById(R.id.no_reflections);

        fab = baseView.findViewById(R.id.reflection_add);
        fab.setOnClickListener(this::onClickActionBtn);

        listener = ((view, position) -> {
            int id = reflections.get(position).getId();
            String title = reflections.get(position).getTitle();
            String description = reflections.get(position).getDescription();
            String activity = reflections.get(position).getActivity();
            String date = reflections.get(position).getDate();

            Intent intent = new Intent(getActivity(), EditReflection.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("activity", activity);
            intent.putExtra("date", date);

            startActivityForResult(intent, INTENT_EDIT);
        });


        getData();

        swipeRefresh.setOnRefreshListener(
                () -> getData()
        );

        if (reflections.size()==0) {
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
        reflections.clear();

        Cursor data = db.getAllReflectionData();
        showLoading();

        if (data.getCount() == 0) {
            return;
        } else {
            while(data.moveToNext()) {
               reflections.add(new Reflection(data.getInt(0),data.getString(1),
                        data.getString(2), data.getString(3), data.getString(4)));
            }
        }

        onGetResult(reflections);
        hideLoading();
    }

    public void showLoading() {
        swipeRefresh.setRefreshing(true);
    }

    public void hideLoading() {
        swipeRefresh.setRefreshing(false);
    }

    public void onGetResult(ArrayList<Reflection> reflections) {
        adapter = new MainAdapter(getActivity(), reflections, listener);
        recycler_view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onErrorLoading(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void onClickActionBtn(View view) {
        Cursor data = db.getAllActivityData();
        if (data.getCount() == 0) {
            Toast.makeText(getActivity(), NO_ACTIVITIES, Toast.LENGTH_SHORT).show();

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, new ActivityFragment()).commit();
        } else {
            getActivity().finish();
            startActivityForResult(new Intent(getActivity(), EditReflection.class), INTENT_ADD);
        }

    }
}
