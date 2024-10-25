package com.fernandokh.koonol_management.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fernandokh.koonol_management.data.api.SellerApiService
import com.fernandokh.koonol_management.data.models.SellerModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import retrofit2.HttpException

class SellerPagingSource (
    private val apiService: SellerApiService,
    private val search: String,
    private val sort: String,
    private val gender: String,
    private val onUpdateTotal: (Int) -> Unit
) : PagingSource<Int, SellerModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SellerModel> {
        return try {
            val currentPage = params.key ?: 1

            val response = apiService.search(
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort,
                gender = gender
            )

            val users = response.data?.results ?: emptyList()
            onUpdateTotal(response.data?.count ?: 0)

            LoadResult.Page(
                data = users,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (users.isEmpty()) null else currentPage + 1
            )
        } catch (e: HttpException) {
            val errorMessage = evaluateHttpException(e)
            Log.e("dev-debug", "Error paginación: $errorMessage")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("dev-debug", "Error paginación ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SellerModel>): Int? {
        return state.anchorPosition
    }
}