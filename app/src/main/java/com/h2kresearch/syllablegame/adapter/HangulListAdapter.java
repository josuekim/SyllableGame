package com.h2kresearch.syllablegame.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Josh on 2017. 12. 18..
 */

public class HangulListAdapter extends BaseAdapter {

  private BaseAdapter mListAdapter;
  private int mListAdapterCount;

  public HangulListAdapter(BaseAdapter listAdapter) {
    if(listAdapter == null) {
      throw new IllegalArgumentException("listAdapter cannot be null.");
    }

    this.mListAdapter = listAdapter;
    this.mListAdapterCount = listAdapter.getCount();
  }

  @Override
  public int getCount() {
    if(mListAdapterCount < 5){
      return mListAdapterCount;
    }else{
      return Integer.MAX_VALUE;
    }
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if(mListAdapterCount < 5){
      return mListAdapter.getView(position, convertView, parent);
    }else{
      return mListAdapter.getView(position % mListAdapterCount, convertView, parent);
    }
  }

  @Override
  public Object getItem(int position) {
    if(mListAdapterCount < 5){
      return mListAdapter.getItem(position);
    }else{
      return mListAdapter.getItem(position % mListAdapterCount);
    }
  }

  @Override
  public long getItemId(int position) {
    if(mListAdapterCount < 5){
      return mListAdapter.getItemId(position);
    }else{
      return mListAdapter.getItemId(position % mListAdapterCount);
    }
  }

  @Override
  public boolean areAllItemsEnabled() {
    return mListAdapter.areAllItemsEnabled();
  }

  @Override
  public int getItemViewType(int position) {
    if(mListAdapterCount < 5){
      return mListAdapter.getItemViewType(position);
    }else{
      return mListAdapter.getItemViewType(position % mListAdapterCount);
    }
  }

  @Override
  public int getViewTypeCount() {
    return mListAdapter.getViewTypeCount();
  }

  @Override
  public boolean isEmpty() {
    return mListAdapter.isEmpty();
  }

  @Override
  public boolean isEnabled(int position) {
    if(mListAdapterCount < 5){
      return mListAdapter.isEnabled(position);
    }else{
      return mListAdapter.isEnabled(position % mListAdapterCount);
    }
  }

  @Override
  public void notifyDataSetChanged() {
    mListAdapter.notifyDataSetChanged();
    mListAdapterCount = mListAdapter.getCount();

    super.notifyDataSetChanged();
  }

  @Override
  public void notifyDataSetInvalidated() {
    mListAdapter.notifyDataSetInvalidated();
    super.notifyDataSetInvalidated();
  }

  @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    if(mListAdapterCount < 5){
      return mListAdapter.getDropDownView(position,
          convertView, parent);
    }else{
      return mListAdapter.getDropDownView(position % mListAdapterCount,
        convertView, parent);
    }
  }

  @Override
  public boolean hasStableIds() {
    return mListAdapter.hasStableIds();
  }
}
