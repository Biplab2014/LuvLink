package com.luv.link.ui.onBoard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luv.link.R
import com.luv.link.ui.theme.DigiSignTheme
import kotlinx.coroutines.launch

class OnBoardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DigiSignTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    OnBoardingScreen()
                }
            }
        }
    }
}

@Composable
fun OnBoardingScreen() {
    val pages = listOf(
        OnBoardingPage(
            imageRes = R.drawable.onb1, // Replace with your image resource
            title = "Life is short and the world is wide",
            description = "At Friends tours and travel, we customize reliable and trustworthy educational tours to destinations all over the world."
        ),
        OnBoardingPage(
            imageRes = R.drawable.onb2, // Replace with your image resource
            title = "It's a big world out there go explore",
            description = "To get the best of your adventure you just need to leave and go where you like, we are waiting for you."
        ),
        OnBoardingPage(
            imageRes = R.drawable.onb3, // Replace with your image resource
            title = "People don't take trips, trips take people",
            description = "To get the best of your adventure you just need to leave and go where you like, we are waiting for you."
        )
    )

    val pagerState = rememberPagerState(initialPage = 1) { 3 }
    val animationScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnBoardingPageContent(pages[page])
        }

        // Progress Indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in pages.indices) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (i == pagerState.currentPage) Color.Blue else Color.Gray)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            if (pagerState.currentPage < pages.size - 1) {
                animationScope.launch{
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            } else {
                // Navigate to the main activity or next screen
            }
        }) {
            Text(text = if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started")
        }
    }
}

@Composable
fun OnBoardingPageContent(page: OnBoardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = null,
            modifier = Modifier.size(200.dp) // Adjust size as needed
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.title,
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

data class OnBoardingPage(val imageRes: Int, val title: String, val description: String)