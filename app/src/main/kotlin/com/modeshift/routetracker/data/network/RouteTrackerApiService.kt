package com.modeshift.routetracker.data.network

import com.modeshift.routetracker.core.models.Resource
import com.modeshift.routetracker.core.network.httpclient.HttpRequestExecutor
import com.modeshift.routetracker.data.network.dto.RouteDto
import com.modeshift.routetracker.data.network.dto.StopDto
import com.modeshift.routetracker.data.network.dto.VisitedStopEventDto
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteTrackerApiService @Inject constructor(
    private val httpRequestExecutor: HttpRequestExecutor
) {
    suspend fun getRoutes(): Resource<List<RouteDto>> {
        return httpRequestExecutor.execute {
            method = HttpMethod.Get
            url {
                path("routes")
                parameters.append("code", CODE)
            }
        }
    }

    suspend fun getStops(): Resource<List<StopDto>> {
        return httpRequestExecutor.execute {
            method = HttpMethod.Get
            url {
                path("stops")
                parameters.append("code", CODE)
            }
        }
    }

    suspend fun sendVisitedStopEvent(visitedStopEvents: List<VisitedStopEventDto>): Resource<Unit> {
        return httpRequestExecutor.execute {
            method = HttpMethod.Post
            url {
                path("visited-stops")
                parameters.append("code", CODE)
            }
            contentType(ContentType.Application.Json)
            setBody(visitedStopEvents)
        }
    }

    companion object {
        private val CODE = "k6HrRTDHiKtj_in7wLa7zmULLgYyWoqhhKErW4HIefPdAzFuKdVIYQ=="
    }
}