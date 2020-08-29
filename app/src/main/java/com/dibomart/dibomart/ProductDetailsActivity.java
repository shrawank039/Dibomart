package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dibomart.dibomart.model.ProductList;

public class ProductDetailsActivity extends AppCompatActivity {

    ProductList ongoing;
    TextView title, desc;
    TextView quantity;
    TextView price;
    TextView special_price;
    TextView item_count;
    ImageView productImg;
    ImageView favImg,imgAdd,imgLess;
    String priceA;
    String priceB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        title = findViewById(R.id.title);
        desc = findViewById(R.id.txt_desc);
        quantity = findViewById(R.id.txt_quantity);
        price = findViewById(R.id.txt_mrp);
        special_price = findViewById(R.id.txt_price);
        productImg = findViewById(R.id.product_image);
        favImg = findViewById(R.id.img_fav);
        imgAdd = findViewById(R.id.img_add);
        imgLess = findViewById(R.id.img_less);
        item_count = findViewById(R.id.txt_itemcount);
        ongoing = (ProductList) getIntent().getSerializableExtra("product_id");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density  = getResources().getDisplayMetrics().density;
        //  float height =  displayMetrics.heightPixels/density;
        float width = displayMetrics.widthPixels/density;
        int b = (int) (width*density);
        LinearLayout llImg = findViewById(R.id.ll_img);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,b);
        llImg.setLayoutParams(param);

        title.setText(ongoing.getName());
        if (!ongoing.getDescription().equals(""))
            desc.setText(ongoing.getDescription());
        String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
        quantity.setText(a);
        item_count.setText(String.valueOf(ongoing.getItem_count()));
        if (!ongoing.getPrice().equals(""))
            price.setText("\u20B9"+ongoing.getPrice());
        else
            price.setVisibility(View.GONE);
        special_price.setText("\u20B9"+ongoing.getSpecial_price());
        Glide.with(this)
                .load(ongoing.getImage_url())
                .placeholder(R.drawable.placeholder)
                .into(productImg);

        priceA = ongoing.getPrice();
        priceB = ongoing.getSpecial_price();

        favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // holder.favImg.setImageDrawable(getResources().getDrawable(R.drawable.heart_selected));
            }
        });
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(ctx, "add", Toast.LENGTH_SHORT).show();
                ongoing.setItem_count(ongoing.getItem_count()+1);
                int b = Integer.parseInt(priceA)*ongoing.getItem_count();
                int c = Integer.parseInt(priceB)*ongoing.getItem_count();
                ongoing.setPrice(String.valueOf(b));
                ongoing.setSpecial_price(String.valueOf(c));
                //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
                if (!ongoing.getPrice().equals(""))
                    price.setText("\u20B9"+ongoing.getPrice());
                else
                    price.setVisibility(View.GONE);
                special_price.setText("\u20B9" + ongoing.getSpecial_price());
                item_count.setText(String.valueOf(ongoing.getItem_count()));
            }
        });
        imgLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ongoing.getItem_count()>1) {
                    ongoing.setItem_count(ongoing.getItem_count() - 1);
                    //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
                    ongoing.setItem_count(ongoing.getItem_count()-1);
                    int b = Integer.parseInt(priceA)*ongoing.getItem_count();
                    int c = Integer.parseInt(priceB)*ongoing.getItem_count();
                    ongoing.setPrice(String.valueOf(b));
                    ongoing.setSpecial_price(String.valueOf(c));
                    //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
                    if (!ongoing.getPrice().equals(""))
                        price.setText("\u20B9"+ongoing.getPrice());
                    else
                        price.setVisibility(View.GONE);
                    special_price.setText("\u20B9"+ongoing.getSpecial_price());
                    item_count.setText(String.valueOf(ongoing.getItem_count()));
                }

            }
        });


    }
}