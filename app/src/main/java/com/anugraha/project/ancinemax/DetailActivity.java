package com.anugraha.project.ancinemax;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anugraha.project.ancinemax.adapter.TrailerAdapter;
import com.anugraha.project.ancinemax.api.Client;
import com.anugraha.project.ancinemax.api.Service;
import com.anugraha.project.ancinemax.model.Trailer;
import com.anugraha.project.ancinemax.model.TrailerResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class DetailActivity extends AppCompatActivity {
    TextView name, plot,rating,tglrilis;
    ImageView poster,ivdetailposter;
    WebView webView;
    RatingBar bintang;
    public String movieName;
    private RecyclerView recyclerView;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        poster = (ImageView) findViewById(R.id.thumbnail_image_header);
        name = (TextView) findViewById(R.id.title);
        plot = (TextView) findViewById(R.id.plotsynopsis);
        rating = (TextView) findViewById(R.id.tv_user_rating);
        tglrilis = (TextView) findViewById(R.id.releasedate);
        ivdetailposter = (ImageView) findViewById(R.id.posterview);
        bintang = (RatingBar) findViewById(R.id.penilaian);
        Intent intentThatStartedThisAct = getIntent();
        if (intentThatStartedThisAct.hasExtra("original_title")){
            String thumbnail = "https://image.tmdb.org/t/p/w780"+getIntent().getExtras().getString("backdrop_path");
            String detailposter = getIntent().getExtras().getString("poster_path");
            movieName = getIntent().getExtras().getString("original_title");
            String overview = getIntent().getExtras().getString("overview");
            String vote_average = getIntent().getExtras().getString("vote_average");
            String release_date = getIntent().getExtras().getString("release_date");
            Glide.with(DetailActivity.this)
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(poster);
            Glide.with(DetailActivity.this)
                    .load(detailposter)
                    .fitCenter()
                    .placeholder(R.drawable.load)
                    .override(100, 175)
                    .into(ivdetailposter);
            name.setText(movieName);
            plot.setText(overview);
            rating.setText(vote_average);
            if(Double.parseDouble(vote_average)<5){
                rating.setTextColor(Color.RED);
            }else if(Double.parseDouble(vote_average)<7){
                rating.setTextColor(Color.parseColor("#FF5C22"));
            }else{
                rating.setTextColor(Color.GREEN);
            }
            bintang.setRating((Float.parseFloat(vote_average)/2));

            tglrilis.setText(release_date);
        }else {
            Toast.makeText(this,"No API Data",Toast.LENGTH_SHORT).show();
        }
        initViews();
    }

    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset){
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(movieName);
                    isShow = true;
                }else if (isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initViews(){
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL,false);
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();


    }

    private void  loadJSON(){
        int movie_id = getIntent().getExtras().getInt("id");

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<TrailerResponse> call = apiService.getTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();

                }
            });

        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}
