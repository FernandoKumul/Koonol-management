package com.fernandokh.koonol_management.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fernandokh.koonol_management.data.api.SalesStallsApiService
import com.fernandokh.koonol_management.data.models.SalesStallsModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import retrofit2.HttpException

class SalesStallsPagingSource(
    private val apiService: SalesStallsApiService,
    private val search: String,
    private val sort: String,
    private val onUpdateTotal: (Int) -> Unit
) : PagingSource<Int, SalesStallsModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SalesStallsModel> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.search(
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort
            )

            val salesStalls = response.data?.results ?: emptyList()
            onUpdateTotal(response.data?.count ?: 0)
            LoadResult.Page(
                data = salesStalls,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (salesStalls.isEmpty()) null else currentPage + 1
            )
        } catch (e: HttpException) {
            val errorMessage = evaluateHttpException(e)
            Log.e("dev-debug", "Error paginación HttpExpeception: $errorMessage")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("dev-debug", "Error paginación Exception ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SalesStallsModel>): Int? {
        return state.anchorPosition
    }
}