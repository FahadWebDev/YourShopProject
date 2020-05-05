package com.example.yourshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import Admin.AdminCategoryActivity;
import Admin.adminAddNewProductActivity;

public class UserCategoryActivity extends AppCompatActivity
{
    private ImageView usertShirts, userSportsShirts, userFemaleDresses, userSweaters;
    private ImageView userGlasses, userHatsCaps, userWalletBagsPurses, userShoes;

    private ImageView userHeadPhonesHandFree, userLaptops, userMobilesPhones, userWatches;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_category);

        usertShirts=(ImageView) findViewById(R.id.user_t_shirts);
        userSportsShirts=(ImageView) findViewById(R.id.user_sports_tShirts);

        userFemaleDresses=(ImageView) findViewById(R.id.user_female_dresses);
        userSweaters=(ImageView) findViewById(R.id.user_sweaters);

        userGlasses=(ImageView) findViewById(R.id.user_glasses);
        userHatsCaps=(ImageView) findViewById(R.id.user_hatsCaps);

        userWalletBagsPurses=(ImageView) findViewById(R.id.user_walletBagesPurses);
        userShoes=(ImageView) findViewById(R.id.user_shoes);

        userHeadPhonesHandFree=(ImageView) findViewById(R.id.user_headPhonesHandFree);
        userLaptops=(ImageView) findViewById(R.id.user_laptops);

        userMobilesPhones=(ImageView) findViewById(R.id.user_mobilePhones);
        userWatches=(ImageView) findViewById(R.id.user_watches);

        usertShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Shirts Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userSportsShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Sports Shirts Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userFemaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Female Dresses Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userSweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Sweaters Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userGlasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Glasses Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userWalletBagsPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Shoes Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userHatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Hats Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userShoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Shoes Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userHeadPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Headphones & Handfree Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userLaptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Laptops Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userWatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Watches Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        userMobilesPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "Mobile Phones Category Here!", Toast.LENGTH_SHORT).show();
            }
        });

        usertShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(UserCategoryActivity.this, "T Shirts Category Here!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
