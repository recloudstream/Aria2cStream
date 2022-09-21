# FetchButton

#### [Work in progress]

[![](https://jitpack.io/v/LagradOst/Aria2cButton.svg)](https://jitpack.io/#LagradOst/Aria2cButton)

Just a simple library to have a download button which works with Aria2c. Mostly for personal usage.

TODO:
- [x] Themeable/Attributes
- [ ] Testing with recyclerview
- [x] More default download buttons

### Usage:
You should declare these in your style, or else it will crash when inflating the view
```
<item name="aria2c_icon_color">?attr/white</item>
<item name="aria2c_fill_color">?attr/white</item>
<item name="aria2c_outline_color">?attr/white</item>
```

```xml
<com.lagradost.fetchbutton.ui.PieFetchButton
    android:id="@+id/download_button"
    android:layout_width="80dp"
    android:layout_height="80dp">
</com.lagradost.fetchbutton.ui.PieFetchButton>
```

```kotlin
val downloadButton = findViewById<PieFetchButton>(R.id.download_button)

// This is required to run once in the app before any aria2c usage.
thread {
	Aria2Starter.start(
		this,
		Aria2Settings(
			UUID.randomUUID().toString(),
			4337,
			filesDir.path,
			"${filesDir.path}/session"
		)
	)
}

// this is used to store data for resuming next app launch
downloadButton.setPersistentId(pId) 

// Arbitrary path
val uriReq = newUriRequest(
    pId, "https://speed.hetzner.de/100MB.bin", "Hello World",
    notificationMetaData = NotificationMetaData(
        pId.toInt(), 
        0xFF0000,
        "contentTitle",
        "Subtext",
        "row2Extra",
        "https://www.royalroadcdn.com/public/covers-full/36735-the-perfect-run.jpg",
        "SpeedTest",
        "secondRow"
    )
)

// This is just to play around with the default behavior, use
// downloadButton.pauseDownload() and such to control the download easily
downloadButton.setDefaultClickListener { 
    listOf(uriReq)
}
```
