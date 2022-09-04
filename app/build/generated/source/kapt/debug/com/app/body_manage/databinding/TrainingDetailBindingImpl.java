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
        sViewsWithIds.put(R.id.date_field, 2);
        sViewsWithIds.put(R.id.date_text, 3);
        sViewsWithIds.put(R.id.add_content, 4);
        sViewsWithIds.put(R.id.training_time, 5);
        sViewsWithIds.put(R.id.time_field, 6);
        sViewsWithIds.put(R.id.weight, 7);
        sViewsWithIds.put(R.id.weight_field, 8);
        sViewsWithIds.put(R.id.fat, 9);
        sViewsWithIds.put(R.id.fat_field, 10);
        sViewsWithIds.put(R.id.prev_img, 11);
        sViewsWithIds.put(R.id.camera, 12);
        sViewsWithIds.put(R.id.save_container, 13);
        sViewsWithIds.put(R.id.back_btn, 14);
        sViewsWithIds.put(R.id.save_btn, 15);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public TrainingDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private TrainingDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ScrollView) bindings[4]
            , (android.widget.Button) bindings[14]
            , (com.google.android.material.floatingactionbutton.FloatingActionButton) bindings[12]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[2]
            , (android.widget.TextView) bindings[3]
            , (android.widget.ImageView) bindings[1]
            , (com.google.android.material.textfield.TextInputLayout) bindings[9]
            , (com.google.android.material.textfield.TextInputEditText) bindings[10]
            , (androidx.viewpager2.widget.ViewPager2) bindings[11]
            , (android.widget.Button) bindings[15]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[13]
            , (com.google.android.material.textfield.TextInputEditText) bindings[6]
            , (com.google.android.material.textfield.TextInputLayout) bindings[5]
            , (com.google.android.material.textfield.TextInputLayout) bindings[7]
            , (com.google.android.material.textfield.TextInputEditText) bindings[8]
            );
        this.deleteMeasureBtn.setTag(null);
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
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
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.viewModel);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeViewModelDeleteButtonVisibility((androidx.lifecycle.MutableLiveData<java.lang.Integer>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeViewModelDeleteButtonVisibility(androidx.lifecycle.MutableLiveData<java.lang.Integer> ViewModelDeleteButtonVisibility, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
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
        androidx.lifecycle.MutableLiveData<java.lang.Integer> viewModelDeleteButtonVisibility = null;
        java.lang.Integer viewModelDeleteButtonVisibilityGetValue = null;
        int androidxDatabindingViewDataBindingSafeUnboxViewModelDeleteButtonVisibilityGetValue = 0;
        com.app.body_manage.ui.measure.form.BodyMeasureEditFormViewModel viewModel = mViewModel;

        if ((dirtyFlags & 0x7L) != 0) {



                if (viewModel != null) {
                    // read viewModel.deleteButtonVisibility
                    viewModelDeleteButtonVisibility = viewModel.getDeleteButtonVisibility();
                }
                updateLiveDataRegistration(0, viewModelDeleteButtonVisibility);


                if (viewModelDeleteButtonVisibility != null) {
                    // read viewModel.deleteButtonVisibility.getValue()
                    viewModelDeleteButtonVisibilityGetValue = viewModelDeleteButtonVisibility.getValue();
                }


                // read androidx.databinding.ViewDataBinding.safeUnbox(viewModel.deleteButtonVisibility.getValue())
                androidxDatabindingViewDataBindingSafeUnboxViewModelDeleteButtonVisibilityGetValue = androidx.databinding.ViewDataBinding.safeUnbox(viewModelDeleteButtonVisibilityGetValue);
        }
        // batch finished
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            this.deleteMeasureBtn.setVisibility(androidxDatabindingViewDataBindingSafeUnboxViewModelDeleteButtonVisibilityGetValue);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): viewModel.deleteButtonVisibility
        flag 1 (0x2L): viewModel
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}