package com.fedeyruben.proyectofinaldamd.ui.customStyleComponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fedeyruben.proyectofinaldamd.ui.theme.BlueColorStyle
import com.fedeyruben.proyectofinaldamd.ui.theme.DisabledButtonColorStyle
import com.fedeyruben.proyectofinaldamd.ui.theme.GreenColorStyle
import com.fedeyruben.proyectofinaldamd.ui.theme.fontFamily

@Composable
fun ButtonStyle(textButton: String, loginEnable: Boolean, modifier: Modifier, onClickAction: () -> Unit) {
    Button(
        onClick = { onClickAction() },
        enabled = loginEnable,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (loginEnable) GreenColorStyle else DisabledButtonColorStyle
        ),
        border = BorderStroke(1.dp, BlueColorStyle),
        contentPadding = PaddingValues(10.dp),
        shape = ButtonDefaults.elevatedShape
    ) {
        Text(
            text = textButton,
            fontFamily = fontFamily(),
            color = if (loginEnable) Color.White else BlueColorStyle,
            fontSize = 24.sp,
        )
    }
}