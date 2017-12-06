package com.h2kresearch.syllablegame.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;

/**
 * Created by ishsrain on 2017. 12. 6..
 */

public class SyllableImageView extends AppCompatImageView {

  public int code = 0;

  public SyllableImageView(Context context) {
    super(context);
  }

  public SyllableImageView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public SyllableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }
}
