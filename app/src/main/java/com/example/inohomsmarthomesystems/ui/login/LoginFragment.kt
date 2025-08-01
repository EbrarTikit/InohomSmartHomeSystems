package com.example.inohomsmarthomesystems.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.inohomsmarthomesystems.R
import com.example.inohomsmarthomesystems.databinding.FragmentLoginBinding
import com.example.inohomsmarthomesystems.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down_fade_in)
        binding.tvVersion.startAnimation(animation)
        binding.btnAccounts.startAnimation(animation)
    }
}