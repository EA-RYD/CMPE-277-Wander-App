package com.example.wander_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wander_app.models.VacationPicture;

public class ViewPagerItemFragment extends Fragment {
    public ImageView imageView;
    private TextView caption;
    private VacationPicture pic;

    public static ViewPagerItemFragment getInstance(VacationPicture picture) {
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        if (picture != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("pic", picture);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pic = getArguments().getParcelable("pic");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_pager_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imageView = view.findViewById(R.id.image);
        caption = view.findViewById(R.id.imageTitle);
        init();
    }

    private void init() {
        if (pic != null) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
            Glide.with(getActivity()).setDefaultRequestOptions(options).load(pic.getImage()).into(imageView);
            caption.setText(pic.getTitle());
        }
    }
}
