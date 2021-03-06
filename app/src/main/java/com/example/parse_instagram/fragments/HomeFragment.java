package com.example.parse_instagram.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parse_instagram.Post;
import com.example.parse_instagram.PostsAdapter;
import com.example.parse_instagram.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

  private SwipeRefreshLayout swipeContainer;

  public static final String TAG = "HomeFragment";
  private RecyclerView rvPosts;
  protected PostsAdapter adapter;
  protected List<Post> allPosts;

  public HomeFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rvPosts = view.findViewById(R.id.rv_posts);

    allPosts = new ArrayList<>();
    adapter = new PostsAdapter(getContext(), allPosts);
    swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
    swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        allPosts.clear();
        swipeContainer.setRefreshing(false);
        queryPosts();
      }
    });

    // 1. Layout for one row in the list
    // 2. Create the adapter
    // 3. Create the data source
    // 4. Set the adapter on the recycler view
    rvPosts.setAdapter(adapter);

    // 5. Set the layout manager on the recycler view
    rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
    queryPosts();
  }

  protected void queryPosts() {

    // Specify which class to query
    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
    query.include(Post.KEY_USER);
    query.setLimit(20);
    query.addDescendingOrder(Post.KEY_CREATED_AT);
    query.findInBackground(new FindCallback<Post>() {

      @Override
      public void done(List<Post> posts, ParseException e) {
        if (e != null) {
          Log.e(TAG, "Issue with getting posts", e);
        }
        for (Post post : posts) {
          Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
        }
        allPosts.addAll(posts);
        adapter.notifyDataSetChanged();
      }
    });
  }
}