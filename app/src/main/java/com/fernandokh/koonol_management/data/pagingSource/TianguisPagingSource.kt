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

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TianguisModel> {
        return try {
            val currentPage = params.key ?: 1

            // Log de parámetros de la solicitud
            Log.d("TianguisPagingSource", "Realizando solicitud al API: page = $currentPage, limit = ${params.loadSize}, search = '$search', sort = '$sort'")

            // Realizamos la solicitud al API para obtener la lista de tianguis
            val response = apiService.search(
                page = currentPage,
                limit = params.loadSize,
                search = search,
                sort = sort,
            )

            // Log de la respuesta completa
            Log.d("TianguisPagingSource", "Respuesta de la API: $response")

            // Extraemos los datos de la respuesta
            val tianguisList = response.data ?: emptyList()

            // Actualizamos el total de registros con el tamaño de la lista
            onUpdateTotal(tianguisList.size)

            // Log de los datos obtenidos
            Log.d("TianguisPagingSource", "Cantidad de tianguis en la lista: ${tianguisList.size}")

            // Log de cada elemento en la lista
            tianguisList.forEachIndexed { index, tianguis ->
                Log.d("TianguisPagingSource", "Tianguis $index: ${tianguis.name}, Localidad: ${tianguis.locality}")
            }

            // Devolvemos los datos para la paginación
            LoadResult.Page(
                data = tianguisList,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (tianguisList.isEmpty()) null else currentPage + 1
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
        return state.anchorPosition
    }
}
