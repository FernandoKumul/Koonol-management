package com.fernandokh.koonol_management.data.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fernandokh.koonol_management.data.api.PromotionApiService
import com.fernandokh.koonol_management.data.models.PromotionSearchModel
import com.fernandokh.koonol_management.utils.evaluateHttpException
import retrofit2.HttpException

class PromotionPagingSource (
    private val apiService: PromotionApiService,
    private val token: String,
    private val search: String,
    private val sort: String,
    private val salesStallId: String?,
    private val minPay: Double?,
    private val maxPay: Double?,
    private val startDate: String?,
    private val endDate: String?,
    private val onUpdateTotal: (Int) -> Unit
) : PagingSource<Int, PromotionSearchModel>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PromotionSearchModel> {
        return try {
            val currentPage = params.key ?: 1

            val response = apiService.search(
                authHeader = "Bearer $token",
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort,
                salesStallId = salesStallId,
                minPay = minPay,
                maxPay = maxPay,
                startDate = startDate,
                endDate = endDate
            )

            val categories = response.data?.results ?: emptyList()
            onUpdateTotal(response.data?.count ?: 0)

            LoadResult.Page(
                data = categories,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (categories.isEmpty()) null else currentPage + 1
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

    override fun getRefreshKey(state: PagingState<Int, PromotionSearchModel>): Int? {
        return state.anchorPosition
    }
}