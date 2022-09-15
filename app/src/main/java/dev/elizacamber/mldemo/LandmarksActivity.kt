package dev.elizacamber.mldemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions

class LandmarksActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    val localModel = LocalModel.Builder()
        .setAssetFilePath("model_landscape_eu.tflite")
        .build()

    val customImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
        .setConfidenceThreshold(0.5f)
        .setMaxResultCount(5)
        .build()

    val labeler = ImageLabeling.getClient(customImageLabelerOptions)

    private val getPhoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                imageView.setImageURI(imageUri)

                val image = InputImage.fromFilePath(this, uri)
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        // Task completed successfully
                        for (label in labels) {
                            val text = label.text
                            val confidence = label.confidence
                            val index = label.index

                            Log.d("LandmarksActivity.kt", "$text - $confidence")
                        }
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                    }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landmarks)

        val btSelect = findViewById<Button>(R.id.bt_select)
        imageView = findViewById(R.id.iv_photo)
        val tvInfo = findViewById<TextView>(R.id.tv_label_info)

        btSelect.setOnClickListener { getPhoto.launch("image/*") }

    }
}