package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT*FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query("""
        UPDATE PostEntity SET
        likesAmount = likesAmount + CASE WHEN likedByMe THEN -1 ELSE +1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """)
    suspend fun likeById(id: Long)

    @Query("""
        UPDATE PostEntity SET
        likesAmount = likesAmount + CASE WHEN likedByMe THEN -1 ELSE +1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """)
    suspend fun unlikeById(id: Long)

    @Query("""
        UPDATE PostEntity SET
        repostAmount = repostAmount + 1
        WHERE id = :id
    """)
    suspend fun repostById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
    suspend fun save(post: PostEntity) =
        if (post.id == 0L) {
            insert(post)
        } else updateContentById(post.id, post.content)
//    fun times()
}