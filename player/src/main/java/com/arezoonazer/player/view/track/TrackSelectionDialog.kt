package com.arezoonazer.player.view.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.arezoonazer.player.databinding.DialogTrackSelectionBinding
import com.arezoonazer.player.util.track.TrackEntity

class TrackSelectionDialog : DialogFragment() {

    private val binding: DialogTrackSelectionBinding by lazy(LazyThreadSafetyMode.NONE) {
        DialogTrackSelectionBinding.inflate(layoutInflater)
    }

    private val items: List<TrackEntity> by lazy(LazyThreadSafetyMode.NONE) {
        arguments?.get(TRACK_ITEMS) as List<TrackEntity>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTrackRecyclerView()
    }

    private fun setupTrackRecyclerView() {
        binding.trackRecyclerview.adapter = TrackAdapter(items)
    }

    companion object {
        private const val TRACK_ITEMS = "trackItems"

        fun newInstance(items: List<TrackEntity>): TrackSelectionDialog {
            return TrackSelectionDialog().apply {
                arguments = bundleOf(TRACK_ITEMS to items)
            }
        }
    }
}