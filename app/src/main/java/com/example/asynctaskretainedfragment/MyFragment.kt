package com.example.asynctaskretainedfragment

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var apiService: ApiService
    private var items: List<Item>? = null
    private var fetchDataTask: FetchDataTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_layout, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = MyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (items != null) {
            adapter.setItems(items!!)
        } else {
            fetchData()
        }
    }

    private fun fetchData() {
        if (fetchDataTask == null) {
            fetchDataTask = FetchDataTask()
            fetchDataTask?.execute()
        }
    }

    private inner class FetchDataTask : AsyncTask<Void, Void, List<Item>>() {
        override fun onPreExecute() {
            // Show progress dialog or update UI before starting the task
        }

        override fun doInBackground(vararg params: Void): List<Item>? {
            val call = apiService.getItems()
            return try {
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: List<Item>?) {
            fetchDataTask = null
            if (result != null) {
                items = result
                adapter.setItems(result)
            } else {
                // Handle error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cancel the AsyncTask if it's running to prevent leaks
        fetchDataTask?.cancel(true)
    }
}
