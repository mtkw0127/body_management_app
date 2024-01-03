package com.app.body_manage.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.Metadata
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.app.body_manage.data.repository.LocalFileRepository
import com.app.body_manage.databinding.CameraPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import timber.log.Timber
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private lateinit var cameraSelector: CameraSelector
    private lateinit var binding: CameraPreviewBinding
    private lateinit var cameraExecutor: ExecutorService

    private val viewModel = CameraViewModel()
    private val sound: MediaActionSound by lazy {
        val sound = MediaActionSound()
        sound.load(MediaActionSound.SHUTTER_CLICK)
        return@lazy sound
    }

    private val deletePhoto: (Int) -> Unit = {
        viewModel.removePhoto(it)
    }

    override fun onDestroy() {
        sound.release()
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraPreviewBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setContentView(binding.root)
        initCamera()
        initBottomSheet()

        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            1
        )

        viewModel.photoList.observe(this) {
            // Preview窓の更新
            if (it.isEmpty()) {
                binding.captured.setImageURI(null)
            }

            // Preview一覧の更新
            binding.bottomSheetInclude.photoListRecyclerView.adapter =
                PhotoListAdapter(
                    dataSet = it.toList(),
                    deletePhoto = deletePhoto
                )
            binding.photoNum.text = viewModel.photoList.value?.size.toString()
            requireNotNull(binding.bottomSheetInclude.photoListRecyclerView.adapter).notifyDataSetChanged()
        }
        viewModel.canTakePhoto.observe(this) {
        }

        // バックグラウンドのエグゼキュータ
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            permissionCheck()
        }
        binding.nextBtn.setOnClickListener {
            // 撮影した結果を返却
            setResult(Activity.RESULT_OK, intent)
            viewModel.photoList.value?.let { it1 -> photoList.addAll(it1) }
            finish()
        }
        // バックの場合、撮影した撮影した撮影を除去
        binding.backFromCameraBtn.setOnClickListener {
            photoList.clear()
            finish()
        }
        binding.switchCamera.setOnClickListener {
            lensFacing = when (lensFacing) {
                CameraSelector.LENS_FACING_BACK -> CameraSelector.LENS_FACING_FRONT
                CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
                else -> CameraSelector.LENS_FACING_BACK
            }
            initCamera()
            startCamera()
        }
        binding.capturedContainer.setOnClickListener {
            if (viewModel.photoList.value?.isNotEmpty() == true) {
                val bottomSheetBehavior =
                    BottomSheetBehavior.from(binding.bottomSheetInclude.bottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        binding.shutterBtn.setOnClickListener {
            viewModel.setCanTakePhoto(false)
            val photoOutputFilePath = createFile(it.context)
            val metadata = Metadata().apply {
                // インカメの場合は写真を反転する
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(photoOutputFilePath.toFile())
                .setMetadata(metadata)
                .build()

            val imageSavedCapture: ImageCapture.OnImageSavedCallback =
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Handler(Looper.getMainLooper()).post {
                            val photoUri = checkNotNull(outputFileResults.savedUri)
                            // 一覧に追加
                            viewModel.addPhoto(photoUri)
                            binding.captured.setImageURI(photoUri)
                            // 最新の写真を端末のギャラリーに保存する
                            LocalFileRepository().savePhotoToExternalDir(
                                photoUri,
                                applicationContext
                            )
                            viewModel.setCanTakePhoto(true)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Handler(Looper.getMainLooper()).post {
                            Timber.e(exception)
                            Toast.makeText(
                                applicationContext,
                                "写真の撮影に失敗しました。",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        viewModel.setCanTakePhoto(true)
                    }
                }
            imageCapture.takePicture(outputOptions, cameraExecutor, imageSavedCapture)
        }
    }

    private fun initBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetInclude.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun initCamera() {
        cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    /**
     * カメラ起動
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            {
                // Camera provider is now guaranteed to be available
                val cameraProvider = cameraProviderFuture.get()

                // Set up the preview use case to display camera preview.
                val preview = Preview.Builder().build()

                // Set up the capture use case to allow users to take photos.
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                cameraProvider.unbindAll()

                // Attach use cases to the camera with the same lifecycle owner
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                    binding.cameraPreview.surfaceProvider
                )
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    private fun permissionCheck() {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA,
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
                return
            }
        }
        finish()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val PHOTO_EXTENSION = ".jpg"
        private const val FILE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSSS"

        val photoList: MutableList<Uri> = mutableListOf()

        fun createCameraActivityIntent(context: Context): Intent {
            photoList.clear()
            return Intent(context.applicationContext, CameraActivity::class.java)
        }

        private fun createFile(context: Context): Path {
            val fileName = SimpleDateFormat(
                FILE_FORMAT,
                Locale.JAPAN
            ).format(System.currentTimeMillis()) + PHOTO_EXTENSION
            return context.filesDir.toPath().resolve(fileName)
        }
    }
}
