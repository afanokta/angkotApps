package com.example.trackingapps.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trackingapps.R
import com.example.trackingapps.databinding.FragmentSecond2Binding
import com.example.trackingapps.model.Track
import com.google.firebase.database.FirebaseDatabase

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddTrackFragment : Fragment() {

    private var _binding: FragmentSecond2Binding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecond2Binding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Tambah Data Trayek"
//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_Second2Fragment_to_First2Fragment)
//        }

        binding.btSave.setOnClickListener {
            var kodeTrayek = binding.etKodeTrayek.text.toString()
            var jumlahArmada = binding.etJumlahArmada.text.toString()
            var izinArmada = binding.etIzinArmada.text.toString()
            var trayek = binding.etTrayek.text.toString()
            var tarifUmum = binding.etTarifUmum.text.toString()
            var tarifPelajar = binding.etTarifPelajar.text.toString()
            if (kodeTrayek.isBlank() || jumlahArmada.isBlank() || izinArmada.isBlank() || trayek.isBlank() || tarifUmum.isBlank() || tarifPelajar.isBlank()) {
                Toast.makeText(context, "Form Harus Diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val track = Track(
                "",
                izinArmada.toInt(),
                jumlahArmada.toInt(),
                kodeTrayek,
                trayek,
                tarifUmum.toInt(),
                tarifPelajar.toInt()
            )
            saveTrack(track)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveTrack(track: Track) {
        val reference = FirebaseDatabase.getInstance().getReference("trayek")
        val key = reference.push().key
        track.uid = key.toString()
        reference.child(key!!).setValue(track).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Berhasil Menyimpan Trayek", Toast.LENGTH_SHORT).show()
                val fragmentRv = RecyclerViewTrackFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.First2Fragment, fragmentRv)?.commit()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal Menyimpan Data: ${it.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }
}