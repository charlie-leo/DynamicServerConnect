package com.dynamic.serverconnect.login

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dynamic.serverconnect.R
import com.dynamic.serverconnect.databinding.LoginActivityBinding
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKManager.EMDKListener
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.ScanDataCollection.ScanData
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData
import com.symbol.emdk.barcode.StatusData.ScannerStates
import com.zebra.scannercontrol.BarCodeView
import com.zebra.scannercontrol.DCSScannerInfo
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Charles Raj I on 18/08/23
 * @project DynamicServerConnect
 * @author Charles Raj
 */

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), EMDKListener,
    Scanner.StatusListener, Scanner.DataListener {

    private lateinit var emdkManager: EMDKManager
    private lateinit var barcodeManager: BarcodeManager
    private var scanner: Scanner? = null

    private lateinit var loginActivityBinding: LoginActivityBinding

    private var statusString = ""

    private var bSoftTriggerSelected = false
    private var bDecoderSettingsChanged = false
    private val bExtScannerDisconnected = false

    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    var scanListInfo: List<DCSScannerInfo> = ArrayList<DCSScannerInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginActivityBinding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        loginActivityBinding.loginViewModel = loginViewModel


        // The callback also will receive in the main thread without blocking it until the EMDK resources are ready.
        // The callback also will receive in the main thread without blocking it until the EMDK resources are ready.
        val results = EMDKManager.getEMDKManager(applicationContext, this)

// Check the return status of getEMDKManager() and update the status TextView accordingly.

// Check the return status of getEMDKManager() and update the status TextView accordingly.
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            updateStatus("EMDKManager object request failed!")
            return
        } else {
            updateStatus("EMDKManager object initialization is   in   progress.......")
        }


        loginViewModel.errorML.observe(this){
            if (it.isNotEmpty()){
                Toast.makeText(application,it, Toast.LENGTH_SHORT).show()
                loginActivityBinding.userNameInput.error = it
                loginActivityBinding.userNameInput.isErrorEnabled = true
                loginActivityBinding.passwordInput.error = it
                loginActivityBinding.passwordInput.isErrorEnabled = true
            }else{
                loginActivityBinding.userNameInput.isErrorEnabled = false
                loginActivityBinding.passwordInput.isErrorEnabled = false
            }
        }

        loginActivityBinding.loginBtn.setOnClickListener{
            //if (scanner?.isReadPending!!){
            scanner?.cancelRead()
            scanner?.read()
            //}
        }

    }

    private fun initBarcodeManager() {
        // Get the feature object such as BarcodeManager object for accessing the feature.
        barcodeManager = emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
        // Add external scanner connection listener.
        if (barcodeManager == null) {
            Toast.makeText(this, "Barcode scanning is not supported.", Toast.LENGTH_LONG).show()
            finish()
        }
    }


    private fun initScanner() {
        if (scanner == null) {
            // Get default scanner defined on the device
            scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            //scanner = emdkManager.getInstance(EMDKManager.FEATURE_TYPE.SIMULSCAN) as Scanner
            if (scanner != null) {
                // Implement the DataListener interface and pass the pointer of this object to get the data callbacks.
                scanner?.addDataListener(this)

                // Implement the StatusListener interface and pass the pointer of this object to get the status callbacks.
                scanner?.addStatusListener(this)

//                var config = scanner?.config
////                config?.scanParams?.decodeLEDFeedback = ScannerConfig.DecodeLEDFeedbac
//                config?.scanParams?.decodeLEDFeedbackMode = ScannerConfig.DecodeLEDFeedbackMode.BOTH
//                scanner?.config = config

                // Hard trigger. When this mode is set, the user has to manually
                // press the trigger on the device after issuing the read call.
                // NOTE: For devices without a hard trigger, use TriggerType.SOFT_ALWAYS.
                scanner?.triggerType = Scanner.TriggerType.SOFT_ONCE
                //scanner?.triggerMode =

                try {
                    // Enable the scanner
                    // NOTE: After calling enable(), wait for IDLE status before calling other scanner APIs
                    // such as setConfig() or read().
                    scanner?.enable()
                } catch (e: ScannerException) {
                    updateStatus(e.message!!)
                    deInitScanner()
                }

            } else {
                updateStatus("Failed to   initialize the scanner device.")
            }
        }
    }


    private fun deInitScanner() {
        if (scanner != null) {
            try {
                // Release the scanner
                scanner?.release()
            } catch (e: Exception) {
                updateStatus(e.message!!)
            }
            scanner?.disable()
        }
    }


    private fun updateStatus( status: String ) {
      loginActivityBinding.ScannedText.text = status
    }

    override fun onResume() {
        super.onResume()

        checkPermission()

        val bluetoothPermission = Manifest.permission.BLUETOOTH
        val bluetoothAdminPermission = Manifest.permission.BLUETOOTH_ADMIN

//        if (ContextCompat.checkSelfPermission(this, bluetoothPermission) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(this, bluetoothAdminPermission) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are granted, you can use Bluetooth functionality

//        try {
//
//            val sdkHandler: SDKHandler = SDKHandler(this)
//            sdkHandler.dcssdkSetDelegate(this)
//            sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL)
//            sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI)
//
//            var notifications_mask = 0
//            // We would like to subscribe to all scanner available/not-available events
//            notifications_mask =
//                notifications_mask or (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value)
//            // We would like to subscribe to all scanner connection events
//            notifications_mask =
//                notifications_mask or (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value)
//            // We would like to subscribe to all barcode events
//            notifications_mask =
//                notifications_mask or DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value
//
//            // subscribe to events set in notification mask
//            sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
//            sdkHandler.dcssdkEnableAvailableScannersDetection(true)
//            sdkHandler.dcssdkGetAvailableScannersList(scanListInfo)
//            sdkHandler.dcssdkGetActiveScannersList(scanListInfo)
//
//            val layoutParams = LinearLayout.LayoutParams(-1, -1)
//            val barCodeView: BarCodeView =
//                sdkHandler.dcssdkGetPairingBarcode(
//                    DCSSDKDefs.DCSSDK_BT_PROTOCOL.SSI_BT_LE,
//                    DCSSDKDefs.DCSSDK_BT_SCANNER_CONFIG.KEEP_CURRENT
//                )
//            updateBarcodeView(layoutParams, barCodeView)
//        }catch (e: Exception){
//            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
//        }
//        } else {
//            Toast.makeText(this,"Bluetooth permission needed.", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
        }else{
            checkPermission()
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")
            }
        }


    private fun updateBarcodeView(
        layoutParams: LinearLayout.LayoutParams,
        barCodeView: BarCodeView
    ) {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y
        val orientation = this.resources.configuration.orientation
        var x = width * 9 / 10
        var y = x / 3
//        if (getDeviceScreenSize() > 6) { // TODO: Check 6 is ok or not
            x = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width / 2
            } else {
                width * 2 / 3
            }
            y = x / 3
