package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.viewmodel.PostViewModel
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )


        val adapter = PostsAdapter(object : OnInteractionListener {


            override fun onLike(post: Post) {
                if (post.likedByMe == false) {
                    viewModel.likeById(post.id)
                } else {
                    viewModel.unlikeById(post.id)
                }
            }

            override fun onRepost(post: Post) {
                viewModel.repostById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "Repost post")
                startActivity(shareIntent)
            }

            override fun onOpen(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment,
                    Bundle().apply {
                        val idArg = putLong("id", post.id)
                    })
            }


            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onPlay(post: Post) {
                val startVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(startVideo)
            }
        })
        binding.list.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
        }

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }
        viewModel.newerCount.observe(viewLifecycleOwner){state ->
            binding.freshPosts.visibility = View.VISIBLE

        }
        binding.freshPosts.setOnClickListener{
            viewModel.loadPosts()
            binding.freshPosts.visibility = View.GONE
        }

        binding.refresh.setOnRefreshListener {
            viewModel.loadPosts()
            binding.refresh.isRefreshing = false
        }

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            if (post.id == 0L) {
                return@observe
            }
            findNavController().navigate(R.id.action_feedFragment_to_editPostFragment,
                Bundle().apply {
                    textArg = post.content
                    arguments = bundleOf(
                        "authorId" to post.authorName,
                        "content" to post.content
                    )
                })
        }
        val draftText = arguments?.textArg.toString()
        binding.newpost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            Bundle().apply {
                textArg = draftText
            }
        }

        return binding.root
    }
}

