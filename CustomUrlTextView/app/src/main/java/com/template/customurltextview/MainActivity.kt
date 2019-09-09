package com.template.customurltextview

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signInTextView.setListener(object : CustomUrlTextView.OnClickLinkListener{
            override fun onClick() {
                ContextCompat.startActivity(this@MainActivity, Intent(this@MainActivity, LoginActivity::class.java), null)
            }
        })

        val urlDestination = "https://google.com"
        signUpTextView.setListener(object : CustomUrlTextView.OnClickLinkListener{
            override fun onClick() {
                ContextCompat.startActivity(this@MainActivity, Intent(Intent.ACTION_VIEW, Uri.parse(urlDestination)), null)
            }
        })


    }
}
