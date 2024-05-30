package com.example.twohand_project.Model;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {

    private final static Model _instance=new Model();
    AppLocalDbRepository localDb=AppLocalDb.getDb();
    FirebaseModel firebaseModel=new FirebaseModel();
    Executor executor= Executors.newSingleThreadExecutor();
    public Handler mainHandler= HandlerCompat.createAsync(Looper.getMainLooper());
    public interface Listener<T>{
        void onComplete(T data);
    }
    private Model(){};

    public static Model instance(){return _instance;}



    public void register(User newUser,String password,Listener<Void> listener){
        firebaseModel.register(newUser,password,listener);
    }

    public void isEmailTaken(String email,Listener<Boolean> listener) {
        firebaseModel.isEmailTaken(email,listener);

    }

    public void isUsernameTaken(String username, Listener<Boolean> listener) {
        firebaseModel.isUsernameTaken(username,listener);
    }

    public void logIn(String username, String password, Listener<Boolean> listener) {
        firebaseModel.logIn(username,password,listener);
    }
    private LiveData<User> user;
    public LiveData<User> getLoggedUser(){
        if (user == null) {
            user=firebaseModel.getLoggedUser();
        }
        return user;
    }
    private LiveData<List<Post>> postList;
    public LiveData<List<Post>> getAllPosts() {
        if(postList == null){
            postList = localDb.postDao().getAll();
            refreshAllPosts();
        }
        return postList;
    }
    public void refreshAllPosts(){
        Long localLastUpdated= Post.getPOSTlastUpdate();
        firebaseModel.getAllPostsSince(localLastUpdated,(posts)->{
            executor.execute(()->{
                helperFunc(localLastUpdated,posts);

            });

        });
    }
    public void helperFunc(Long localLastUpdated,List<Post> posts){
        Long time=localLastUpdated;
        for (Post post : posts) {
            localDb.postDao().insert(post);
            if (post.lastUpdated > time) {
                time=post.lastUpdated;
            }
        }
        Post.setPOSTlastUpdate(time);
    }
    public void getPostsByCategory(String kind,String color,Listener<List<Post>> listener){
        refreshAllPosts();
            executor.execute(()->{
                List<Post> data=localDb.postDao().getPostsByCategories(kind,color);
                mainHandler.post(()->listener.onComplete(data));
            });


    }
    public void getUserPosts(String username,Listener<List<Post>> listener){
        refreshAllPosts();
            executor.execute(()->{
                List<Post> data=localDb.postDao().getUserPosts(username);
                mainHandler.post(()->listener.onComplete(data));
            });


    }

    public void addPost(Post post,Listener<Void> listener){
        firebaseModel.addPost(post,listener);
    }
    public void getPostById(String id,Listener<Post> listener) {
        refreshAllPosts();
            executor.execute(()->{
                Post post=localDb.postDao().getPostById(id);
                mainHandler.post(()->{listener.onComplete(post);});
            });


    }
    public void getFavoritesPosts(String username,Listener<List<Post>> listener){
        refreshAllPosts();
            firebaseModel.getFavorites(username,(favorites)->{
                executor.execute(()->{
                    List<Post> data=localDb.postDao().getFavorites(favorites);
                    mainHandler.post(()->{listener.onComplete(data);});
                });
            });

    }

    public void updateFavorites(User user){
        firebaseModel.updateFavorites(user);
        re
    }



    public List<String> getAllClothesKinds() {
        List<String> kinds=new ArrayList<>();
        kinds.add("Kind");
        kinds.add("Shoes");
        kinds.add("Jeans");
        kinds.add("T-Shirt");
        kinds.add("Pants");
        return kinds;
    }

    public List<String> getAllColors() {
        List<String> colors=new ArrayList<>();
        colors.add("Color");
        colors.add("red");
        colors.add("yellow");
        colors.add("blue");
        colors.add("green");
        return colors;
    }
    public List<String> getLocations() {
        List<String> cities=new ArrayList<>();
        cities.add("Tel-Aviv");
        return cities;
    }




    public void updatePost(String price, String description) {


    }




    public void uploadImage(String id, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadPhoto(id,bitmap,listener);
    }



}
