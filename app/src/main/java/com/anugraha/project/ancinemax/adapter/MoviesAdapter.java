package com.anugraha.project.ancinemax.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anugraha.project.ancinemax.DetailActivity;
import com.anugraha.project.ancinemax.R;
import com.anugraha.project.ancinemax.model.Movie;
import com.bumptech.glide.Glide;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewGHolder> {
    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;

    }


    @Override
    public MyViewGHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.movie_card, viewGroup, false);

        return new MyViewGHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewGHolder viewHolder, int i) {
        viewHolder.title.setText(movieList.get(i).getOriginalTitle());
        String vote = Double.toString(movieList.get(i).getVoteAverage());
        viewHolder.userrating.setText(vote);

        Glide.with(mContext)
                .load(movieList.get(i).getPosterPath())
                .placeholder(R.drawable.load)
                .into(viewHolder.thumbnail);
    }

    @Override
    public int getItemCount(){
        return movieList.size();
    }

    public class MyViewGHolder extends RecyclerView.ViewHolder{
        public TextView title, userrating;
        public ImageView thumbnail;

        public MyViewGHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            userrating = (TextView) view.findViewById(R.id.tv_user_rating);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movieList.get(pos);
                        Intent intent1 = new Intent(mContext, DetailActivity.class);
                        intent1.putExtra("original_title",movieList.get(pos).getOriginalTitle());
                        intent1.putExtra("poster_path",movieList.get(pos).getPosterPath());
                        intent1.putExtra("overview",movieList.get(pos).getOverview());
                        intent1.putExtra("vote_average",Double.toString(movieList.get(pos).getVoteAverage()));
                        intent1.putExtra("release_date",movieList.get(pos).getReleaseDate());
                        intent1.putExtra("id",movieList.get(pos).getId());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent1);
                        Toast.makeText(v.getContext(), "You're clicked "+clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();


                    }
                }
            });
        }
    }
}
