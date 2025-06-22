package com.fernan.pomodorotime.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Secondary700,
    onPrimary = Secondary200,
    primaryContainer = Primary100,
    secondary = Secondary500,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary100,
    background = BackgroundL,
    onBackground = Color(0xFFFFFFFF),
    surface = SurfaceL,
    onSurface = OnSurface,
    error = Error900,
    onError = OnError
)

private val DarkColorScheme = darkColorScheme(
    primary = Secondary700,
    onPrimary = Secondary200,
    primaryContainer = Primary700,
    secondary = Secondary500,
    onSecondary = OnBackground,
    secondaryContainer = Secondary700,
    background = Background,
    onBackground = Color(0xFFFFFFFF),
    surface = Surface,
    error = Error300,
    onError = OnSurface,

    //text
    onSurface = OnSurface

)


@Composable
fun PomodoroTimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme // usa los que definiste arriba
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
