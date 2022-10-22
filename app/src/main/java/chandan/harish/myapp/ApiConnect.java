package chandan.harish.myapp;


import chandan.harish.myapp.pojo.Users;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiConnect {
    String LOGIN_URL = "http://harish.42web.io/ecommerce/";
    @FormUrlEncoded
    @POST("simpleregister.php")
    Call<Users> getUserRegister(
            @Field("users_name") String users_name,
            @Field("users_email") String users_email,
            @Field("users_password") String users_password,
            @Field("users_mobile") String users_mobile


    );


}