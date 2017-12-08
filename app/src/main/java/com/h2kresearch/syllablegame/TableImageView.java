package com.h2kresearch.syllablegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

public class TableImageView extends AppCompatImageView {

  // Sound Source
  int mSound = 0;

  // Normal Image
  int mImage = 0;

  // Select Image
  int mSelectImage = 0;

  // Ratio
  int mRatio = 4;

  // Selection
  boolean mSelect = false;

  // String
  String mStr;

  // Character
  char mCh1; // 초성
  char mCh2; // 중성
  char mCh3; // 종성

  // Listener
  SelectViewListener mCallback;

  // Constructor
  public TableImageView(Context context) {
    super(context);

    mCallback = (SelectViewListener) context;

    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!mSelect) {
          selectImage();
          updateImage();
        } else {
          cancelImage();
          updateImage();
        }
      }
    });
  }

  public TableImageView(Context context,
      @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public TableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

  }

  // If Image is selected,
  void selectImage(){
    setImage(mSelectImage);
    mSelect = true;
  }

  // Canceled
  void cancelImage(){
    setImage(mImage);
    mSelect = false;

  }

  void updateImage(){
    mCallback.selectView(this);
  }

  // Resource ID
  void setNormalImageID(int resource){
    mImage = resource;
  }

  int getNormalImageID(){
    return mImage;
  }

  void setSelectImageID(int resource){
    mSelectImage = resource;
  }

  int getSelectImageID(){
    return mSelectImage;
  }

  void setSoundID(int resource){
    mSound = resource;
  }

  int getSoundID(){
    return mSound;
  }

  // String
  void setString(String text) {
    mStr = text;
  }

  void setChar(char cho, char jung, char jong) {
    mCh1 = cho;
    mCh2 = cho;
    mCh3 = cho;
  }

  // Resize Image
  void setImage(int resource) {

    setImageResource(resource);

//    Bitmap bmp = BitmapFactory.decodeResource(getResources(), resource);
//    Bitmap resizeBitmap = Bitmap.createScaledBitmap(bmp, bmp.getWidth()/mRatio, bmp.getHeight()/mRatio, true);
////    bmp.recycle();
////    BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
////    if(bitmapDrawable != null) {
////      Bitmap currentBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
////      if (currentBitmap != null) {
////        currentBitmap.recycle();
////      }
////    }
//    setImageBitmap(resizeBitmap);
  }

  // Interface
  public interface SelectViewListener {
    void selectView(TableImageView view);
  }
}