//        }
        barCodeView.setSize(x, y)
        loginActivityBinding.barCodeFrame.addView(barCodeView, layoutParams)
    }

    override fun onOpened(p0: EMDKManager?) {
        // Get a reference to EMDKManager
        if (p0 != null) {
            this.emdkManager = p0
        };

// Get a  reference to the BarcodeManager feature object
        initBarcodeManager();

// Initialize the scanner
        initScanner();
    }

    override fun onClosed() {
        if (emdkManager != null) {
            emdkManager.release()
        }
        updateStatus("EMDK closed unexpectedly! Please close and restart the application.");
    }

    override fun onStatus(statusData: StatusData?) {
        updateStatus(statusData.toString());

        val state = statusData!!.state
        when (state) {
            ScannerStates.IDLE -> {
                statusString = statusData!!.friendlyName + " is enabled and idle..."
                updateStatus(statusString)
                 //set trigger type
                if (bSoftTriggerSelected) {
                    scanner!!.triggerType = Scanner.TriggerType.SOFT_ONCE
                    bSoftTriggerSelected = false
                } else {
                    scanner!!.triggerType = Scanner.TriggerType.HARD
                }
                // set decoders
                if (bDecoderSettingsChanged) {
                    setDecoders()
                    bDecoderSettingsChanged = false
                }
                 //submit read
                if (!scanner!!.isReadPending && !bExtScannerDisconnected) {
                    try {
                        scanner!!.read()
                    } catch (e: ScannerException) {
                        updateStatus(e.message!!)
                    }
                }
            }

            ScannerStates.WAITING -> {
                statusString = "Scanner is waiting for trigger press..."
                updateStatus(statusString)
            }

            ScannerStates.SCANNING -> {
                statusString = "Scanning..."
                updateStatus(statusString)
            }

            ScannerStates.DISABLED -> {
                statusString = statusData!!.friendlyName + " is disabled."
                updateStatus(statusString)
            }

            ScannerStates.ERROR -> {
                statusString = "An error has occurred."
                updateStatus(statusString)
            }

            else -> {}
        }
    }

    private fun setDecoders() {
        if (scanner != null) {
            try {
                val config = scanner!!.config
                // Set EAN8
                config.decoderParams.ean8.enabled = true
                // Set EAN13
                config.decoderParams.ean13.enabled = true
                // Set Code39
                config.decoderParams.code39.enabled = true
                //Set Code128
                config.decoderParams.code128.enabled = true
                scanner!!.config = config
            } catch (e: ScannerException) {
                updateStatus(e.message!!)
            }
        }
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        // The ScanDataCollection object gives scanning result and the collection of ScanData. Check the data and its status.
        // The ScanDataCollection object gives scanning result and the collection of ScanData. Check the data and its status.
        Log.d("TAG", "onData: Data Received")
        var dataStr = ""
        if (scanDataCollection != null && scanDataCollection.result == ScannerResults.SUCCESS) {
            val scanData: ArrayList<ScanData> = scanDataCollection.scanData
            // Iterate through scanned data and prepare the data.
            for (data in scanData) {
                // Get the scanned data
                val barcodeData = data.data
                // Get the type of label being scanned
                val labelType = data.labelType
                // Concatenate barcode data and label type
                dataStr = "$barcodeData  $labelType"
            }
            // Update EditText with scanned data and type of label on UI thread.
            updateData(dataStr)
        }
    }

    private fun updateData(dataStr: String) {
        loginActivityBinding.DataUpdate.text = dataStr
    }


    override fun onDestroy() {
        super.onDestroy()
        if (emdkManager != null) {
            emdkManager.release();
        }
    }

}