package com.app.body_manage.databinding;
import com.app.body_manage.R;
import com.app.body_manage.BR;
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
        sViewsWithIds.put(R.id.add_content, 1);
        sViewsWithIds.put(R.id.training_time, 2);
        sViewsWithIds.put(R.id.time_field, 3);
        sViewsWithIds.put(R.id.weight, 4);
        sViewsWithIds.put(R.id.weight_field, 5);
        sViewsWithIds.put(R.id.fat, 6);
        sViewsWithIds.put(R.id.fat_field, 7);
        sViewsWithIds.put(R.id.prev_img, 8);
        sViewsWithIds.put(R.id.camera, 9);
        sViewsWithIds.put(R.id.save_container, 10);
        sViewsWithIds.put(R.id.back_btn, 11);
        sViewsWithIds.put(R.id.save_btn, 12);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public TrainingDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 13, sIncludes, sViewsWithIds));
    }
    private TrainingDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ScrollView) bindings[1]
            , (android.widget.Button) bindings[11]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[9]
            , (com.google.android.material.textfield.TextInputLayout) bindings[6]
            , (com.google.android.material.textfield.TextInputEditText) bindings[7]
            , (androidx.viewpager2.widget.ViewPager2) bindings[8]
            , (android.widget.Button) bindings[12]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[10]
            , (com.google.android.material.textfield.TextInputEditText) bindings[3]
            , (com.google.android.material.textfield.TextInputLayout) bindings[2]
            , (com.google.android.material.textfield.TextInputLayout) bindings[4]
            , (com.google.android.material.textfield.TextInputEditText) bindings[5]
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
                mDirtyFlags = 0x2L;
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
        if (BR.viewModel == variableId) {
            setViewModel((com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setViewModel(@Nullable com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel ViewModel) {
        this.mViewModel = ViewModel;
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
        flag 0 (0x1L): viewModel
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}