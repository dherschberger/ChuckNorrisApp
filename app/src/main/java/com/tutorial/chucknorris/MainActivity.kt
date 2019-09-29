package com.tutorial.chucknorris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onRefreshClicked(view: View) {
        Log.v("MainActivity", "onRefreshClicked")
        textViewJoke.text = "Refresh was clicked. Loading..."

        val url = "http://api.icndb.com/jokes/random/5?escape=javascript"
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        val jsonObjRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                onResultSuccess(response)
            },
            Response.ErrorListener { error ->
                onResultError(error.toString())
            })

        queue.add(jsonObjRequest)
    }

    private fun onResultError(error: String) {
        textViewJoke.text = error
    }

    private fun onResultSuccess(resultJson: JSONObject) {
        // { "type": "success", "value": { "id": 268, "joke": "Time waits for no man. Unless that man is Chuck Norris." } }
        // or will have an array of values in "value"
        val worked = resultJson.optString("type", "failed")

        if ("success" != worked.toLowerCase()) {
            onResultError("Result was not successful.")
            return
        }

        var jokesString = ""

        try {
            val jokes = resultJson.optJSONArray("value")
            if (null != jokes) {
                for (jokeNum in 0..jokes.length() - 1) {
                    jokesString += (jokeNum + 1).toString() + ". " + jokes.getJSONObject(jokeNum).getString("joke").replace("\\", "")
                    jokesString += "\n"
                }
            } else {
                val joke = resultJson.getJSONObject("value").getString("joke")
                jokesString = joke
            }
        } catch (e: Exception) {
            jokesString = "Exception parsing JSON"
        }

        textViewJoke.text = jokesString.trim()
    }
}
