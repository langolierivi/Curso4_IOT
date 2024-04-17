package com.example.curso4iot

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Composable
fun Dial(
    onAngle:(Float) -> Unit
) {
    var angulo by remember {
        mutableStateOf(0f)
    }

    Column {
        Image(
            painter = painterResource(R.mipmap.dial),
            contentDescription = "dial",
            modifier = Modifier
                .size(200.dp)
                .rotate(angulo)//.clickable { angulo = (angulo + 90) % 360f }
                .pointerInput(Unit){
                    detectDragGestures ( onDrag= { change, dragAmount ->
                        if (angulo > -90 && dragAmount.y < 0)
                            angulo = (angulo + dragAmount.y) % 360f
                        else if (angulo < 90 && dragAmount.y > 0)
                            angulo = (angulo + dragAmount.y) % 360f

                    }, onDragEnd = {
                        onAngle((angulo+90))
                    })




                }
        )
        Text(text = angulo.toString())
    }

}

@Preview
@Composable
fun DialPreview() {
    Dial {
        Log.i("angulo", it.toString())
    }
}