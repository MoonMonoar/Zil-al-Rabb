package com.immo2n.halalife.Main.Adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Profile;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.LoadMedia;
import com.immo2n.halalife.DataObjects.PostsObject;
import com.immo2n.halalife.Main.DataObjects.ProfileGrid;
import com.immo2n.halalife.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
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
            PostsObject object = item.getPostsObject();
            Profile postUser = object.getUser_profile();
            //Elements
            CircleImageView face = view.findViewById(R.id.userImage),
                            faceImage = view.findViewById(R.id.faceImage);
            ImageView badge = view.findViewById(R.id.badge);

            TextView username = view.findViewById(R.id.userName),
                    time = view.findViewById(R.id.timeAgo),
                    bodyText = view.findViewById(R.id.bodyText),
                    likes = view.findViewById(R.id.likeCount),
                    comments = view.findViewById(R.id.commentCount),
                    shares = view.findViewById(R.id.shareCount),
                    menu = view.findViewById(R.id.menu);

            LinearLayout mediaLoading = view.findViewById(R.id.imageLoading),
                    mediaLoadFailed = view.findViewById(R.id.loadFailed);
            RelativeLayout postBodyHolder = view.findViewById(R.id.postBody),
                           contentHolder = view.findViewById(R.id.postContent),
                            mediaBody = view.findViewById(R.id.mediaBody),
                            dpLayer = view.findViewById(R.id.dpLayer);

             contentHolder.removeAllViews(); //Needed

            likes.setText(String.format(Locale.getDefault(), "%d", object.getLikes()));
            comments.setText(String.format(Locale.getDefault(), "%d", object.getComments()));
            shares.setText(String.format(Locale.getDefault(), "%d", object.getShares()));

            //This is the profile of the post user not the viewer. The viewer is on item.getProfile()
            username.setText(postUser.getFull_name());
            if(postUser.getVerified_badge()){
                badge.setVisibility(View.VISIBLE);
            }
            if(null != postUser.getFace()){
                Picasso.get().load(Server.getUserAsset(postUser.getFace())).placeholder(global.getDrawable(R.drawable.user_image_default)).into(face);
            }
            if(null != object.getTime()){
                time.setText(object.getTime());
            }

            //Media
            List<String> files = item.getPostsObject().getFile_array();

            //Media layout
            if(object.getType().equals("DP_UPDATE")) {
                loadMedia.get(Server.getUserAsset(files.get(0)), new LoadMedia.CallBack() {
                    @Override
                    public void onDone(File file) {
                        global.runOnUI(() -> {
                            faceImage.setImageURI(Uri.fromFile(file));
                            mediaLoading.setVisibility(View.GONE);
                            dpLayer.setVisibility(View.VISIBLE);
                        });
                    }

                    @Override
                    public void onFail(String message) {
                        global.runOnUI(() -> {
                            mediaLoading.setVisibility(View.GONE);
                            mediaLoadFailed.setVisibility(View.VISIBLE);
                        });
                    }
                });
            }
            else if(object.getType().equals("POST")){
                if(files.size() > 0) {
                    View child;
                    switch (files.size()) {
                        case 1:
                            View grid = LayoutInflater.from(context).inflate(R.layout.post_1_image, parent, false);
                            ZoomageView image = grid.findViewById(R.id.image);
                            loadMedia.get(Server.getUserAsset(files.get(0)), new LoadMedia.CallBack() {
                                @Override
                                public void onDone(File file) {
                                    global.runOnUI(() -> {
                                        image.setImageURI(Uri.fromFile(file));
                                        mediaLoading.setVisibility(View.GONE);
                                    });
                                }
                                @Override
                                public void onFail(String message) {
                                    global.runOnUI(() -> {
                                        mediaLoading.setVisibility(View.GONE);
                                        mediaLoadFailed.setVisibility(View.VISIBLE);
                                    });
                                }
                            });
                            child = grid;
                            break;
                        case 2:
                            View grid2 = LayoutInflater.from(context).inflate(R.layout.post_2_image, parent, false);
                            List<ImageView> views = new ArrayList<>();
                            views.add(grid2.findViewById(R.id.image1));
                            views.add(grid2.findViewById(R.id.image2));
                            populate(files, views, mediaLoading);
                            child = grid2;
                            break;
                        case 3:
                            View grid3 = LayoutInflater.from(context).inflate(R.layout.post_3_image, parent, false);
                            List<ImageView> views3 = new ArrayList<>();
                            views3.add(grid3.findViewById(R.id.image1_3));
                            views3.add(grid3.findViewById(R.id.image2_3));
                            views3.add(grid3.findViewById(R.id.image3_3));
                            populate(files, views3, mediaLoading);
                            child = grid3;
                            break;
                        case 4:
                            View grid4 = LayoutInflater.from(context).inflate(R.layout.post_4_image, parent, false);
                            List<ImageView> views4 = new ArrayList<>();
                            views4.add(grid4.findViewById(R.id.image1_4));
                            views4.add(grid4.findViewById(R.id.image2_4));
                            views4.add(grid4.findViewById(R.id.image3_4));
                            views4.add(grid4.findViewById(R.id.image4_4));
                            populate(files, views4, mediaLoading);
                            child = grid4;
                            break;
                        default:
                            //More than 4
                            View grid4_plus = LayoutInflater.from(context).inflate(R.layout.post_4_plus_image, parent, false);
                            List<ImageView> views4_plus = new ArrayList<>();
                            views4_plus.add(grid4_plus.findViewById(R.id.image1_4_plus));
                            views4_plus.add(grid4_plus.findViewById(R.id.image2_4_plus));
                            views4_plus.add(grid4_plus.findViewById(R.id.image3_4_plus));
                            views4_plus.add(grid4_plus.findViewById(R.id.image4_4_plus));
                            populate(files, views4_plus, mediaLoading);
                            TextView mode = grid4_plus.findViewById(R.id.more);
                            mode.setText(MessageFormat.format("+{0}", files.size() - 4));
                            child = grid4_plus;
                            break;
                    }
                    contentHolder.addView(child);
                    contentHolder.setVisibility(View.VISIBLE);
                }
                else {
                    mediaBody.setVisibility(View.GONE);
                }
            }

            //Post types -- for body text
            if(null != object.getBody() && !object.getBody().isEmpty()){
                global.setBoldText(bodyText, postUser.getUsername()+" "+ object.getBody(), postUser.getUsername());
                postBodyHolder.setVisibility(View.VISIBLE);
            }
        }
        holder.mainCardBody.addView(view);
        freeLoading(holder);
    }

    int view_index = 0;
    private void populate(List<String> files, List<ImageView> views, LinearLayout mediaLoading) {
        view_index = 0;
        for(String file : files){
            Picasso.get().load(Server.getUserAsset(file)).into(views.get(view_index));
            view_index++;
            if(view_index >= views.size()){
                break;
            }
        }
        mediaLoading.setVisibility(View.GONE);
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