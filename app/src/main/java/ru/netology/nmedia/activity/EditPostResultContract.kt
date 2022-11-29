package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

class EditPostResultContract : ActivityResultContract<Post, String?>() {
    override fun createIntent(context: Context, input: Post): Intent =
        Intent(context, EditPostActivity::class.java)
            .putExtra(Intent.EXTRA_TEXT, input.content)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}
