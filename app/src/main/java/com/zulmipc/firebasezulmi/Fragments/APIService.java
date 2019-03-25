package com.zulmipc.firebasezulmi.Fragments;

import com.zulmipc.firebasezulmi.Notifications.MyResponse;
import com.zulmipc.firebasezulmi.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAkJe97Zw:APA91bFHghIMDZ9D086Xik9h_Xx19pu39upkpxMykmEsxF4UXbSCq-vsyi04N-t6YF9kzrGJ_rWJWEQd5Co4Qh3ZS25GrSvSdJQO5fIBfRosTtdE0V2zxiWDq8BTk18oe-3D5O1iUFGt"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
