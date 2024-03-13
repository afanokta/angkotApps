package com.example.trackingapps.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackingapps.R
import com.example.trackingapps.bind.TrackAdapter
import com.example.trackingapps.databinding.FragmentFirst2Binding
import com.example.trackingapps.model.Track
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RecyclerViewTrackFragment : Fragment() {

    private var _binding: FragmentFirst2Binding? = null
    private val binding get() = _binding!!
    private lateinit var trackList: ArrayList<Track>
    private lateinit var dbRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var trackRv: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var editTrackFragment: EditTrackFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirst2Binding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Daftar Trayek"
        editTrackFragment = EditTrackFragment()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trackRv = _binding!!.rvTrayek
        trackRv.layoutManager = LinearLayoutManager(requireContext())
        trackRv.setHasFixedSize(true)
        trackList = arrayListOf<Track>()
        sharedPreferences = (activity as AppCompatActivity).getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        var userRole = sharedPreferences.getString("role", null)
        getListTrack()
        if(userRole != "ADMIN"){
            binding.fabAdd.visibility = View.INVISIBLE
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getListTrack() {
        dbRef = FirebaseDatabase.getInstance().getReference("trayek")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    trackList = ArrayList()
                    for (trackSnapshot in snapshot.children) {
                        val track = trackSnapshot.getValue(Track::class.java)
                        trackList.add(track!!)
                    }
                    trackAdapter = TrackAdapter(trackList)
                    itemOnCLick()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal Mendapatkan Data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun itemOnCLick() {
        trackRv.adapter = trackAdapter
        trackAdapter.onItemClick = {
            Log.w("TRACKLIST", it.trayek.toString())
            var bundle = Bundle()
            bundle.putSerializable("track", it)
            parentFragmentManager.beginTransaction().apply {
                findNavController().navigate(R.id.action_First2Fragment_to_editTrackFragment2, bundle)
            }
        }
    }
}