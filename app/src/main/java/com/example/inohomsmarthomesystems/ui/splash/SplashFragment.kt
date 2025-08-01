package com.example.inohomsmarthomesystems.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.inohomsmarthomesystems.R
import com.example.inohomsmarthomesystems.databinding.FragmentSplashBinding
import com.example.inohomsmarthomesystems.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToOnBoarding.collect { shouldNavigate ->
                if (shouldNavigate) {
                    navigateToNextScreen()
                }
            }
        }
    }
    
    private fun navigateToNextScreen() {
        try {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        } catch (e: Exception) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}