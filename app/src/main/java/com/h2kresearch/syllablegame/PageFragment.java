package com.h2kresearch.syllablegame;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by ishsrain on 2017. 12. 11..
 */

public class PageFragment extends Fragment {

  private int mPageNumber;

  public static PageFragment create(int pageNumber) {
    PageFragment fragment = new PageFragment();
    Bundle args = new Bundle();
    args.putInt("page", pageNumber);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mPageNumber = getArguments().getInt("page");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_page, container, false);

    String imageName = "tutorial"+ (mPageNumber + 1);
    int imageID = getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName());
    ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);
    imageView.setImageResource(imageID);

    return rootView;
  }
}
