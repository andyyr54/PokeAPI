package com.example.pokeapi

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.pokeapi.MainActivity.PokeInfo.jsonResult
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class InfoScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_screen)
        // write permission to access the storage
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

        // this is the card view whose screenshot
        // we will take in this article
        // get the view using fin view bt id
        val theLayout:ConstraintLayout = findViewById(R.id.theLayout)

        // on click of this button it will capture
        // screenshot and save into gallery
        val captureButton = findViewById<Button>(R.id.button2)
        captureButton.setOnClickListener {
            // get the bitmap of the view using
            // getScreenShotFromView method it is
            // implemented below
            val bitmap = getScreenShotFromView(theLayout)

            // if bitmap is not null then
            // save it to gallery
            if (bitmap != null) {
                saveMediaToStorage(bitmap)
            }
        }

    }

    private fun fetch() {
        var myResult: String?
        val nameOfPokemon = intent.getStringExtra("Pokemon")
        val pokePic: ImageView = findViewById(R.id.imageView)
        val pokeAbility1: EditText = findViewById(R.id.editTextTextPersonName4)
        val pokeAbility2: EditText = findViewById(R.id.editTextTextPersonName3)
        val pokeType: EditText = findViewById(R.id.editTextTextPersonName5)


        val myConnect: makeAPIRequest = makeAPIRequest()
        val builder = Uri.Builder()
        builder.scheme("https")
        builder.authority("pokeapi.co")
        builder.appendPath("api")
        builder.appendPath("v2")
        builder.appendPath("pokemon")
        builder.appendPath("$nameOfPokemon")


        lifecycleScope.launch(Dispatchers.IO) {
            myResult = myConnect.getRequest(builder.toString())

            withContext(Dispatchers.Main) {
                jsonResult = JSONObject(myResult.toString())
                val jsonAbilities = jsonResult.getJSONArray("abilities")
                val jsonTypes = jsonResult.getJSONArray("types")
                val jsonPic = jsonResult.getJSONObject("sprites")

                val tyZeroIndex = jsonTypes.getJSONObject(0)
                val firstType = tyZeroIndex.getJSONObject("type")
                val abZeroIndex = jsonAbilities.getJSONObject(0)
                val firstAbility = abZeroIndex.getJSONObject("ability")

                val other = jsonPic.getJSONObject("other")
                val artwork = other.getJSONObject("official-artwork")
                val imageUrl = artwork.getString("front_default")


                Picasso.get().load(imageUrl).into(pokePic)


                if (jsonTypes.length() > 1 && jsonTypes.length() == 2) {
                    val tyFirstIndex = jsonTypes.getJSONObject(1)
                    val secondType = tyFirstIndex.getJSONObject("type")
                    pokeType.setText(firstType.getString("name") + " and " + secondType.getString("name"))
                } else {
                    pokeType.setText(firstType.getString("name"))
                }

                if (jsonAbilities.length() > 1 && jsonAbilities.length() == 2) {
                    val abFirstIndex = jsonAbilities.getJSONObject(1)
                    val secondAbility = abFirstIndex.getJSONObject("ability")

                    pokeAbility1.setText(firstAbility.getString("name"))
                    pokeAbility2.setText(secondAbility.getString("name"))
                } else {
                    pokeAbility2.setText("This Pokemon only has one ability")
                    pokeAbility1.setText(firstAbility.getString("name"))
                }


            }

        }
    }





            private fun getScreenShotFromView(v: View): Bitmap? {
                // create a bitmap object
                var screenshot: Bitmap? = null
                try {
                    // inflate screenshot object
                    // with Bitmap.createBitmap it
                    // requires three parameters
                    // width and height of the view and
                    // the background color
                    screenshot = Bitmap.createBitmap(
                        v.measuredWidth,
                        v.measuredHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    // Now draw this bitmap on a canvas
                    val canvas = Canvas(screenshot)
                    v.draw(canvas)
                } catch (e: Exception) {
                    Log.e("GFG", "Failed to capture screenshot because:" + e.message)
                }
                // return the bitmap
                return screenshot
            }


            // this method saves the image to gallery
            fun saveMediaToStorage(bitmap: Bitmap) {
                // Generating a file name
                val filename = "${System.currentTimeMillis()}.jpg"

                // Output stream
                var fos: OutputStream? = null

                // For devices running android >= Q
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // getting the contentResolver
                    this.contentResolver?.also { resolver ->

                        // Content resolver will process the contentvalues
                        val contentValues = ContentValues().apply {

                            // putting file information in content values
                            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            put(
                                MediaStore.MediaColumns.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES
                            )
                        }

                        // Inserting the contentValues to
                        // contentResolver and getting the Uri
                        val imageUri: Uri? = resolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )

                        // Opening an outputstream with the Uri that we got
                        fos = imageUri?.let { resolver.openOutputStream(it) }
                    }
                } else {
                    // These for devices running on android < Q
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = File(imagesDir, filename)
                    fos = FileOutputStream(image)
                }

                fos?.use {
                    // Finally writing the bitmap to the output stream that we opened
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    Toast.makeText(this, "Captured View and saved to Gallery", Toast.LENGTH_SHORT)
                        .show()
                }
            }



    fun display(view: View) {
        fetch()
    }


    fun goBack(view: View) {
        this.finish()
    }
}



