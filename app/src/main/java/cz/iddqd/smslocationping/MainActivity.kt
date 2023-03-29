package cz.iddqd.smslocationping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnSvcGetStatus).setOnClickListener(this::onSvcGetStatusClick)
        findViewById<Button>(R.id.btnSvcTurnOn).setOnClickListener(this::onClick)
        findViewById<Button>(R.id.btnSvcTurnOff).setOnClickListener(this::onClick)
    }

    override fun onClick(v: View?) {
        Log.d("EVENT", "$v")
    }

    private fun onSvcGetStatusClick(v: View?) {
        Log.d("EVENT", "onSvcGetStatusClick")
    }
}