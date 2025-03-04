package dev.jdtech.jellyfin.fragments

import android.app.UiModeManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.jdtech.jellyfin.adapters.ServerGridAdapter
import dev.jdtech.jellyfin.databinding.FragmentServerSelectBinding
import dev.jdtech.jellyfin.dialogs.DeleteServerDialogFragment
import dev.jdtech.jellyfin.viewmodels.ServerSelectViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServerSelectFragment : Fragment() {

    private lateinit var binding: FragmentServerSelectBinding
    private lateinit var uiModeManager: UiModeManager
    private val viewModel: ServerSelectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServerSelectBinding.inflate(inflater)
        uiModeManager =
            requireContext().getSystemService(AppCompatActivity.UI_MODE_SERVICE) as UiModeManager

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        binding.serversRecyclerView.adapter =
            ServerGridAdapter(
                ServerGridAdapter.OnClickListener { server ->
                    viewModel.connectToServer(server)
                },
                ServerGridAdapter.OnLongClickListener { server ->
                    DeleteServerDialogFragment(viewModel, server).show(
                        parentFragmentManager,
                        "deleteServer"
                    )
                    true
                }
            )

        binding.buttonAddServer.setOnClickListener {
            navigateToAddServerFragment()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToMain.collect {
                    if (it) {
                        navigateToMainActivity()
                    }
                }
            }
        }

        return binding.root
    }

    private fun navigateToAddServerFragment() {
        findNavController().navigate(
            ServerSelectFragmentDirections.actionServerSelectFragmentToAddServerFragment()
        )
    }

    private fun navigateToMainActivity() {
        if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION) {
            findNavController().navigate(ServerSelectFragmentDirections.actionServerSelectFragmentToHomeFragmentTv())
        } else {
            findNavController().navigate(ServerSelectFragmentDirections.actionServerSelectFragmentToHomeFragment())
        }
    }
}
