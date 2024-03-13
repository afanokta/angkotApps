package com.example.trackingapps.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapps.databinding.FragmentEditTrackBinding
import com.example.trackingapps.databinding.FragmentSecond2Binding
import com.example.trackingapps.model.Track
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [EditTrackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditTrackFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentEditTrackBinding? = null
    private val binding get() = _binding!!
    private lateinit var track: Track
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditTrackBinding.inflate(inflater, container, false)
        val data = arguments
        (activity as AppCompatActivity).supportActionBar?.title = "Detail Trayek"
        track = data?.getSerializable("track") as Track
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity!!.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        var userRole = sharedPreferences.getString("role", null)
        binding.etIzinArmada.setText(track.izinArmada.toString())
        binding.etJumlahArmada.setText(track.jumlahArmada.toString())
        binding.etKodeTrayek.setText(track.kodeTrayek.toString())
        binding.etTrayek.setText(track.trayek.toString())
        binding.etTarifUmum.setText(track.tarifUmum.toString())
        binding.etTarifPelajar.setText(track.tarifPelajar.toString())
        if (userRole == "PENUMPANG") {
            binding.btSave.visibility = View.INVISIBLE
            binding.tvTitle.text = "DETAIL DATA TRAYEK"
            binding.etIzinArmada.isEnabled = false
            binding.etJumlahArmada.isEnabled = false
            binding.etKodeTrayek.isEnabled = false
            binding.etTrayek.isEnabled = false
            binding.etTarifUmum.isEnabled = false
            binding.etTarifPelajar.isEnabled = false
        }

        binding.btSave.setOnClickListener {
            val izinArmada = binding.etIzinArmada.text.toString()
            val jumlahArmada = binding.etJumlahArmada.text.toString()
            val kodeTrayek = binding.etKodeTrayek.text.toString()
            val trayek = binding.etTrayek.text.toString()
            var tarifUmum = binding.etTarifUmum.text.toString()
            var tarifPelajar = binding.etTarifPelajar.text.toString()
            if (izinArmada.isBlank() || jumlahArmada.isBlank() || kodeTrayek.isBlank() || trayek.isBlank() || tarifUmum.isBlank() || tarifPelajar.isBlank()) {
                Toast.makeText(context, "Form Harus Diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val newTrack =
                Track(
                    track.uid,
                    izinArmada.toInt(),
                    jumlahArmada.toInt(),
                    kodeTrayek,
                    trayek,
                    tarifUmum.toInt(),
                    tarifPelajar.toInt()
                )
            FirebaseDatabase.getInstance().getReference("trayek").child(track.uid)
                .setValue(newTrack).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(context, "Data Berhasil Diupdate !", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Data Gagal Berhasil Diupdate !, Silahkan Coba Ulang", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment EditTrackFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            EditTrackFragment().apply {
//            }
//    }
}