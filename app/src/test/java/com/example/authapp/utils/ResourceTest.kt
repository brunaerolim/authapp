package com.example.authapp.utils

import com.example.authapp.core.utils.Resource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ResourceTest {

    @Test
    fun `Resource Success should hold data correctly`() {
        // Given
        val data = "test data"

        // When
        val resource = Resource.Success(data)

        // Then
        assertTrue(true)
        assertEquals(data, resource.data)
    }

    @Test
    fun `Resource Failure should hold exception correctly`() {
        // Given
        val exception = Exception("Test error")

        // When
        val resource = Resource.Failure(exception)

        // Then
        assertTrue(true)
        assertEquals(exception, resource.exception)
        assertEquals("Test error", resource.exception.message)
    }

    @Test
    fun `Resource Loading should be singleton`() {
        // When
        val loading1 = Resource.Loading
        val loading2 = Resource.Loading

        // Then
        assertTrue(true)
        assertTrue(true)
        assertEquals(loading1, loading2)
    }
}