package com.sabir.yoteformo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sabir.yoteformo.adapters.SeriesAdapter;
import com.sabir.yoteformo.adapters.SliderAdapter;
import com.sabir.yoteformo.models.SeriesModel;
import com.sabir.yoteformo.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView rvPopular, rvLike;

    private SliderAdapter sliderAdapter;
    private SeriesAdapter popularAdapter;
    private SeriesAdapter likeAdapter;

    private List<SeriesModel> sliderList;
    private List<SeriesModel> popularList;
    private List<SeriesModel> likeList;

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            setupSlider(getView());
            loadPopularSeries(getView());
            loadLikeSeries(getView());
        }
    }

    private void setupSlider(View view) {
        SliderView svNewSeries = view.findViewById(R.id.svNewSeries);

        sliderAdapter = new SliderAdapter(getContext());
        svNewSeries.setSliderAdapter(sliderAdapter);
        svNewSeries.setIndicatorAnimation(IndicatorAnimationType.WORM);
        svNewSeries.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        svNewSeries.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        svNewSeries.setScrollTimeInSec(5);

        svNewSeries.startAutoCycle();

        svNewSeries.setAutoCycle(true);

        renewSliderItems(svNewSeries);
        loadSliderData();
    }

    private void renewSliderItems(View view) {
        sliderList = new ArrayList<>();
        SeriesModel dataItem = new SeriesModel();
        sliderList.add(dataItem);

        sliderAdapter.renewItems(sliderList);
        sliderAdapter.deleteItems(0);
    }

    private void loadSliderData() {
        DatabaseReference releaseReference = database.getReference("series");
        Query latestSeriesQuery = releaseReference.orderByChild("release").limitToLast(5);

        sliderList = new ArrayList<>();
        latestSeriesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sliderList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot seriesSnapshot : snapshot.getChildren()) {
                        SeriesModel series = seriesSnapshot.getValue(SeriesModel.class);
                        if (series != null) {
                            sliderList.add(series);
                        }
                    }
                    sliderList.sort((s1, s2) -> s2.getRelease().compareTo(s1.getRelease()));
                    sliderAdapter.renewItems(sliderList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load slider data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPopularSeries(View view) {
        DatabaseReference popularReference = database.getReference("series");
        Query popularQuery = popularReference.orderByChild("like").limitToLast(10);

        rvPopular = view.findViewById(R.id.rvPopular);
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, true));

        popularList = new ArrayList<>();
        popularAdapter = new SeriesAdapter(popularList, userId);
        rvPopular.setAdapter(popularAdapter);

        popularQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                popularList.clear();
                for (DataSnapshot contentSnapShot : snapshot.getChildren()) {
                    SeriesModel popularSeries = contentSnapShot.getValue(SeriesModel.class);
                    popularList.add(popularSeries);
                }
                popularAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLikeSeries(View view) {
        rvLike = view.findViewById(R.id.rvLike);
        rvLike.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        likeList = new ArrayList<>();
        likeAdapter = new SeriesAdapter(likeList, userId);
        rvLike.setAdapter(likeAdapter);

        DatabaseReference likeReference = database.getReference().child("user").child(userId).child("likeSeries");
        likeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot likeSnapshot : snapshot.getChildren()) {
                        String seriesId = likeSnapshot.getValue(String.class);

                        database.getReference().child("series").child(seriesId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                SeriesModel series = snapshot.getValue(SeriesModel.class);
                                if (series != null) {
                                    likeList.add(series);
                                    likeAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
