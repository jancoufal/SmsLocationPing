package cz.iddqd.smslocationping

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cz.iddqd.smslocationping.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private var REQUIRED_PERMISSIONS = listOf(
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.READ_SMS,
		Manifest.permission.SEND_SMS,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.ACCESS_FINE_LOCATION
	)

	private lateinit var binding : ActivityMainBinding
	private lateinit var textPermissionDenied : String
	private lateinit var textPermissionGranted : String
	private lateinit var permissionElements : List<PermissionUiPack>

	data class PermissionUiPack(val permission: String, val uiBinding: TextView, val name: String)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		Log.d(TAG, "onCreate")

		textPermissionDenied = resources.getString(R.string.permission_denied)
		textPermissionGranted = resources.getString(R.string.permission_granted)

		with(binding) {
			btnRefreshStatus.setOnClickListener(this@MainActivity::onRefreshStatusClick)
			btnDebug.setOnClickListener(this@MainActivity::onDebugClick)

			permissionElements = listOf(
				PermissionUiPack(Manifest.permission.RECEIVE_SMS, binding.textReceiveSms, resources.getString(R.string.receive_sms)),
				PermissionUiPack(Manifest.permission.READ_SMS, binding.textReadSms, resources.getString(R.string.read_sms)),
				PermissionUiPack(Manifest.permission.SEND_SMS, binding.textSendSms, resources.getString(R.string.send_sms)),
				PermissionUiPack(Manifest.permission.ACCESS_COARSE_LOCATION, binding.textAccessCoarseLocation, resources.getString(R.string.access_coarse_location)),
				PermissionUiPack(Manifest.permission.ACCESS_FINE_LOCATION, binding.textAccessFineLocation, resources.getString(R.string.access_fine_location)),
			)
		}

		refreshUi()
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

	@SuppressLint("SetTextI18n")
	private fun refreshUi() {
		permissionElements.forEach {
			val permissionGranted = ContextCompat.checkSelfPermission(baseContext, it.permission) == PackageManager.PERMISSION_GRANTED
			with(it.uiBinding) {
				text = "* ${it.name}? ${if(permissionGranted) textPermissionGranted else textPermissionDenied}"
				setTextColor(if(permissionGranted) Color.GREEN else Color.RED)
			}
		}
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