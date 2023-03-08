package com.example.tippingapp

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tippingapp.ui.theme.TippingAppTheme
import java.lang.Math.round
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TippingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    val totalPerPerson = remember {
        mutableStateOf(0.00)
    }
    val billInput = remember {
        mutableStateOf("0")
    }
    val splitTipCount = remember {
        mutableStateOf(1)
    }

    val percentToTip = remember {
        mutableStateOf(0.0F)
    }
    Surface(color = Color.White) {
        Column(
            modifier = Modifier.padding(20.dp)
        ){
            Total(
                calcTotalOwed(
                    billInput.value,
                    splitTipCount.value,
                    percentToTip.value
                )
            )

            BillDetails(
                billInput = billInput.value,
                splitTipCount = splitTipCount.value,
                percentToTip = percentToTip.value,
                onBillInputChanged = {
                    billInput.value = it
                }, onSplitTipInputChanged = {
                    splitTipCount.value = it
                }, onPercentChanged = {
                    percentToTip.value = it
                }
            )
        }
    }
}

@Composable
fun Total(value: Double) {
    Card(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total per person",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp
            )

            Text(
                text = "$${if (value > 0) roundTo2Decimals(value) else 0.00}",
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BillDetails(billInput: String, splitTipCount: Int, percentToTip: Float,onBillInputChanged: (String) -> Unit,
                onSplitTipInputChanged: (Int) -> Unit, onPercentChanged: (Float) -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 20.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 50.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween)  {
                OutlinedTextField(
                    value = billInput,
                    onValueChange = {
                        onBillInputChanged(it)
                    },
                    label = {Text(text = "Enter Bill")},
                    modifier = Modifier.padding(10.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Icon(
                        painter = painterResource(
                            id = R.drawable.money
                        ),
                        contentDescription = "Clear"
                    )}
                )
            }

            Spacer(modifier = Modifier.padding(20.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Split", modifier = Modifier.padding(horizontal = 15.dp))

                SplitTip(
                    splitTipCount = splitTipCount,
                    modifier = Modifier
                ){
                    onSplitTipInputChanged(it)
                }
            }

            Spacer(modifier = Modifier.padding(5.dp))

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tip", modifier = Modifier.padding(horizontal = 15.dp), textAlign = TextAlign.Justify)

                Text(
                    "$${
                        roundTo2Decimals(
                            calcTip(
                                bill = billInput, 
                                split = splitTipCount, 
                                percent = percentToTip
                            )
                        )}",
                    modifier = Modifier
                        .padding(top = 15.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )

            }

            Column(){
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                    Text("${(percentToTip * 100).roundToInt()}%")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Row(){
                    Slider(value = percentToTip, onValueChange = {
                        onPercentChanged(it)
                    })
                }
            }
        }
    }
}

@Composable
fun SplitTip(splitTipCount: Int, modifier: Modifier, onSplitTipCountChanged: (Int) -> Unit) {
    Column(modifier = modifier){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            CreateCircle(count = splitTipCount, isPositive = false, icon = R.drawable.delete){
                onSplitTipCountChanged(it)
            }

            Spacer(modifier = Modifier.padding(5.dp))

            Text("$splitTipCount", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.padding(5.dp))
            CreateCircle(count = splitTipCount, isPositive = true, icon = R.drawable.add){
                onSplitTipCountChanged(it)
            }
        }
    }

}

@Composable
fun CreateCircle(count: Int, isPositive: Boolean, icon: Int, updateSplitTipCount: (Int) -> Unit){
    Card(modifier = Modifier
        .padding(3.dp)
        .size(50.dp)
        .clickable {
            if (isPositive) {
                updateSplitTipCount(count + 1)
            } else if (!isPositive) {
                if (count > 1) {
                    updateSplitTipCount(count - 1)
                }

            }
        },
        shape = CircleShape,
        elevation = 4.dp) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(
                    id = icon
                ),
                contentDescription = "Clear"
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TippingAppTheme() {
        App()
    }
}