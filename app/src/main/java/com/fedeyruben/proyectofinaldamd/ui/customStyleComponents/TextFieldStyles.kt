package com.fedeyruben.proyectofinaldamd.ui.customStyleComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.fedeyruben.proyectofinaldamd.ui.theme.BlueColorStyle

/**
 * Modificaciones de los OutlinedTextField de Material3
 * para poder ser reutilizales en toda la aplicación
 * con el estilo de OmkRom. OutlinedTextFieldOmkRom() personalizado
 * y variante de contraseña OutlinedTextFieldPasswordOmkRom().
 */

@Composable
fun OutlineTextFieldStyle(
    modifier: Modifier,
    value: String,
    label: String,
    keyboardType: KeyboardType,
    onValueChange: ((String) -> Unit)?,
    enabled: Boolean,
    readOnly: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (onValueChange != null) {
                onValueChange(it)
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = TextStyle(
            color = BlueColorStyle,
            fontSize = 18.sp,
        ),
        label = { Text(text = label) },


        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        shape = RectangleShape,
        colors = textFieldColors()
    )
}

@Composable
fun OutlineTextFieldPasswordStyle(
    modifier: Modifier,
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisibility by rememberSaveable {
        mutableStateOf(false)
    }
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier,
        enabled = true,
        textStyle = TextStyle(
            color = BlueColorStyle,
            fontSize = 18.sp,
        ),
        label = { Text(text = label) },

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                tint = BlueColorStyle,
                contentDescription = "email"
            )
        },
        trailingIcon = {
            if (passwordVisibility) {
                Icon(
                    imageVector = Icons.Filled.VisibilityOff,
                    tint = BlueColorStyle,
                    contentDescription = "don't view",
                    modifier = Modifier
                        .clickable { passwordVisibility = !passwordVisibility }
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    tint = BlueColorStyle,
                    contentDescription = "view",
                    modifier = Modifier
                        .clickable { passwordVisibility = !passwordVisibility }
                )
            }
        },
        visualTransformation =
        if (!passwordVisibility) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        minLines = 1,
        shape = RectangleShape,
        colors = textFieldColors()
    )
}


@Composable
fun textFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = BlueColorStyle,
    unfocusedIndicatorColor = BlueColorStyle,
    disabledIndicatorColor = BlueColorStyle,
    focusedLabelColor = BlueColorStyle,
    unfocusedLabelColor = BlueColorStyle,
    disabledLabelColor = BlueColorStyle,
    cursorColor = BlueColorStyle,
    disabledTextColor = BlueColorStyle
)