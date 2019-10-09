package com.anugraha.project.ancinemax.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anugraha.project.ancinemax.DetailActivity;
import com.anugraha.project.ancinemax.PersonActivity;
import com.anugraha.project.ancinemax.R;
import com.anugraha.project.ancinemax.model.Cast;
import com.anugraha.project.ancinemax.model.Movie;
import com.anugraha.project.ancinemax.model.Trailer;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.MyViewHolder> {
    private List<Cast> castList;
    private Context mContext;
    public CreditAdapter(Context mContext, List<Cast> castList ){
        this.castList = castList;
        this.mContext=mContext;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cast_card, viewGroup, false);
        return new CreditAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv_castact.setText(castList.get(position).getCharacter());
        holder.tv_castname.setText(castList.get(position).getName());
        Glide.with(mContext)
                .load("https://image.tmdb.org/t/p/w154/"+castList.get(position).getProfilePath())
                .placeholder(R.drawable.load)
                .override(100, 175)
                .into(holder.iv_castpic);
    }

    @Override
    public int getItemCount() {
        return castList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_castname, tv_castact;
        public ImageView iv_castpic;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_castpic = (ImageView) itemView.findViewById(R.id.iv_cast);
            tv_castact = (TextView) itemView.findViewById(R.id.tv_castact);
            tv_castname = (TextView) itemView.findViewById(R.id.tv_castname);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Cast clickedDataItem = castList.get(pos);
                        Intent intent1 = new Intent(mContext, PersonActivity.class);
                        intent1.putExtra("id",castList.get(pos).getId());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent1);

                    }
                }
            });
        }
    }
}
