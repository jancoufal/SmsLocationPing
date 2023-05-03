package cz.iddqd.smslocationping

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cz.iddqd.smslocationping.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private lateinit var binding : ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		Log.d(TAG, "onCreate")

		with(binding) {
			btnRefreshStatus.setOnClickListener(this@MainActivity::onRefreshStatusClick)
			btnDebug.setOnClickListener(this@MainActivity::onDebugClick)
		}
	}

	override fun onPause() {
		super.onPause()
		Log.d(TAG, "onPause")
	}

	override fun onResume() {
		super.onResume()
		Log.d(TAG, "onResume")
	}

	override fun onRestart() {
		super.onRestart()
		Log.d(TAG, "onRestart")
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.d(TAG, "onDestroy")
	}

	private fun onRefreshStatusClick(v: View?) {
		Log.d(TAG, "onRefreshStatusClick($v)")
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
		Log.d(TAG, "permissionCheck")

		val missingAnyPermission = requiredPermissions.stream()
			.peek { Log.d(TAG, "permission $it") }
			.map { ContextCompat.checkSelfPermission(baseContext, it) }
			.peek { Log.d(TAG, "...$it (" + (if (it == PackageManager.PERMISSION_GRANTED) "ok" else "denied") + ")") }
			.anyMatch { it != PackageManager.PERMISSION_GRANTED }

		Log.d(TAG, "missingAnyPermission $missingAnyPermission")

		return missingAnyPermission
	}

	private fun askForPermissions(requiredPermissions: List<String>) {
		ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), 666)
	}

	companion object {
		val TAG: String = MainActivity::class.java.simpleName
	}
}