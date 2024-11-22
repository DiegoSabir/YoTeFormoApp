package com.sabir.yoteformo.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sabir.yoteformo.activities.DetailsActivity;
import com.sabir.yoteformo.R;
import com.sabir.yoteformo.models.SeriesModel;

import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.MyViewHolder>{

    private final List<SeriesModel> modelList;
    private final String userId;

    public SeriesAdapter(List<SeriesModel> modelList, String userId) {
        this.modelList = modelList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.itemSeriesTitle.setText(modelList.get(position).getTitle());

        String thumbUrl = "https://drive.google.com/uc?export=download&id=" + modelList.get(position).getThumb();
        Glide.with(holder.itemSeriesThumb.getContext()).load(thumbUrl).into(holder.itemSeriesThumb);

        holder.itemView.setOnClickListener(v -> {
            Intent sendDataToDetailsActivity = new Intent(holder.itemView.getContext(), DetailsActivity.class);
            sendDataToDetailsActivity.putExtra("idSeries", modelList.get(position).getIdSeries());
            sendDataToDetailsActivity.putExtra("title", modelList.get(position).getTitle());
            sendDataToDetailsActivity.putExtra("link", modelList.get(position).getEpisodes());
            sendDataToDetailsActivity.putExtra("cover", modelList.get(position).getCover());
            sendDataToDetailsActivity.putExtra("thumb", modelList.get(position).getThumb());
            sendDataToDetailsActivity.putExtra("description", modelList.get(position).getSynopsis());
            sendDataToDetailsActivity.putExtra("cast", modelList.get(position).getCast());
            sendDataToDetailsActivity.putExtra("trailer", modelList.get(position).getTrailer());
            sendDataToDetailsActivity.putExtra("like", modelList.get(position).getLike());

            sendDataToDetailsActivity.putExtra("seriesIndex", position);
            sendDataToDetailsActivity.putExtra("userId", userId);

            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity)holder.itemView.getContext(), holder.itemSeriesThumb, "imageMain");

            holder.itemView.getContext().startActivity(sendDataToDetailsActivity, optionsCompat.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView itemSeriesThumb;
        TextView itemSeriesTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSeriesTitle = itemView.findViewById(R.id.itemSeriesTitle);
            itemSeriesThumb = itemView.findViewById(R.id.itemSeriesThumb);
        }
    }
}
