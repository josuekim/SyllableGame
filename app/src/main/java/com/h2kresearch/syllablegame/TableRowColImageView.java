package com.h2kresearch.syllablegame;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import java.util.ArrayList;

public class TableRowColImageView extends TableImageView {

  // Array List
  ArrayList<TableImageView> mList = new ArrayList<TableImageView>();

  public TableRowColImageView(Context context) {
    super(context);
  }

  public TableRowColImageView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TableRowColImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  void selectImage() {
    boolean mode = ((MainActivity)mCallback).mSelectMode;
    if(mode) {
      super.selectImage();
      for (int i = 0; i < mList.size(); i++) {
        TableImageView item = mList.get(i);
        if (!item.mSelect) {
          item.selectImage();
          item.updateImage();
        }
      }
    }
  }

  @Override
  void cancelImage() {
    boolean mode = ((MainActivity)mCallback).mSelectMode;
    if(mode) {
      super.cancelImage();
      for (int i = 0; i < mList.size(); i++) {
        TableImageView item = mList.get(i);
        if (item.mSelect) {
          item.cancelImage();
          item.updateImage();
        }
      }
    }
  }

  @Override
  void updateImage() {
    //super.updateImage();
  }
}
