package com.fernandokh.koonol_management.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fernandokh.koonol_management.data.api.UserApiService
import com.fernandokh.koonol_management.data.models.UserInModel

class UserPagingSource(
    private val apiService: UserApiService,
    private val search: String,
    private val sort: String,
    private val rol: String,
    private val onUpdateTotal: (Int) -> Unit
) : PagingSource<Int, UserInModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInModel> {
        return try {
            val currentPage = params.key ?: 1

            val response = apiService.search(
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort,
                rol = rol
            )

            val users = response.data?.results ?: emptyList()
            onUpdateTotal(response.data?.count ?: 0)

            LoadResult.Page(
                data = users,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (users.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserInModel>): Int? {
        return state.anchorPosition
    }
}
