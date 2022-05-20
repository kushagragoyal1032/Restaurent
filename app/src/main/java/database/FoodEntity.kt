package database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "food")
class FoodEntity(
    @PrimaryKey val food_id: Int,
    @ColumnInfo(name = "food_name") val foodName: String,
    @ColumnInfo(name = "book_price") val foodPrice: String,
    @ColumnInfo(name = "book_rating") val foodRating: String,
    @ColumnInfo(name = "book_image") val foodImage: String
)
