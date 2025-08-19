package com.coroutines.swisstime.ui.components

import AboutAppText
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutines.swisstime.R
import com.coroutines.swisstime.ui.theme.SwissTimeTheme
import com.coroutines.swisstime.ui.theme.ThemeMode
import com.coroutines.swisstime.ui.theme.DarkNavy
import com.coroutines.swisstime.utils.getApplicationVersionInfo
import androidx.compose.ui.platform.LocalContext
import com.coroutines.swisstime.ui.theme.DarkGold
import com.coroutines.swisstime.utils.darken

@Composable
fun ModalDrawerContent(){
    ModalDrawerSheet (drawerContainerColor = DarkNavy.darken(0.45f)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                color = Color.White,
                text = "World Timezone Clock",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(30.dp))

            val context = LocalContext.current
            val (versionName, versionCode) = getApplicationVersionInfo(context)

            ListItem(
                headlineContent = { Text("Application Version") },
                supportingContent = { Text("Version $versionName") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    supportingColor = DarkGold,
                    leadingIconColor = Color.White),

                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Numbers,
                        contentDescription = "Version"
                    )
                }
            )

            ListItem(
                headlineContent = { Text("About") },
                supportingContent = { Text(AboutAppText) },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    supportingColor = DarkGold,
                    leadingIconColor = Color.White),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About"
                    )
                }
            )
        }


        /*
    // About item
    ListItem(
        headlineContent = { Text("About") },
        supportingContent = { Text("World Timezone Clock") },
        leadingContent = {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "About"
        )
    }
    )*/
    }

}

@Preview(name = "Light Mode", uiMode =  Configuration.UI_MODE_NIGHT_NO)
@Composable
fun ModalDrawerContentPreview(){
    SwissTimeTheme(
        themeMode = ThemeMode.DAY,
        dynamicColor = false
    ) {
        ModalDrawerContent()
    }
}