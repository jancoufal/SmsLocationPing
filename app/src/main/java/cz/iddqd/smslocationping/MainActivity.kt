package cz.iddqd.smslocationping

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

	private val myNameIs = "I'm MainActivity"
	private val intentFilter: IntentFilter = IntentFilter()
	private var activityBroadcastReceiver: BroadcastReceiver? = null

	init {
		intentFilter.addAction("myAction1")
		intentFilter.addAction("myAction2")
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		Log.d("EVENT", "onCreate")

		findViewById<Button>(R.id.btnSvcGetStatus).setOnClickListener(this::onSvcGetStatusClick)
		findViewById<Button>(R.id.btnSvcTurnOn).setOnClickListener(this::onClick)
		findViewById<Button>(R.id.btnSvcTurnOff).setOnClickListener(this::onClick)
		findViewById<Button>(R.id.btnDebug).setOnClickListener(this::onDebugClick)

		bindBroadcastReceiver()


	}

	override fun onPause() {
		super.onPause()
		Log.d("EVENT", "onPause")
	}

	override fun onResume() {
		super.onResume()
		Log.d("EVENT", "onResume")
	}

	override fun onRestart() {
		super.onRestart()
		Log.d("EVENT", "onRestart")
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.d("EVENT", "onDestroy")
	}

	private fun onClick(v: View?) {
		Log.d("EVENT", "$v")
	}

	private fun onSvcGetStatusClick(v: View?) {
		Log.d("EVENT", "onSvcGetStatusClick")
		sendBroadcast(Intent("myAction1"))
		sendBroadcast(Intent("myAction2"))
		sendBroadcast(Intent("myAction3"))
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

	private fun bindBroadcastReceiver() {
		if (activityBroadcastReceiver == null) {
			activityBroadcastReceiver = object : BroadcastReceiver() {
				override fun onReceive(context: Context?, intent: Intent?) {
					Log.d(TAG, "activity::myNameIs $myNameIs")
					Thread.sleep(1_000)
					Log.d(TAG, "$myNameIs: activityBroadcastReceiver.onReceive(context: $context, intent: $intent)")
				}
			}.also { registerReceiver(it, intentFilter) }
		}
	}

	companion object {
		val TAG: String = MainActivity::class.java.simpleName
	}
}