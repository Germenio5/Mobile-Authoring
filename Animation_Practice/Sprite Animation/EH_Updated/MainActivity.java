package com.example.dalida_eh2;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    AnimationDrawable spriteAnim1;
    AnimationDrawable spriteAnim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView mySprite1 = findViewById(R.id.imageView1);
        ImageView mySprite2 = findViewById(R.id.imageView2);
        Button myStart = findViewById(R.id.start_button);

        mySprite2.setVisibility(View.INVISIBLE);
        spriteAnim1 = (AnimationDrawable) mySprite1.getDrawable();
        spriteAnim2 = (AnimationDrawable) mySprite2.getDrawable();
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);


        myStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spriteAnim1.stop();
                spriteAnim2.stop();
                mySprite2.clearAnimation();

                mySprite1.setImageResource(R.drawable.anim_list1);
                mySprite2.setImageResource(R.drawable.anim_list2);

                spriteAnim1 = (AnimationDrawable) mySprite1.getDrawable();
                spriteAnim2 = (AnimationDrawable) mySprite2.getDrawable();

                mySprite2.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(
                        MainActivity.this, R.anim.animation
                );

                spriteAnim1.start();
                spriteAnim2.start();
                mySprite2.startAnimation(animation);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}