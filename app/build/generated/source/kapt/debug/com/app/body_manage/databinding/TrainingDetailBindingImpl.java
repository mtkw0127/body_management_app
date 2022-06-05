package com.app.body_manage.databinding;
import com.app.body_manage.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class TrainingDetailBindingImpl extends TrainingDetailBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.date_field, 1);
        sViewsWithIds.put(R.id.date_text, 2);
        sViewsWithIds.put(R.id.add_content, 3);
        sViewsWithIds.put(R.id.time_field, 4);
        sViewsWithIds.put(R.id.time_label, 5);
        sViewsWithIds.put(R.id.training_time, 6);
        sViewsWithIds.put(R.id.weight_field, 7);
        sViewsWithIds.put(R.id.weight_label, 8);
        sViewsWithIds.put(R.id.weight, 9);
        sViewsWithIds.put(R.id.fat_field, 10);
        sViewsWithIds.put(R.id.fat_label, 11);
        sViewsWithIds.put(R.id.fat, 12);
        sViewsWithIds.put(R.id.photo_field, 13);
        sViewsWithIds.put(R.id.photo_label, 14);
        sViewsWithIds.put(R.id.camera, 15);
        sViewsWithIds.put(R.id.prev_img, 16);
        sViewsWithIds.put(R.id.save_container, 17);
        sViewsWithIds.put(R.id.back_btn, 18);
        sViewsWithIds.put(R.id.save_btn, 19);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public TrainingDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 20, sIncludes, sViewsWithIds));
    }
    private TrainingDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ScrollView) bindings[3]
            , (android.widget.Button) bindings[18]
            , (android.widget.ImageButton) bindings[15]
            , (android.widget.FrameLayout) bindings[1]
            , (android.widget.TextView) bindings[2]
            , (android.widget.TextView) bindings[12]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[10]
            , (android.widget.TextView) bindings[11]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[13]
            , (android.widget.TextView) bindings[14]
            , (androidx.viewpager2.widget.ViewPager2) bindings[16]
            , (android.widget.Button) bindings[19]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[17]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[4]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[6]
            , (android.widget.TextView) bindings[9]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[7]
            , (android.widget.TextView) bindings[8]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}