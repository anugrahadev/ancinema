package com.anugraha.project.ancinemax;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anugraha.project.ancinemax.api.Service;
import com.anugraha.project.ancinemax.model.Person;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonActivity extends AppCompatActivity {
    private RecyclerView recyclerViewPerson;
    private List<Person> personList;
    public ImageView iv_foto;
    public TextView tv_ttl, tv_gender, tv_dob, tv_birthplace,tv_name,tv_bio,tv_occupation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Intent intentThatStartedThisAct = getIntent();
        iv_foto = (ImageView) findViewById(R.id.iv_foto);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_dob = (TextView) findViewById(R.id.tv_dob);
        tv_birthplace = (TextView) findViewById(R.id.tv_birthplace);
        tv_bio = (TextView) findViewById(R.id.tv_bio);
        tv_occupation = (TextView) findViewById(R.id.tv_occupation);
        loadJSONCast();

    }
    public static int getAge(String dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateOfBirth);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        birthDate.setTime(convertedDate);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of
        // leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR)
                - today.get(Calendar.DAY_OF_YEAR) > 3)
                || (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of
            // month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
                && (birthDate.get(Calendar.DAY_OF_MONTH) > today
                .get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    private void loadJSONCast() {
        int movie_id = getIntent().getExtras().getInt("id");

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.themoviedb.org/3/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Service request = retrofit.create(Service.class);
            Call<Person> call = request.getPerson(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<Person>() {
                @Override
                public void onResponse(Call<Person> call, Response<Person> response) {
                    int age = getAge(response.body().getBirthday());

                    if (response.isSuccessful()){
                        tv_bio.setText(response.body().getBiography());
                        tv_birthplace.setText(response.body().getPlaceOfBirth());

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy" );
                        Date date;
                        try {
                            String dob = response.body().getBirthday();
                            date = originalFormat.parse(dob);
                            tv_dob.setText(targetFormat.format(date)+" (age "+age+")");
                        } catch (ParseException ex) {
                            // Handle Exception.
                        }


                        tv_name.setText(response.body().getName());
                        Glide.with(getApplicationContext())
                                .load("https://image.tmdb.org/t/p/w154/"+response.body().getProfilePath())
                                .placeholder(R.drawable.load)
                                .into(iv_foto);
                        tv_occupation.setText(response.body().getKnownForDepartment());
                        String gender = null;
                        if (response.body().getGender() == 2){
                            gender = "Male";
                        }else{
                            gender = "Female";
                        }
                        tv_gender.setText(gender);

                    }
                }

                @Override
                public void onFailure(Call<Person> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                }
            });

        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
