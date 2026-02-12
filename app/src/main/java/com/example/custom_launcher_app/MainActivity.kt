package com.example.custom_launcher_app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.custom_launcher_app.ui.theme.Custom_launcher_appTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val chromeIcon: ImageView = findViewById(R.id.chromeButton)
        chromeIcon.setImageDrawable(getActivityIcon(this, "com.android.chrome", "com.google.android.apps.chrome.Main")
        )
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Custom_launcher_appTheme {
        Greeting("Android")
    }
}
fun getActivityIcon(context: Context, packageName: String, activityName: String): Drawable? {
    val pm: PackageManager = context.packageManager
    val intent: Intent = Intent()
    intent.setComponent(ComponentName(packageName, activityName))
    val resolveInfo: ResolveInfo? = pm.resolveActivity(intent, 0)
    return resolveInfo?.loadIcon(pm)
}