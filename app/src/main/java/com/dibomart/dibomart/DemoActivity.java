package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;

import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener{

    private SliderLayout mDemoSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mDemoSlider = findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        // ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://lh3.googleusercontent.com/proxy/cmTEHUV6Fryx1xbcCEmfZR05ON9mjxqTRVLBSQ7IobHEVXuDg-AnMDCSfpQkNlKfzW7I_GIjeNhLSucmXJ-ZsU4hNuZbZChmVX4vQ9-C");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        listUrl.add("https://www.revive-adserver.com/media/GitHub.jpg");
        // listName.add("JPG - Github");
        // listName.add("PNG - Android Studio");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    //    .description(listName.get(i))
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);

            //add your extra information
            sliderView.bundle(new Bundle());
            //  sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}