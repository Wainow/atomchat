package com.example.atomchat;

import com.example.atomchat.Notifications.MyResponse;
import com.example.atomchat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA7RnaVB8:APA91bGMlWio7XNCpEp_q_f5QOX4MZS8DXS4FEyAQ4p08EUSzDPGZAMtEkU68EGyUKLAQ6Bg27n7gmbrRMme62l5t64t8MtEkhi2bvMOhKxPZklgbMe58l6Kf6vRkj7EdFftlnoAv4pM"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}