package com.sabir.yoteformo.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sabir.yoteformo.adapters.SeriesAdapter;
import com.sabir.yoteformo.models.SeriesModel;
import com.sabir.yoteformo.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    private RecyclerView rvSeries;
    private SeriesAdapter seriesAdapter;
    private List<SeriesModel> seriesList;
    private List<SeriesModel> filteredList;
    private String userId;

    private SearchView svSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }

        rvSeries = view.findViewById(R.id.rvSeries);
        rvSeries.setLayoutManager(new GridLayoutManager(getContext(), 3));

        seriesList = new ArrayList<>();
        filteredList = new ArrayList<>();
        seriesAdapter = new SeriesAdapter(filteredList, userId);
        rvSeries.setAdapter(seriesAdapter);

        svSearch = view.findViewById(R.id.svSearch);
        setupSearchView();

        loadSeries();

        return view;
    }

    private void loadSeries() {
        DatabaseReference seriesReference = database.getReference("series");

        seriesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seriesList.clear();
                for (DataSnapshot seriesSnapshot : snapshot.getChildren()) {
                    SeriesModel seriesModel = seriesSnapshot.getValue(SeriesModel.class);
                    seriesList.add(seriesModel);
                }
                filteredList.clear();
                filteredList.addAll(seriesList);
                seriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar series", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSeries(newText);
                return true;
            }
        });
    }

    private void filterSeries(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            // Si la consulta está vacía, mostrar todos los elementos
            filteredList.addAll(seriesList);
        } else {
            // Filtrar seriesList basado en el título que contiene la consulta
            String lowerCaseQuery = query.toLowerCase();
            for (SeriesModel series : seriesList) {
                if (series.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(series);
                }
            }
        }
        seriesAdapter.notifyDataSetChanged();
    }
}
