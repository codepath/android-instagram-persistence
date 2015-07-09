package com.codepath.persistencedemo.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.codepath.persistencedemo.R;
import com.codepath.persistencedemo.models.InstagramPost;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class InstagramPostsAdapter extends RecyclerView.Adapter<InstagramPostsAdapter.PostItemViewHolder> {
    private static final String TAG = "InstagramPostsAdapter";

    private List<InstagramPost> posts;

    public InstagramPostsAdapter(List<InstagramPost> posts) {
        this.posts = (posts == null ? new ArrayList<InstagramPost>() : posts);
    }

    @Override
    public PostItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_post, viewGroup, false);
        return new PostItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostItemViewHolder postItemViewHolder, int position) {
        final InstagramPost instagramPost = posts.get(position);

        postItemViewHolder.tvUserName.setText(instagramPost.user.userName);
        postItemViewHolder.tvRelativeTimestamp.setText(
                DateUtils.getRelativeTimeSpanString(instagramPost.createdTime * 1000,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));

        // Reset image view
        postItemViewHolder.sdvProfileImage.setImageURI(null);

        Uri profilePictureUri = Uri.parse(instagramPost.user.profilePictureUrl);
        postItemViewHolder.sdvProfileImage.setImageURI(profilePictureUri);
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public static final class PostItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvRelativeTimestamp;
        SimpleDraweeView sdvProfileImage;

        public PostItemViewHolder(View itemView) {
            super(itemView);

            tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            tvRelativeTimestamp = (TextView) itemView.findViewById(R.id.tvRelativeTimestamp);
            sdvProfileImage = (SimpleDraweeView) itemView.findViewById(R.id.sdvProfileImage);
        }
    }
}
