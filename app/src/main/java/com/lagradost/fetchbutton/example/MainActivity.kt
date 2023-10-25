package com.lagradost.fetchbutton.example

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import com.lagradost.fetchbutton.aria2c.Aria2Settings
import com.lagradost.fetchbutton.aria2c.Aria2Starter
import com.lagradost.fetchbutton.aria2c.newUriRequest
import java.io.File
import java.util.*
import kotlin.concurrent.thread


/**id, stringRes */
@SuppressLint("RestrictedApi")
fun View.popupMenuNoIcons(
    items: List<Pair<Int, String>>,
    onMenuItemClick: MenuItem.() -> Unit,
): PopupMenu {
    val popup = PopupMenu(context, this, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0)

    items.forEach { (id, stringRes) ->
        popup.menu.add(0, id, 0, stringRes)
    }

    (popup.menu as? MenuBuilder)?.setOptionalIconsVisible(true)

    popup.setOnMenuItemClickListener {
        it.onMenuItemClick()
        true
    }

    popup.show()
    return popup
}

class MainActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                Log.d("TAG", "onActivityResult: " + uri.path);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dir = "${filesDir.path}/session"

        thread {
            Aria2Starter.start(
                this,
                Aria2Settings(
                    UUID.randomUUID().toString(),
                    4337,
                    dir,
                )
            )
            val tmpdir = "${cacheDir.absolutePath}/torrent_tmp"
            File(tmpdir).deleteRecursively()
            val id = 0L

            val uriReq = newUriRequest(
                id = id,
                uri = "",
                fileName = "Hello World",
                directory = tmpdir,
                seed = false
            )

            Aria2Starter.download(
                uriReq
            )
        }
    }
}

