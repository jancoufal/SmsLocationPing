package cz.iddqd.smslocationping

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cz.iddqd.smslocationping.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
	private val REQUIRED_PERMISSIONS = listOf(
		Manifest.permission.RECEIVE_SMS,
		Manifest.permission.READ_SMS,
		Manifest.permission.SEND_SMS,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.ACCESS_BACKGROUND_LOCATION,
	)

	private lateinit var binding : ActivityMainBinding
	private lateinit var textNo : String
	private lateinit var textYes : String
	private lateinit var textOverallBad : String
	private lateinit var textOverallGood : String
	private lateinit var permissionElements : List<PermissionUiPack>

	data class PermissionUiPack(val permission: String, val uiBinding: TextView, val name: String)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		Log.d(TAG, "onCreate")

		textNo = resources.getString(R.string.no)
		textYes = resources.getString(R.string.yes)
		textOverallBad = resources.getString(R.string.overall_status_bad)
		textOverallGood = resources.getString(R.string.overall_status_good)

		with(binding) {
			btnRefreshStatus.setOnClickListener(this@MainActivity::onRefreshStatusClick)
			btnDebug.setOnClickListener(this@MainActivity::onDebugClick)

			permissionElements = listOf(
				PermissionUiPack(Manifest.permission.RECEIVE_SMS, binding.textReceiveSms, resources.getString(R.string.receive_sms)),
				PermissionUiPack(Manifest.permission.READ_SMS, binding.textReadSms, resources.getString(R.string.read_sms)),
				PermissionUiPack(Manifest.permission.SEND_SMS, binding.textSendSms, resources.getString(R.string.send_sms)),
				PermissionUiPack(Manifest.permission.ACCESS_COARSE_LOCATION, binding.textAccessCoarseLocation, resources.getString(R.string.access_coarse_location)),
				PermissionUiPack(Manifest.permission.ACCESS_FINE_LOCATION, binding.textAccessFineLocation, resources.getString(R.string.access_fine_location)),
				PermissionUiPack(Manifest.permission.ACCESS_BACKGROUND_LOCATION, binding.textAccessBackgroundLocation, resources.getString(R.string.access_background_location)),
			)
		}

		refreshUi()
	}

	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		when(requestCode)
		{
			666 -> {}
		}
	}

	override fun onPause() {
		super.onPause()
		Log.d(TAG, "onPause")
	}

	override fun onResume() {
		super.onResume()
		Log.d(TAG, "onResume")
		refreshUi()
	}

	override fun onRestart() {
		super.onRestart()
		Log.d(TAG, "onRestart")
		refreshUi()
	}

	override fun onDestroy() {
		super.onDestroy()
		Log.d(TAG, "onDestroy")
	}

	private fun onRefreshStatusClick(v: View?) {
		Log.d(TAG, "onRefreshStatusClick($v)")

		// ensure permissions
		val permissionMap = REQUIRED_PERMISSIONS.associateWith { ContextCompat.checkSelfPermission(baseContext, it) }
		if (permissionMap.values.any { it != PackageManager.PERMISSION_GRANTED })
			ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS.toTypedArray(), 666)

		refreshUi()
	}

	private fun onDebugClick(v: View?) {
		Toast.makeText(this, ":P", Toast.LENGTH_LONG).show()
	}

	@SuppressLint("SetTextI18n")
	private fun refreshUi() {
		fun updateTextView(v: TextView, text: String, permissionGranted: Boolean) {
			v.text = text
			v.setTextColor(if(permissionGranted) Color.LTGRAY else Color.RED)
		}

		fun updatePermissionTextView(v: TextView, title: String, permissionGranted: Boolean) {
			updateTextView(v, "$title ${if(permissionGranted) textYes else textNo}", permissionGranted)
		}

		Log.d(TAG, "refreshUi()")

		var overallStatus = true
		permissionElements.forEach {
			val permissionGranted = ContextCompat.checkSelfPermission(baseContext, it.permission) == PackageManager.PERMISSION_GRANTED
			overallStatus = overallStatus && permissionGranted
			updatePermissionTextView(it.uiBinding, it.name, permissionGranted)
		}

		updateTextView(binding.textOverallStatus, if(overallStatus) textOverallGood else textOverallBad, overallStatus)
	}

	companion object {
		val TAG: String = MainActivity::class.java.simpleName
	}
}