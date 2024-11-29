package com.fernandokh.koonol_management.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fernandokh.koonol_management.data.api.TianguisApiService
import com.fernandokh.koonol_management.data.models.TianguisModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import retrofit2.HttpException

class TianguisPagingSource(
    private val apiService: TianguisApiService,
    private val search: String,
    private val sort: String,
    private val onUpdateTotal: (Int) -> Unit
) : PagingSource<Int, TianguisModel>() {

    // Set para almacenar IDs únicos cargados previamente
    private val loadedIds = mutableSetOf<String>()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TianguisModel> {
        return try {
            val currentPage = params.key ?: 1

            Log.d("TianguisPagingSource", "Realizando solicitud al API: page = $currentPage, limit = ${params.loadSize}, search = '$search', sort = '$sort'")

            val response = apiService.search(
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort,
            )

            val tianguis = response.data?.results ?: emptyList()
            onUpdateTotal(response.data?.count ?: 0)

            LoadResult.Page(
                data = tianguis,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (tianguis.isEmpty()) null else currentPage + 1
            )
        } catch (e: HttpException) {
            val errorMessage = evaluateHttpException(e)
            Log.e("TianguisPagingSource", "HttpException: $errorMessage")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e("TianguisPagingSource", "Exception: ${e.localizedMessage}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TianguisModel>): Int? {
        val anchorPosition = state.anchorPosition
        return anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }
}
