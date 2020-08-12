package com.dibomart.dibomart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.dibomart.dibomart.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SliderLayout mDemoSlider;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mDemoSlider = view.findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
         ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://images.vexels.com/media/users/3/194732/raw/90137aa80ed4d208bc0cda1fc224cfff-online-shopping-web-slider.jpg");
        listUrl.add("https://mall.cmsmasters.net/wp-content/uploads/2015/11/slider2.png");
        listUrl.add("https://lh3.googleusercontent.com/proxy/cmTEHUV6Fryx1xbcCEmfZR05ON9mjxqTRVLBSQ7IobHEVXuDg-AnMDCSfpQkNlKfzW7I_GIjeNhLSucmXJ-ZsU4hNuZbZChmVX4vQ9-C");

//         listName.add("JPG - Github");
//         listName.add("PNG - Android Studio");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(getActivity());
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                      //  .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);

            //add your extra information
            sliderView.bundle(new Bundle());
            //  sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }
        return view;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(), slider.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show();
    }
}