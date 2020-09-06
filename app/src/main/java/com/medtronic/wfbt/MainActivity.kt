package com.medtronic.wfbt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private fun connectUsingNetworkSuggestion(ssid: String, password: String) {
        val wifiNetworkSuggestion = WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(password)
                .build()

        // Optional (Wait for post connection broadcast to one of your suggestions)
        val intentFilter =
                IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return
                }
                showToast("Connection Suggestion Succeeded")
                // do post connect processing here
            }
        }
        registerReceiver(broadcastReceiver, intentFilter)

        val suggestionsList = listOf(wifiNetworkSuggestion)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

        var status = wifiManager.addNetworkSuggestions(suggestionsList)
        Log.i("WifiNetworkSuggestion", "Adding Network suggestions status is $status")
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE) {
            showToast("Suggestion Update Needed")
            status = wifiManager.removeNetworkSuggestions(suggestionsList)
            Log.i("WifiNetworkSuggestion", "Removing Network suggestions status is $status")
            status = wifiManager.addNetworkSuggestions(suggestionsList)
        }
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//            lastSuggestedNetwork = wifiNetworkSuggestion
//            lastSuggestedNetworkSSID = ssid
            showToast("Suggestion Added")
        }
    }

    private fun showToast(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var switch1 = findViewById<Switch>(R.id.switch1)
        switch1.setOnClickListener(View.OnClickListener {

            val networkSSID = "AndroidWifi"
            val networkPass = ""
            connectUsingNetworkSuggestion(networkSSID, networkPass)
        })
    }
}