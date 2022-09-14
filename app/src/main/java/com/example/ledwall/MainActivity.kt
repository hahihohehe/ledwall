package com.example.ledwall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.ledwall.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnUpload.setOnClickListener {
            upload(
                binding.etAdress.text.toString(),
                binding.etText.text.toString()
            )
        }
    }

    private fun upload(urlStr: String, text: String) {
        val job = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val url = URL(urlStr)

                val con: HttpURLConnection = url.openConnection() as HttpURLConnection
                con.requestMethod = "POST"

                con.setRequestProperty("Content-Type", "application/json; utf-8")

                con.doOutput = true

                con.outputStream.use { os ->
                    val input = text.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }

                println("Sent: $text")

                val code: Int = con.responseCode
                println(code)

                BufferedReader(InputStreamReader(con.inputStream, "utf-8")).use { br ->
                    val response = StringBuilder()
                    var responseLine: String? = null
                    while (br.readLine().also { responseLine = it } != null) {
                        response.append(responseLine!!.trim { it <= ' ' })
                    }
                    println(response.toString())
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }
}