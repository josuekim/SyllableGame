package com.h2kresearch.syllablegame;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class TutorialActivity extends BGMActivity {

  ViewPager mViewPager;
  PagerAdapter mPagerAdapter;
  RadioGroup mRadioGroup;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tutorial);

    mViewPager = (ViewPager) findViewById(R.id.pager);
    mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
    mViewPager.setAdapter(mPagerAdapter);

    mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);


    mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Toast.makeText(getApplicationContext(), "scrolled", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onPageSelected(int position) {

        String buttonName = "radioButton"+ position;
        int buttonID = getResources().getIdentifier(buttonName, "id", getPackageName());
        mRadioGroup.check(buttonID);

        if(position == mPagerAdapter.getCount() - 1) {
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
              finish();
              overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
          }, 500);
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {
//        Toast.makeText(getApplicationContext(), "scrollstate", Toast.LENGTH_LONG).show();
      }
    });
  }

  private class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      // 해당하는 page의 Fragment를 생성합니다.
      return PageFragment.create(position);
    }

    @Override
    public int getCount() {
      return 4;  // 총 4개의 page를 보여줍니다.
    }
  }

  @Override
  public void onBackPressed() {
//    super.onBackPressed();
  }
}
