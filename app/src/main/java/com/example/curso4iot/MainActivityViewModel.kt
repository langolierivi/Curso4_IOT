package com.example.curso4iot

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class MainActivityViewModel:ViewModel() {
    var _btsocket= MutableStateFlow<BluetoothSocket?>(null)
    var btsocket: StateFlow<BluetoothSocket?>  = _btsocket
    var _connected= MutableStateFlow(false)
    var connected: StateFlow<Boolean>  = _connected

    var uuid:UUID?=null
    var dev: BluetoothDevice?=null

    @SuppressLint("MissingPermission")
    fun conectar(){
        if ( dev!=null && uuid!=null){

            try{
                if (_btsocket.value?.isConnected==false){
                    _btsocket.value =dev?.createRfcommSocketToServiceRecord(uuid)
                    _btsocket.value?.connect()
                    _connected.value=true
                }

            }catch (e:Exception){
                _connected.value=false

            }

        }
    }


    @SuppressLint("MissingPermission")
    fun enviarComando(comando:String){
        Log.i("Envio",comando)
        try{
            _btsocket.value?.outputStream?.write(comando.toByteArray())
        }catch (e:Exception){
            Log.i("Error", e.message.toString())
            _connected.value=false
        }
    }

    @SuppressLint("MissingPermission")
    fun arriba(){
        var t="D-D"
        Log.i("Envio",t)
        try{
            btsocket.value?.outputStream?.write(t.toByteArray())
        }catch (e:Exception){
            Log.i("Error", e.message.toString())
            _connected.value=false
        }


    }

    @SuppressLint("MissingPermission")
    fun abajo(){
        var t="D-I"
        Log.i("Envio",t)
        try{
            btsocket.value?.outputStream?.write(t.toByteArray())
        }
        catch (e:Exception){
            Log.i("Error", e.message.toString())
            _connected.value=false
        }

    }
    @SuppressLint("MissingPermission")
    fun parar(){
        var t="D-P"
        Log.i("Envio",t)
        try{
            btsocket.value?.outputStream?.write(t.toByteArray())
        }
        catch (e:Exception){
            Log.i("Error", e.message.toString())
            _connected.value=false
        }

    }

    @SuppressLint("MissingPermission")
    fun gira(angulo:Int
    ){
        var t="M-${angulo}"
        Log.i("Envio",t)
        try{
            btsocket.value?.outputStream?.write(t.toByteArray())
        }
        catch (e:Exception){
            Log.i("Error", e.message.toString())
            _connected.value=false
        }

    }
}