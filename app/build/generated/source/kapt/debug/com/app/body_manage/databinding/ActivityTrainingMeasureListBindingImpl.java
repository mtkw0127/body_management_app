package com.app.body_manage.databinding;
import com.app.body_manage.R;
import com.app.body_manage.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityTrainingMeasureListBindingImpl extends ActivityTrainingMeasureListBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.date_field, 2);
        sViewsWithIds.put(R.id.date_text, 3);
        sViewsWithIds.put(R.id.training_measure_list, 4);
        sViewsWithIds.put(R.id.back_btn, 5);
        sViewsWithIds.put(R.id.body_btn, 6);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityTrainingMeasureListBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 7, sIncludes, sViewsWithIds));
    }
    private ActivityTrainingMeasureListBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (com.google.android.material.button.MaterialButton) bindings[5]
            , (com.google.android.material.button.MaterialButton) bindings[6]
            , (android.widget.FrameLayout) bindings[2]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[1]
            , (androidx.recyclerview.widget.RecyclerView) bindings[4]
            );
        this.isEmptyMessage.setTag(null);
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
        if (BR.vm == variableId) {
            setVm((com.app.body_manage.ui.measure.list.MeasureListViewModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setVm(@Nullable com.app.body_manage.ui.measure.list.MeasureListViewModel Vm) {
        this.mVm = Vm;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.vm);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeVmMeasureList((androidx.lifecycle.LiveData<java.util.List<com.app.body_manage.model.BodyMeasureEntity>>) object, fieldId);
        }
        return false;
    }
    private boolean onChangeVmMeasureList(androidx.lifecycle.LiveData<java.util.List<com.app.body_manage.model.BodyMeasureEntity>> VmMeasureList, int fieldId) {
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
        java.util.List<com.app.body_manage.model.BodyMeasureEntity> vmMeasureListGetValue = null;
        int vmMeasureListSize = 0;
        boolean vmMeasureListSizeInt0 = false;
        com.app.body_manage.ui.measure.list.MeasureListViewModel vm = mVm;
        androidx.lifecycle.LiveData<java.util.List<com.app.body_manage.model.BodyMeasureEntity>> vmMeasureList = null;

        if ((dirtyFlags & 0x7L) != 0) {



                if (vm != null) {
                    // read vm.measureList
                    vmMeasureList = vm.getMeasureList();
                }
                updateLiveDataRegistration(0, vmMeasureList);


                if (vmMeasureList != null) {
                    // read vm.measureList.getValue()
                    vmMeasureListGetValue = vmMeasureList.getValue();
                }


                if (vmMeasureListGetValue != null) {
                    // read vm.measureList.getValue().size()
                    vmMeasureListSize = vmMeasureListGetValue.size();
                }


                // read vm.measureList.getValue().size() == 0
                vmMeasureListSizeInt0 = (vmMeasureListSize) == (0);
        }
        // batch finished
        if ((dirtyFlags & 0x7L) != 0) {
            // api target 1

            com.app.body_manage.extension.ViewExtensionKt.setIsExist(this.isEmptyMessage, vmMeasureListSizeInt0);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): vm.measureList
        flag 1 (0x2L): vm
        flag 2 (0x3L): null
    flag mapping end*/
    //end
}