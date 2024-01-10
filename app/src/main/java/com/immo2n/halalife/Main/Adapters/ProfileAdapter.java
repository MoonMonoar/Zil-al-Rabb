package com.immo2n.halalife.Main.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Profile;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.LoadMedia;
import com.immo2n.halalife.Main.DataObjects.ProfileGrid;
import com.immo2n.halalife.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.GridViewHolder> {
    private Global global;
    private AppState appState;
    private Profile profile;
    private Profile appUserProfile;
    private final List<ProfileGrid> itemList;
    private final Context context;
    ViewGroup parent;
    LoadMedia loadMedia;

    public ProfileAdapter(Context context, Activity activity, List<ProfileGrid> itemList, Profile profile) {
        this.context = context;
        this.itemList = itemList;
        this.global = new Global(context, activity);
        this.appState = new AppState(global);
        this.profile = profile;
        this.appUserProfile = appState.getProfile();
        loadMedia = new LoadMedia(context);
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_card_holder, parent, false);
        this.parent = parent;
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        holder.mainCardBody.removeAllViews(); //NEVER EVER FORGET THIS LINE, IT CAUSES POSITION ERRORS AND GHOSTLY BEHAVIOUR
        ProfileGrid item = itemList.get(position);
        View view;
        if(item.isInfoGreed()){
            //Show the profile info
            view = LayoutInflater.from(context).inflate(R.layout.profile_main_grid, parent, false);
            //Do process info
            //VIEWS
            ImageView image = view.findViewById(R.id.profileImage);
            TextView name = view.findViewById(R.id.name),
                    posts = view.findViewById(R.id.postCount),
                    followers = view.findViewById(R.id.followersCount),
                    followings = view.findViewById(R.id.followingCount),
                    bio = view.findViewById(R.id.bio),
                    address = view.findViewById(R.id.addressText),
                    institute = view.findViewById(R.id.collegeInfo),
                    living = view.findViewById(R.id.livesInText),
                    work = view.findViewById(R.id.workInfo),
                    memberSince = view.findViewById(R.id.infoMemberSinceText),
                    editProfile = view.findViewById(R.id.editProfile);
            ImageView badge = view.findViewById(R.id.badge);
            LinearLayout professionalView = view.findViewById(R.id.professionalView);

            //Set
            if(null != profile.getFace()){
                Picasso.get().load(Server.getUserAsset(profile.getFace())).placeholder(global.getDrawable(R.drawable.user_image_default)).into(image);
            }
            name.setText(profile.getFull_name());
            if(profile.getVerified_badge()){
                badge.setVisibility(View.VISIBLE);
            }
            if(profile.getId() == appUserProfile.getId()){
                //Self
                editProfile.setVisibility(View.VISIBLE);
            }
            if(null != profile.getBio()){
                bio.setText(profile.getBio());
                bio.setVisibility(View.VISIBLE);
            }
            if(profile.getProfessional_mode()){
                posts.setText(String.format(Locale.getDefault(), "%d", profile.getPosts()));
                followers.setText(String.format(Locale.getDefault(), "%d", profile.getFollowers()));
                followings.setText(String.format(Locale.getDefault(), "%d", profile.getFollowings()));
                professionalView.setVisibility(View.VISIBLE);
            }
            if(null != profile.getAddress()){
                global.setBoldText(address, "From "+profile.getAddress(), profile.getAddress());
                view.findViewById(R.id.infoAddressSection).setVisibility(View.VISIBLE);
            }
            if(null != profile.getLiving()){
                global.setBoldText(living, "Lives in "+profile.getLiving(), profile.getLiving());
                living.findViewById(R.id.livesInSection).setVisibility(View.VISIBLE);
            }
            if(null != profile.getInstitute()){
                global.setBoldText(institute, "Went to "+profile.getInstitute(), profile.getInstitute());
                institute.findViewById(R.id.collegeInfoSection).setVisibility(View.VISIBLE);
            }
            if(null != profile.getWork()){
                global.setBoldText(work, "Works at "+profile.getWork(), profile.getWork());
                view.findViewById(R.id.workSection).setVisibility(View.VISIBLE);
            }
            if(null != profile.getTime_joined()){
                global.setBoldText(memberSince, "Member since "+profile.getTime_joined(), profile.getTime_joined());
            }

        }
        else {
            //Its a post
            view = LayoutInflater.from(context).inflate(R.layout.posts, parent, false);
            //Do process
            //Elements
            CircleImageView face = view.findViewById(R.id.userImage);
            ImageView badge = view.findViewById(R.id.badge);

            TextView username = view.findViewById(R.id.userName),
                    time = view.findViewById(R.id.timeAgo),
                    menu = view.findViewById(R.id.menu);

            ZoomageView mediaImage = view.findViewById(R.id.PostImage);
            LinearLayout mediaLoading = view.findViewById(R.id.imageLoading),
                    mediaLoadFailed = view.findViewById(R.id.loadFailed);

            //This is the profile of the post user not the viewer. The viewer is on item.getProfile()
            Profile postUser = item.getPostsObject().getUser_profile();
            username.setText(postUser.getFull_name());
            if(postUser.getVerified_badge()){
                badge.setVisibility(View.VISIBLE);
            }
            if(null != postUser.getFace()){
                Picasso.get().load(Server.getUserAsset(postUser.getFace())).placeholder(global.getDrawable(R.drawable.user_image_default)).into(face);
            }

            //Media
            List<String> files = item.getPostsObject().getFile_array();
            loadMedia.get(Server.getUserAsset(Server.getUserAsset(files.get(0))), new LoadMedia.CallBack() {
                @Override
                public void onDone(File file) {
                    mediaImage.setImageURI(Uri.fromFile(file));
                    mediaLoading.setVisibility(View.GONE);
                    mediaImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail(String message) {
                    Log.d(Global.LOG_TAG, message);
                    mediaLoading.setVisibility(View.GONE);
                    mediaLoadFailed.setVisibility(View.VISIBLE);
                }
            });
        }
        holder.mainCardBody.addView(view);
        freeLoading(holder);
    }

    private void freeLoading(GridViewHolder holder){
        //Free loading
        holder.loading.setVisibility(View.GONE);
        holder.mainCardBody.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mainCardBody, loading;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            mainCardBody = itemView.findViewById(R.id.mainCardBody);
            loading = itemView.findViewById(R.id.loading);
        }
    }
}