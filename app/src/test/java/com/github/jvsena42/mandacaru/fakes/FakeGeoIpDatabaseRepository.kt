package com.github.jvsena42.mandacaru.fakes

import com.github.jvsena42.mandacaru.data.GeoIpDatabaseRepository

class FakeGeoIpDatabaseRepository : GeoIpDatabaseRepository {
    override suspend fun refresh(force: Boolean) = Unit
    override suspend fun deleteDatabase() = Unit
}
