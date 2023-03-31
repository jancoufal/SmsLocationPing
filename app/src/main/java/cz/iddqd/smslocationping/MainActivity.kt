package cz.iddqd.smslocationping

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		Log.d("EVENT", "onCreate")

		findViewById<Button>(R.id.btnSvcGetStatus).setOnClickListener(this::onSvcGetStatusClick)
		findViewById<Button>(R.id.btnSvcTurnOn).setOnClickListener(this::onClick)
		findViewById<Button>(R.id.btnSvcTurnOff).setOnClickListener(this::onClick)
		findViewById<Button>(R.id.btnDebug).setOnClickListener(this::onDebugClick)
	}

	override fun onClick(v: View?) {
		Log.d("EVENT", "$v")
	}

	private fun onSvcGetStatusClick(v: View?) {
		Log.d("EVENT", "onSvcGetStatusClick")
	}

	private fun onDebugClick(v: View?) {
		ensurePermissions()
	}

	private fun ensurePermissions(): Boolean {
		val requiredPermissions = listOf(
			Manifest.permission.RECEIVE_SMS,
			Manifest.permission.READ_SMS,
			Manifest.permission.SEND_SMS,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
		)

		if (missingAnyPermission(requiredPermissions))
			askForPermissions(requiredPermissions)

		return missingAnyPermission(requiredPermissions)
	}

	private fun missingAnyPermission(requiredPermissions: List<String>): Boolean {
		Log.d("ASSERT", "permissionCheck")

		val missingAnyPermission = requiredPermissions.stream()
			.peek { p -> Log.d("ASSERT", "permission $p") }
			.map { p -> ContextCompat.checkSelfPermission(baseContext, p) }
			.peek { p ->
				Log.d(
					"ASSERT",
					"...$p (" + (if (p == PackageManager.PERMISSION_GRANTED) "ok" else "denied") + ")"
				)
			}
			.anyMatch { p -> p != PackageManager.PERMISSION_GRANTED }

		Log.d("MAIN", "missingAnyPermission $missingAnyPermission")

		return missingAnyPermission
	}

	private fun askForPermissions(requiredPermissions: List<String>) {
		ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), 666)
	}
}