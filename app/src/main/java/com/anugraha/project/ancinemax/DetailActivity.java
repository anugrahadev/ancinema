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

import com.anugraha.project.ancinemax.adapter.CreditAdapter;
import com.anugraha.project.ancinemax.adapter.TrailerAdapter;
import com.anugraha.project.ancinemax.api.Client;
import com.anugraha.project.ancinemax.api.Service;
import com.anugraha.project.ancinemax.model.Cast;
import com.anugraha.project.ancinemax.model.CreditResponse;
import com.anugraha.project.ancinemax.model.Trailer;
import com.anugraha.project.ancinemax.model.TrailerResponse;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private RecyclerView recyclerViewtrailer;
    private TrailerAdapter adaptertrailer;
    private List<Trailer> trailerList;

    private RecyclerView recyclerViewcast;
    private CreditAdapter adaptercast;
    private List<Cast> castList;
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


            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy" );
            Date date;
            try {
                String release_date = getIntent().getExtras().getString("release_date");
                date = originalFormat.parse(release_date);
                tglrilis.setText(targetFormat.format(date));
            } catch (ParseException ex) {
                // Handle Exception.
            }


            Glide.with(DetailActivity.this)
                    .load(thumbnail)
                    .placeholder(R.drawable.load)
                    .into(poster);
            Glide.with(DetailActivity.this)
                    .load(detailposter)
                    .fitCenter()
                    .placeholder(R.drawable.load)
                    .override(245, 175)
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
        adaptertrailer = new TrailerAdapter(this, trailerList);

        recyclerViewtrailer = (RecyclerView) findViewById(R.id.recycler_view_trailer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL,false);
        mLayoutManager.setAutoMeasureEnabled(true);
        recyclerViewtrailer.setLayoutManager(mLayoutManager);
        recyclerViewtrailer.setNestedScrollingEnabled(false);
        recyclerViewtrailer.setHasFixedSize(false);
        recyclerViewtrailer.setAdapter(adaptertrailer);
        adaptertrailer.notifyDataSetChanged();

        loadJSONVideos();

        castList= new ArrayList<>();
        adaptercast = new  CreditAdapter(this, castList);
        recyclerViewcast = (RecyclerView) findViewById(R.id.recycler_view_cast);

        RecyclerView.LayoutManager mLayoutManagerCast = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL,false);
        mLayoutManagerCast.setAutoMeasureEnabled(true);
        recyclerViewcast.setLayoutManager(mLayoutManagerCast);
        recyclerViewcast.setNestedScrollingEnabled(false);
        recyclerViewcast.setHasFixedSize(false);
        recyclerViewcast.setAdapter(adaptercast);
        adaptercast.notifyDataSetChanged();
        loadJSONCast();


    }


    private void  loadJSONVideos(){
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
                    recyclerViewtrailer.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
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


    private void loadJSONCast() {
        int movie_id = getIntent().getExtras().getInt("id");
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<CreditResponse> call = apiService.getCredits(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<CreditResponse>() {
                @Override
                public void onResponse(Call<CreditResponse> call, Response<CreditResponse> response) {
                    List<Cast> cast = response.body().getCast();
                    recyclerViewcast.setAdapter(new CreditAdapter(getApplicationContext(), cast));
                }

                @Override
                public void onFailure(Call<CreditResponse> call, Throwable t) {
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
