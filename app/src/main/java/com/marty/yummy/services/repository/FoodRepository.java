package com.marty.yummy.services.repository;

import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.marty.yummy.dbutilities.AppDatabase;
import com.marty.yummy.model.FoodDetails;
import com.marty.yummy.services.APIClient;
import com.marty.yummy.services.YummyAPIServices;
import com.marty.yummy.worker.SaveFoodMenu;
import com.marty.yummy.worker.UpdateCart;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//Singleton Repository instance.
public class FoodRepository {

    private static FoodRepository instance;
    private static final String TAG = "FoodRepository";

    //API service for transactions over network.
    private final YummyAPIServices yummyAPIServices = APIClient.getClient().create(YummyAPIServices.class);

    //Method to get network response.
    public MutableLiveData<Boolean> getFoodMenu(final Context context){

        final MutableLiveData<Boolean> isFoodCallOngoing = new MutableLiveData<>();
        isFoodCallOngoing.setValue(true);

        yummyAPIServices.getFoodData().enqueue(new Callback<List<FoodDetails>>() {
            @Override
            public void onResponse(Call<List<FoodDetails>> call, Response<List<FoodDetails>> response) {
                if(response.isSuccessful()) {
                    new SaveFoodMenu(AppDatabase.getDatabase(context), response.body()).execute();
                    isFoodCallOngoing.setValue(false);
                }else{
                    Log.e(TAG,"response not successful");
                }
            }

            @Override
            public void onFailure(Call<List<FoodDetails>> call, Throwable t) {
                Log.e(TAG,t.toString());
            }
        });
        return isFoodCallOngoing;
    }

    //Singleton implementation getInstance method.
    public static FoodRepository getInstance() {
        if(instance == null){
            synchronized (FoodRepository.class){
                if(instance == null){
                    instance = new FoodRepository();
                }
            }
        }
        return instance;
    }

    //Populate the database.
    public void updateCart(final AppDatabase db, FoodDetails foodDetails) {
        new UpdateCart(db).execute(foodDetails);
    }
}
