package com.dynamic.serverconnect.setting

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.dynamic.serverconnect.R
import com.dynamic.serverconnect.SessionManager
import com.dynamic.serverconnect.StaticVariable
import com.dynamic.serverconnect.databinding.ActivityMainBinding
import com.dynamic.serverconnect.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Arrays
import javax.inject.Inject

/**
 * Created by Charles Raj I on 17/08/23.
 * @author Charles Raj I
 */

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityMainBinding
    @Inject lateinit var sessionManager: SessionManager

    var permissionsList: java.util.ArrayList<String>? = null
    var permissionsCount = 0

    private val BLE_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val ANDROID_13_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.CAMERA
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val ANDROID_12_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA
    )


    var permissionsLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String, Boolean>> { result ->
                val list = ArrayList(result.values)
                permissionsList = ArrayList()
                permissionsCount = 0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    for (i in list.indices) {
                        if (shouldShowRequestPermissionRationale(
                                ANDROID_13_BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsList!!.add(
                                ANDROID_13_BLE_PERMISSIONS[i]
                            )
                        } else if (!hasPermission(
                                this ,
                                ANDROID_13_BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsCount++
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    for (i in list.indices) {
                        if (shouldShowRequestPermissionRationale(
                                ANDROID_12_BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsList!!.add(
                                ANDROID_12_BLE_PERMISSIONS[i]
                            )
                        } else if (!hasPermission(
                                this ,
                                ANDROID_12_BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsCount++
                        }
                    }
                } else {
                    for (i in list.indices) {
                        if (shouldShowRequestPermissionRationale(
                                BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsList!!.add(
                                BLE_PERMISSIONS[i]
                            )
                        } else if (!hasPermission(
                                this ,
                                BLE_PERMISSIONS[i]
                            )
                        ) {
                            permissionsCount++
                        }
                    }
                }
                if (permissionsList!!.size > 0) {
                    //Some permissions are denied and can be asked again.
                    askForPermissions(permissionsList!!)
                } else if (permissionsCount > 0) {
                    //Show alert dialog
                    showPermissionDialog()
                }
            })

    private fun hasPermission(context: Context, permissionStr: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionStr
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun askForPermissions(permissionsList: java.util.ArrayList<String>) {
        if (permissionsList.size > 0) {
            permissionsLauncher.launch(permissionsList.toTypedArray())
        } else {
//            showPermissionDialog()
        }
    }
    private fun showPermissionDialog() {
        var alertDialog: AlertDialog? = null
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission required")
            .setCancelable(false)
            .setMessage("Some permissions are need to be allowed to use this app without any problems.")
            .setPositiveButton("Settings") { dialog: DialogInterface, which: Int ->
                //askForPermissions(permissionsList);
                openAppSettings()
                dialog.dismiss()
            }
        if (alertDialog == null) {
            alertDialog = builder.create()
            if (!alertDialog.isShowing()) {
                alertDialog.show()
            }
        }
    }

    fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", this .getPackageName(), null)
        intent.data = uri
        this .startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        loadData()
        if (intent.getStringExtra("from").equals("Main")){
            activityBinding.toolbar.visibility = View.VISIBLE
        }

        permissionsList = java.util.ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList!!.addAll(Arrays.asList<String>( *ANDROID_13_BLE_PERMISSIONS))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsList!!.addAll(Arrays.asList<String>( *ANDROID_12_BLE_PERMISSIONS))
        } else {
            permissionsList!!.addAll(Arrays.asList<String>( *BLE_PERMISSIONS))
        }
        askForPermissions(permissionsList!!)

        //http://demo.inter-concept.co.uk:8090/json/count?dbname=sbodemogb&sql=exec%20_YPR_AS_EXISTUSER%20@sparam=%27sales%27,@sparam2=%27%27,@hostname=%27DEMO8%27,@app=%27iSAP%20Scanner%27,@version=%272.0.4%27

        activityBinding.saveBtn.setOnClickListener {
            if (isValidated()) {
                sessionManager.setString(
                    StaticVariable.DEVICE_NAME,
                    activityBinding.devicename.text.toString()
                )
                sessionManager.setString(
                    StaticVariable.SCHEMA,
                    activityBinding.schema.text.toString()
                )
                sessionManager.setString(
                    StaticVariable.SERVER,
                    activityBinding.server.text.toString()
                )
                sessionManager.setString(StaticVariable.PORT, activityBinding.port.text.toString())
                sessionManager.setString(
                    StaticVariable.DATABASE,
                    activityBinding.database.text.toString()
                )
                val intent: Intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        activityBinding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadData(){
        if (sessionManager.getString(StaticVariable.DEVICE_NAME)?.isEmpty() != true){
            activityBinding.devicename.setText(sessionManager.getString(StaticVariable.DEVICE_NAME))
        }
        if (sessionManager.getString(StaticVariable.SCHEMA)?.isEmpty() != true){
            activityBinding.schema.setText(sessionManager.getString(StaticVariable.SCHEMA))
        }
        if (sessionManager.getString(StaticVariable.SERVER)?.isEmpty() != true){
            activityBinding.server.setText(sessionManager.getString(StaticVariable.SERVER))
        }
        if (sessionManager.getString(StaticVariable.PORT)?.isEmpty() != true){
            activityBinding.port.setText(sessionManager.getString(StaticVariable.PORT))
        }
        if (sessionManager.getString(StaticVariable.DATABASE)?.isEmpty() != true){
            activityBinding.database.setText(sessionManager.getString(StaticVariable.DATABASE))
        }
    }
    private fun isValidated(): Boolean{
        var validation = true
        if (activityBinding.devicename.text.toString().isEmpty()) {
            activityBinding.devicename.error = "Device name not be empty."
            validation = false
        }
        if (activityBinding.schema.text.toString().isEmpty()) {
            activityBinding.schema.error = "Schema not be empty."
            validation = false
        }
        if (activityBinding.server.text.toString().isEmpty()) {
            activityBinding.server.error = "Server IP name not be empty."
            validation = false
        }
        if (activityBinding.port.text.toString().isEmpty()) {
            activityBinding.port.error = "Port not be empty."
            validation = false
        }
        if (activityBinding.database.text.toString().isEmpty()) {
            activityBinding.database.error = "Database not be empty."
            validation = false
        }
        return validation
    }


}