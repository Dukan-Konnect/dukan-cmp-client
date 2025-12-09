package org.example.project.home.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.example.project.home.data.local.entities.BookingEntity

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY booking_date DESC")
    fun observeAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: String): BookingEntity?

    @Query("SELECT * FROM bookings WHERE order_id = :orderId")
    suspend fun getBookingsByOrderId(orderId: String): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookings(bookings: List<BookingEntity>)

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("DELETE FROM bookings WHERE id = :bookingId")
    suspend fun deleteBooking(bookingId: String)

    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY booking_date DESC")
    fun observeBookingsByStatus(status: String): Flow<List<BookingEntity>>
}

