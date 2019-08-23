package takahashi.kazuyoshi.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    private var mHandler = Handler()
    private var mIsStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ContentsInit()
            }
            else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        }
        else {
            ContentsInit()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ContentsInit()
                }
        }
    }

    private fun ContentsInit() {
        buttonsInit()
        getContentsInfo()
    }
    private fun buttonsInit() {
        Play_Stopbutton.setOnClickListener{
            if(!mIsStarted) {
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mTimerSec += 0.1
                        mHandler.post {
                            imageCange(1)
                        }
                    }
                }, 100, 2000)
                mIsStarted = true
                Play_Stopbutton.text = "停止"
                Previousbutton.isEnabled = false
                Nextbutton.isEnabled = false

            }
            else {
                mTimer!!.cancel()
                mIsStarted = false
                Play_Stopbutton.text = "再生"
                Previousbutton.isEnabled = true
                Nextbutton.isEnabled = true
            }

        }
        //再生・停止ボタン


        //次へボタン
        Nextbutton.setOnClickListener{
            imageCange(1)
        }

        //前へボタン
        Previousbutton.setOnClickListener{
            imageCange(-1)
        }

    }

    private var imageUriList: MutableList<Uri> = mutableListOf()
    private fun getContentsInfo() {
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor!!.moveToFirst()) {
            do {
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageUriList.add(imageUri)
            } while (cursor.moveToNext());
            cursor.close()
        }
    }

    var image_no = 0
    private  fun imageCange(change_num: Int) {
        image_no += change_num
        image_no %= imageUriList.size

        if (image_no < 0) {
            image_no += imageUriList.size

        }

        val uri = imageUriList.get(image_no)

        imageView.setImageURI(uri)
    }
}
