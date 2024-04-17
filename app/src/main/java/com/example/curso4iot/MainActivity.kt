package com.example.curso4iot

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.curso4iot.ui.theme.Curso4IOTTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    var receiver: BroadcastReceiver? = null
    var dev:BluetoothDevice?=null
    lateinit var mainActivityViewModel:MainActivityViewModel

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothPermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.BLUETOOTH_CONNECT, false) -> {
                    //Concedemos permisos a la aplicacion
                }

                permissions.getOrDefault(android.Manifest.permission.BLUETOOTH, false) -> {
                    //Concedemos permisos a la aplicacion
                }

                permissions.getOrDefault(android.Manifest.permission.BLUETOOTH_ADMIN, false) -> {
                    //Concedemos permisos a la aplicacion
                }

                else -> {
                    //Si no damos permisos por ejemplo cerramos la aplicacion
                }

            }

        }

        val bluetoothManager: BluetoothManager =
            getSystemService(BluetoothManager::class.java) as BluetoothManager
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            // No se soporta el bluetooth cerrariamos la aplicación
            finish()
        }
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)
        }
        if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this,"No tienes permisos para bluetooth",Toast.LENGTH_LONG).show()
            }



        setContent {
            var times by remember {
                mutableStateOf(0)
            }
            var status by remember{
                mutableStateOf("Desconectado")
            }

            mainActivityViewModel=viewModel()

            var connected=mainActivityViewModel.connected.collectAsState()

            Principal(estado = status,connected=connected.value,onDir={
                when(it){
                    "Arriba" -> {
                        mainActivityViewModel.arriba()
                    }
                    "Abajo" -> {
                        mainActivityViewModel.abajo()
                    }
                    "Parar" -> {
                        mainActivityViewModel.parar()
                    }
                }
            }, onAngule = {
                mainActivityViewModel.gira(it.toInt())
            })
            LaunchedEffect(key1 = times) {
                delay(1000L)
                mainActivityViewModel.conectar()
                if (connected.value == true) {
                    status = "Conectado"
                } else {
                    status = "Desconectado"

                }
                times++
            }

        }

        bluetoothPermissions.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
            Log.i("TAG", "Device Name: $deviceName , mac: $deviceHardwareAddress")
            if (device.name.toString() == "ESP32") {
                Log.i("Buscar", "Encontrado")
                dev=bluetoothAdapter.getRemoteDevice(device.address)
                mainActivityViewModel.uuid=dev?.uuids?.get(0)?.uuid
                mainActivityViewModel.dev=dev

        }
        }
        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent) {

                Log.i("Action", p1.action.toString())
                when (p1.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        var device: BluetoothDevice? =
                            p1?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Log.i("Buscar", "No tiene permisos")
                            return
                        }
                        Log.i("Buscar", "Device Name: ${device?.name}, mac: ${device?.address}")
                        if (device?.name.toString() == "Toldo") {
                            Log.i("Buscar", "Encontrado")
                            bluetoothAdapter?.cancelDiscovery()
                            dev=bluetoothAdapter?.getRemoteDevice(device?.address)
                            dev?.let{
                                it.createBond()
                                for (u in it.uuids){
                                    Log.i("UUID",u.toString())
                                }
                                mainActivityViewModel.uuid=it.uuids[0].uuid
                                mainActivityViewModel.dev=it
                                mainActivityViewModel._btsocket.value=it.createRfcommSocketToServiceRecord(it.uuids[0].uuid)


                            }
                        }

                    }
                }

            }
        }
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        bluetoothAdapter?.startDiscovery()


    }

    override fun onPause() {
        super.onPause()
        receiver?.let{
            unregisterReceiver(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let{
            unregisterReceiver(it)
        }
    }




}


@Composable
fun Principal(estado:String,connected:Boolean, onDir: (String) -> Unit,onAngule:(Float)->Unit) {
    Column {
        Text(text = "Contectado:$estado")
        Button(onClick = { onDir("Arriba") }, modifier = Modifier.size(100.dp),enabled = connected) {
            Icon(imageVector = Icons.Filled.KeyboardArrowUp, contentDescription ="Arriba" )

        }
        Button(onClick = { onDir("Parar") }, modifier = Modifier.size(100.dp),enabled = connected) {
            Icon(imageVector = Icons.Filled.Menu, contentDescription ="Parar" )

        }

        Button(onClick = { onDir("Abajo") }, modifier = Modifier.size(100.dp),enabled = connected) {
            Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription ="Abajo" )

        }

        Dial(onAngle = onAngule)
    }

}