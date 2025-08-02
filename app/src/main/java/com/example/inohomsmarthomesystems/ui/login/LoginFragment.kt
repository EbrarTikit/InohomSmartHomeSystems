package com.example.inohomsmarthomesystems.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.inohomsmarthomesystems.R
import com.example.inohomsmarthomesystems.databinding.FragmentLoginBinding
import com.example.inohomsmarthomesystems.data.remote.WebSocketService
import com.example.inohomsmarthomesystems.ui.home.HomeFragment
import com.example.inohomsmarthomesystems.utils.state.UIState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.inohomsmarthomesystems.utils.state.ConnectionState

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupAnimations()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupAnimations() {
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down_fade_in)
        binding.tvVersion.startAnimation(animation)
        binding.btnAccounts.startAnimation(animation)
    }

    // 'Hesaplar' butonuna tıklanınca authentication akışı tetiklenir
    private fun setupClickListeners() {
        binding.btnAccounts.setOnClickListener { viewModel.onAccountsButtonClicked()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                handleUiState(state)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.connectionState.collectLatest { state ->
                handleConnectionState(state)
            }
        }
    }

    // UIState durumlarına uygun olarak buton ve mesajları günceller
    private fun handleUiState(state: UIState<String>) {
        when (state) {
            is UIState.Idle -> {
                binding.btnAccounts.isEnabled = true
                binding.btnAccounts.text = getString(R.string.accounts)
            }
            is UIState.Loading -> {
                binding.btnAccounts.isEnabled = false
                binding.btnAccounts.text = getString(R.string.connecting)
            }
            is UIState.Success -> {
                Toast.makeText(requireContext(), "${getString(R.string.login_success)}: ${state.data}", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
            is UIState.Error -> {
                binding.btnAccounts.isEnabled = true
                binding.btnAccounts.text = getString(R.string.accounts)
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleConnectionState(state: ConnectionState) {
        when (state) {
            ConnectionState.CONNECTED -> {
                binding.btnAccounts.text = getString(R.string.authenticating)
            }
            ConnectionState.ERROR -> {
                binding.btnAccounts.isEnabled = true
                binding.btnAccounts.text = getString(R.string.accounts)
            }
            ConnectionState.DISCONNECTED -> {
                binding.btnAccounts.isEnabled = true
                binding.btnAccounts.text = getString(R.string.accounts)
            }
        }
    }
    private fun navigateToHome() {
        try {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        } catch (e: Exception) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}