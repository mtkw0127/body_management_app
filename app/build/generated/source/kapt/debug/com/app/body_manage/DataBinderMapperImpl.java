package com.app.body_manage;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.app.body_manage.databinding.ActivityGraphBindingImpl;
import com.app.body_manage.databinding.ActivityMainBindingImpl;
import com.app.body_manage.databinding.BottomSheetCapturedListBindingImpl;
import com.app.body_manage.databinding.CameraPreviewBindingImpl;
import com.app.body_manage.databinding.SlideItemContainerBindingImpl;
import com.app.body_manage.databinding.TrainingDetailBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYGRAPH = 1;

  private static final int LAYOUT_ACTIVITYMAIN = 2;

  private static final int LAYOUT_BOTTOMSHEETCAPTUREDLIST = 3;

  private static final int LAYOUT_CAMERAPREVIEW = 4;

  private static final int LAYOUT_SLIDEITEMCONTAINER = 5;

  private static final int LAYOUT_TRAININGDETAIL = 6;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(6);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.activity_graph, LAYOUT_ACTIVITYGRAPH);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.activity_main, LAYOUT_ACTIVITYMAIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.bottom_sheet_captured_list, LAYOUT_BOTTOMSHEETCAPTUREDLIST);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.camera_preview, LAYOUT_CAMERAPREVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.slide_item_container, LAYOUT_SLIDEITEMCONTAINER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.app.body_manage.R.layout.training_detail, LAYOUT_TRAININGDETAIL);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYGRAPH: {
          if ("layout/activity_graph_0".equals(tag)) {
            return new ActivityGraphBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_graph is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMAIN: {
          if ("layout/activity_main_0".equals(tag)) {
            return new ActivityMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_main is invalid. Received: " + tag);
        }
        case  LAYOUT_BOTTOMSHEETCAPTUREDLIST: {
          if ("layout/bottom_sheet_captured_list_0".equals(tag)) {
            return new BottomSheetCapturedListBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for bottom_sheet_captured_list is invalid. Received: " + tag);
        }
        case  LAYOUT_CAMERAPREVIEW: {
          if ("layout/camera_preview_0".equals(tag)) {
            return new CameraPreviewBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for camera_preview is invalid. Received: " + tag);
        }
        case  LAYOUT_SLIDEITEMCONTAINER: {
          if ("layout/slide_item_container_0".equals(tag)) {
            return new SlideItemContainerBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for slide_item_container is invalid. Received: " + tag);
        }
        case  LAYOUT_TRAININGDETAIL: {
          if ("layout/training_detail_0".equals(tag)) {
            return new TrainingDetailBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for training_detail is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(2);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "viewModel");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(6);

    static {
      sKeys.put("layout/activity_graph_0", com.app.body_manage.R.layout.activity_graph);
      sKeys.put("layout/activity_main_0", com.app.body_manage.R.layout.activity_main);
      sKeys.put("layout/bottom_sheet_captured_list_0", com.app.body_manage.R.layout.bottom_sheet_captured_list);
      sKeys.put("layout/camera_preview_0", com.app.body_manage.R.layout.camera_preview);
      sKeys.put("layout/slide_item_container_0", com.app.body_manage.R.layout.slide_item_container);
      sKeys.put("layout/training_detail_0", com.app.body_manage.R.layout.training_detail);
    }
  }
}
