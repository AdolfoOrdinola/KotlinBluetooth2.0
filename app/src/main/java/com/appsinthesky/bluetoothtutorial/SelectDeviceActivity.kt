package com.appsinthesky.bluetoothtutorial

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.DialogCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.select_device_layout.*
import org.jetbrains.anko.toast

class SelectDeviceActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    //----------------------------------------------------------------------------------------------

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
        val EXTRA_NAME: String = "Device_names"
    }

    //----------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_device_layout)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter == null) {
            toast("this device doesn't support bluetooth")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_list
        select_device_refresh.setOnClickListener{ pairedDeviceList() }

    }

    //----------------------------------------------------------------------------------------------

    private fun pairedDeviceList() {
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices

        val list : ArrayList<BluetoothDevice> = ArrayList()

        if (!m_pairedDevices.isEmpty()) {

            for (device: BluetoothDevice in m_pairedDevices) {

                list.add(device)
                Log.i("Dispositivo", ""+device.name)
            }
        } else {
            toast("no paired bluetooth devices found")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)


        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->


            //Creo un elemento de tipo Cuadro de Diálogo
            lateinit var dialogConfirmacion : AlertDialog.Builder

            //Referencio el cuadro de diálogo a la Activity actual
            dialogConfirmacion = AlertDialog.Builder(this)


            //ícono de bluetooh
            dialogConfirmacion.setIcon(R.drawable.ic_bluetooth_512px)
            dialogConfirmacion.setTitle("Conectando...")
            dialogConfirmacion.setMessage(
                    "¿Iniciar emparejamiento con " + list[position].name + "?\n" +
                            "\nAddress: " + list[position].address)

            //Botón de "C"ancelar"
            dialogConfirmacion.setNegativeButton("Cancelar", null)

            //Cuando se selecciona "Sí, aceptar" en el cuadro de diálogo, se va inicia la segunda Activity
            dialogConfirmacion.setPositiveButton("Sí, aceptar", { dialogInterface, i ->


                val device: BluetoothDevice = list[position]

                val address: String = device.address
                val name: String = device.name

                //Se carga un "intent" con la Activity que se quiere abrir
                val intent = Intent(this, ControlActivity::class.java)


                //Se le adjuntan datos para que se envíen a dicha Activity
                intent.putExtra(EXTRA_ADDRESS, address) //Dirección
                intent.putExtra(EXTRA_NAME, name) //Nombre del dispositivo


                //Se inicia la segunda Activity
                startActivity(intent)
            })


            val dialog = dialogConfirmacion.create()
            dialog.show()

        }
    }

    //----------------------------------------------------------------------------------------------

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter!!.isEnabled) {
                    toast("Bluetooth has been enabled")
                } else {
                    toast("Bluetooth has been disabled")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                toast("Bluetooth enabling has been canceled")
            }
        }
    }
}
